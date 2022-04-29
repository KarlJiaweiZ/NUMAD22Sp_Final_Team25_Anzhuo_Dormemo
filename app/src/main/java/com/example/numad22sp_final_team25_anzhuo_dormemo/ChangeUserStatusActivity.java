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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChangeUserStatusActivity extends AppCompatActivity {
    private Button button_yes, button_cancel;
    private FirebaseUser currentUser;
    private FirebaseAuth firebaseAuth;
    private EditText etNewUserStatus;
    private DatabaseReference usersRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_user_status);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        if(currentUser == null){
            SendUserToLoginActivity();
        }
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid());

        etNewUserStatus = (EditText) findViewById(R.id.new_status);

        button_yes = (Button) findViewById(R.id.change_status_yes_button);
        button_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = etNewUserStatus.getText().toString();
                if (text.length() > 15 || text.length() < 1) {
                    Toast.makeText(ChangeUserStatusActivity.this, "please enter status between 1 and 15", Toast.LENGTH_SHORT).show();
                } else {
                    updateUserStatus(text);
                }

            }
        });

        button_cancel = (Button) findViewById(R.id.change_status_cancel_button);
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //back to main activity
                sendUserToMainActivity();
            }
        });
    }

    private void updateUserStatus(String newStatus) {
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersRef.child("Status").setValue(newStatus).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("ChangeUserStatusActivity", "change status");
                        sendUserToMainActivity();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("ChangeUserStatusActivity", "change status error");
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("ChangeUserStatusActivity", "change status: database error");
            }
        });

    }

    private void sendUserToMainActivity() {
        Intent mainIntent = new Intent(ChangeUserStatusActivity.this, MainActivity.class);// problem?
        startActivity(mainIntent);
    }

    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(ChangeUserStatusActivity.this, LoginActivity.class);
        startActivity(loginIntent);
    }
}