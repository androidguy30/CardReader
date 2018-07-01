package cardreader.sunplus.com.cardreader;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;
import com.wonderkiln.camerakit.CameraKitEventCallback;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraView;

import java.util.List;

import cardreader.sunplus.com.cardreader.utils.AppConstants;
import cardreader.sunplus.com.cardreader.utils.AppUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, CameraKitEventCallback<CameraKitImage> {

    CameraView cameraView;
    FloatingActionButton captureButton;
    AppUtils appUtils;
    Dialog dialog;
    String name, number, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    public void initViews() {
        appUtils = new AppUtils();
        cameraView = findViewById(R.id.cameraView);
        captureButton = findViewById(R.id.captureButton);
        captureButton.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();
    }

    @Override
    protected void onPause() {
        cameraView.stopVideo();
        super.onPause();
    }

    @Override
    public void onClick(View view) {
        showDialog();
        cameraView.captureImage(this);
    }


    @Override
    public void callback(CameraKitImage cameraKitImage) {
        FirebaseVisionImage visionImage = FirebaseVisionImage.fromBitmap(cameraKitImage.getBitmap());
        FirebaseVisionTextDetector textDetector = FirebaseVision.getInstance().getVisionTextDetector();
        textDetector.detectInImage(visionImage)
                .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                    @Override
                    public void onSuccess(FirebaseVisionText firebaseVisionText) {
                        handleText(firebaseVisionText);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "" + e, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void handleText(FirebaseVisionText textData) {
        List<FirebaseVisionText.Block> blocksData = textData.getBlocks();
        if (blocksData.size() == 0) {
            dismissDialog();
            Snackbar.make(cameraView, "No Text Found", Snackbar.LENGTH_LONG).show();
            return;
        }
        String tempString = "";
        for (int i = 0; i < blocksData.size(); i++) {
            List<FirebaseVisionText.Line> lines = blocksData.get(i).getLines();
            for (int j = 0; j < lines.size(); j++) {
                List<FirebaseVisionText.Element> elements = lines.get(j).getElements();
                for (int k = 0; k < elements.size(); k++) {
                    if (AppUtils.extractEmail(elements.get(k).getText()).isEmpty()) {
                        email = elements.get(k).getText();
                    } else if (AppUtils.extractName(elements.get(k).getText()).isEmpty()) {
                        this.name = elements.get(k).getText();
                    } else if (AppUtils.extractPhone(elements.get(k).getText()).isEmpty()) {
                        this.number = elements.get(k).getText();
                    }
                    tempString = elements.get(k).getText() + " " + tempString;
                }
            }
        }
        Toast.makeText(MainActivity.this, tempString, Toast.LENGTH_SHORT).show();
        dismissDialog();
        MyBottomSheet bottomSheet = new MyBottomSheet();

        Bundle bundle = new Bundle();
        bundle.putString(AppConstants.KEY_PHONE_NUMBER, number);
        bundle.putString(AppConstants.KEY_EMAIL_NUMBER, email);
        bundle.putString(AppConstants.KEY_NAME, name);
        bottomSheet.setArguments(bundle);


        //.show(getSupportFragmentManager(), "CustomDialog");
    }


    public void showDialog() {
        dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.progress_layout);
        dialog.setCancelable(false);
        dialog.show();
    }

    public void dismissDialog() {
        dialog.dismiss();
    }


}
