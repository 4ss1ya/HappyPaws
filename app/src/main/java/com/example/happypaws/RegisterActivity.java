
        package com.example.happypaws;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RegisterActivity extends AppCompatActivity {

    private EditText nameEditText, usernameEditText, passwordEditText;
    private UserDatabaseHelper dbHelper;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nameEditText = findViewById(R.id.name);
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        Button registerButton = findViewById(R.id.registerButton);
        Button loginRedirectButton = findViewById(R.id.loginRedirectButton);

        dbHelper = new UserDatabaseHelper(this);
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        registerButton.setOnClickListener(v -> registerUser());

        loginRedirectButton.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void registerUser() {
        String name = nameEditText.getText().toString().trim();
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (name.isEmpty() ||  username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM People WHERE username = ?", new String[]{username});

        if (cursor.moveToFirst()) {
            Toast.makeText(this, "Этот логин уже занят!", Toast.LENGTH_SHORT).show();
            cursor.close();
            db.close();
            return;
        }
        cursor.close();

        String hashedPassword = hashPassword(password);
        if (hashedPassword.isEmpty()) {
            Toast.makeText(this, "Ошибка хеширования пароля!", Toast.LENGTH_SHORT).show();
            db.close();
            return;
        }

        ContentValues values = new ContentValues();
        values.put("Name", name);
        values.put("username", username);
        values.put("password", hashedPassword);

        long result = db.insert("People", null, values);
        db.close();

        if (result == -1) {
            Toast.makeText(this, "Ошибка регистрации!", Toast.LENGTH_SHORT).show();
        } else {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isRegistered", true);
            editor.putString("username", username);
            editor.apply();

            Toast.makeText(this, "Регистрация успешна! Добавьте питомца.", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, RegisterAnimalActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
            finish();
        }
    }


    private String hashPassword(String password) {
        if (password.isEmpty()) {
            return "";
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }
}