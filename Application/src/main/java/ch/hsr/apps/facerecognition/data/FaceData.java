package ch.hsr.apps.facerecognition.data;

import android.net.Uri;

import java.io.File;

/**
 * Created by viruch on 27.09.17.
 */

public class FaceData {
    private String imageName;
    private double predictionScore;
    private String predictionLabel;
    private String predictionImagePath;
    private String taggedLabel;
    private transient FaceRepository repository;

    FaceData(FaceRepository repository){
        this.repository = repository;
    }

    public void save() {
        repository.save();
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

    public String getPredictionLabel() {
        return predictionLabel;
    }

    public void setPredictionLabel(String predictionLabel) {
        this.predictionLabel = predictionLabel;
    }

    public Uri getPredictionImageUri() {
        if (predictionImagePath == null)
            return null;
        return Uri.parse(repository.getServerAddress()).buildUpon()
                .encodedPath(predictionImagePath)
                .build();
    }

    public void setPredictionImagePath(String predictionImagePath) {
        this.predictionImagePath = predictionImagePath;
    }

    public String getTaggedLabel() {
        return taggedLabel;
    }

    public void setTaggedLabel(String taggedLabel) {
        this.taggedLabel = taggedLabel;
    }

    public FaceRepository getRepository() {
        return repository;
    }

    public void setRepository(FaceRepository repository) {
        this.repository = repository;
    }

    public String getImageName() {
        return imageName;
    }
}