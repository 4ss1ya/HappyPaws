package com.example.happypaws;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MedicalBookActivity extends AppCompatActivity {

    private int petId;
    private EditText editTextDisease, editTextDate, editTextRecipe, editTextVaccine;
    private Button buttonAddNote;
    private TableLayout tableMedicalNotes;
    private BookDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_book);

        // Получаем ID питомца из Intent
        petId = getIntent().getIntExtra("PET_ID", -1);
        if (petId == -1) {
            Toast.makeText(this, "Ошибка: ID питомца не найден", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Инициализация полей
        editTextDisease = findViewById(R.id.editTextDisease);
        editTextDate = findViewById(R.id.editTextDate);
        editTextRecipe = findViewById(R.id.editTextRecipe);
        editTextVaccine = findViewById(R.id.editTextVaccine); // Новое поле для вакцины
        buttonAddNote = findViewById(R.id.buttonAddNote);
        tableMedicalNotes = findViewById(R.id.tableMedicalNotes);

        dbHelper = new BookDatabaseHelper(this);

        // Устанавливаем обработчик для кнопки
        buttonAddNote.setOnClickListener(v -> addMedicalNote());

        // Загружаем записи в таблицу
        loadMedicalNotes();
    }

    private void addMedicalNote() {
        String disease = editTextDisease.getText().toString().trim();
        String date = editTextDate.getText().toString().trim();
        String recipe = editTextRecipe.getText().toString().trim();
        String vaccine = editTextVaccine.getText().toString().trim();

        // Проверка на пустые поля
        if (disease.isEmpty() || date.isEmpty() || recipe.isEmpty() || vaccine.isEmpty()) {
            Toast.makeText(this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        // Проверка на то, что ID питомца корректное
        if (petId == -1) {
            Toast.makeText(this, "Ошибка: ID питомца не найден", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            // Если питомец не существует, добавляем его
            ContentValues petValues = new ContentValues();
            petValues.put("name", "New Pet");
            petValues.put("species", "Dog");
            petValues.put("age", 2);
            long insertedPetId = db.insert("Animals", null, petValues);  // Вставка нового питомца

            // Добавляем медицинскую запись
            ContentValues values = new ContentValues();
            values.put("animal_id", insertedPetId);  // Используем ID питомца, который мы только что добавили
            values.put("procedure_type", disease);
            values.put("date", date);
            values.put("description", recipe);
            values.put("vaccine", vaccine); // Добавляем вакцину в запись

            long rowId = db.insert("MedicalRecords", null, values);

            if (rowId == -1) {
                Toast.makeText(this, "Ошибка при добавлении записи", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Запись добавлена", Toast.LENGTH_SHORT).show();
                loadMedicalNotes();  // Перезагружаем записи
            }
        } catch (Exception e) {
            Toast.makeText(this, "Ошибка: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void loadMedicalNotes() {
        tableMedicalNotes.removeAllViews();

        // Заголовок таблицы
        TableRow headerRow = new TableRow(this);
        addTextToRow(headerRow, "Болезнь");
        addTextToRow(headerRow, "Дата");
        addTextToRow(headerRow, "Рецепт");
        addTextToRow(headerRow, "Вакцина"); // Добавляем колонку для вакцины
        tableMedicalNotes.addView(headerRow);

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {"procedure_type", "date", "description", "animal_id", "vaccine"};
        String selection = "animal_id = ?";
        String[] selectionArgs = { String.valueOf(petId) };

        Cursor cursor = db.query("MedicalRecords", projection, selection, selectionArgs, null, null, null);

        while (cursor.moveToNext()) {
            String disease = cursor.getString(cursor.getColumnIndexOrThrow("procedure_type"));
            String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
            String recipe = cursor.getString(cursor.getColumnIndexOrThrow("description"));
            String vaccine = cursor.getString(cursor.getColumnIndexOrThrow("vaccine")); // Получаем информацию о вакцине

            TableRow row = new TableRow(this);
            addTextToRow(row, disease);
            addTextToRow(row, date);
            addTextToRow(row, recipe);
            addTextToRow(row, vaccine); // Добавляем вакцину в таблицу
            tableMedicalNotes.addView(row);
        }

        cursor.close();
    }

    private void addTextToRow(TableRow row, String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setPadding(8, 8, 8, 8);
        row.addView(textView);
    }
}
