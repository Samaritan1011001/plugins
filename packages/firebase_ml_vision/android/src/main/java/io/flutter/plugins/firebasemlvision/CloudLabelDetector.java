package io.flutter.plugins.firebasemlvision;

import android.support.annotation.NonNull;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.cloud.FirebaseVisionCloudDetectorOptions;
import com.google.firebase.ml.vision.cloud.label.FirebaseVisionCloudLabel;
import com.google.firebase.ml.vision.cloud.label.FirebaseVisionCloudLabelDetector;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import io.flutter.plugin.common.MethodChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class CloudLabelDetector implements Detector {
  public static final CloudLabelDetector instance = new CloudLabelDetector();

  private CloudLabelDetector() {}

  @Override
  public void handleDetection(
      FirebaseVisionImage image, Map<String, Object> options, final MethodChannel.Result result) {
    FirebaseVisionCloudDetectorOptions detectorOptions =
        FirebaseMlVisionPlugin.parseCloudDetectorOptions(options);

    FirebaseVisionCloudLabelDetector detector =
        FirebaseVision.getInstance().getVisionCloudLabelDetector(detectorOptions);

    detector
        .detectInImage(image)
        .addOnSuccessListener(
            new OnSuccessListener<List<FirebaseVisionCloudLabel>>() {
              @Override
              public void onSuccess(List<FirebaseVisionCloudLabel> firebaseVisionCloudLabels) {
                List<Map<String, Object>> labels =
                    new ArrayList<>(firebaseVisionCloudLabels.size());
                for (FirebaseVisionCloudLabel label : firebaseVisionCloudLabels) {
                  Map<String, Object> labelData = new HashMap<>();
                  labelData.put("confidence", (double) label.getConfidence());
                  labelData.put("entityId", label.getEntityId());
                  labelData.put("label", label.getLabel());

                  labels.add(labelData);
                }

                result.success(labels);
              }
            })
        .addOnFailureListener(
            new OnFailureListener() {
              @Override
              public void onFailure(@NonNull Exception e) {
                result.error("labelDetectorError", e.getLocalizedMessage(), null);
              }
            });
  }
}
