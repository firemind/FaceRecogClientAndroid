package ch.hsr.apps.facerecognition.adapters;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import ch.hsr.apps.facerecognition.R;
import ch.hsr.apps.facerecognition.data.FaceData;
import ch.hsr.apps.facerecognition.data.FaceRepository;

/**
 * Created by viruch on 27.09.17.
 */

public class FaceAdapter extends RecyclerView.Adapter<FaceAdapter.ViewHolder> {
    private final FaceAction action;
    private final List<FaceData> galleryList;
    private final Activity activity;
    private final String notPredictedMessage;
    private final int image_width;
    private final int image_height;
    private final Drawable personPlaceholderImage;

    public FaceAdapter(Activity activity, FaceRepository repo, FaceAction action) {
        this.activity = activity;
        this.action = action;
        galleryList = repo.getAll();
        repo.registerListener(new FaceRepository.RepoListener() {
            @Override
            public void onDelete(final String s) {
                activity.runOnUiThread(() -> {
                    for (int i = 0; i < galleryList.size(); i++) {
                        FaceData f = galleryList.get(i);
                        if (f.getId().equals(s)) {
                            galleryList.remove(i);
                            notifyItemRemoved(i);
                            break;
                        }
                    }
                });
            }

            @Override
            public void onModify(final FaceData faceData) {
                activity.runOnUiThread(() -> {
                    for (int i = 0; i < galleryList.size(); i++) {
                        FaceData f = galleryList.get(i);
                        if (f.compareTo(faceData) == 0) {
                            galleryList.set(i, faceData);
                            notifyItemChanged(i);
                            return;
                        }
                    }

                    galleryList.add(0, faceData);
                    notifyItemInserted(0);
                    action.onFaceInserted(0);
                });
            }
        });
        Resources res = activity.getResources();
        personPlaceholderImage = res.getDrawable(R.drawable.person_placeholder);
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
        holder.actionRepredict
                .setOnClickListener((view) -> action.onFaceRepredict(galleryList.get(i)));
        holder.actionDelete
                .setOnClickListener((view) -> action.onFaceDelete(galleryList.get(i)));

    }

    private void renderToImageView(Uri uri, ImageView testImageView) {
        Picasso.with(activity)
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
        private ImageButton actionRepredict;
        private ImageButton actionDelete;

        ViewHolder(View view) {
            super(view);

            testImageView = view.findViewById(R.id.test_image);
            testLabel = view.findViewById(R.id.test_label);
            predictionLabel = view.findViewById(R.id.prediction_label);
            predictionScore = view.findViewById(R.id.prediction_score);
            predictionImageView = view.findViewById(R.id.prediction_image);
            actionRepredict = view.findViewById(R.id.action_repredict_face);
            actionDelete = view.findViewById(R.id.action_delete_face);
        }
    }
}
