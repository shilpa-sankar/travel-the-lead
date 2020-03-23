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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class login extends AppCompatActivity {

     private EditText emailid;
     private EditText password;
    private Button btn1;
    private TextView signup;
    private TextView userregister;
     private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

       emailid=(EditText)findViewById(R.id.ete1);
        password=( EditText)findViewById(R.id.ete2);
        btn1=(Button)findViewById(R.id.btn1);
        signup=(TextView)findViewById(R.id.tev2);
        userregister=(TextView)findViewById(R.id.tev1);

        firebaseAuth=FirebaseAuth.getInstance();

        FirebaseUser user=firebaseAuth.getCurrentUser();

        if (user !=null){
            finish();
            startActivity(new Intent(login.this,user.class));
        }


        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate(emailid.getText().toString(),password.getText().toString());
            }
        });

       signup.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               startActivity(new Intent(login.this,MainActivity.class));
           }
       });


    }
    private void validate (String ete1,String ete2){
        if(!ete1.isEmpty() && !ete2.isEmpty()) {
            firebaseAuth.signInWithEmailAndPassword(ete1, ete2).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(login.this, "login successful", Toast.LENGTH_SHORT).show();
                        checkEmailVerification();
                        startActivity(new Intent(login.this, user.class));
                    } else {
                        Toast.makeText(login.this, "login failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(this,"enter all the details",Toast.LENGTH_SHORT).show();
        }
    }
    private void checkEmailVerification() {
        FirebaseUser firebaseUser = firebaseAuth.getInstance().getCurrentUser();
        boolean emailflag = firebaseUser.isEmailVerified();

        startActivity(new Intent(login.this, user.class));


    }
}

