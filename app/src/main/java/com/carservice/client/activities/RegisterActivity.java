package com.carservice.client.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;

import com.carservice.client.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText etFirstName, etLastName, etEmail, etPhone, etPassword;
    ImageView btnTogglePassword;
    Button btnRegister;
    FirebaseAuth auth;
    FirebaseFirestore db;

    TextView ruleLength, ruleUpper, ruleLower, ruleNumber, ruleSpecial, tvPasswordStrength;
    RadioGroup rgVerificationMethod;
    RadioButton rbEmailOTP, rbPhoneOTP;
    View strengthBar;

    boolean passwordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        btnTogglePassword = findViewById(R.id.btnTogglePassword);
        btnRegister = findViewById(R.id.btnRegister);

        // Password rule labels
        ruleLength = findViewById(R.id.ruleLength);
        ruleUpper = findViewById(R.id.ruleUpper);
        ruleLower = findViewById(R.id.ruleLower);
        ruleNumber = findViewById(R.id.ruleNumber);
        ruleSpecial = findViewById(R.id.ruleSpecial);

        // Strength meter widgets
        tvPasswordStrength = findViewById(R.id.tvPasswordStrength);
        strengthBar = findViewById(R.id.strengthBar);

        // Verification method radio
        rgVerificationMethod = findViewById(R.id.rgVerificationMethod);
        rbEmailOTP = findViewById(R.id.rbEmailOTP);
        rbPhoneOTP = findViewById(R.id.rbPhoneOTP);

        // Toggle show password
        btnTogglePassword.setOnClickListener(v -> {
            passwordVisible = !passwordVisible;
            etPassword.setInputType(passwordVisible ?
                    InputType.TYPE_CLASS_TEXT :
                    InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            etPassword.setSelection(etPassword.getText().length());
        });

        // Password strength
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validatePasswordRules(s.toString());
                updateStrengthBar(s.toString());
            }
        });

        btnRegister.setOnClickListener(v -> registerUser());
    }

    private boolean isValidName(String name) {
        return name.matches("^[A-Z][a-zA-Z' -]*$");
    }

    private boolean isValidPhone(String phone) {
        return phone.matches("^0[6-8][0-9]{8}$");
    }

    private boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPasswordValid(String pass) {
        return pass.length() >= 6 && pass.length() <= 15 &&
                pass.matches(".*[A-Z].*") &&
                pass.matches(".*[a-z].*") &&
                pass.matches(".*[0-9].*") &&
                pass.matches(".*[!@#$%^&*].*");
    }

    private void validatePasswordRules(String pass) {
        setRuleColor(ruleLength, pass.length() >= 6 && pass.length() <= 15);
        setRuleColor(ruleUpper, pass.matches(".*[A-Z].*"));
        setRuleColor(ruleLower, pass.matches(".*[a-z].*"));
        setRuleColor(ruleNumber, pass.matches(".*[0-9].*"));
        setRuleColor(ruleSpecial, pass.matches(".*[!@#$%^&*].*"));
    }

    private void updateStrengthBar(String pass) {
        int strength = 0;
        if (pass.length() >= 6) strength++;
        if (pass.matches(".*[A-Z].*")) strength++;
        if (pass.matches(".*[a-z].*")) strength++;
        if (pass.matches(".*[0-9].*")) strength++;
        if (pass.matches(".*[!@#$%^&*].*")) strength++;

        if (strength <= 2) {
            tvPasswordStrength.setText("Weak");
            strengthBar.setBackgroundColor(Color.parseColor("#FF0000"));
        } else if (strength <= 4) {
            tvPasswordStrength.setText("Medium");
            strengthBar.setBackgroundColor(Color.parseColor("#FFA500"));
        } else {
            tvPasswordStrength.setText("Strong");
            strengthBar.setBackgroundColor(Color.parseColor("#008000"));
        }
    }

    private void setRuleColor(TextView tv, boolean ok) {
        tv.setTextColor(ok ? Color.parseColor("#008000") : Color.parseColor("#FF0000"));
    }

    private void registerUser() {
        String first = etFirstName.getText().toString().trim();
        String last = etLastName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validation
        if (!isValidName(first)) { etFirstName.setError("Invalid name"); return; }
        if (!isValidName(last)) { etLastName.setError("Invalid surname"); return; }
        if (!isEmailValid(email)) { etEmail.setError("Invalid email"); return; }
        if (!isValidPhone(phone)) { etPhone.setError("Invalid phone"); return; }
        if (!isPasswordValid(password)) { Toast.makeText(this, "Password must meet requirements", Toast.LENGTH_SHORT).show(); return; }

        // Determine OTP method
        boolean emailMethod = rbEmailOTP.isChecked();

        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> {
                    String uid = result.getUser().getUid();

                    HashMap<String, Object> user = new HashMap<>();
                    user.put("uid", uid);
                    user.put("firstname", first);
                    user.put("lastname", last);
                    user.put("email", email);
                    user.put("phone", phone);
                    user.put("role", "client");

                    db.collection("users").document(uid)
                            .set(user)
                            .addOnSuccessListener(r -> {

                                if (emailMethod) {
                                    auth.getCurrentUser().sendEmailVerification();
                                    Toast.makeText(this, "Account created! Verify email to continue", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(this, "Phone OTP (future upgrade — Firebase paid tier)", Toast.LENGTH_LONG).show();
                                }

                                startActivity(new Intent(this, DashboardActivity.class));
                                finish();
                            });
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }
}
