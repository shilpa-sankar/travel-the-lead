package com.example.travel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class create extends AppCompatActivity {
    private EditText username;
    private EditText password;
    private Button bcreate;
    private EditText group;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        username=(EditText)findViewById(R.id.etcr1);
        password=(EditText)findViewById(R.id.etcr2);
        group=(EditText)findViewById(R.id.etcr3);
        bcreate=(Button)findViewById(R.id.btncreate);

        firebaseFirestore=FirebaseFirestore.getInstance();

        bcreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String etcr1=username.getText().toString().trim();
                final String etcr2=password.getText().toString().trim();
                final String etcr3=group.getText().toString().trim();
                Map<String,String>Userdata=new HashMap<>( );
                Userdata.put("username",etcr1);
                Userdata.put("password",etcr2);
                Userdata.put("groupname",etcr3);

                firebaseFirestore.collection("Userdata").add(Userdata).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                        Toast.makeText(create.this,"group created",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(create.this,MapsActivity.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String error=e.getMessage();

                        Toast.makeText(create.this,"failed to create"+error,Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
