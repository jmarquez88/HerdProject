package com.example.a7510.herdproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.a7510.herdproject.Common.Common;
import com.example.a7510.herdproject.ViewHolder.QBUsersHolder;
import com.quickblox.chat.QBChatService;
import com.quickblox.content.QBContent;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class UserProfile extends AppCompatActivity {
    EditText editPassword, editOldPassword, editFullName, editEmail, editPhone;
    Button buttonUpdate, buttonCancel;

    ImageView user_avatar;

    //Populates the menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_update_menu, menu);
        return true;
    }

    //If the user log out image is clicked, then the logout function is called which signs out the user
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.user_update_log_out:
                logout();
                break;
            default:
                break;
        }
        return true;
    }

    //Function that logs out the user
    private void logout() {
        QBUsers.signOut().performAsync(new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {
                QBChatService.getInstance().logout(new QBEntityCallback<Void>() {
                    @Override
                    public void onSuccess(Void aVoid, Bundle bundle) {
                        Toast.makeText(UserProfile.this, "Logged out!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(UserProfile.this, MainActivity.class);
                        //Removes all of the previous activities
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(QBResponseException e) {

                    }
                });
            }

            @Override
            public void onError(QBResponseException e) {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        //Add toolbar
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.user_update_toolbar);
        toolbar.setTitle("Herd Chat");
        setSupportActionBar(toolbar);

        initializeViews();

        //Loads user profile from web services
        loadUserProfile();

        //Set the event for the variable so when the user clicks the image
        user_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), Common.SELECT_PICTURE);
            }
        });

        //If user clicks cancel button, then the user is sent back to the home activity
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserProfile.this, Home.class);
                startActivity(intent);
                finish();
            }
        });

        //If the user clicks the update button then the data is retrieved, validated, and the QuickBlox user information gets updated with the data inputted
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = editPassword.getText().toString();
                String oldPassword = editOldPassword.getText().toString();
                String email = editEmail.getText().toString();
                String fullName = editFullName.getText().toString();

                QBUser user = new QBUser();
                user.setId(QBChatService.getInstance().getUser().getId());

                if(!Common.isNullOrEmptyString(oldPassword)){
                    user.setOldPassword(oldPassword);
                }
                if(!Common.isNullOrEmptyString(password)){
                    user.setPassword(password);
                }
                if(!Common.isNullOrEmptyString(fullName)){
                    user.setFullName(fullName);
                }
                if(!Common.isNullOrEmptyString(email)){
                    user.setEmail(email);
                }

                final ProgressDialog messageDialog = new ProgressDialog(UserProfile.this);

                messageDialog.setMessage("Please wait...");
                messageDialog.show();
                QBUsers.updateUser(user).performAsync(new QBEntityCallback<QBUser>() {
                    @Override
                    public void onSuccess(QBUser qbUser, Bundle bundle) {
                        Toast.makeText(UserProfile.this, "User: " + qbUser.getLogin() + " updated", Toast.LENGTH_SHORT).show();
                        messageDialog.dismiss();


                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Toast.makeText(UserProfile.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });
    }

    //Loads the user profile using QuickBlox methods and classes
    private void loadUserProfile() {

        //Load the avatar
        QBUsers.getUser(QBChatService.getInstance().getUser().getId()).performAsync(new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser qbUser, Bundle bundle) {
                //Save to the cache
                QBUsersHolder.getInstance().putUser(qbUser);

                if(qbUser.getFileId() != null){
                    int profilePictureId = qbUser.getFileId();

                    QBContent.getFile(profilePictureId).performAsync(new QBEntityCallback<QBFile>() {
                        @Override
                        public void onSuccess(QBFile qbFile, Bundle bundle) {
                            String fileUrl = qbFile.getPublicUrl();
                            Picasso.with(getBaseContext()).load(fileUrl).into(user_avatar);
                        }

                        @Override
                        public void onError(QBResponseException e) {

                        }
                    });
                }
            }

            @Override
            public void onError(QBResponseException e) {

            }
        });

        QBUser currentUser = QBChatService.getInstance().getUser();

        String fullName = currentUser.getFullName();
        String email = currentUser.getEmail();

        editEmail.setText(email);
        editFullName.setText(fullName);
    }

    //Function that initializes the views
    private void initializeViews() {
        buttonCancel = (Button)findViewById(R.id.update_user_button_cancel);
        buttonUpdate = (Button)findViewById(R.id.update_user_button_update);

        editEmail = (EditText)findViewById(R.id.update_edit_email);
        editFullName = (EditText)findViewById(R.id.update_edit_full_name);
        editPassword = (EditText)findViewById(R.id.update_edit_password);
        editOldPassword = (EditText)findViewById(R.id.update_edit_old_password);

        user_avatar = (ImageView)findViewById(R.id.user_avatar);
    }


    //This function runs when the user wants to change their image in the user profile section
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode == Common.SELECT_PICTURE){
                Uri selectedImageUri  = data.getData();

                final ProgressDialog messageDialog = new ProgressDialog(UserProfile.this);
                messageDialog.setMessage("Please wait...");
                messageDialog.setCanceledOnTouchOutside(false);
                messageDialog.show();

                //Update user avatar
                try{
                    InputStream in = getContentResolver().openInputStream(selectedImageUri);
                    final Bitmap bitmap = BitmapFactory.decodeStream(in);
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                    File file = new File(Environment.getExternalStorageDirectory() + "/myimage.png");
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(bos.toByteArray());
                    fos.flush();
                    fos.close();

                    //Get the file size
                    final int imageSizeKb = (int)file.length() / 1024;

                    if(imageSizeKb >= (1024 * 100)){
                        Toast.makeText(this, "Error: Image size it too large", Toast.LENGTH_SHORT).show();
                    }

                    //Upload the file to the QB server
                    QBContent.uploadFileTask(file, true, null).performAsync(new QBEntityCallback<QBFile>() {
                        @Override
                        public void onSuccess(QBFile qbFile, Bundle bundle) {
                            //Set the avatar for the user
                            QBUser user = new QBUser();
                            user.setId(QBChatService.getInstance().getUser().getId());
                            user.setFileId(Integer.parseInt(qbFile.getId().toString()));

                            //Update the user
                            QBUsers.updateUser(user).performAsync(new QBEntityCallback<QBUser>() {
                                @Override
                                public void onSuccess(QBUser qbUser, Bundle bundle) {
                                    messageDialog.dismiss();
                                    user_avatar.setImageBitmap(bitmap);
                                }

                                @Override
                                public void onError(QBResponseException e) {

                                }
                            });
                        }

                        @Override
                        public void onError(QBResponseException e) {

                        }
                    });
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
