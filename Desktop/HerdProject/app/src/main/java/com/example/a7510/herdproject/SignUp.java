package com.example.a7510.herdproject;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.a7510.herdproject.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.session.QBSession;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.rengwuxian.materialedittext.MaterialEditText;

public class SignUp extends AppCompatActivity {

    MaterialEditText editEmail, editName, editPassword;
    Button buttonSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        registerSecession();

        editName = (MaterialEditText)findViewById(R.id.editName);
        editPassword = (MaterialEditText)findViewById(R.id.editPassword);
        editEmail = (MaterialEditText)findViewById(R.id.editEmail);

        buttonSignUp = (Button)findViewById(R.id.buttonSignUp);

        //Initializes FireBase
        final FirebaseDatabase db = FirebaseDatabase.getInstance();
        final DatabaseReference tb_user = db.getReference("User");

        buttonSignUp.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){

                final ProgressDialog messageDialog = new ProgressDialog(SignUp.this);
                messageDialog.setMessage("Please wait....");
                messageDialog.show();

                //Authenticates user inputted information and displays wheter or not they have access
                QBUser qbUser = new QBUser(editEmail.getText().toString(), editPassword.getText().toString());

                qbUser.setFullName(editName.getText().toString());

                QBUsers.signUp(qbUser).performAsync(new QBEntityCallback<QBUser>() {
                    @Override
                    public void onSuccess(QBUser qbUser, Bundle bundle) {
                        Toast.makeText(getBaseContext(), "Signed Up Successfully", Toast.LENGTH_SHORT).show();
                        //finish();
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Toast.makeText(getBaseContext(), e.getMessage() + "", Toast.LENGTH_SHORT).show();

                    }
                });

                tb_user.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Check if the user has an email
                        if(dataSnapshot.child(editEmail.getText().toString()).exists()){
                            Toast.makeText(SignUp.this, "Email Address already registered!", Toast.LENGTH_SHORT).show();

                        }
                        else{
                            messageDialog.dismiss();
                            User user = new User(editName.getText().toString(), editPassword.getText().toString());
                            tb_user.child(editEmail.getText().toString()).setValue(user);
                            Toast.makeText(SignUp.this, "Signed up successfully!", Toast.LENGTH_SHORT).show();
                            finish();

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    //Creates QuickBlox session
    private void registerSecession() {
        QBAuth.createSession().performAsync(new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(QBSession qbSession, Bundle bundle) {

            }

            @Override
            public void onError(QBResponseException e) {
                Log.e("ERROR", e.getMessage());
            }
        });
    }
}
