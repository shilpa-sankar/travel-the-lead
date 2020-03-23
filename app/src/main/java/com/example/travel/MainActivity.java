package com.example.travel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

import java.util.regex.Matcher;

public class MainActivity extends AppCompatActivity {
    private EditText name, password, email;
    private String et1, et3, et4;
    private TextView signin;
    private Button bt1;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupUIViews();

        firebaseAuth=FirebaseAuth.getInstance();



        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validate()){

                    firebaseAuth.createUserWithEmailAndPassword(et3, et4).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "registration successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(MainActivity.this, user.class));
                            } else {

                                Toast.makeText(MainActivity.this, "registration failed", Toast.LENGTH_SHORT).show();
                            }


                        }
                    });

                }
            }
        });
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,login.class));
            }
        });

    }

    private void setupUIViews(){
        name=(EditText)findViewById(R.id.et1);
        email=(EditText)findViewById(R.id.et3);
        password=(EditText)findViewById(R.id.et4);
        bt1=(Button)findViewById(R.id.bt1);
        signin=(TextView)findViewById(R.id.tv2);

}
   private boolean validate(){
        boolean result=false;
       et1=name.getText().toString().trim();
       et4=password.getText().toString().trim();
       et3=email.getText().toString().trim();

       if (et4.isEmpty()|| et3.isEmpty()){
           Toast.makeText(this,"enter all the details",Toast.LENGTH_SHORT).show();

       }else {
           result=true;
       }
       return result;


   }
}