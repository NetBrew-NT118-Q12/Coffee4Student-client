package com.example.ordercoffee.untils;

import android.util.Patterns;
public class Validator {
    public static boolean isEmail(String input) {
        return Patterns.EMAIL_ADDRESS.matcher(input).matches();
    }

    public static boolean isPhone(String input) {
        return Patterns.PHONE.matcher(input).matches();
    }
}
