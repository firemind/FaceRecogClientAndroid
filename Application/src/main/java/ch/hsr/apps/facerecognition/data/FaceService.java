package ch.hsr.apps.facerecognition.data;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.URLConnection;

import ch.hsr.apps.facerecognition.SettingsActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by viruch on 06.10.17.
 */

public class FaceService {

    private OkHttpClient client;

    public FaceService() {
        client = new OkHttpClient();
    }

    public void classifyFace(FaceData faceData, Activity galleryActivity, ClassifyCallback listener) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(galleryActivity);
        final String serverAddress = sharedPref.getString(SettingsActivity.KEY_PREF_SERVER_ADDRESS, "");

        MediaType contentType = MediaType.parse(URLConnection.guessContentTypeFromName(faceData.getImageName()));
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("align", "true")
                .addFormDataPart("image", faceData.getImageName(),
                        RequestBody.create(contentType, faceData.getImageFile()))
                .build();
        Request request = new Request.Builder()
                .url(serverAddress + "/classify")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                galleryActivity.runOnUiThread(() -> listener.onFailure(e));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }

                ClassifyResponse result = new Gson().fromJson(response.body().string(),
                        ClassifyResponse.class);

                galleryActivity.runOnUiThread(() -> {
                    faceData.setPredictionLabel(result.label);
                    faceData.setPredictionScore(result.score);
                    faceData.setPredictionImagePath(result.image);
                    faceData.save();
                });
            }
        });
    }

    public interface ClassifyCallback {
        void onFailure(IOException e);
    }

    private static class ClassifyResponse {
        String label;
        double score;
        String image;
    }
}
