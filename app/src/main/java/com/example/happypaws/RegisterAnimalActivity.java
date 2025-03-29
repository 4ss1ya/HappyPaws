package com.example.happypaws;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterAnimalActivity extends AppCompatActivity {

    private EditText petNameEditText, animalTypeEditText, birthDateEditText, genderEditText;
    private Button registerAnimalButton;
    private UserDatabaseHelper dbHelper;
    private String username; // Логин владельца

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_animal);

        Log.d("RegisterAnimalActivity", "Активность запущена!");

        // Привязываем элементы интерфейса
        petNameEditText = findViewById(R.id.petNameEditText);
        animalTypeEditText = findViewById(R.id.animalTypeEditText);
        birthDateEditText = findViewById(R.id.birthDateEditText);
        genderEditText = findViewById(R.id.genderEditText);
        registerAnimalButton = findViewById(R.id.buttonRegisterAnimal);

        dbHelper = new UserDatabaseHelper(this);
        username = getIntent().getStringExtra("username"); // Получаем имя владельца

        if (username == null) {
            Toast.makeText(this, "Ошибка: нет данных о пользователе!", Toast.LENGTH_LONG).show();
            Log.e("RegisterAnimalActivity", "Ошибка: username = null");
            finish();
            return;
        }

        registerAnimalButton.setOnClickListener(v -> registerAnimal());
    }

    private void registerAnimal() {
        String petName = petNameEditText.getText().toString().trim();
        String animalType = animalTypeEditText.getText().toString().trim();
        String birthDate = birthDateEditText.getText().toString().trim();
        String gender = genderEditText.getText().toString().trim();

        if (petName.isEmpty() ||  animalType.isEmpty() || birthDate.isEmpty() || gender.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            Log.e("RegisterAnimalActivity", "Ошибка: не все поля заполнены!");
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Log.d("RegisterAnimal", "Ищем пользователя с username: " + username);
        Cursor cursor = db.rawQuery("SELECT id FROM People WHERE username = ?", new String[]{username});

        if (cursor.moveToFirst()) {
            int ownerId = cursor.getInt(0);
            cursor.close();

            ContentValues values = new ContentValues();
            values.put("owner_id", ownerId);
            values.put("name", petName);
            values.put("species", animalType);
            values.put("birth_date", birthDate);
            values.put("gender", gender);

            long newRowId = db.insert("Animals", null, values);

            if (newRowId != -1) {
                Toast.makeText(this, "Питомец зарегистрирован!", Toast.LENGTH_SHORT).show();
                Log.d("RegisterAnimalActivity", "Питомец успешно добавлен в базу данных!");

                // Сохраняем в SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("PET_ID", (int) newRowId); // Записываем новый ID питомца
                editor.putString("PET_NAME", petName);   // Записываем имя питомца
                editor.putString("PET_TYPE", animalType); // Записываем тип питомца
                editor.putString("PET_BIRTHDATE", birthDate); // Записываем дату рождения
                editor.putString("PET_GENDER", gender); // Записываем пол
                editor.apply();

// Переход в главное меню
                Intent intent = new Intent(RegisterAnimalActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Ошибка при добавлении питомца", Toast.LENGTH_SHORT).show();
                Log.e("RegisterAnimalActivity", "Ошибка: запись в базу данных не удалась!");
            }
        } else {
            Toast.makeText(this, "Ошибка: пользователь не найден!", Toast.LENGTH_SHORT).show();
            Log.e("RegisterAnimalActivity", "Ошибка: owner_id не найден!");
            cursor.close();
        }

        db.close();
    }
}