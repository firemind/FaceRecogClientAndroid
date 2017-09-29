package com.example.android.camera2basic.data;

public class ClassifyResponse {
    public String label;
    public double score;
    public String image;
    public String errorMessage = null;

    boolean successful(){
        return errorMessage == null;
    }
}
