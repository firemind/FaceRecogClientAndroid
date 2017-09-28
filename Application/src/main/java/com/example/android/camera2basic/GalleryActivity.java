package com.example.android.camera2basic;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.android.camera2basic.adapters.MyAdapter;
import com.example.android.camera2basic.data.Face;
import com.example.android.camera2basic.data.FaceRepository;

import java.util.List;

public class GalleryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        setContentView(R.layout.activity_gallery);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);

        setupRecyclerView();

        FloatingActionButton button = (FloatingActionButton) findViewById(R.id.action_go_to_camera_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraActivity = new Intent(GalleryActivity.this, CameraActivity.class);
                startActivity(cameraActivity);
            }
        });
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.image_gallery);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(),2);
        recyclerView.setLayoutManager(layoutManager);
        List<Face> imageItems = prepareData();
        MyAdapter adapter = new MyAdapter(getApplicationContext(), imageItems);
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

    private List<Face> prepareData() {
        return FaceRepository.getFaceRepository(this).getAll();
    }

}
