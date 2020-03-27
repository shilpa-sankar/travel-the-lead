package com.example.travel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class create extends AppCompatActivity {
    private EditText groupid;
    private EditText password;
    private Button bcreate;
    private EditText groupname;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        groupid = (EditText)findViewById(R.id.groupid);
        password = (EditText)findViewById(R.id.password);
        groupname = (EditText)findViewById(R.id.groupname);
        bcreate = (Button)findViewById(R.id.btncreate);

        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        bcreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String docid = groupid.getText().toString().trim();

                Map<String, String> usersInGroup = new HashMap<>();
                final String uid = firebaseAuth.getCurrentUser().getUid();
                usersInGroup.put(uid, "null");
                final Map<String, Object> GroupData = new HashMap<>();
                GroupData.put("password", password.getText().toString().trim());
                GroupData.put("groupname", groupname.getText().toString().trim());
                GroupData.put("users", usersInGroup);


                DocumentReference docRef = firestore.collection("groups").document(docid);
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            if(doc.exists()) {
                                Toast.makeText(create.this, "groupID already in use!", Toast.LENGTH_SHORT).show();
                            } else {
                                firestore.collection("groups").document(docid).set(GroupData)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                firestore.collection("users").document(uid).update("group", docid).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()) {
                                                            Intent intent = new Intent(create.this, MapsActivity.class);
                                                            startActivity(intent);
                                                        }
                                                    }
                                                });
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(create.this, "failed to create group!", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }
                    }
                });
            }
        });
    }
}
