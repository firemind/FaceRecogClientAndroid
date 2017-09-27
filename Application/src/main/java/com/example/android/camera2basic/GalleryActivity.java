package com.example.android.camera2basic;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.android.camera2basic.adapters.ImageItem;
import com.example.android.camera2basic.adapters.MyAdapter;

import java.io.File;
import java.util.ArrayList;

public class GalleryActivity extends AppCompatActivity {

    private final String image_titles[] = {
            "Img1",
            "Img2",
            "Img3",
            "Img4",
            "Img5",
            "Img6",
            "Img7",
            "Img8",
            "Img9",
            "Img10",
            "Img11",
            "Img12",
            "Img13",
    };

    private final Integer image_ids[] = {
            R.drawable.img1,
            R.drawable.img2,
            R.drawable.img3,
            R.drawable.img4,
            R.drawable.img5,
            R.drawable.img6,
            R.drawable.img7,
            R.drawable.img8,
            R.drawable.img9,
            R.drawable.img10,
            R.drawable.img11,
            R.drawable.img12,
            R.drawable.img13,
    };

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
            imageItem.setUri(Uri.parse(aFile.toURI().toString()));
            list.add(imageItem);
        }
        return list;
    }
}
