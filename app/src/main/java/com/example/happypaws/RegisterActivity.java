package com.example.happypaws;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText nameEditText, usernameEditText, passwordEditText;
    private SQLiteDatabase db;
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

        db = getBaseContext().openOrCreateDatabase("app.db", MODE_PRIVATE, null);
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        registerButton.setOnClickListener(v -> registerUser());

        // Обработчик для перехода на страницу авторизации
        loginRedirectButton.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Удаляем RegisterActivity из стека
            startActivity(intent);
            finish();
        });
    }

    private void registerUser() {
        String name = nameEditText.getText().toString().trim();
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (name.isEmpty() || username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        db.execSQL("INSERT INTO People (Name, username, password) VALUES (?, ?, ?)", new Object[]{name, username, password});

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isRegistered", true);
        editor.apply();

        Toast.makeText(this, "Регистрация успешна! Теперь войдите.", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Удаляем RegisterActivity из стека
        startActivity(intent);
        finish();
    }
}
