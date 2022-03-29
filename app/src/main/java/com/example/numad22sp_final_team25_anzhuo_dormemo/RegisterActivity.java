package com.example.numad22sp_final_team25_anzhuo_dormemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private Button registerButton;
    private EditText userEmail, userPassword, userName, dormName;
    private CheckBox dormLeaderCheck;

    private FirebaseAuth firebaseAuth;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();
        
        initializeFields();

        dormLeaderCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    dormName.setBackgroundColor(Color.GRAY);
                    dormName.setEnabled(false);
                }
                else {
                    dormName.setBackground(getResources().getDrawable(R.drawable.inputs));
                    dormName.setEnabled(true);
                }
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewAccount();
                sendRegisterToLoginActivity();
            }
        });
    }

    private void initializeFields() {
        registerButton = (Button) findViewById(R.id.register_button);
        userEmail = (EditText) findViewById(R.id.register_email);
        userPassword = (EditText) findViewById(R.id.register_password);
        userName = (EditText) findViewById(R.id.register_username);
        dormName = (EditText) findViewById(R.id.register_dormname);
        dormLeaderCheck = (CheckBox) findViewById(R.id.register_dormleader);

        progressBar = (ProgressBar) findViewById(R.id.register_progressbar);
    }

    private void sendRegisterToLoginActivity(){
        Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(loginIntent);
    }

    private void createNewAccount(){
        String email = userEmail.getText().toString();
        String password = userPassword.getText().toString();
        String username = userName.getText().toString();
        String dormname = dormName.getText().toString();
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please enter email",Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please enter password",Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(username)){
            Toast.makeText(this, "Please enter username",Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(dormname) && !dormLeaderCheck.isChecked()){
            Toast.makeText(this, "Please enter dorm name or be the leader",Toast.LENGTH_SHORT).show();
        }
        else {
            progressBar.setVisibility(View.VISIBLE);
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                sendRegisterToLoginActivity();
                                Toast.makeText(RegisterActivity.this,"Account created successful", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                            else {
                                String message = task.getException().toString();
                                Toast.makeText(RegisterActivity.this, "Error" + message, Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
        }
    }
}