package com.example.project.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.project.R;
import com.example.project.databinding.FragmentNewprogramBinding;

public class NewProgramFragment extends Fragment {

    private static final String TAG = "NewProgramFragment";

    private FragmentNewprogramBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentNewprogramBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        EditText pgmNameInp = root.findViewById(R.id.pgminput);
        EditText exerciseNameInp = root.findViewById(R.id.exerinput);
        EditText numberOfSetsInp = root.findViewById(R.id.setinput);
        EditText numberOfRepsInp = root.findViewById(R.id.repinput);
        EditText weightInp = root.findViewById(R.id.weightinput);

        Button btn = root.findViewById(R.id.savepmg);
        btn.setOnClickListener(event -> {
            String pgmName = pgmNameInp.getText().toString();
            String exerciseName = exerciseNameInp.getText().toString();
            int numberOfSets = Integer.parseInt(numberOfSetsInp.getText().toString());
            int numberOfReps = Integer.parseInt(numberOfRepsInp.getText().toString());
            int weight = Integer.parseInt(weightInp.getText().toString());

            Log.d(TAG, "onCreateView: save new program to database : " + pgmName + ", " + exerciseName + ", " + numberOfSets + ", " + numberOfReps + ", " + weight);

            Toast.makeText(getContext(), "Program saved", Toast.LENGTH_LONG).show();
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}