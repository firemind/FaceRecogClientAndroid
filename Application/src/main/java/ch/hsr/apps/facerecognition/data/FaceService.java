package ch.hsr.apps.facerecognition.data;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.URLConnection;
import java.util.concurrent.TimeUnit;

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
        client = new OkHttpClient()
                .newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.MINUTES)
                .writeTimeout(5, TimeUnit.MINUTES)
                .build();
    }

    public void classifyFace(FaceData faceData, Activity activity, ClassifyCallback listener) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
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
                activity.runOnUiThread(() -> {
                    listener.onFailure(e);
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }


                String string = response.body().string();
                Log.i("WebResponse", string);

                ClassifyResponse result = new Gson().fromJson(string,
                        ClassifyResponse.class);

                activity.runOnUiThread(() -> {
                    faceData.setPredictionLabel(result.label);
                    faceData.setPredictionScore(result.score);
                    faceData.setPredictionImagePath(result.image);
                    faceData.save();
                    listener.onSuccess(faceData);
                });
            }
        });
    }

    public void submitFace(FaceData faceData, Activity activity, String label, ClassifyCallback listener) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
        final String serverAddress = sharedPref.getString(SettingsActivity.KEY_PREF_SERVER_ADDRESS, "");

        MediaType contentType = MediaType.parse(URLConnection.guessContentTypeFromName(faceData.getImageName()));
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("align", "true")
                .addFormDataPart("label", label)
                .addFormDataPart("image", faceData.getImageName(),
                        RequestBody.create(contentType, faceData.getImageFile()))
                .build();
        Request request = new Request.Builder()
                .url(serverAddress + "/store")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                activity.runOnUiThread(() -> listener.onFailure(e));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }

                String string = response.body().string();
                Log.i("WebResponse", string);
                StoreResponse result = new Gson().fromJson(string,
                        StoreResponse.class);

                activity.runOnUiThread(() -> {
                    faceData.setPredictionImagePath(result.image);
                    faceData.save();
                    listener.onSuccess(faceData);
                });
            }
        });
    }

    public interface ClassifyCallback {
        void onFailure(IOException e);

        void onSuccess(FaceData faceData);
    }

    private static class StoreResponse {
        String image;
    }

    private static class ClassifyResponse {
        String label;
        double score;
        String image;
    }
}
