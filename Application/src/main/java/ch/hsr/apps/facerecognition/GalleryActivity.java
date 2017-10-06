package ch.hsr.apps.facerecognition;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.URLConnection;
import java.util.Collections;
import java.util.List;

import ch.hsr.apps.facerecognition.adapters.FaceAdapter;
import ch.hsr.apps.facerecognition.data.ClassifyResponse;
import ch.hsr.apps.facerecognition.data.FaceData;
import ch.hsr.apps.facerecognition.data.FaceRepository;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GalleryActivity extends AppCompatActivity {
    private final OkHttpClient client = new OkHttpClient();
    private RecyclerView recyclerView;
    private ProgressDialog dlg;
    private Drawable personPlaceholderImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        setContentView(R.layout.activity_gallery);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final AppBarLayout appBarLayout = findViewById(R.id.app_bar_layout);

        personPlaceholderImage = getDrawable(R.drawable.person_placeholder);

        setupRecyclerView();

        FloatingActionButton button = findViewById(R.id.action_go_to_camera_button);
        button.setOnClickListener(view -> requestFacePhoto());
    }

    private void requestFacePhoto() {
        Intent cameraActivity = new Intent(GalleryActivity.this, CameraActivity.class);
        startActivityForResult(cameraActivity, CameraActivity.GET_FACE_PHOTO_REQUEST);
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.image_gallery);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        FaceAdapter adapter = new FaceAdapter(getApplicationContext(), prepareData(), personPlaceholderImage, this::classifyFace);
        recyclerView.setAdapter(adapter);
    }

    public void reloadImages() {
        super.onResume();
        FaceAdapter adapter = new FaceAdapter(getApplicationContext(), prepareData(), personPlaceholderImage, this::classifyFace);
        recyclerView.setAdapter(adapter);
        recyclerView.invalidate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CameraActivity.GET_FACE_PHOTO_REQUEST) {
            if (resultCode == RESULT_OK) {
                FaceRepository repo = FaceRepository.getFaceRepository(this);
                FaceData face = repo.find(data.getStringExtra("faceId"));
                reloadImages();
                classifyFace(face);
            }
        }
    }

    private void classifyFace(FaceData faceData) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        final String serverAddress = sharedPref.getString(SettingsActivity.KEY_PREF_SERVER_ADDRESS, "");

        dlg = new ProgressDialog(this);
        dlg.setMessage(getString(R.string.message_classifying_face));
        dlg.show();

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
                runOnUiThread(() -> {
                    showError(e.getLocalizedMessage());
                    if (dlg.isShowing()) {
                        dlg.dismiss();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }

                ClassifyResponse result = new Gson().fromJson(response.body().string(),
                        ClassifyResponse.class);

                runOnUiThread(() -> {
                    faceData.setPredictionLabel(result.label);
                    faceData.setPredictionScore(result.score);
                    faceData.setPredictionImagePath(result.image);
                    faceData.save();
                    reloadImages();
                    if (dlg.isShowing()) {
                        dlg.dismiss();
                    }
                });
            }
        });
    }

    private List<FaceData> prepareData() {
        List<FaceData> faces = FaceRepository.getFaceRepository(this).getAll();
        Collections.reverse(faces);
        return faces;
    }


    public void showError(final String text) {
        Snackbar.make(recyclerView, text, Snackbar.LENGTH_LONG).show();
    }
}
