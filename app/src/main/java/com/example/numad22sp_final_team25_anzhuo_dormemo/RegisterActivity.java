package com.example.numad22sp_final_team25_anzhuo_dormemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

public class RegisterActivity extends AppCompatActivity {

    private Button registerButton;
    private EditText userEmail, userPassword, userName, dormName;
    private CheckBox dormLeaderCheck;

    private FirebaseAuth firebaseAuth;

    private DatabaseReference databaseReference;

    private ProgressBar progressBar;

    private boolean registerStatus;

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
        databaseReference = FirebaseDatabase.getInstance().getReference();

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
                //createNewAccount();
                checkInput();
                if(registerStatus)
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
                                registerStatus = true;
                                String currentUserID = firebaseAuth.getCurrentUser().getUid();
                                databaseReference.child("Users").child(currentUserID).child("Email").setValue(email);
                                databaseReference.child("Users").child(currentUserID).child("Password").setValue(password);
                                databaseReference.child("Users").child(currentUserID).child("Username").setValue(username);
                                databaseReference.child("Users").child(currentUserID).child("DormName").setValue(dormname);
                                if(dormLeaderCheck.isChecked()){
                                    databaseReference.child("Dorms").child(dormname).child("Members").child("Leader").child(currentUserID).setValue(username);
                                }
                                else{
                                    databaseReference.child("Dorms").child(dormname).child("Members").child("OtherMembers").child(currentUserID).setValue(username);
                                }
                                sendRegisterToMainActivity();
                                Toast.makeText(RegisterActivity.this,"Account created successful", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                            else {
                                String message = task.getException().toString();
                                Toast.makeText(RegisterActivity.this, message.substring(message.indexOf("[")), Toast.LENGTH_LONG).show();

                                Log.e("Register failed", message);

                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
        }
    }

    private void checkInput(){
        DatabaseReference datacheck = databaseReference.child("Dorms");
        datacheck.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    createNewAccount();
                }
                else{
                    Iterator<DataSnapshot> dataSnapshots = snapshot.getChildren().iterator();
                    while (dataSnapshots.hasNext()){
                        String dormname = dormName.getText().toString();
                        DataSnapshot snapshotchild = dataSnapshots.next();
                        String temp_dormname = snapshotchild.toString();
                        if(temp_dormname == dormname){
                            Toast.makeText(RegisterActivity.this,"Duplicate dormname",Toast.LENGTH_SHORT).show();
                        }else {
                            createNewAccount();
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendRegisterToLoginActivity(){
        Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(loginIntent);
    }

    private void sendRegisterToMainActivity(){
        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}