package com.example.android.camera2basic.tasks;

class ClassifyResponse {
    String label;
    double score;
    String image;
    String errorMessage = null;

    boolean successful(){
        return errorMessage == null;
    }
}
