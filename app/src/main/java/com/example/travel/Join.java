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
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import java.util.HashMap;
import java.util.Map;

public class Join extends AppCompatActivity {

    private EditText groupid_ET;
    private EditText password_ET;
    private Button join_BTN;

    private FirebaseUser firebaseUser;
    private FirebaseFirestore firestore;

    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        setLayoutData();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null){
            startActivity(new Intent(Join.this, Login.class));
            finish();
        }

        firestore = FirebaseFirestore.getInstance();

        firestore.collection("users").document(firebaseUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {
                    username = documentSnapshot.getString("name");
                }
            }
        });

        join_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String groupid = groupid_ET.getText().toString().trim();
                final String password = password_ET.getText().toString().trim();

                final DocumentReference docRef = firestore.collection("groups").document(groupid);
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            if(doc.exists()) {
                                if(password.equals(doc.get("password").toString())) {
                                    setJoinData();
                                } else {
                                    Toast.makeText(Join.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(Join.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    private void setJoinData() {

                        String uid = firebaseUser.getUid();
                        DocumentReference userDoc = firestore.collection("users").document(uid);
                        DocumentReference groupDoc = firestore.collection("groups").document(groupid);

                        Map<String, String> userData = new HashMap<>();
                        userData.put("name", username);
                        userData.put("location", "null");

                        Map<String, Object> newUser = new HashMap<>();
                        newUser.put(uid, userData);

                        Map<String, Object> usersMap = new HashMap<>();
                        usersMap.put("users", newUser);

                        WriteBatch batch = firestore.batch();
                        batch.update(userDoc, "group", groupid);
                        batch.set(groupDoc, usersMap, SetOptions.merge());
                        batch.commit()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Intent intent = new Intent(Join.this, MapsActivity.class);
                                        intent.putExtra("groupid", groupid);
                                        startActivity(intent);
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(Join.this, "failed to join group!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }

                });

            }
        });

    }

    private void setLayoutData() {
        groupid_ET = (EditText)findViewById(R.id.login_group_id);
        password_ET = (EditText)findViewById(R.id.login_group_password);
        join_BTN = (Button)findViewById(R.id.btnjoin);
    }
}