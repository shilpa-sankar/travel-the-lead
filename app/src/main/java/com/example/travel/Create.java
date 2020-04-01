package com.example.travel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

public class Create extends AppCompatActivity {
    private EditText groupid;
    private EditText password;
    private Button bcreate;
    private EditText groupname;

    private FirebaseFirestore firestore;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        groupid = (EditText)findViewById(R.id.groupid);
        password = (EditText)findViewById(R.id.password);
        groupname = (EditText)findViewById(R.id.groupname);
        bcreate = (Button)findViewById(R.id.btncreate);

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        firestore.collection("users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {
                    username = documentSnapshot.getString("name");
                }
            }
        });

        bcreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String docid = groupid.getText().toString().trim();

                if(groupid.getText().length() == 0 || groupname.getText().length() == 0) {
                    Toast.makeText(getApplicationContext(), "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(password.getText().length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password should have atleast 6 characters", Toast.LENGTH_SHORT).show();
                    return;
                }

                DocumentReference docRef = firestore.collection("groups").document(docid);
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            if(doc.exists()) {
                                Toast.makeText(Create.this, "groupID already in use!", Toast.LENGTH_SHORT).show();
                            } else {

                                Map<String, String> userData = new HashMap<>();
                                userData.put("name", username);
                                userData.put("location", "null");
                                Map<String, Object> usersInGroup = new HashMap<>();
                                final String uid = user.getUid();
                                usersInGroup.put(uid, userData);
                                final Map<String, Object> GroupData = new HashMap<>();
                                GroupData.put("users", usersInGroup);

                                GroupData.put("password", password.getText().toString().trim());
                                GroupData.put("groupname", groupname.getText().toString().trim());
                                GroupData.put("strength", 1);

                                firestore.collection("groups").document(docid).set(GroupData)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                firestore.collection("users").document(uid).update("group", docid).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()) {
                                                            Intent intent = new Intent(Create.this, MapsActivity.class);
                                                            intent.putExtra("groupid", docid);
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    }
                                                });
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(Create.this, "failed to create group!", Toast.LENGTH_SHORT).show();
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
