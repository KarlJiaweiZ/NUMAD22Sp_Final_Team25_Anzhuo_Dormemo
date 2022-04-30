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

    private boolean registerStatus = false;
    private boolean hasDorm;
    private boolean hasName;

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

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //createNewAccount();
                checkInput();
                if (registerStatus) sendRegisterToLoginActivity();
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

    private void createNewAccount() {
        String email = userEmail.getText().toString();
        String password = userPassword.getText().toString();
        String username = userName.getText().toString();
        String dormname = dormName.getText().toString();

        progressBar.setVisibility(View.VISIBLE);
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            registerStatus = true;
                            String currentUserID = firebaseAuth.getCurrentUser().getUid();
                            //User Part
                            databaseReference.child("Users").child(currentUserID).child("Email").setValue(email);
                            databaseReference.child("Users").child(currentUserID).child("Password").setValue(password);
                            databaseReference.child("Users").child(currentUserID).child("Username").setValue(username);
                            databaseReference.child("Users").child(currentUserID).child("DormName").setValue(dormname);
                            databaseReference.child("Users").child(currentUserID).child("UserPic").setValue("https://firebasestorage.googleapis.com/v0/b/numad22sp-final-dormemo.appspot.com/o/images%2Fdefault_avatar.png?alt=media&token=a92b6a69-cc1d-46dc-8c3e-b98b1fa4682a");
                            //Dorm Part
                            if (dormLeaderCheck.isChecked()) {
                                //to be the leader
                                databaseReference.child("Dorms").child(dormname).child("Members").child("Leader").child(currentUserID).setValue(username);
                            } else {
                                //to be a member
                                databaseReference.child("Dorms").child(dormname).child("Members").child("OtherMembers").child(currentUserID).setValue(username);
                            }
                            sendRegisterToMainActivity();
                            Toast.makeText(RegisterActivity.this, "Account created successful", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        } else {
                            String message = task.getException().toString();
                            Toast.makeText(RegisterActivity.this, message.substring(message.indexOf("[")), Toast.LENGTH_SHORT).show();
                            Log.e("Rigister failed", message);
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void checkInput() {
        String dormname = dormName.getText().toString();
        String username = userName.getText().toString();
        String password = userPassword.getText().toString();
        String email = userEmail.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Please enter username", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(dormname)) {
            Toast.makeText(this, "Please enter dorm name", Toast.LENGTH_SHORT).show();
        } else if (dormname.length() > 15) {
            Toast.makeText(RegisterActivity.this, "dorm name too long!", Toast.LENGTH_SHORT).show();
        } else if (username.length() > 15) {
            Toast.makeText(RegisterActivity.this, "user name too long!", Toast.LENGTH_SHORT).show();
        } else if (password.length() < 6) {
            Toast.makeText(RegisterActivity.this, "pass word too short!", Toast.LENGTH_SHORT).show();
        } else
            checkDormnameDup();
    }

    //check if the dorm name has already taken
    private void checkDormnameDup() {
        DatabaseReference datacheck = databaseReference.child("Dorms");
        datacheck.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    createNewAccount();
                } else {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String dormname = dormName.getText().toString();
                        String temp_dormname = dataSnapshot.getKey().toString();
                        if (temp_dormname.equals(dormname)) {
                            hasDorm = true;
                        }
                        //Log.e("Dorm:", temp_dormname);
                    }
                    if (dormLeaderCheck.isChecked() && hasDorm) {
                        //notice user that this dorm name is taken
                        Toast.makeText(RegisterActivity.this, "This dorm name has been taken, please rename;\nIf you would like to be a member of this dorm, please uncheck.", Toast.LENGTH_SHORT).show();
                        hasDorm = false;
                    } else if (dormLeaderCheck.isChecked() && !hasDorm) {
                        //create account as dorm leader
                        checkUsernameDup();
                    } else if (!dormLeaderCheck.isChecked() && hasDorm) {
                        //create account as dorm member
                        checkUsernameDup();
                    } else {
                        //notice user this dorm does not exist
                        Toast.makeText(RegisterActivity.this, "Dorm does not exist, please enter a valid name or be the leader.", Toast.LENGTH_SHORT).show();
                        hasDorm = false;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //check whether user name has already taken
    private void checkUsernameDup() {
        String username = userName.getText().toString();
        DatabaseReference existedUsers = databaseReference.child("Users");
        existedUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    createNewAccount();
                } else {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String tempName = dataSnapshot.child("Username").getValue().toString();
                        if (username.equals(tempName)) {
                            hasName = true;
                        }
                        Log.d("username", tempName);
                        Log.d("username", String.valueOf(hasName));
                    }
                    if (hasName) {
                        Toast.makeText(RegisterActivity.this, "User name has been taken, enter another one.", Toast.LENGTH_SHORT).show();
                        hasName = false;
                    } else {
                        createNewAccount();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendRegisterToLoginActivity() {
        Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(loginIntent);
    }

    private void sendRegisterToMainActivity() {
        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}