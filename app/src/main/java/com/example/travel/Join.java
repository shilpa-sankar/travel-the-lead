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

    private EditText groupid;
    private EditText password;
    private Button btjoin;
    private FirebaseUser currentUser;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        groupid = (EditText)findViewById(R.id.login_group_id);
        password = (EditText)findViewById(R.id.login_group_password);
        btjoin = (Button)findViewById(R.id.btnjoin);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        btjoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String docid = groupid.getText().toString().trim();
                final String pass = password.getText().toString().trim();

                final DocumentReference docRef = firestore.collection("groups").document(docid);
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            if(doc.exists()) {
                                if(pass.equals(doc.get("password").toString())) {

                                    WriteBatch batch = firestore.batch();
                                    String uid = currentUser.getUid();
                                    DocumentReference userDoc = firestore.collection("users").document(uid);
                                    batch.update(userDoc, "group", docid);

                                    DocumentReference groupDoc = firestore.collection("groups").document(docid);
                                    Map<String, Object> usersMap = new HashMap<>();
                                    Map<String, Object> newUser = new HashMap<>();
                                    newUser.put(uid, "null");
                                    usersMap.put("users", newUser);
                                    batch.set(groupDoc, usersMap, SetOptions.merge());
                                    batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()) {
                                                Intent intent = new Intent(Join.this, MapsActivity.class);
                                                intent.putExtra("groupid", docid);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }
                                    });

                                } else {
                                    Toast.makeText(Join.this, "typed: "+pass, Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                Toast.makeText(Join.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

            }
        });
    }
}