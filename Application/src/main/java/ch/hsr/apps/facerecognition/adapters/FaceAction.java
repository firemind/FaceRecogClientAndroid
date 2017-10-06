package ch.hsr.apps.facerecognition.adapters;

/**
 * Created by viruch on 06.10.17.
 */

public interface FaceAction {
    void onFaceRepredict(String id);
    void onFaceInserted(int position);

    void onFaceDelete(String id);
}
