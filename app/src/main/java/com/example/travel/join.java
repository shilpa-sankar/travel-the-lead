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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class join extends AppCompatActivity {

    private TextView joinac;
    private EditText name;
    private EditText pass;
    private Button joingroup;
    private FirebaseFirestore firebaseFirestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        joinac=(TextView)findViewById(R.id.textv1);
        name=(EditText)findViewById(R.id.edit1);
        pass=(EditText)findViewById(R.id.edit2);
        joingroup=(Button)findViewById(R.id.button1);

        firebaseFirestore=FirebaseFirestore.getInstance();

        joingroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String textv1=joinac.getText().toString().trim();
                final String edit1=name.getText().toString().trim();
                final String edit2=pass.getText().toString().trim();

                Map<String,String> Userdata=new HashMap<>( );
                Userdata.put("name",edit1);
                Userdata.put("password",edit2);


                firebaseFirestore.collection("Userdata").add(Userdata).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(join.this,"joined group",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(join.this,MapsActivity.class));

                          }
                    }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String error=e.getMessage();

                        Toast.makeText(join.this,"failed to join"+error,Toast.LENGTH_SHORT).show();
                    }
                });
           }
        });
    }
}