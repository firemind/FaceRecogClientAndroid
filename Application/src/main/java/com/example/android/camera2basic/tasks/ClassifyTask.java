package com.example.android.camera2basic.tasks;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.example.android.camera2basic.CameraActivity;
import com.example.android.camera2basic.data.Face;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
    private final OkHttpClient client = new OkHttpClient();

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
        ClassifyResponse jsonResponse;

        MediaType contentType = MediaType.parse(URLConnection.guessContentTypeFromName(image.getName()));
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                //.addFormDataPart("label", "some name")
                .addFormDataPart("image", image.getName(),
                        RequestBody.create(contentType, image))
                .build();
        Request request = new Request.Builder()
                .url(serverAddress + "/classify")
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful())
                    throw new IOException("Unexpected code " + response);

                jsonResponse = new Gson().fromJson(response.body().string(), ClassifyResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
            ClassifyResponse failed = new ClassifyResponse();
            failed.errorMessage = e.getMessage();
            return failed;
        }
        return jsonResponse;
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
            face.setPredictionLabel(result.label);
            face.setPredictionScore(result.score);
            face.setPredictionImagePath(result.image);
            face.save();
            ma.showToast((int)(face.getPredictionScore() * 100) + "% : " + face.getPredictionLabel() + face.getPredictionImageUri());
        } else {
            ma.showToast(result.errorMessage);
        }
    }

}

