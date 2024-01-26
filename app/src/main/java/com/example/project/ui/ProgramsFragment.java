package com.example.project.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.project.R;
import com.example.project.databinding.FragmentProgramsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Map;

public class ProgramsFragment extends Fragment {

    private static final String TAG = "ProgramsFragment";

    private FragmentProgramsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentProgramsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final LinearLayout trainingLayout = (LinearLayout) root.findViewById(R.id.trainingLayout);
        loadData(trainingLayout);
        return root;
    }

    private void loadData(LinearLayout layout) {
        Log.d(TAG, "Loading data from firestore");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Training")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                View programView = LayoutInflater.from(getContext()).inflate(R.layout.layout_training_element, null);

                                TextView pgmName = (TextView) programView.findViewById(R.id.pgmName);
                                pgmName.setText(document.getString("name"));

                                TableLayout pgmTable = (TableLayout) programView.findViewById(R.id.pgmTable);

                                List<Map<String, Object>> exercises = (List<Map<String, Object>>) document.get("exercise");
                                for (Map<String, Object> exercise : exercises) {
                                    TableRow row = new TableRow(getActivity());

                                    TextView col1 = new TextView(getActivity());
                                    col1.setTextAppearance(R.style.TableCell);
                                    col1.setText(exercise.get("name").toString());
                                    row.addView(col1);

                                    TextView col2 = new TextView(getActivity());
                                    col2.setTextAppearance(R.style.TableCell);
                                    col2.setText(exercise.get("sets").toString());
                                    row.addView(col2);

                                    TextView col3 = new TextView(getActivity());
                                    col3.setTextAppearance(R.style.TableCell);
                                    col3.setText(exercise.get("repetitions").toString());
                                    row.addView(col3);

                                    TextView col4 = new TextView(getActivity());
                                    col4.setTextAppearance(R.style.TableCell);
                                    col4.setText(exercise.get("weight").toString() + " kg");
                                    row.addView(col4);

                                    pgmTable.addView(row);

                                }


                                layout.addView(programView);
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}