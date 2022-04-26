package com.example.numad22sp_final_team25_anzhuo_dormemo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LaunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        new Handler().postDelayed(new Runnable() {
            @Overridefeng
            public void run() {
                startActivity(new Intent(LaunchActivity.this, MainActivity.class));
                finish();
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                if(currentUser == null || Objects.isNull(currentUser)){
                    SendUserToLoginActivity();
                }
            }
        }, 1000);
    }

    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(LaunchActivity.this, LoginActivity.class);
        startActivity(loginIntent);
    }
}
