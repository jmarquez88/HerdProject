package com.example.a7510.herdproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.a7510.herdproject.Common.Common;
import com.example.a7510.herdproject.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.rengwuxian.materialedittext.MaterialEditText;

public class SignIn extends AppCompatActivity {
    EditText editEmail, editPassword;
    Button buttonSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        editPassword = (MaterialEditText)findViewById(R.id.editPassword);
        editEmail = (MaterialEditText)findViewById(R.id.editEmail);
        buttonSignIn = (Button)findViewById(R.id.buttonSignIn);

        //this initializes firebase
        final FirebaseDatabase db = FirebaseDatabase.getInstance();
        final DatabaseReference tb_user = db.getReference("User");

        buttonSignIn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){

                final String userEmail = editEmail.getText().toString();
                final String password = editPassword.getText().toString();

                QBUser qbUser = new QBUser(userEmail, password);

                //Checks with QuickBlox if the user is valid and displays appropiate information as a Toast
                QBUsers.signIn(qbUser).performAsync(new QBEntityCallback<QBUser>() {
                    @Override
                    public void onSuccess(QBUser qbUser, Bundle bundle) {
                        Toast.makeText(getBaseContext(), "Login successful", Toast.LENGTH_SHORT).show();

                        //Intent intent = new Intent(SignIn.this, ChatDialogs.class);
                        //intent.putExtra("user", user);
                        //intent.putExtra("password", password);
                        //startActivity(intent);

                        //Close login activity after logged
                       //finish();
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Toast.makeText(getBaseContext(), e.getMessage() + "", Toast.LENGTH_SHORT).show();

                    }
                });

                //A message dialog is displayed asking the user to please wait until FireBase authentication is done
                final ProgressDialog messageDialog = new ProgressDialog(SignIn.this);
                messageDialog.setMessage("Please wait....");
                messageDialog.show();

                tb_user.addValueEventListener(new ValueEventListener(){

                    public void onDataChange(DataSnapshot dataSnapshot){

                        //This will check if the user exists in the database
                        if(dataSnapshot.child(editEmail.getText().toString()).exists()) {


                            //get the user information
                            messageDialog.dismiss();
                            User user = dataSnapshot.child(editEmail.getText().toString()).getValue(User.class);
                            if (user.getPassword().equals(editPassword.getText().toString())) {
                                Intent homeIntent = new Intent(SignIn.this, Home.class);
                                Common.currentUser = user;
                                homeIntent.putExtra("user", userEmail);
                                homeIntent.putExtra("password", password);
                                startActivity(homeIntent);
                                finish();
                            } else {
                                Toast.makeText(SignIn.this, "Incorrect Password!", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            messageDialog.dismiss();
                            Toast.makeText(SignIn.this, "User does not exist!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    public void onCancelled(DatabaseError databaseError){

                    }

                });
            }
        });
    }
}
