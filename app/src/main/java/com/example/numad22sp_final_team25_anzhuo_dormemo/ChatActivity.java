package com.example.numad22sp_final_team25_anzhuo_dormemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toolbar;

public class ChatActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageButton sendMessageButton;
    private EditText userMessageInput;
    private ScrollView scrollView;
    private TextView displayTextMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initializeFields();
    }

    private void initializeFields() {
        //toolbar = (Toolbar) findViewById(R.id.cha)
        sendMessageButton = (ImageButton) findViewById(R.id.chat_send_message_button);
        userMessageInput = (EditText) findViewById(R.id.chat_input_message);
        scrollView = (ScrollView) findViewById(R.id.chat_scrollview);
        displayTextMessages = (TextView) findViewById(R.id.chat_text_display);

    }
}