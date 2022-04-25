package com.example.numad22sp_final_team25_anzhuo_dormemo;

public class ChatMessages {

    private String UserName;
    private String Message;
    private String Date;
    private String Time;
    private String UserID;

    public ChatMessages(){

    }

    public ChatMessages(String userName, String message, String date, String time, String userID) {
        UserName = userName;
        Message = message;
        Date = date;
        Time = time;
        UserID = userID;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }
}
