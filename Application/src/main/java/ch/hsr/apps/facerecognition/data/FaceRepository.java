package ch.hsr.apps.facerecognition.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ch.hsr.apps.facerecognition.SettingsActivity;

/**
 * Created by viruch on 27.09.17.
 */

public class FaceRepository {
    private File repoDir;
    private File photoDir;
    private Gson gson;
    private List<FaceData> faces = null;
    private String serverAddress;
    private DateFormat filenameFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.ENGLISH);

    private FaceRepository(File home, String serverAddress) {
        this.repoDir = new File(home, "faces");
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
        faces = new ArrayList<>();

        if (repoDir.exists()) {
            try {
                for(File f : repoDir.listFiles()){
                    FaceData face = gson.fromJson(new FileReader(f), FaceData.class);
                    face.initialize(this);
                    faces.add(face);
                }
            } catch(FileNotFoundException e){
                e.printStackTrace();
            }
        }
    }

    private void writeToFile(){
        FileWriter writer = null;
        try {
            repoDir.mkdir();
            for (FaceData face : faces) {
                File f = new File(repoDir, face.getImageName().replace("jpg", "json"));
                writer = new FileWriter(f, false);
                gson.toJson(face, writer);
                writer.close();
            }
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

    public List<FaceData> getAll(){
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

    public void delete(FaceData face){
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
        return getNewId() + ".jpg";
    }

    private String getNewId() {
        return filenameFormat.format(new Date());
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

    public FaceData create() {
        FaceData face = new FaceData();
        face.initialize(this);
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
