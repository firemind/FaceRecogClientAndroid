package com.example.android.camera2basic;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.android.camera2basic.adapters.ImageItem;
import com.example.android.camera2basic.adapters.MyAdapter;

import java.io.File;
import java.util.ArrayList;

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
        ArrayList<ImageItem> imageItems = prepareData();
        MyAdapter adapter = new MyAdapter(getApplicationContext(), imageItems);
        recyclerView.setAdapter(adapter);
    }

    private ArrayList<ImageItem> prepareData() {
        File f = getExternalFilesDir("photos");
        File file[] = f.listFiles();

        ArrayList<ImageItem> list = new ArrayList<>();
        if (file == null) {
            return list;
        }
        for (File aFile : file) {
            ImageItem imageItem = new ImageItem();

            imageItem.setTitle(aFile.getName());
            imageItem.setFile(aFile);
            list.add(imageItem);
        }
        return list;
    }
}
