package com.example.android.camera2basic.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.example.android.camera2basic.SettingsActivity;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Created by viruch on 27.09.17.
 */

public class FaceRepository {
    private File repo;
    private File photoDir;
    private Gson gson;
    private List<Face> faces = null;
    private String serverAddress;

    private FaceRepository(File home, String serverAddress) {
        this.repo = new File(home, "faces.json");
        this.photoDir = new File(home, "photos");
        this.serverAddress = serverAddress;
        gson = new Gson();
    }

    @NonNull
    public static FaceRepository getFaceRepository(Context context) {
        File home = context.getExternalFilesDir("");
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String serverAddress = sharedPref.getString(SettingsActivity.KEY_PREF_SERVER_ADDRESS, "");
        return new FaceRepository(home, serverAddress);
    }

    private void readFromFile() {
        if (faces == null){
            faces = new ArrayList<>();
        }

        if (repo.exists()) {
            try {
                faces = new ArrayList<>(
                        Arrays.asList(
                                gson.fromJson(new FileReader(repo), Face[].class)));
                for (Face f : faces){
                    f.setRepository(this);
                }
            } catch(FileNotFoundException e){
                e.printStackTrace();
            }
        }
    }

    private void writeToFile(){
        FileWriter writer = null;
        try {
            repo.createNewFile();
            writer = new FileWriter(repo, false);
            gson.toJson(faces, writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public List<Face> getAll(){
        if (faces == null){
            readFromFile();
        }
        return faces;
    }

    public String saveImage(Bitmap bitmap, int rotationDegrees){
        Matrix m = new Matrix();
        m.postRotate(rotationDegrees);
        Bitmap corrected = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, false);

        File photo = new File(
                photoDir,
                getContinousFilename()
        );
        String name = null;
        try{
            photo.getParentFile().mkdirs();
            photo.createNewFile();
            saveAsJpeg(corrected, photo);
            name = photo.getName();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return name;
    }

    public void delete(Face face){
        File photo = face.getImageFile();
        if (photo.exists()) {
            photo.delete();
        }
        getAll().remove(face);
        save();
    }

    public void save() {
        writeToFile();
    }

    private String getContinousFilename() {
        String[] filenames = photoDir.list();
        int maxFileNumber = 0;
        if (filenames != null){
            for (String filename: filenames){
                String nameOnlyNumber = filename.replaceAll("[^\\d]", "");
                if (nameOnlyNumber.length() == 0)
                    continue;
                maxFileNumber = Math.max(Integer.parseInt(nameOnlyNumber), maxFileNumber);
            }
        }
        return String.format(Locale.ENGLISH, "%04d.jpg", maxFileNumber + 1);
    }

    private void saveAsJpeg(Bitmap bmp, File photo) throws FileNotFoundException {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(photo);
            bmp.compress(Bitmap.CompressFormat.JPEG, 95, out);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Face create() {
        Face face = new Face(this);
        getAll().add(face);
        return face;
    }

    public File getPhotoDir() {
        return photoDir;
    }

    public String getServerAddress() {
        return serverAddress;
    }
}
