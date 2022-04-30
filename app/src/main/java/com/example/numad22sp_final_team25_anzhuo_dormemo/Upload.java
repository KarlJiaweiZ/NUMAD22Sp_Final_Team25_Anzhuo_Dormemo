package com.example.numad22sp_final_team25_anzhuo_dormemo;

public class Upload {
    private String mImageUri;

    public Upload() {
        //empty constructor needed
    }

    public Upload(String imageUri) {
        mImageUri = imageUri;
    }

    public String getImageUri() {
        return mImageUri;
    }

    public void setImageUri(String imageUri) {
        mImageUri = imageUri;
    }
}
