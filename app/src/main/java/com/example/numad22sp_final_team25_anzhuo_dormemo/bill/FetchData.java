package com.example.numad22sp_final_team25_anzhuo_dormemo.bill;

import java.util.Arrays;

public class FetchData {
    String[] allRoommates = new String[100];
    int p = 0;

    public void addRoommates(String rm){
        allRoommates[p++] = rm;
    }
    public String[] getAllRoommates(){
        return Arrays.copyOfRange(allRoommates, 0, p);
    }

}
