package com.example.numad22sp_final_team25_anzhuo_dormemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class ChatActivity extends AppCompatActivity {

    private View chatsFragmentView;
    private ScrollView scrollView;
    private ImageButton sendMessageButton;
    private EditText userSendMessage;
    private TextView groupChatTextDisplay;

    private String currentUserID, currentUserName, currentDormName, currentDate, currentTime;

    private FirebaseUser currentUser;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference usersRef, dormRef, groupMessageKeyRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initializeFields();
    }

    private void initializeFields() {
        userSendMessage = (EditText) chatsFragmentView.findViewById(R.id.chat_input_message);
        sendMessageButton = (ImageButton) chatsFragmentView.findViewById(R.id.chat_send_message_button);
        groupChatTextDisplay = (TextView) chatsFragmentView.findViewById(R.id.chat_text_display);
        scrollView = (ScrollView) chatsFragmentView.findViewById(R.id.chat_scrollview);

    }
}