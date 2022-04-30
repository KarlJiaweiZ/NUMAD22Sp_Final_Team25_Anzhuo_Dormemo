package com.example.numad22sp_final_team25_anzhuo_dormemo.board.Comment;

public class Comments {
    public String uid;
    public String comment;
    public String date;
    public String time;
    public String profileImage;
    public String username;

    public Comments() {}

    public Comments(String uid, String comment, String date, String time, String profileImage, String username) {
        this.uid = uid;
        this.comment = comment;
        this.date = date;
        this.time = time;
        this.profileImage = profileImage;
    }
}
