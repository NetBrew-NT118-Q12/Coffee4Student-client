package com.example.ordercoffee.ui.auth;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ordercoffee.R;
import com.example.ordercoffee.data.model.User;
import com.example.ordercoffee.data.network.ApiEndpoints;
import com.example.ordercoffee.data.network.ApiRequest;
import com.example.ordercoffee.ui.home.HomeActivity;
import com.example.ordercoffee.untils.SharedPref;
import com.example.ordercoffee.untils.Validator;

import org.json.JSONException;
import org.json.JSONObject;

public class SigninActivity extends AppCompatActivity {

    private EditText edtEmailPhone, edtPassword;
    private Button btnSignIn, btnGoogle, btnFacebook;
    private TextView txtSignup, txtForgotPassword;
    private SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        sharedPref = new SharedPref(this);
        sharedPref.clearUser(); //Xóa trạng thái đăng nhập

        // ✅ Kiểm tra nếu đã đăng nhập thì qua Home luôn
        User savedUser = sharedPref.getUser();
        if (savedUser != null && savedUser.getToken() != null) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
            return;
        }

        // Ánh xạ view
        edtEmailPhone = findViewById(R.id.edtEmailPhone);
        edtPassword = findViewById(R.id.edtPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        btnGoogle = findViewById(R.id.btnGoogle);
        btnFacebook = findViewById(R.id.btnFacebook);
        txtSignup = findViewById(R.id.txtSignup);
        txtForgotPassword = findViewById(R.id.txtForgotPassword);

        // Đăng nhập
        btnSignIn.setOnClickListener(v -> signIn());

        // Chuyển sang trang đăng ký
        txtSignup.setOnClickListener(v -> {
            Intent intent = new Intent(SigninActivity.this, SignupActivity.class);
            startActivity(intent);
        });
    }

    private void signIn() {
        String input = edtEmailPhone.getText().toString().trim();  // Có thể là email hoặc phone
        String password = edtPassword.getText().toString().trim();

        if (input.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập Email/Số điện thoại và Mật khẩu", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra xem input là email hay số điện thoại
        // Kiểm tra định dạng bằng Validator
        boolean isEmail = Validator.isEmail(input);
        boolean isPhone = Validator.isPhone(input);

        if (!isEmail && !isPhone) {
            Toast.makeText(this, "Định dạng không hợp lệ! Hãy nhập Email hoặc Số điện thoại", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject loginData = new JSONObject();
        try {
            if (isEmail) {
                loginData.put("email", input);
            } else {
                loginData.put("phone", input);
            }
            loginData.put("password", password);
        } catch (JSONException e) {
            Toast.makeText(this, "Lỗi tạo JSON", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiRequest.post(
                this,
                ApiEndpoints.LOGIN_URL,
                loginData,
                response -> {
                    try {
                        boolean success = response.getBoolean("success");
                        if (success) {
                            String id = response.optString("id", "");
                            String name = response.optString("name", "");
                            String email = response.optString("email", "");
                            String token = response.optString("token", "");

                            User user = new User(id, name, email, token);
                            sharedPref.saveUser(user);

                            Toast.makeText(this, "Đăng nhập thành công! Xin chào " + name, Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(this, HomeActivity.class));
                            finish();
                        } else {
                            Toast.makeText(this, response.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(this, "Phản hồi không hợp lệ", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(this, "Không thể kết nối server", Toast.LENGTH_SHORT).show();
                }
        );
    }


    // Ẩn bàn phím khi bấm ngoài EditText
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