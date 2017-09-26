package com.example.android.camera2basic.tasks;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.camera2basic.Camera2BasicFragment;
import com.example.android.camera2basic.CameraActivity;
import com.example.android.camera2basic.MultipartUtility;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

/**
 * Created by mike on 23.09.17.
 */

public class ClassifyTask extends AsyncTask<Void, Integer, String> {
    CameraActivity ma;
    protected ProgressDialog dlg;
    public ClassifyTask(CameraActivity ma, File file) {
        this.ma = ma;

        dlg = new ProgressDialog(ma);
        dialog = new Dialog(ma);
        mFile = file;
    }

    /**
     * progress dialog to show user that the backup is processing.
     */
    private Dialog dialog;
    /**
     * The file we save the image into.
     */
    private final File mFile;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dlg.setMessage("Please wait");
        dlg.show();
    }

    // This gets executed on a background thread
    protected String doInBackground(Void... arg) {
        try {
            MultipartUtility multipart = new MultipartUtility("http://192.168.1.127:5000/classify", "utf-8");
            multipart.addFilePart("image", mFile);
            //multipart.addFormField("label", "mike");
            List<String> response = multipart.finish();
            for(String s : response){
                Log.e("ImageSaver", s);
                return s;
            }

        } catch (IOException e) {
            e.printStackTrace();
            return ("io exception on classifying");
        }

        return "Downloaded hands";
    }

    @Override
    protected void onProgressUpdate(Integer... deckCount) {
    }

    @Override
    protected void onPostExecute(String result) {
        if (dlg.isShowing()) {
            dlg.dismiss();
        }
        ma.showToast(result);
    }
}