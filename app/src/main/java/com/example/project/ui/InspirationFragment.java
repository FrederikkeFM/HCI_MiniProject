package com.example.project.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.project.R;
import com.example.project.databinding.FragmentInspirationBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.ByteArrayInputStream;
import java.util.Base64;
import java.util.List;


public class InspirationFragment extends Fragment {


    private static final String TAG = "InspirationFragment";
    private FragmentInspirationBinding binding;

    private LinearLayout mainLayout;

    private String queryString;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentInspirationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mainLayout = (LinearLayout) root.findViewById(R.id.inspirationLayout);
        loadData(mainLayout);

        final FloatingActionButton btn = root.findViewById(R.id.floatingActionButton);
        btn.setOnClickListener(view -> {
            queryString = null;
            initQRCodeScanner();
        });
        return root;
    }

    private void loadData(LinearLayout layout) {
        Log.d(TAG, "Loading data from firestore");

        // Remove all existing workouts
        mainLayout.removeAllViews();

        // Query workouts - if QR code found a workout id find that or else find all workouts
        // Should only retrieve a limited number of workouts at a time to save network
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection("Workouts");
        if (queryString != null) {
            Log.d(TAG, "loadData: by documentId = " + queryString);
            query = query.whereEqualTo(FieldPath.documentId(), queryString);
        }

        // Retrieve all matching workouts
        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                View workoutView = InspirationFragment.this.createWorkoutView(document);
                                layout.addView(workoutView);
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    @NonNull
    private View createWorkoutView(QueryDocumentSnapshot document) {
        View inflater = LayoutInflater.from(getContext()).inflate(R.layout.layout_workout_element, null);
        TextView txt = (TextView) inflater.findViewById(R.id.workoutTitle);
        txt.setText(document.getString("title"));

        TextView txtDescription = inflater.findViewById(R.id.workoutDescription);
        txtDescription.setText(document.getString("howto"));

        TextView txtTags = inflater.findViewById(R.id.workoutTags);
        List<String> tags = (List<String>) document.get("musclegroup");
        txtTags.setText(String.join(", ", tags));

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Log.d(TAG, "Read and parse image thumbnail");
            String encodedImg = document.get("thumbnail").toString();
            if (encodedImg != null && encodedImg.length() > 0) {
                byte[] imgData = new byte[0];
                imgData = Base64.getDecoder().decode(encodedImg);
                ByteArrayInputStream bytes = new ByteArrayInputStream(imgData);
                BitmapDrawable bmd = new BitmapDrawable(bytes);
                Bitmap bmp = bmd.getBitmap();

                ImageView img = inflater.findViewById(R.id.workoutImage);
                img.setImageBitmap(bmp);
            }

        }
        return inflater;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void initQRCodeScanner() {
        // Initialize QR code scanner here
        IntentIntegrator.forSupportFragment(this)
                .setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
                .setOrientationLocked(false)
                .setPrompt("Scan a QR code")
                .initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Log.d(TAG, "onActivityResult: scan cancelled");
                queryString = null;
            } else {
                Log.d(TAG, "onActivityResult: scanned id " + result.getContents());
                queryString = result.getContents();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
        loadData(mainLayout);
    }

}