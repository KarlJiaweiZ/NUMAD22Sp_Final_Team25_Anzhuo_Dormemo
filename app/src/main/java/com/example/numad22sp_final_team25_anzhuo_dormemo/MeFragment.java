package com.example.numad22sp_final_team25_anzhuo_dormemo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class MeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int PICK_IMAGE_REQUEST = 1;

    private View meFragmentView;
    private String currentUserID, currentUserName, currentDormName, currentEmail, currentPass;

    private ImageView userPicIV;
    private TextView userStatusTV;
    private TextView currentUserNameTV;
    private Button changePasswordButton;
    private Button changeDormNameButton;
    private Button logOutButton;
    private EditText newPassET, oldPassET;

    private FirebaseUser currentUser;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference usersRef, dormRef, groupMessageKeyRef;
    private StorageReference storageReference;
    //private DatabaseReference getImage;


    public MeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
        startActivity(loginIntent);
    }

    private void sendUserToMainActivity() {
        Intent mainIntent = new Intent(getActivity(), MainActivity.class);
        startActivity(mainIntent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        meFragmentView =  inflater.inflate(R.layout.fragment_me, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        if(currentUser == null){
            SendUserToLoginActivity();
        }
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        dormRef = FirebaseDatabase.getInstance().getReference().child("Dorms");

        if(currentUser == null){
            SendUserToLoginActivity();
        }

        initializeFields();
        retrieveUserInfo();
        return meFragmentView;
    }

    private void initializeFields() {
        userPicIV = (ImageView) meFragmentView.findViewById(R.id.ivUserPic);
        userPicIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        currentUserNameTV = (TextView) meFragmentView.findViewById(R.id.tvUserName);
        changePasswordButton = (Button) meFragmentView.findViewById(R.id.buttonChangePassword);
        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog();
            }
        });
        logOutButton = (Button) meFragmentView.findViewById(R.id.buttonLogOut);
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                SendUserToLoginActivity();
            }
        });
    }

    private void retrieveUserInfo() {
        usersRef.child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists() && snapshot.hasChild("Username")){
                    currentEmail = snapshot.child("Email").getValue().toString();
                    //currentPass = snapshot.child("Password").getValue().toString();
                    String retrieveUsername = snapshot.child("Username").getValue().toString();
                    currentUserNameTV.setText(retrieveUsername);
                    String imageUri = snapshot.child("UserPic").getValue().toString();
                    Picasso.get().load(imageUri).into(userPicIV);

                } else {
                    Log.d("MeFragment", "retrieveUserInfo: userRef wrong!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("MeFragment", "retrieveUserInfo: userRef canceled wrong!");
            }
        });

    }

    private void mUpdatePassword(String oldPass, String newPass) {
        //String email = currentUser.getEmail();
        currentEmail = "karlzhangjw@outlook.com";
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
    protected void dialog() {

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_change_pass,
                (ViewGroup) meFragmentView.findViewById(R.id.dialog_change_pass));
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        oldPassET = (EditText) layout.findViewById(R.id.etOldPass);
        newPassET = (EditText) layout.findViewById(R.id.etNewPass);

        builder.setTitle("Change new password");
        builder.setView(layout);

        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String oldPass = oldPassET.toString();
                String newPass = newPassET.toString();
                oldPass = "123456";
                newPass = "1234";
                mUpdatePassword(oldPass, newPass);
                sendUserToMainActivity();
                dialog.dismiss();

            }
        });

        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
}