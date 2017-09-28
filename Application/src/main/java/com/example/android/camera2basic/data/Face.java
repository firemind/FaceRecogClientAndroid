package com.example.android.camera2basic.data;

import android.graphics.Bitmap;

import java.io.File;

/**
 * Created by viruch on 27.09.17.
 */

public class Face {
    private String imageName;
    private double predictionScore;
    private String predictionPerson;
    private String predictionPersonImageUri;
    private String taggedPerson;
    private transient FaceRepository repository;

    Face(FaceRepository repository){
        this.repository = repository;
    }

    public void save() {
        repository.save();
    }

    public void saveImage(Bitmap bitmap, int rotationDegrees) {
        repository.save(this, bitmap, rotationDegrees);
    }

    public File getImageFile(){
        return new File(repository.getPhotoDir(), imageName);
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public double getPredictionScore() {
        return predictionScore;
    }

    public void setPredictionScore(double predictionScore) {
        this.predictionScore = predictionScore;
    }

    public String getPredictionPerson() {
        return predictionPerson;
    }

    public void setPredictionPerson(String predictionPerson) {
        this.predictionPerson = predictionPerson;
    }

    public String getPredictionPersonImageUri() {
        return predictionPersonImageUri;
    }

    public void setPredictionPersonImageUri(String predictionPersonImageUri) {
        this.predictionPersonImageUri = predictionPersonImageUri;
    }

    public String getTaggedPerson() {
        return taggedPerson;
    }

    public void setTaggedPerson(String taggedPerson) {
        this.taggedPerson = taggedPerson;
    }

    public FaceRepository getRepository() {
        return repository;
    }

    public void setRepository(FaceRepository repository) {
        this.repository = repository;
    }
}
