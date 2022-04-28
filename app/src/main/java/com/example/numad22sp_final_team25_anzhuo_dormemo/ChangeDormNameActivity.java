package com.example.numad22sp_final_team25_anzhuo_dormemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class ChangeDormNameActivity extends AppCompatActivity {
    private EditText etNewDormName;
    private Button button_yes, button_cancel;
    private FirebaseUser currentUser;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference usersRef, dormsRef;
    private String oldDormName;

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
                sendUserToMainActivity();
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
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("DormName", newDormName);
        usersRef.child(currentUser.getUid()).updateChildren(userMap);
        dormsRef.child(oldDormName).setValue(newDormName);

    }
}