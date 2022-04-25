package com.example.numad22sp_final_team25_anzhuo_dormemo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.numad22sp_final_team25_anzhuo_dormemo.Board.Message.MessageViewAdaptor;
import com.example.numad22sp_final_team25_anzhuo_dormemo.Board.Message.Messages;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
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
    private String currentUserProfileImage = "https://firebasestorage.googleapis.com/v0/b/numad22sp-final-dormemo.appspot.com/o/images%2Fdefault_avatar.png?alt=media&token=a92b6a69-cc1d-46dc-8c3e-b98b1fa4682a";
    private MessageViewAdaptor messageViewAdaptor;
    private RecyclerView messageRecyclerView;
    private HashMap<String, Messages> messageRecords;
    private View rootView;

    public BoardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        dbReference = FirebaseDatabase.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_board, container, false);

        messageRecords = new HashMap<>();

        BottomNavigationView bottomNavigationView = rootView.findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.bforum:
                        displayMessagesFrame(rootView);
                        break;
                    case R.id.badd:
                        displayAddMessageFrame(rootView);
                        break;
                    case R.id.bsearch:
                        displaySearchMessageFrame(rootView);
                        break;
                }
                return true;
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        // default: display messages
        init();
        displayMessagesFrame(rootView);

        // create dropdown menu
        createDropdown();
    }

    private void createDropdown() {
        String[] priorities = getResources().getStringArray(R.array.priority);
        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(getContext(), R.layout.message_dropdown_item, priorities);
        AutoCompleteTextView pv = (AutoCompleteTextView) rootView.findViewById(R.id.message_priority);
        pv.setAdapter(itemsAdapter);
    }

    private void init() {
        dbReference.getReference().child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    currentDormName = Objects.requireNonNull(snapshot.child("DormName").getValue()).toString();
                    currentUsername = Objects.requireNonNull(snapshot.child("Username").getValue()).toString();
                    try {
                        currentUserProfileImage = Objects.requireNonNull(snapshot.child("UserPic").getValue()).toString();
                    } catch (java.lang.NullPointerException e) {
                        currentUserProfileImage = "https://firebasestorage.googleapis.com/v0/b/numad22sp-final-dormemo.appspot.com/o/images%2Fdefault_avatar.png?alt=media&token=a92b6a69-cc1d-46dc-8c3e-b98b1fa4682a";
                    }
                    messageReference = dbReference.getReference().child("Dorms").child(currentDormName).child("Messages");
                    messageReference.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                            Messages newMessage = snapshot.getValue(Messages.class);
                            messageRecords.put(snapshot.getKey(), newMessage);
                            messageViewAdaptor.notifyItemInserted(0);
                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
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

        messageRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        messageViewAdaptor = new MessageViewAdaptor(messageRecords, getContext());
        messageRecyclerView.setAdapter(messageViewAdaptor);
        messageRecyclerView.setLayoutManager(linearLayoutManager);
    }

    private void displayAddMessageFrame(View rootView) {
        rootView.findViewById(R.id.add_message_layout).bringToFront();

        // get text content
        EditText newMessage = rootView.findViewById(R.id.add_question_txt);

        // get priority
        AutoCompleteTextView priority = rootView.findViewById(R.id.message_priority);

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
                                addNewMessage(newMessage.getText().toString(), priority.getText().toString());
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
    }

    private void displaySearchMessageFrame(View rootView) {
        rootView.findViewById(R.id.search_message_layout).bringToFront();

    }

    private void addNewMessage(String newMessage, String priority) {
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
        hashmap.put("dormName", currentDormName);
        hashmap.put("likes", new HashMap<String, Boolean>() {{put("dummy", true);}});
        hashmap.put("priority", priority);

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