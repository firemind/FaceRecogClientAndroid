package com.example.android.camera2basic.adapters;

import android.net.Uri;

/**
 * Created by viruch on 27.09.17.
 */

public class ImageItem {
    private String imageTitle;
    private Integer imageId;
    private Uri uri;

    public String getTitle() {
        return imageTitle;
    }

    public void setTitle(String android_version_name) {
        this.imageTitle = android_version_name;
    }

    public Integer getId() {
        return imageId;
    }

    public void setId(Integer android_image_url) {
        this.imageId = android_image_url;
    }

    public Uri getURI() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }
}