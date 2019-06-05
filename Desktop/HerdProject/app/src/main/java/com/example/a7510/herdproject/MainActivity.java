package com.example.a7510.herdproject;

        import android.*;
        import android.content.Intent;
        import android.content.pm.PackageManager;
        import android.graphics.Typeface;
        import android.support.annotation.NonNull;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.quickblox.auth.session.QBSettings;

public class MainActivity extends AppCompatActivity {

    Button buttonSignIn, buttonSignUp;
    TextView textSlogan;

    static final String APP_ID = "69155";
    static final String AUTH_KEY = "6EcCcLTTDgtFqvy";
    static final String AUTH_SECRET = "6Vvd4Nn-NMV363S";
    static final String ACCOUNT_KEY = "sBD7SPyxfhGm7y9VW1LQ";

    static final int REQUEST_CODE = 1000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestRuntimePermission();

        initializeFramework();

        buttonSignIn = (Button) findViewById(R.id.buttonSignIn);
        buttonSignUp = (Button) findViewById(R.id.buttonSignUp);

        textSlogan = (TextView) findViewById(R.id.textSlogan);
        Typeface fontFace = Typeface.createFromAsset(getAssets(), "fonts/NABILA.TTF");
        textSlogan.setTypeface(fontFace);

        //If the button sign up is clicked then the user is sent to the signUp activity
        buttonSignUp.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent signUp = new Intent(MainActivity.this, SignUp.class);
                startActivity(signUp );
            }
        });

        //If the button sign in is clicked then the user is sent to the signIn activity
        buttonSignIn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent signIn = new Intent(MainActivity.this, SignIn.class);
                startActivity(signIn);
            }
        });
    }

    //for chat function that retrieves permissions from QuickBlox
    private void requestRuntimePermission() {
        if(checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, REQUEST_CODE);
        }
    }

    //for  chat function that displays whether the user is granted access or not to QuickBlox
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case REQUEST_CODE:
            {
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(getBaseContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getBaseContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    //Initiates the QuickBlox Framework
    private void initializeFramework() {
        QBSettings.getInstance().init(getApplicationContext(), APP_ID, AUTH_KEY, AUTH_SECRET);
        QBSettings.getInstance().setAccountKey(ACCOUNT_KEY);
    }
}
