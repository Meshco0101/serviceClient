package com.carservice.client.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.carservice.client.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    EditText etFirstName, etLastName, etPhone, etEmail;
    Button btnSave, btnChangePassword;
    FirebaseAuth auth;
    FirebaseFirestore db;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        uid = auth.getUid();

        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        btnSave = findViewById(R.id.btnSave);
        btnChangePassword = findViewById(R.id.btnChangePassword);

        loadUserData();

        btnSave.setOnClickListener(v -> updateProfile());
        btnChangePassword.setOnClickListener(v -> updatePassword());
    }

    private void loadUserData() {
        db.collection("users").document(uid).get()
                .addOnSuccessListener(doc -> {
                    etFirstName.setText(doc.getString("firstname"));
                    etLastName.setText(doc.getString("lastname"));
                    etPhone.setText(doc.getString("phone"));
                    etEmail.setText(doc.getString("email"));
                });
    }

    private void updateProfile() {
        String first = etFirstName.getText().toString().trim();
        String last = etLastName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String newEmail = etEmail.getText().toString().trim();

        // Update Firestore
        Map<String, Object> map = new HashMap<>();
        map.put("firstname", first);
        map.put("lastname", last);
        map.put("phone", phone);
        map.put("email", newEmail);

        db.collection("users").document(uid).update(map);

        // Update Firebase Authentication Email
        assert auth.getCurrentUser() != null;
        if (auth.getCurrentUser() != null) {
            auth.getCurrentUser().verifyBeforeUpdateEmail(newEmail)
                    .addOnSuccessListener(unused ->
                            Toast.makeText(this, "Verification email sent to new address. Check your inbox!", Toast.LENGTH_LONG).show()
                    )
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Email update failed: " + e.getMessage(), Toast.LENGTH_LONG).show()
                    );
        }

    }

    private void updatePassword() {
        assert auth.getCurrentUser() != null;
        auth.sendPasswordResetEmail(Objects.requireNonNull(auth.getCurrentUser().getEmail()))
                .addOnSuccessListener(x ->
                        Toast.makeText(this, "Password reset email sent", Toast.LENGTH_SHORT).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }
}
