package com.example.happypaws;

import android.content.Intent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        buttonCalendar.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CalendarActivity.class);
            startActivity(intent);
        });
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }
}