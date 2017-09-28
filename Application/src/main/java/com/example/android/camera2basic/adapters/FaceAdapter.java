package com.example.android.camera2basic.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.camera2basic.data.Face;
import com.example.android.camera2basic.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by viruch on 27.09.17.
 */

public class FaceAdapter extends RecyclerView.Adapter<FaceAdapter.ViewHolder> {
    private List<Face> galleryList;
    private Context context;
    private String notPredictedMessage;
    private int image_width;
    private int image_height;

    public FaceAdapter(Context context, List<Face> galleryList) {
        this.galleryList = galleryList;
        this.context = context;
        Resources res = context.getResources();
        notPredictedMessage = res.getString(R.string.not_predicted);
        image_width = (int) res.getDimension(R.dimen.image_preview_width);
        image_height = (int) res.getDimension(R.dimen.image_preview_heigth);
    }

    @Override
    public FaceAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_face, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FaceAdapter.ViewHolder holder, int i) {
        Face face = galleryList.get(i);

        String predictionLabel = face.getPredictionLabel();
        renderToImageView(Uri.fromFile(face.getImageFile()), holder.testImageView);

        if (predictionLabel == null){
            holder.predictionLabel.setText(notPredictedMessage);
            holder.predictionScore.setVisibility(View.INVISIBLE);
        } else {
            holder.predictionLabel.setText(predictionLabel);
            holder.predictionScore.setText("" + (int)(face.getPredictionScore() * 100f) + "%");
        }

        Uri predictionImageUri = face.getPredictionImageUri();
        if (predictionImageUri != null){
            renderToImageView(predictionImageUri, holder.predictionImageView);
        }
    }

    private void renderToImageView(Uri uri, ImageView testImageView) {
        Picasso.with(context)
                .load(uri)
                .resize(image_width, image_height)
                .centerCrop()
                .into(testImageView);
    }

    @Override
    public int getItemCount() {
        return galleryList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView testImageView;
        private TextView testLabel;
        private TextView predictionScore;
        private TextView predictionLabel;
        private ImageView predictionImageView;

        ViewHolder(View view) {
            super(view);

            testImageView = (ImageView) view.findViewById(R.id.test_image);
            testLabel = (TextView) view.findViewById(R.id.test_label);
            predictionLabel = (TextView)view.findViewById(R.id.prediction_label);
            predictionScore = (TextView)view.findViewById(R.id.prediction_score);
            predictionImageView = (ImageView)view.findViewById(R.id.prediction_image);

        }
    }
}