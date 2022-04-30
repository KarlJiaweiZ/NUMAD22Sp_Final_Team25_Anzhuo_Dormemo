package com.example.numad22sp_final_team25_anzhuo_dormemo.board.Message;

import java.util.HashMap;

public class Messages {
    public String message;
    public String username;
    public String uid;
    public String profilePicture;
    public String time;
    public String date;
    public String dormName;
    public HashMap<String, Boolean> likes;
    public String priority; // technically, should use enum

    public Messages() {
    }

    public Messages(String message, String username, String uid, String profilePicture, String time, String date, String dormName, String priority) {
        this.message = message;
        this.username = username;
        this.uid = uid;
        if (profilePicture == null || profilePicture.isEmpty()) {
            profilePicture = "https://firebasestorage.googleapis.com/v0/b/numad22sp-final-dormemo.appspot.com/o/images%2Fdefault_avatar.png?alt=media&token=a92b6a69-cc1d-46dc-8c3e-b98b1fa4682a";
        }
        this.profilePicture = profilePicture;
        this.time = time;
        this.date = date;
        this.likes = new HashMap<>();
        this.dormName = dormName;
        this.priority = priority;
    }
}
