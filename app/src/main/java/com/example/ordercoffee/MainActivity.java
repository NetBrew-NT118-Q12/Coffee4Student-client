package com.example.ordercoffee;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    EditText edtEmail, edtPassword;
    Button btnSignIn, btnGoogle, btnFacebook;
    TextView txtSignup, txtForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        btnGoogle = findViewById(R.id.btnGoogle);
        btnFacebook = findViewById(R.id.btnFacebook);
        txtSignup = findViewById(R.id.txtSignup);
        txtForgotPassword = findViewById(R.id.txtForgotPassword);

        // 🟢 Sự kiện Sign In
        btnSignIn.setOnClickListener(v -> signIn());

        // 🟢 Mở trang Sign Up
        txtSignup.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
    }

    private void signIn() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập Email và Mật khẩu", Toast.LENGTH_SHORT).show();
            return;
        }

        // Địa chỉ server Node.js (10.0.2.2 cho emulator, đổi thành IP nếu là máy thật)
        String url = "http://10.0.2.2:3000/login";

        // Tạo JSON body
        JSONObject loginData = new JSONObject();
        try {
            loginData.put("email", email);
            loginData.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi JSON", Toast.LENGTH_SHORT).show();
            return;
        }

        // Gửi request
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                loginData,
                response -> {
                    try {
                        boolean success = response.getBoolean("success");
                        if (success) {
                            String name = response.getString("name");
                            Toast.makeText(this, "Đăng nhập thành công! Xin chào " + name, Toast.LENGTH_SHORT).show();

                            // Chuyển sang activity khác (nếu muốn)
                            // startActivity(new Intent(this, HomeActivity.class));
                        } else {
                            Toast.makeText(this, response.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(this, "Phản hồi không hợp lệ từ server", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(this, "Không thể kết nối đến server", Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
                }
        );

        queue.add(request);
    }

    // Ẩn bàn phím khi chạm ngoài EditText
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}