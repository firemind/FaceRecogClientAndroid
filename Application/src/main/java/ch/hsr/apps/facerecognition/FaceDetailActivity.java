package ch.hsr.apps.facerecognition;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;

import ch.hsr.apps.facerecognition.data.FaceData;
import ch.hsr.apps.facerecognition.data.FaceRepository;
import ch.hsr.apps.facerecognition.data.FaceService;

public class FaceDetailActivity extends AppCompatActivity {
    public static final String FACE_ID = "faceId";

    private ImageView testImageView;
    private TextView testTag;
    private TextView predictionScore;
    private TextView predictionLabel;
    private ImageView predictionImageView;
    private Button submitTagButton;
    private Switch isPredictionCorrect;
    private FaceRepository repository;
    private FaceData face;
    private int imageWidth;
    private int imageHeight;
    private TextInputLayout testTagLabel;
    private FaceService service;
    private ProgressDialog dlg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_detail);

        service = new FaceService();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        testImageView = findViewById(R.id.test_image);
        testTag = findViewById(R.id.test_tag);
        testTagLabel = findViewById(R.id.test_tag_label);
        predictionScore = findViewById(R.id.prediction_score);
        predictionLabel = findViewById(R.id.prediction_label);
        predictionImageView = findViewById(R.id.prediction_image);
        submitTagButton = findViewById(R.id.action_submit_tag);
        isPredictionCorrect = findViewById(R.id.switch_correct_prediction);

        isPredictionCorrect.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                testTagLabel.setVisibility(View.INVISIBLE);
            } else {
                testTagLabel.setVisibility(View.VISIBLE);
            }
        });

        submitTagButton.setOnClickListener(view -> submit());

        imageHeight = (int) getResources().getDimension(R.dimen.image_detail_heigth);
        imageWidth = (int) getResources().getDimension(R.dimen.image_detail_width);

        final String faceId = getIntent().getStringExtra(FACE_ID);
        if (faceId == null || faceId.isEmpty()) {
            throw new RuntimeException("Missing Face Id");
        }

        repository = FaceRepository.getFaceRepository(this);
        face = repository.find(faceId);
        populate();
    }

    private void submit() {
        String label = isPredictionCorrect.isChecked() ?
                face.getPredictionLabel() :
                testTag.getText().toString();

        dlg = new ProgressDialog(this);
        dlg.setMessage(getString(R.string.message_storing_face));
        dlg.show();
        service.submitFace(face, FaceDetailActivity.this,
                label, new FaceService.ClassifyCallback() {
                    @Override
                    public void onFailure(IOException e) {
                        if (dlg.isShowing()) {
                            dlg.dismiss();
                        }
                        showError(e);
                    }

                    @Override
                    public void onSuccess(FaceData faceData) {
                        if (dlg.isShowing()) {
                            dlg.dismiss();
                        }
                        terminate();
                    }
                });
    }

    private void terminate() {
        finish();
    }

    private void populate() {
        String label = face.getPredictionLabel();
        renderToImageView(Uri.fromFile(face.getImageFile()), testImageView);

        if (label == null) {
            predictionScore.setVisibility(View.INVISIBLE);
            isPredictionCorrect.setChecked(false);
            isPredictionCorrect.setVisibility(View.INVISIBLE);
        } else {
            predictionScore.setVisibility(View.VISIBLE);
            predictionScore.setText("" + (int) (face.getPredictionScore() * 100.) + "%");
            predictionLabel.setText(label);
            isPredictionCorrect.setVisibility(View.VISIBLE);
            testTagLabel.setVisibility(View.INVISIBLE);
        }

        Uri predictionImageUri = face.getPredictionImageUri();
        if (predictionImageUri != null) {
            renderToImageView(predictionImageUri, predictionImageView);
        }
    }


    private void renderToImageView(Uri uri, ImageView testImageView) {
        Picasso.with(this)
                .load(uri)
                .resize(imageWidth, imageHeight)
                .centerCrop()
                .into(testImageView);
    }


    protected void showError(IOException e) {
        Snackbar.make(predictionImageView, e.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
    }
}
