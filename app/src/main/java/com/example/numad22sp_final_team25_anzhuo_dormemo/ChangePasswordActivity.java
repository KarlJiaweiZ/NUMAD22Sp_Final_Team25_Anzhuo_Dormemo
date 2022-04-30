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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {
    private String currentEmail;
    private EditText etOldPass, etNewPass;
    private Button button_yes, button_cancel;
    private FirebaseUser currentUser;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        if(currentUser == null){
            SendUserToLoginActivity();
        }

        currentEmail = getIntent().getStringExtra("EXTRA_current_Email");
        etOldPass = (EditText) findViewById(R.id.login_old_password);
        etNewPass = (EditText) findViewById(R.id.login_new_password);

        button_yes = (Button) findViewById(R.id.change_pass_yes_button);
        button_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUpdatePassword(etOldPass.getText().toString(), etNewPass.getText().toString());
            }
        }
        );
        button_cancel = (Button) findViewById(R.id.change_pass_cancel_button);
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //back to main activity
                sendUserToMainActivity();
            }
        });
    }

    private void mUpdatePassword(String oldPass, String newPass) {
        //String email = currentUser.getEmail();
        AuthCredential credential = EmailAuthProvider.getCredential(currentEmail, oldPass);
        // Prompt the user to re-provide their sign-in credentials
        currentUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    currentUser.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("MeFragment", "Password updated");
                                sendUserToMainActivity();
                            } else {
                                Log.d("MeFragment", "Error password not updated");
                            }
                        }
                    });
                } else {
                    Log.d("MeFragment", "Error auth failed");
                }
            }
        });
    }
    private void sendUserToMainActivity() {
        Intent mainIntent = new Intent(ChangePasswordActivity.this, MainActivity.class);// problem?
        startActivity(mainIntent);
    }

    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
        startActivity(loginIntent);
    }
}