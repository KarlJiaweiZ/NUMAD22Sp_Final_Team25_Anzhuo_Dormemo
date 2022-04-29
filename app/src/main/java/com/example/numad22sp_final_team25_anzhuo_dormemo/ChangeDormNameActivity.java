package com.example.numad22sp_final_team25_anzhuo_dormemo;

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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ChangeDormNameActivity extends AppCompatActivity {
    private EditText etNewDormName;
    private Button button_yes, button_cancel;
    private FirebaseUser currentUser;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference usersRef, dormsRef;
    private String oldDormName;
    private Boolean hasDorm = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_dorm_name);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        if(currentUser == null){
            SendUserToLoginActivity();
        }
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        dormsRef = FirebaseDatabase.getInstance().getReference().child("Dorms");

        oldDormName = getIntent().getStringExtra("EXTRA_oldDormName");

        etNewDormName = (EditText) findViewById(R.id.new_dorm_name);
        button_yes = (Button) findViewById(R.id.change_dorm_name_yes_button);
        button_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUpdateDormName(etNewDormName.getText().toString());
            }
        });
        button_cancel = (Button) findViewById(R.id.change_dorm_name_cancel_button);
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUserToMainActivity();
            }
        });
    }

    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(ChangeDormNameActivity.this, LoginActivity.class);
        startActivity(loginIntent);
    }

    private void sendUserToMainActivity() {
        Intent mainIntent = new Intent(ChangeDormNameActivity.this, MainActivity.class);
        startActivity(mainIntent);
    }

    private void mUpdateDormName(String newDormName) {
        isDormNameDup(newDormName);
        if (hasDorm) {
            Toast.makeText(ChangeDormNameActivity.this,"Dorm Name exists", Toast.LENGTH_SHORT);
        } else {
            HashMap<String, Object> userMap = new HashMap<>();
            userMap.put("DormName", newDormName);
            usersRef.child(currentUser.getUid()).updateChildren(userMap);
            copyRecord(dormsRef.child(oldDormName), dormsRef.child(newDormName));
            sendUserToMainActivity();
        }


    }

    private void copyRecord(DatabaseReference fromPath, final DatabaseReference toPath) {
        fromPath.addListenerForSingleValueEvent(new ValueEventListener()  {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                toPath.setValue(dataSnapshot.getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("ChangeDormNameActivity", "Copy Record Success!");
                        dormsRef.child(oldDormName).removeValue();
                    }
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("ChangeDormNameActivity", "Copy Record on cancelled");
            }
        });
    }

    //check if the dorm name has already taken
    private void isDormNameDup(String newDormName) {

        dormsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    hasDorm = false;
                } else {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String temp_dormname = dataSnapshot.getKey().toString();
                        if (temp_dormname.equals(newDormName)) {
                            hasDorm = true;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}