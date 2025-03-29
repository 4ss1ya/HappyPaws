package com.example.happypaws;

import android.content.ContentValues;
import android.content.SharedPreferences;
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
    private EditText editTextDisease, editTextDate, editTextRecipe;
    private Button buttonAddNote;
    private TableLayout tableMedicalNotes;
    private BookDatabaseHelper dbHelper;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_book);

        petId = getIntent().getIntExtra("PET_ID", -1);
        if (petId == -1) {
            Toast.makeText(this, "Ошибка: ID питомца не найден", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        editTextDisease = findViewById(R.id.editTextDisease);
        editTextDate = findViewById(R.id.editTextDate);
        editTextRecipe = findViewById(R.id.editTextRecipe);
        buttonAddNote = findViewById(R.id.buttonAddNote);
        tableMedicalNotes = findViewById(R.id.tableMedicalNotes);

        dbHelper = new BookDatabaseHelper(this);
        currentUserId = getCurrentUserId();

        buttonAddNote.setOnClickListener(v -> addMedicalNote());
        loadMedicalNotes(currentUserId);
    }

    private int getCurrentUserId() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        return prefs.getInt("USER_ID", -1);
    }

    private void addMedicalNote() {
        String disease = editTextDisease.getText().toString().trim();
        String date = editTextDate.getText().toString().trim();
        String recipe = editTextRecipe.getText().toString().trim();

        if (disease.isEmpty() || date.isEmpty() || recipe.isEmpty()) {
            Toast.makeText(this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        AnimalDatabaseHelper animalDbHelper = new AnimalDatabaseHelper(this);
        Animal animal = animalDbHelper.getAnimalByPetId(petId);

        if (animal == null || animal.getUserId() != currentUserId) {
            Toast.makeText(this, "Этот питомец не принадлежит текущему пользователю", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("animal_id", petId);
            values.put("procedure_type", disease);
            values.put("date", date);
            values.put("description", recipe);
            long rowId = db.insert("MedicalRecords", null, values);

            if (rowId == -1) {
                Toast.makeText(this, "Ошибка при добавлении записи", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Запись добавлена", Toast.LENGTH_SHORT).show();
                loadMedicalNotes(currentUserId);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Ошибка: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void loadMedicalNotes(int currentUserId) {
        tableMedicalNotes.removeAllViews();

        TableRow headerRow = new TableRow(this);
        addTextToRow(headerRow, "Болезнь");
        addTextToRow(headerRow, "Дата");
        addTextToRow(headerRow, "Рецепт");
        tableMedicalNotes.addView(headerRow);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {"procedure_type", "date", "description", "animal_id"};
        String selection = "animal_id IN (SELECT animal_id FROM animals WHERE user_id = ?)";
        String[] selectionArgs = { String.valueOf(currentUserId) };

        Cursor cursor = db.query("MedicalRecords", projection, selection, selectionArgs, null, null, null);

        while (cursor.moveToNext()) {
            String disease = cursor.getString(cursor.getColumnIndexOrThrow("procedure_type"));
            String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
            String recipe = cursor.getString(cursor.getColumnIndexOrThrow("description"));

            TableRow row = new TableRow(this);
            addTextToRow(row, disease);
            addTextToRow(row, date);
            addTextToRow(row, recipe);
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