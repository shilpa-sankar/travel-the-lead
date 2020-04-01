package com.example.travel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class User extends AppCompatActivity {

    private Button b1;
    private Button logout;
    private Button b2;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        firebaseAuth=FirebaseAuth.getInstance();
        b1=(Button)findViewById(R.id.b1) ;
        b2=(Button)findViewById(R.id.b2) ;
        logout=(Button)findViewById(R.id.btnlog);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                startActivity(new Intent(User.this, Login.class));
                finish();
            }
        });

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(User.this, Create.class ));
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(User.this, Join.class));
            }
        });

    }

}
