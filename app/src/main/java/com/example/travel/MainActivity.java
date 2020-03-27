package com.example.travel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private EditText nameET, passwordET, emailET;
    private Button btn;
    private TextView loginlink;
    private String name, password, email;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupUIViews();

        firebaseAuth = FirebaseAuth.getInstance();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()){
                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                String uid = firebaseAuth.getCurrentUser().getUid();
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                Map<String, String> userData = new HashMap<>();
                                userData.put("name", name);
                                db.collection("users").document(uid).set(userData);
                                startActivity(new Intent(MainActivity.this, user.class));
                            } else {
                                Toast.makeText(MainActivity.this, "registration failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("shamilsdq", e.toString());
                        }
                    });
                }
            }
        });

        loginlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,login.class));
            }
        });

    }

    private void setupUIViews() {

        nameET = (EditText)findViewById(R.id.signup_name);
        emailET = (EditText)findViewById(R.id.signup_email);
        passwordET = (EditText)findViewById(R.id.signup_password);
        btn = (Button)findViewById(R.id.singup_btn);
        loginlink = (TextView)findViewById(R.id.login_lnk);

    }

    private boolean validate() {

        boolean result = false;
        name = nameET.getText().toString().trim();
        email = emailET.getText().toString().trim();
        password = passwordET.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this,"enter all the details",Toast.LENGTH_SHORT).show();
        } else {
           result=true;
        }

        return result;

    }

}