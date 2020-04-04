package com.example.travel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    private EditText email_ET, password_ET;
    private TextView signup_LNK;
    private Button login_BTN;
    private String email, password;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setLayoutData();

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null){
            startActivity(new Intent(Login.this, User.class));
            finish();
        }

        login_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                email = email_ET.getText().toString().trim();
                password = password_ET.getText().toString().trim();

                if(email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(Login.this, "Fill all fields", Toast.LENGTH_SHORT).show();
                } else {
                    firebaseAuth.signInWithEmailAndPassword(email, password)
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    startActivity(new Intent(Login.this, User.class));
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Login.this, "login failed", Toast.LENGTH_SHORT).show();
                                }
                            });
                }

            }
        });

        signup_LNK.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               startActivity(new Intent(Login.this, MainActivity.class));
               finish();
            }
        });

    }

    private void setLayoutData() {
        email_ET = (EditText)findViewById(R.id.login_email);
        password_ET = (EditText)findViewById(R.id.login_password);
        login_BTN = (Button)findViewById(R.id.btn1);
        signup_LNK = (TextView)findViewById(R.id.signup_link);
    }

}

