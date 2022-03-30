package com.example.numad22sp_final_team25_anzhuo_dormemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class MeActivity extends AppCompatActivity {

    private Button logout;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me);

        initializeFields();

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                sendUserToLoginActivity();
            }
        });
    }

    private void initializeFields() {
        logout = (Button) findViewById(R.id.me_logout_button);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void sendUserToLoginActivity() {
        Intent loginIntent = new Intent(MeActivity.this, LoginActivity.class);
        startActivity(loginIntent);
    }
}