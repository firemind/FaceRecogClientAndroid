package com.example.android.camera2basic.tasks;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.example.android.camera2basic.CameraActivity;
import com.example.android.camera2basic.data.Face;
import com.example.android.camera2basic.MultipartUtility;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;

/**
 * Created by mike on 23.09.17.
 */

public class ClassifyTask extends AsyncTask<Void, Integer, ClassifyResponse> {
    private CameraActivity ma;
    private ProgressDialog dlg;
    private Dialog dialog;
    private Face face;
    private final File image;
    private final String serverAddress;
    private ClassifyResponse classifyResponse;

    public ClassifyTask(CameraActivity ma, Face face, String serverAddress) {
        this.ma = ma;

        this.serverAddress = serverAddress;
        dlg = new ProgressDialog(ma);
        dialog = new Dialog(ma);
        this.face = face;
        image = face.getImageFile();
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dlg.setMessage("Please wait");
        dlg.show();
    }

    // This gets executed on a background thread
    protected ClassifyResponse doInBackground(Void... arg) {
        ClassifyResponse response;
        try {
            MultipartUtility multipart = new MultipartUtility(serverAddress + "/classify", "utf-8");
            multipart.addFilePart("image", image);
            //multipart.addFormField("label", "mike");
            response = new Gson().fromJson(multipart.finish(), ClassifyResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
            ClassifyResponse failed = new ClassifyResponse();
            failed.errorMessage = "IO Exception";
            return failed;
        }
        return response;
    }

    @Override
    protected void onProgressUpdate(Integer... deckCount) {
    }

    @Override
    protected void onPostExecute(ClassifyResponse result) {
        if (dlg.isShowing()) {
            dlg.dismiss();
        }

        if (result.successful()) {
            face.setPredictionPerson(result.label);
            face.setPredictionScore(result.score);
            face.setPredictionPersonImageUri(result.image);
            face.save();
            ma.showToast(face.getPredictionScore() + ": " + face.getPredictionPerson());
        } else {
            ma.showToast(result.errorMessage);
        }
    }

}

