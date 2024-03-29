package ch.hsr.apps.facerecognition.data;

import android.net.Uri;
import android.support.annotation.NonNull;

import java.io.File;

/**
 * Created by viruch on 27.09.17.
 */

public class FaceData implements Comparable<FaceData> {
    private String id;
    private double predictionScore;
    private String predictionLabel;
    private String predictionImagePath;
    private String taggedLabel;
    private transient FaceRepository repository;
    private transient File photoDir;
    private transient String serverAddress;

    FaceData(String id) {
        this.id = id;
    }

    public void save() {
        repository.save(this);
    }

    public String getImageName() {
        return id + ".jpg";
    }

    public String getId() {
        return id;
    }

    public File getImageFile(){
        return new File(photoDir, getImageName());
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
        if (predictionImagePath == null || predictionImagePath.length() == 0)
            return null;
        return Uri.parse(serverAddress).buildUpon()
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

    void initialize(FaceRepository repository) {
        this.repository = repository;
        photoDir = repository.getPhotoDir();
        serverAddress = repository.getServerAddress();
    }

    @Override
    public int compareTo(@NonNull FaceData faceData) {
        return faceData.getId().compareTo(getId());
    }
}
