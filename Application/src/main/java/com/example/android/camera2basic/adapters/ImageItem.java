package com.example.android.camera2basic.adapters;

import android.net.Uri;

import java.io.File;

/**
 * Created by viruch on 27.09.17.
 */

public class ImageItem {
    private String imageTitle;
    private File file;

    public String getTitle() {
        return imageTitle;
    }

    public void setTitle(String android_version_name) {
        this.imageTitle = android_version_name;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}