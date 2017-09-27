package com.example.android.camera2basic.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.camera2basic.Face;
import com.example.android.camera2basic.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by viruch on 27.09.17.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private List<Face> galleryList;
    private Context context;

    public MyAdapter(Context context, List<Face> galleryList) {
        this.galleryList = galleryList;
        this.context = context;
    }

    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cell_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyAdapter.ViewHolder viewHolder, int i) {
        String predictionPerson = galleryList.get(i).getPredictionPerson();
        viewHolder.title.setText(predictionPerson == null ?
                context.getResources().getString(R.string.not_predicted): predictionPerson);
        viewHolder.img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Picasso.with(context)
                .load(galleryList.get(i).getImageFile())
                .resize(240,120)
                .centerInside()
                .into(viewHolder.img);

        viewHolder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"Image",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return galleryList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView title;
        private ImageView img;
        ViewHolder(View view) {
            super(view);

            title = (TextView)view.findViewById(R.id.title);
            img = (ImageView) view.findViewById(R.id.img);
        }
    }
}