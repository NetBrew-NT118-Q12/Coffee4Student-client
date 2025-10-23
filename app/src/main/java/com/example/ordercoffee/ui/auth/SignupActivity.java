package com.example.ordercoffee.ui.auth;

import android.os.Bundle;
import android.widget.Toast;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ordercoffee.R;
import com.example.ordercoffee.data.network.ApiEndpoints;
import com.example.ordercoffee.data.network.ApiRequest;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

public class SignupActivity extends AppCompatActivity {

    private TextInputEditText editEmail, editPhone, editFullName, editDate, editPassword;
    private Button btnSignUp;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        editEmail = findViewById(R.id.editEmail);
        editPhone = findViewById(R.id.editPhone);
        editFullName = findViewById(R.id.editFullName);
        editDate = findViewById(R.id.editDate);
        editPassword = findViewById(R.id.editPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());
        btnSignUp.setOnClickListener(v -> signUp());
    }

    private void signUp() {
        String email = editEmail.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();
        String fullName = editFullName.getText().toString().trim();
        String dob = editDate.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        if (email.isEmpty() || phone.isEmpty() || fullName.isEmpty() || dob.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject body = new JSONObject();
        try {
            body.put("email", email);
            body.put("phone", phone);
            body.put("full_name", fullName);
            body.put("dob", dob);
            body.put("password", password);
        } catch (JSONException e) {
            Toast.makeText(this, "Lỗi tạo JSON", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiRequest.post(this, ApiEndpoints.SIGNUP_URL, body,
                response -> {
                    Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                    //finish(); // Quay lại màn hình đăng nhập
                },
                error -> {
                    Toast.makeText(this, "Lỗi kết nối hoặc đăng ký thất bại", Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
                });
    }
}
