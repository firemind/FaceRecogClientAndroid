package ch.hsr.apps.facerecognition.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import ch.hsr.apps.facerecognition.R;
import ch.hsr.apps.facerecognition.data.FaceData;

/**
 * Created by viruch on 27.09.17.
 */

public class FaceAdapter extends RecyclerView.Adapter<FaceAdapter.ViewHolder> {
    private List<FaceData> galleryList;
    private Context context;
    private String notPredictedMessage;
    private int image_width;
    private int image_height;
    private Drawable personPlaceholderImage;

    public FaceAdapter(Context context, List<FaceData> galleryList, Drawable personPlaceholderImage) {
        this.galleryList = galleryList;
        this.context = context;
        this.personPlaceholderImage = personPlaceholderImage;
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
        FaceData face = galleryList.get(i);

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
        } else {
            holder.predictionImageView.setImageDrawable(personPlaceholderImage);
        }
    }

    private void renderToImageView(Uri uri, ImageView testImageView) {
        Picasso.with(context)
                .load(uri)
                .resize(image_width, image_height)
                .centerCrop()
                .placeholder(personPlaceholderImage)
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

            testImageView = view.findViewById(R.id.test_image);
            testLabel = view.findViewById(R.id.test_label);
            predictionLabel = view.findViewById(R.id.prediction_label);
            predictionScore = view.findViewById(R.id.prediction_score);
            predictionImageView = view.findViewById(R.id.prediction_image);

        }
    }
}