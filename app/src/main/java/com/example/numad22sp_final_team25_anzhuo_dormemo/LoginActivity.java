package com.example.numad22sp_final_team25_anzhuo_dormemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseUser currentUser;
    private Button loginButton;
    private EditText userEmail, userPassword;
    private TextView createAccountLink;

    private FirebaseAuth firebaseAuth;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        initializeFields();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allowLogin();
            }
        });

        createAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUserToRegisterActivity();
            }
        });
    }



    private void initializeFields() {
        loginButton = (Button) findViewById(R.id.login_button);
        userEmail = (EditText) findViewById(R.id.login_email);
        userPassword = (EditText) findViewById(R.id.login_password);
        createAccountLink = (TextView) findViewById(R.id.login_register_link);
        progressBar = (ProgressBar) findViewById(R.id.login_progressbar);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(currentUser != null){
            sendUserToMainActivity();
        }
    }

    private void sendUserToMainActivity() {
        Intent loginIntent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(loginIntent);
    }

    private void sendUserToRegisterActivity(){
        Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(registerIntent);
    }

    private void allowLogin() {
        String email = userEmail.getText().toString();
        String password = userPassword.getText().toString();
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please enter email",Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please enter password",Toast.LENGTH_SHORT).show();
        }
        else{
            progressBar.setVisibility(View.VISIBLE);
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                sendUserToMainActivity();
                                Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                            else{
                                String message = task.getException().toString();
                                Toast.makeText(LoginActivity.this, "Error" + message, Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
        }
    }
}