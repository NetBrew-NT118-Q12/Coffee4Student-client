package com.example.ordercoffee;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import android.os.StrictMode;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;
import java.util.Scanner;
public class SignUpActivity extends AppCompatActivity {
    TextInputEditText editEmailPhone, editFullName, editDate, editPassword;
    Button btnSignUp;
    ImageButton imbtnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        editEmailPhone = findViewById(R.id.editEmailPhone);
        editFullName = findViewById(R.id.editFullName);
        editDate = findViewById(R.id.editDate);
        editPassword = findViewById(R.id.editPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        imbtnBack = findViewById(R.id.btnBack);

        // Cho phép network chạy tạm thời trong main thread (demo)
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        btnSignUp.setOnClickListener(v -> signUp());

        imbtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void signUp() {
        new Thread(() -> { // Dùng thread riêng để tránh NetworkOnMainThreadException
            try {
                String input = editEmailPhone.getText().toString().trim();
                String fullName = editFullName.getText().toString().trim();
                String dob = editDate.getText().toString().trim();
                String password = editPassword.getText().toString().trim();

                // Xác định loại dữ liệu email hoặc phone
                String email = null, phone = null;
                if (android.util.Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
                    email = input;
                } else if (android.util.Patterns.PHONE.matcher(input).matches()) {
                    phone = input;
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(this, "Email hoặc số điện thoại không hợp lệ", Toast.LENGTH_SHORT).show()
                    );
                    return;
                }

                URL url = new URL("http://10.0.2.2:3000/signup");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);

                // Tạo JSON body
                JSONObject json = new JSONObject();
                json.put("email", email);
                json.put("phone", phone);
                json.put("full_name", fullName);
                json.put("dob", dob);
                json.put("password", password);

                Log.d("SIGNUP_JSON", json.toString());

                // Gửi dữ liệu
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] inputBytes = json.toString().getBytes("UTF-8");
                    os.write(inputBytes, 0, inputBytes.length);
                    os.flush();
                }

                int responseCode = conn.getResponseCode();
                InputStream inputStream = (responseCode >= 200 && responseCode < 300)
                        ? conn.getInputStream()
                        : conn.getErrorStream();

                Scanner in = new Scanner(inputStream);
                StringBuilder response = new StringBuilder();
                while (in.hasNext()) response.append(in.nextLine());
                in.close();

                Log.d("SERVER_RESPONSE", response.toString());

                runOnUiThread(() -> {
                    if (responseCode == 200) {
                        Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Đăng ký thất bại: " + responseCode, Toast.LENGTH_SHORT).show();
                    }
                });

                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }
}
