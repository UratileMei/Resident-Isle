package com.coderisle.residentisle.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.coderisle.residentisle.MainActivity;
import com.coderisle.residentisle.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private TextInputLayout tilFullName, tilEmail, tilPassword, tilConfirmPassword;
    private TextInputEditText etEmail, etPassword, etConfirmPassword, etFullName;
    private TextView tvForgotPassword;
    private MaterialButton btnSubmit;

    private boolean isSignUpMode = false;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        tabLayout = findViewById(R.id.tabLayout);
        tilFullName = findViewById(R.id.tilFullName);
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        btnSubmit = findViewById(R.id.btnSubmit);

        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        setupTabLayout();
        btnSubmit.setOnClickListener(v -> authentication());
    }

    private void setupTabLayout() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getText() == null) return;
                String tabName = tab.getText().toString();

                if (tabName.equals("Login")) {
                    showLoginFields();
                    isSignUpMode = false;
                } else if (tabName.equals("Sign Up")) {
                    showSignUpFields();
                    isSignUpMode = true;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
    }

    private void showLoginFields() {
        tilFullName.setVisibility(View.GONE);
        tilConfirmPassword.setVisibility(View.GONE);
        tvForgotPassword.setVisibility(View.VISIBLE);
        btnSubmit.setText("Login");
    }

    private void showSignUpFields() {
        tilFullName.setVisibility(View.VISIBLE);
        tilConfirmPassword.setVisibility(View.VISIBLE);
        tvForgotPassword.setVisibility(View.GONE);
        btnSubmit.setText("Sign Up");
    }

    private void authentication() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isSignUpMode) {
            String fullName = etFullName.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            if (fullName.isEmpty()) {
                Toast.makeText(this, "Please enter your full name", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            // === Firebase Sign Up ===
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user == null) return;

                        Map<String, Object> userData = new HashMap<>();
                        userData.put("userId", user.getUid());
                        userData.put("fullName", fullName);
                        userData.put("email", email);
                        userData.put("role", "citizen");
                        userData.put("createdAt", FieldValue.serverTimestamp());

                        firestore.collection("users")
                                .document(user.getUid())
                                .set(userData)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show();
                                    moveToMain("citizen");
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(this, "Error saving user data: " + e.getMessage(), Toast.LENGTH_LONG).show());
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Registration failed: " + e.getMessage(), Toast.LENGTH_LONG).show());

        } else {
            // === Firebase Login ===
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user == null) return;

                        firestore.collection("users")
                                .document(user.getUid())
                                .get()
                                .addOnSuccessListener(snapshot -> {
                                    if (snapshot.exists()) {
                                        String role = snapshot.getString("role");
                                        moveToMain(role != null ? role : "citizen");
                                    } else {
                                        moveToMain("citizen");
                                    }
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(this, "Error fetching user data", Toast.LENGTH_LONG).show());
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Login failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
        }
    }

    private void moveToMain(String role) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("userRole", role);
        startActivity(intent);
        finish();
    }
}

