package com.example.numad22sp_final_team25_anzhuo_dormemo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.numad22sp_final_team25_anzhuo_dormemo.Board.MessageViewAdaptor;
import com.example.numad22sp_final_team25_anzhuo_dormemo.Board.Messages;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

public class BoardFragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseDatabase dbReference;
    private DatabaseReference messageReference;
    private String currentUserID;
    private String currentDormName;
    private String currentUsername;
    private String currentUserProfileImage;
    private MessageViewAdaptor messageViewAdaptor;
    private RecyclerView messageRecyclerView;
    private ArrayList<Messages> messageRecords;

    public BoardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_board, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        dbReference = FirebaseDatabase.getInstance();
        getDormName();

        BottomNavigationView bottomNavigationView = rootView.findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.bforum:
                        displayMessagesFrame(rootView);
                        break;
                    case R.id.badd:
                        displayAddFrame(rootView);
                        break;
                    case R.id.bsearch:
                        displaySearchFrame(rootView);
                        break;
                }
                return true;
            }
        });

        // default: display messages
        displayMessagesFrame(rootView);

        return rootView;
    }

    private void getDormName() {
        dbReference.getReference().child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    currentDormName = Objects.requireNonNull(snapshot.child("DormName").getValue()).toString();
                    currentUsername = Objects.requireNonNull(snapshot.child("Username").getValue()).toString();
                    try {
                        currentUserProfileImage = Objects.requireNonNull(snapshot.child("UserPic").getValue()).toString();
                    } catch (java.lang.NullPointerException e) {
                        currentUserProfileImage = "";
                    }
                    messageReference = dbReference.getReference().child("Dorms").child(currentDormName).child("Messages");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void displayMessagesFrame(View rootView) {
        messageRecyclerView = rootView.findViewById(R.id.all_que_list);
        messageRecyclerView.bringToFront();

        messageRecords = new ArrayList<>();
        messageRecords.add(new Messages("message", "username", "uid", "https://firebasestorage.googleapis.com/v0/b/numad22sp-final-dormemo.appspot.com/o/images%2Fdefault_avatar.png?alt=media&token=a92b6a69-cc1d-46dc-8c3e-b98b1fa4682a", "time", "date"));

        messageRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        messageViewAdaptor = new MessageViewAdaptor(messageRecords, getContext());
        messageRecyclerView.setAdapter(messageViewAdaptor);
        messageRecyclerView.setLayoutManager(linearLayoutManager);
    }

    private void displayAddFrame(View rootView) {
        rootView.findViewById(R.id.add_message_layout).bringToFront();

        // get text content
        EditText newMessage = rootView.findViewById(R.id.add_question_txt);
        Button addButton = rootView.findViewById(R.id.add_question_btn);
        addButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // pop up alert window
                new AlertDialog.Builder(getActivity())
                        .setMessage("Are you sure to post this new message?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                addNewMessage(newMessage.getText().toString());
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
    }

    private void displaySearchFrame(View rootView) {
        rootView.findViewById(R.id.search_message_layout).bringToFront();

    }

    private void addNewMessage(String newMessage) {
        if (TextUtils.isEmpty(newMessage)) {
            Toast.makeText(getActivity(),"Message body cannot be empty.",Toast.LENGTH_LONG).show();
            return;
        }

        String pushID = messageReference.push().getKey();
        HashMap hashmap = new HashMap();
        hashmap.put("message", newMessage);
        hashmap.put("username", currentUsername);
        hashmap.put("uid", currentUserID);
        hashmap.put("profilePicture", currentUserProfileImage);
        hashmap.put("date", getCurrentDate());
        hashmap.put("time", getCurrentTime());

        messageReference.child(pushID).updateChildren(hashmap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getActivity(),"Message added successfully",Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), Objects.requireNonNull(task.getException()).toString(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMM-yy");
        return currentDate.format(calendar.getTime());
    }

    private String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        return currentTime.format(calendar.getTime());
    }
}