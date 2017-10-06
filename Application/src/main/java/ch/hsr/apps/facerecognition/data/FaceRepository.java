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

    private void readAllFaces() {
        faces = new ArrayList<>();

        if (repoDir.exists()) {
            try {
                for(File f : repoDir.listFiles()){
                    faces.add(readFace(f));
                }
            } catch(FileNotFoundException e){
                e.printStackTrace();
            }
        }
    }

    @NonNull
    private FaceData readFace(File f) throws FileNotFoundException {
        FaceData face = gson.fromJson(new FileReader(f), FaceData.class);
        face.initialize(this);
        return face;
    }

    private void writeToFile(FaceData face) {
        FileWriter writer = null;
        try {
            repoDir.mkdir();
            File f = new File(repoDir, face.getId() + ".json");
            writer = new FileWriter(f, false);
            gson.toJson(face, writer);
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

    public List<FaceData> getAll(){
        if (faces == null){
            readAllFaces();
        }
        return faces;
    }

    public FaceData find(String id) {
        FaceData face = null;
        if (repoDir.exists()) {
            File f = new File(repoDir, id + ".json");
            try {
                face = readFace(f);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return face;
    }

    public FaceData create(Bitmap bitmap, int rotationDegrees) {
        FaceData face = new FaceData(getNewId());
        face.initialize(this);

        Matrix m = new Matrix();
        m.postRotate(rotationDegrees);
        Bitmap corrected = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, false);

        File photo = new File(
                photoDir,
                face.getImageName()
        );
        try{
            photo.getParentFile().mkdirs();
            photo.createNewFile();
            saveAsJpeg(corrected, photo);
        } catch (IOException e) {
            e.printStackTrace();
        }

        getAll().add(face);
        save(face);

        return face;
    }

    public void delete(FaceData face){
        File photo = face.getImageFile();
        if (photo.exists()) {
            photo.delete();
        }
        getAll().remove(face);
    }

    public void save(FaceData face) {
        writeToFile(face);
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

    public File getPhotoDir() {
        return photoDir;
    }

    public String getServerAddress() {
        return serverAddress;
    }
}
