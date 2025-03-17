package com.example.happypaws;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private EditText editTextTask;
    private Button buttonAdd, buttonCalendar;
    private RecyclerView recyclerView;
    private ToDoAdapter adapter;
    private List<Task> taskList;
    private DatabaseHelper dbHelper;
    private String selectedDate;
    private SQLiteDatabase db;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализация элементов
        editTextTask = findViewById(R.id.editTextTask);
        buttonAdd = findViewById(R.id.buttonAdd);
        buttonCalendar = findViewById(R.id.buttonCalendar);
        recyclerView = findViewById(R.id.recyclerView);

        dbHelper = new DatabaseHelper(this);
        selectedDate = getCurrentDate();
        taskList = dbHelper.getAllTasks();

        adapter = new ToDoAdapter(taskList, dbHelper, selectedDate);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Добавление задачи
        buttonAdd.setOnClickListener(v -> {
            String taskText = editTextTask.getText().toString().trim();
            if (!taskText.isEmpty()) {
                Task newTask = new Task(taskText, selectedDate);
                dbHelper.addTask(newTask);
                taskList.add(newTask);
                adapter.notifyDataSetChanged();
                editTextTask.setText("");
            }
        });

        // Переход в календарь
        buttonCalendar.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CalendarActivity.class);
            startActivity(intent);
        });

        // Настройка БД
        db = getBaseContext().openOrCreateDatabase("app.db", MODE_PRIVATE, null);
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        // Создание таблиц
        db.execSQL("CREATE TABLE IF NOT EXISTS People ( id_People INTEGER PRIMARY KEY AUTOINCREMENT, id_pet INTEGER, Name TEXT NOT NULL, Number TEXT, Adres TEXT, username TEXT UNIQUE NOT NULL, password TEXT NOT NULL, FOREIGN KEY (id_pet) REFERENCES Pet(id_Pet) ON DELETE SET NULL)");

        db.execSQL("CREATE TABLE IF NOT EXISTS Pet ( id_Pet INTEGER PRIMARY KEY AUTOINCREMENT, Name TEXT NOT NULL, Birthday DATE, Gender TEXT, id_type INTEGER, PHOTO BLOB, FOREIGN KEY (id_type) REFERENCES Type(id_type) ON DELETE SET NULL)");

        db.execSQL("CREATE TABLE IF NOT EXISTS Type ( id_type INTEGER PRIMARY KEY AUTOINCREMENT, NameType TEXT NOT NULL, who TEXT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS Books ( id_Books INTEGER PRIMARY KEY AUTOINCREMENT, pet_id INTEGER, text TEXT, FOREIGN KEY (pet_id) REFERENCES Pet(id_Pet) ON DELETE CASCADE)");

        db.execSQL("CREATE TABLE IF NOT EXISTS vaccine ( id_vaccine INTEGER PRIMARY KEY AUTOINCREMENT, pet_id INTEGER, text TEXT, FOREIGN KEY (pet_id) REFERENCES Pet(id_Pet) ON DELETE CASCADE)");

        db.execSQL("CREATE TABLE IF NOT EXISTS Events ( id_Events INTEGER PRIMARY KEY AUTOINCREMENT, Date DATE NOT NULL, text TEXT, is_done BOOLEAN DEFAULT 0)");
    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (!isLoggedIn) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    public void logoutUser(View view) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        startActivity(new Intent(this, RegisterActivity.class));
        finish();
    }

    public void goMapBtn(View view){
        Intent intent = new Intent(this, GoogleMapActivity.class);
        startActivity(intent);
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }
}
