package com.coderisle.residentisle.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.coderisle.residentisle.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ReportIssueFragment extends Fragment {

    private TextInputLayout tilCategory, tilTitle, tilDescription, tilLocation;
    private AutoCompleteTextView actvCategory;
    private TextInputEditText etTitle, etDescription, etLocation;
    private RadioGroup rgPriority;
    private ProgressBar progressBar;
    private Button btnSubmit;

    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_report_issue, container, false);

        // Initialize Firebase
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Initialize views
        tilCategory = view.findViewById(R.id.tilCategory);
        tilTitle = view.findViewById(R.id.tilTitle);
        tilDescription = view.findViewById(R.id.tilDescription);
        tilLocation = view.findViewById(R.id.tilLocation);
        actvCategory = view.findViewById(R.id.actvCategory);
        etTitle = view.findViewById(R.id.etTitle);
        etDescription = view.findViewById(R.id.etDescription);
        etLocation = view.findViewById(R.id.etLocation);
        rgPriority = view.findViewById(R.id.rgPriority);
        progressBar = view.findViewById(R.id.progressBar);
        btnSubmit = view.findViewById(R.id.btnSubmit);

        setupCategoryDropdown();

        btnSubmit.setOnClickListener(v -> submitReport());

        return view;
    }

    private void setupCategoryDropdown() {
        String[] categories = {"Water Issue", "Electricity", "Road Damage", "Waste Collection", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, categories);
        actvCategory.setAdapter(adapter);
    }

    private void submitReport() {
        String category = actvCategory.getText().toString().trim();
        String title = etTitle.getText() != null ? etTitle.getText().toString().trim() : "";
        String description = etDescription.getText() != null ? etDescription.getText().toString().trim() : "";
        String location = etLocation.getText() != null ? etLocation.getText().toString().trim() : "";

        int selectedPriorityId = rgPriority.getCheckedRadioButtonId();
        RadioButton selectedPriorityButton = selectedPriorityId != -1
                ? getView().findViewById(selectedPriorityId)
                : null;
        String priority = selectedPriorityButton != null ? selectedPriorityButton.getText().toString() : "Medium";

        if (category.isEmpty() || title.isEmpty() || description.isEmpty() || location.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnSubmit.setEnabled(false);

        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : "anonymous";
        String reportId = UUID.randomUUID().toString();

        Map<String, Object> reportData = new HashMap<>();
        reportData.put("reportId", reportId);
        reportData.put("userId", userId);
        reportData.put("category", category);
        reportData.put("title", title);
        reportData.put("description", description);
        reportData.put("location", location);
        reportData.put("priority", priority);
        reportData.put("timestamp", System.currentTimeMillis());
        reportData.put("status", "Pending");

        firestore.collection("reports")
                .document(reportId)
                .set(reportData)
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    btnSubmit.setEnabled(true);
                    Toast.makeText(requireContext(), "Report submitted successfully!", Toast.LENGTH_SHORT).show();
                    clearFields();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    btnSubmit.setEnabled(true);
                    Toast.makeText(requireContext(), "Failed to submit report. Try again.", Toast.LENGTH_SHORT).show();
                });
    }

    private void clearFields() {
        actvCategory.setText("");
        etTitle.setText("");
        etDescription.setText("");
        etLocation.setText("");
        rgPriority.check(R.id.rbMedium);
    }
}
