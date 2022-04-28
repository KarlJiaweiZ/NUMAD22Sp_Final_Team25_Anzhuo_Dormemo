package com.example.numad22sp_final_team25_anzhuo_dormemo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

    private static final int PICK_IMAGE_REQUEST = 1;

    private View meFragmentView;
    private String currentEmail, currentDormName;

    private ImageView userPicIV;
    private TextView userStatusTV;
    private TextView currentUserNameTV;
    private Button changePasswordButton;
    private Button changeDormNameButton;
    private Button logOutButton;

    private FirebaseUser currentUser;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference usersRef;

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

    private void SendUserToUploadActivity() {
        Intent uploadIntent = new Intent(getActivity(), UploadImageActivity.class);
        startActivity(uploadIntent);
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
                SendUserToUploadActivity();
            }
        });
        currentUserNameTV = (TextView) meFragmentView.findViewById(R.id.tvUserName);
        changePasswordButton = (Button) meFragmentView.findViewById(R.id.buttonChangePassword);
        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ChangePasswordActivity.class);
                intent.putExtra("EXTRA_current_Email", currentEmail);
                startActivity(intent);
            }
        });

        changeDormNameButton = (Button) meFragmentView.findViewById(R.id.buttonChangeDormName);
        changeDormNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ChangeDormNameActivity.class);
                intent.putExtra("EXTRA_oldDormName", currentDormName);
                startActivity(intent);
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
                    currentDormName = snapshot.child("DormName").getValue().toString();
                    //currentPass = snapshot.child("Password").getValue().toString();
                    String retrieveUsername = snapshot.child("Username").getValue().toString();
                    currentUserNameTV.setText(retrieveUsername);
                    String gsUrl = snapshot.child("UserPic").getValue().toString();
                    StorageReference gsReference = FirebaseStorage.getInstance().getReferenceFromUrl(gsUrl);
                    final Uri[] downloadUri = new Uri[1];
                    gsReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            downloadUri[0] = uri;
                            Picasso.get().load(downloadUri[0]).into(userPicIV);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("MeFragment", "download uri wrong");
                        }
                    });

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


}