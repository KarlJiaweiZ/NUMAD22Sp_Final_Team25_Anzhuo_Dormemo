package com.example.numad22sp_final_team25_anzhuo_dormemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MeActivity extends AppCompatActivity {

    private ImageView userProfilePhoto;
    private EditText userUsername;
    private Button logout;

    private FirebaseAuth firebaseAuth;
    private String currentUserID;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUserID = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        initializeFields();

        retrieveUserInfo();

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                sendUserToLoginActivity();
            }
        });
    }

    private void initializeFields() {
        userProfilePhoto = (ImageView) findViewById(R.id.me_profile_photo);
        userUsername = (EditText) findViewById(R.id.me_user_name);
        logout = (Button) findViewById(R.id.me_logout_button);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void retrieveUserInfo(){
        databaseReference.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists() && snapshot.hasChild("Username")){
                    String retrieveUsername = snapshot.child("Username").getValue().toString();
                    userUsername.setText(retrieveUsername);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendUserToLoginActivity() {
        Intent loginIntent = new Intent(MeActivity.this, LoginActivity.class);
        startActivity(loginIntent);
    }
}