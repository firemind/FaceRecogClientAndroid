package ch.hsr.apps.facerecognition;

import android.content.Intent;
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

import java.io.IOException;

import ch.hsr.apps.facerecognition.adapters.FaceAction;
import ch.hsr.apps.facerecognition.adapters.FaceAdapter;
import ch.hsr.apps.facerecognition.data.FaceData;
import ch.hsr.apps.facerecognition.data.FaceRepository;
import ch.hsr.apps.facerecognition.data.FaceService;

public class GalleryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FaceRepository repo;
    private FaceService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        setContentView(R.layout.activity_gallery);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final AppBarLayout appBarLayout = findViewById(R.id.app_bar_layout);

        repo = FaceRepository.getFaceRepository(this);
        service = new FaceService();

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
        FaceAdapter adapter = new FaceAdapter(this, repo, new FaceAction() {
            @Override
            public void onFaceRepredict(String id) {
                service.classifyFace(repo.find(id), GalleryActivity.this,
                        GalleryActivity.this::showError);
            }

            @Override
            public void onFaceInserted(int position) {
                recyclerView.smoothScrollToPosition(position);
            }

            @Override
            public void onFaceDelete(String id) {
                repo.delete(id);
            }
        });
        recyclerView.setAdapter(adapter);
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
                FaceData face = repo.find(data.getStringExtra("faceId"));
                service.classifyFace(face, this, this::showError);
            }
        }
    }

    protected void showError(IOException e) {
        Snackbar.make(recyclerView, e.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
    }

}
