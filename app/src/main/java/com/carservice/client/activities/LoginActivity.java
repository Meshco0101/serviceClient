package com.carservice.client.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.*;

import com.carservice.client.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

public class LoginActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    ImageView btnTogglePass;
    Button btnLogin;
    TextView tvRegister, btnForgotPassword;

    FirebaseAuth auth;
    FirebaseFirestore db;

    boolean passwordVisible = false;

    SharedPreferences prefs;
    int failedAttempts = 0;
    long lockEndTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        failedAttempts = prefs.getInt("failedAttempts", 0);
        lockEndTime = prefs.getLong("lockEndTime", 0);

        // UI elements
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnTogglePass = findViewById(R.id.btnTogglePassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnForgotPassword = findViewById(R.id.btnForgotPassword);
        tvRegister = findViewById(R.id.tvRegister);

        checkLockStatus();

        // Password show/hide icon
        btnTogglePass.setOnClickListener(v -> {
            passwordVisible = !passwordVisible;
            etPassword.setInputType(passwordVisible ?
                    InputType.TYPE_CLASS_TEXT :
                    InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            etPassword.setSelection(etPassword.getText().length());
        });

        // Login button
        btnLogin.setOnClickListener(v -> loginUser());

        // Forgot Password
        btnForgotPassword.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class)));


        // Go to register screen
        tvRegister.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));

        //btnTogglePass.setOnClickListener(v ->
                //startActivity(new  Intent(LoginActivity.this, ForgotPasswordActivity.class)));
    }

    private void checkLockStatus() {
        long now = System.currentTimeMillis();

        if (now < lockEndTime) {
            long secondsLeft = (lockEndTime - now) / 1000;
            btnLogin.setEnabled(false);
            btnLogin.setText("Locked (" + secondsLeft + "s)");
            btnLogin.setBackgroundColor(Color.GRAY);

            new android.os.Handler().postDelayed(this::checkLockStatus, 1000);
        } else {
            btnLogin.setEnabled(true);
            btnLogin.setText("Login");
            prefs.edit().putInt("failedAttempts", 0).apply();
        }
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter a valid email (example@email.com)");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password required");
            return;
        }

        long now = System.currentTimeMillis();
        if (now < lockEndTime) {
            Toast.makeText(this, "Account locked. Try again later", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    prefs.edit().putInt("failedAttempts", 0).apply();

                    FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {
                        String uid = authResult.getUser().getUid();
                        db.collection("users").document(uid).update("fcmToken", token);
                    });

                    Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    failedAttempts++;
                    prefs.edit().putInt("failedAttempts", failedAttempts).apply();

                    if (failedAttempts >= 5) {
                        lockEndTime = System.currentTimeMillis() + (60 * 1000); // 1 min
                        prefs.edit().putLong("lockEndTime", lockEndTime).apply();
                        Toast.makeText(this, "Too many attempts. Locked 1 minute.", Toast.LENGTH_LONG).show();
                        checkLockStatus();
                    } else {
                        Toast.makeText(this, "Login failed (" + failedAttempts + "/5)", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendPasswordReset() {
        String email = etEmail.getText().toString();

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter email to reset");
            return;
        }

        auth.sendPasswordResetEmail(email)
                .addOnSuccessListener(a ->
                        Toast.makeText(this, "Reset link sent", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }
}
