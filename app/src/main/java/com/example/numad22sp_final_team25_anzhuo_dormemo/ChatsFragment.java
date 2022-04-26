package com.example.numad22sp_final_team25_anzhuo_dormemo;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class ChatsFragment extends Fragment {

    private View chatsFragmentView;
    private ScrollView scrollView;
    private ImageButton sendMessageButton;
    private EditText userSendMessage;
    private TextView groupChatTextDisplay;

    private String currentUserID, currentUserName, currentDormName, currentDate, currentTime;

    private FirebaseUser currentUser;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference usersRef, dormRef, groupMessageKeyRef;

    private List<ChatMessages> chatMessagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private ChatMessagesAdapter messageAdapter;
    private RecyclerView userMessageList;


    public ChatsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        usersRef.child(currentUser.getUid()).child("DormName").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    currentDormName = dataSnapshot.getValue(String.class);
//                    Toast.makeText(getActivity(),"current"+currentDormName,Toast.LENGTH_SHORT).show();

                    dormRef.child(currentDormName).child("Chats").addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                            //displayMessage(snapshot);

                            ChatMessages messages = snapshot.getValue(ChatMessages.class);
                            chatMessagesList.add(messages);
                            messageAdapter.notifyDataSetChanged();
                            linearLayoutManager.scrollToPosition(chatMessagesList.size()-1);
                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                            if (snapshot.exists()) {

                            }
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
                else {
                    Log.d("ChatsFragment", "currentDormName is null");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("ChatsFragment", "currentDormName is null");
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
        startActivity(loginIntent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        chatsFragmentView = inflater.inflate(R.layout.fragment_chats, container, false);



        if(currentUser == null || Objects.isNull(currentUser)){
            SendUserToLoginActivity();
        }
        dormRef = FirebaseDatabase.getInstance().getReference().child("Dorms");
        initializeFields();

        currentUserID = firebaseAuth.getCurrentUser().getUid();

        getUserInfo();


        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveMessagesToDatabase();
                userSendMessage.setText("");

                //scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });

        return chatsFragmentView;
    }


    private void initializeFields() {
        userSendMessage = (EditText) chatsFragmentView.findViewById(R.id.chat_input_message);
        sendMessageButton = (ImageButton) chatsFragmentView.findViewById(R.id.chat_send_message_button);
        //groupChatTextDisplay = (TextView) chatsFragmentView.findViewById(R.id.chat_text_display);
        //scrollView = (ScrollView) chatsFragmentView.findViewById(R.id.chat_scrollview);
        messageAdapter = new ChatMessagesAdapter(chatMessagesList);
        userMessageList = (RecyclerView) chatsFragmentView.findViewById(R.id.message_lists_recycler_view);
        linearLayoutManager = new LinearLayoutManager(this.getActivity());
        userMessageList.setLayoutManager(linearLayoutManager);
        userMessageList.setAdapter(messageAdapter);

    }

    private void getUserInfo() {
        usersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    currentUserName = snapshot.child("Username").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void saveMessagesToDatabase() {
        String message = userSendMessage.getText().toString();
        DatabaseReference dormMessageRef = dormRef.child(currentDormName).child("Chats");
        String messageKey = dormMessageRef.push().getKey();
        if(TextUtils.isEmpty(message)){
            Toast.makeText(getActivity(),"Please write message", Toast.LENGTH_SHORT).show();
        }
        else{
            Calendar calendarDate;
            calendarDate = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
            currentDate = dateFormat.format(calendarDate.getTime());

            Calendar calendarTime;
            calendarTime = Calendar.getInstance();
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:MM a");
            currentTime = timeFormat.format(calendarTime.getTime());

            HashMap<String, Object> groupMessageKey = new HashMap<>();
            dormMessageRef.updateChildren(groupMessageKey);
            groupMessageKeyRef = dormMessageRef.child(messageKey);

            HashMap<String, Object> messageInfoMap = new HashMap<>();
            messageInfoMap.put("UserName", currentUserName);
            messageInfoMap.put("Message", message);
            messageInfoMap.put("Date", currentDate);
            messageInfoMap.put("Time", currentTime);
            messageInfoMap.put("UserID", currentUserID);
            groupMessageKeyRef.updateChildren(messageInfoMap);
        }
    }

    private void displayMessage(DataSnapshot dataSnapshot) {
        /*
        Iterator iterator = dataSnapshot.getChildren().iterator();
        while(iterator.hasNext()){
            String chatDate = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatMessage = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatTime = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatUserName = (String) ((DataSnapshot)iterator.next()).getValue();

            groupChatTextDisplay.append(chatUserName + " :\n" + chatMessage + "\n" + chatTime + "\n" + chatDate + "\n\n");
        }
        */



    }
}