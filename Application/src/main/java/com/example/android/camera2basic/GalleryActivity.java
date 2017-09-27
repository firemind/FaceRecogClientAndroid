package com.example.android.camera2basic;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.android.camera2basic.adapters.MyAdapter;

import java.util.List;

public class GalleryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        setupRecyclerView();
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

    private List<Face> prepareData() {
        return FaceRepository.getFaceRepository(this).getAll();
    }

}
