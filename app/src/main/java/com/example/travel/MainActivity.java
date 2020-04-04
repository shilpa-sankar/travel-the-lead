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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText name_ET, password_ET, email_ET;
    private Button signup_BTN;
    private TextView login_LNK;
    private String name, password, email;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setLayoutData();

        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser != null) {
            startActivity(new Intent(MainActivity.this, User.class));
            finish();
        }

        signup_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validate()){
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    String uid = firebaseAuth.getCurrentUser().getUid();
                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    Map<String, String> userData = new HashMap<>();
                                    userData.put("name", name);
                                    db.collection("users").document(uid).set(userData);
                                    startActivity(new Intent(MainActivity.this, User.class));
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(MainActivity.this, "registration failed", Toast.LENGTH_SHORT).show();
                                }
                            });
                }

            }
        });

        login_LNK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Login.class));
                finish();
            }
        });

    }

    private void setLayoutData() {
        name_ET = (EditText)findViewById(R.id.signup_name);
        email_ET = (EditText)findViewById(R.id.signup_email);
        password_ET = (EditText)findViewById(R.id.signup_password);
        signup_BTN = (Button)findViewById(R.id.singup_btn);
        login_LNK = (TextView)findViewById(R.id.login_lnk);
    }

    private boolean validate() {

        boolean result = false;
        name = name_ET.getText().toString().trim();
        email = email_ET.getText().toString().trim();
        password = password_ET.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this,"enter all the details",Toast.LENGTH_SHORT).show();
        } else {
           result=true;
        }

        return result;

    }

}