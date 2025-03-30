package com.example.happypaws;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.DatePickerDialog;
import android.content.pm.PackageManager;
import android.Manifest;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_PERMISSION_READ_STORAGE = 102;

    private EditText editTextTask, editTextDate;
    private Button buttonAdd, buttonCalendar, buttonMedicalBook, buttonPetRules, buttonMap;
    private RecyclerView recyclerView;
    private ToDoAdapter toDoAdapter;
    private List<Task> taskList;
    private DatabaseHelper dbHelper;
    private String selectedDate;
    private SharedPreferences sharedPreferences;
    private TextView textViewAnimalInfo;
    private AnimalDatabaseHelper animalDbHelper;
    private ImageView imageViewPet;

    private ActivityResultLauncher<Intent> pickImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        dbHelper = new DatabaseHelper(this);
        animalDbHelper = new AnimalDatabaseHelper(this);

        // Регистрируем ActivityResultLauncher в onCreate()
        pickImage = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        // Сохраняем URI в SharedPreferences
                        sharedPreferences.edit().putString("PET_PHOTO_URI", selectedImageUri.toString()).apply();
                        // Используем ImageDecoder для загрузки изображения
                        try {
                            ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), selectedImageUri);
                            Bitmap bitmap = ImageDecoder.decodeBitmap(source);
                            imageViewPet.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Ошибка при загрузке изображения", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        initUI();
        setupRecyclerView();
        displayAnimalInfo();
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkLoginStatus();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
        animalDbHelper.close();
    }

    private void initUI() {
        editTextTask = findViewById(R.id.editTextTask);
        editTextDate = findViewById(R.id.editTextDate);
        buttonAdd = findViewById(R.id.buttonAdd);
        buttonCalendar = findViewById(R.id.buttonCalendar);
        buttonMedicalBook = findViewById(R.id.buttonMedicalBook);
        buttonPetRules = findViewById(R.id.buttonPetRules);
        buttonMap = findViewById(R.id.mapBtn);
        recyclerView = findViewById(R.id.recyclerView);
        textViewAnimalInfo = findViewById(R.id.textViewAnimalInfo);
        imageViewPet = findViewById(R.id.photo); // ID из XML

        // Проверка разрешений перед загрузкой фото
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_READ_STORAGE);
        } else {
            imageViewPet.setOnClickListener(v -> openGallery());
        }

        selectedDate = getCurrentDate();
        editTextDate.setText(selectedDate);

        buttonAdd.setOnClickListener(v -> addTask());
        buttonCalendar.setOnClickListener(v -> openActivity(CalendarActivity.class));
        buttonMedicalBook.setOnClickListener(v -> openMedicalBook());
        buttonPetRules.setOnClickListener(v -> openActivity(PetRulesActivity.class));
        buttonMap.setOnClickListener(v -> openActivity(GoogleMapActivity.class));
        editTextDate.setOnClickListener(v -> showDatePicker());
    }

    private void setupRecyclerView() {
        taskList = dbHelper.getAllTasks();
        toDoAdapter = new ToDoAdapter(taskList, dbHelper, selectedDate);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(toDoAdapter);
    }

    private void addTask() {
        String taskText = editTextTask.getText().toString().trim();
        if (taskText.isEmpty()) {
            Toast.makeText(this, "Введите задачу", Toast.LENGTH_SHORT).show();
            return;
        }
        Task newTask = new Task(taskText, selectedDate);
        dbHelper.addTask(newTask);
        refreshTaskList();
        editTextTask.setText("");
    }

    private void checkLoginStatus() {
        if (!sharedPreferences.getBoolean("isLoggedIn", false)) {
            startActivity(new Intent(this, RegisterActivity.class));
            finish();
        }
    }

    public void logoutUser(View view) {
        sharedPreferences.edit().clear().apply();
        startActivity(new Intent(this, RegisterActivity.class));
        finish();
    }

    private String getCurrentDate() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, day) -> {
            selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, day);
            editTextDate.setText(selectedDate);
            refreshTaskList();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void refreshTaskList() {
        taskList.clear();
        taskList.addAll(dbHelper.getAllTasks());
        toDoAdapter.notifyDataSetChanged();
    }

    private void displayAnimalInfo() {
        int petId = sharedPreferences.getInt("PET_ID", -1);
        if (petId == -1) {
            textViewAnimalInfo.setText("Информация о питомце не найдена.");
            return;
        }

        String petName = sharedPreferences.getString("PET_NAME", "Неизвестно");
        String petType = sharedPreferences.getString("PET_TYPE", "Неизвестно");
        String birthDate = sharedPreferences.getString("PET_BIRTHDATE", "Неизвестно");
        String gender = sharedPreferences.getString("PET_GENDER", "Неизвестно");

        textViewAnimalInfo.setText(String.format(Locale.getDefault(),
                "Питомец: %s\nТип: %s\nДата рождения: %s\nПол: %s",
                petName, petType, birthDate, gender));

        // Загружаем фото, если оно сохранено
        String photoUriString = sharedPreferences.getString("PET_PHOTO_URI", null);
        if (photoUriString != null) {
            try {
                Uri selectedImageUri = Uri.parse(photoUriString);
                // Используем ImageDecoder для совместимости с более новыми версиями Android
                ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), selectedImageUri);
                Bitmap bitmap = ImageDecoder.decodeBitmap(source);
                imageViewPet.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Ошибка при загрузке изображения", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openActivity(Class<?> activityClass) {
        startActivity(new Intent(this, activityClass));
    }

    private void openMedicalBook() {
        int petId = sharedPreferences.getInt("PET_ID", -1);
        if (petId == -1) {
            Toast.makeText(this, "Ошибка: ID питомца не найден. Пожалуйста, добавьте питомца.", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, MedicalBookActivity.class);
        intent.putExtra("PET_ID", petId);
        startActivity(intent);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");  // Указываем тип файла как изображение

        pickImage.launch(intent);  // Запускаем интент для выбора изображения
    }
}
