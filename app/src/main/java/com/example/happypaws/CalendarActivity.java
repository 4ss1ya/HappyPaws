package com.example.happypaws;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ToDoAdapter toDoAdapter;
    private DatabaseHelper dbHelper;
    private List<Task> taskList;
    private TextView selectedDateText;
    private String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        CalendarView calendarView = findViewById(R.id.calendarView);
        recyclerView = findViewById(R.id.calendarRecyclerView);
        selectedDateText = findViewById(R.id.selectedDateText);
        EditText taskInput = findViewById(R.id.taskInput);
        Button addButton = findViewById(R.id.addButton);

        dbHelper = new DatabaseHelper(this);
        selectedDate = getCurrentDate();
        selectedDateText.setText("Selected Date: " + selectedDate);

        taskList = dbHelper.getTasksByDate(selectedDate);
        toDoAdapter = new ToDoAdapter(taskList, dbHelper, selectedDate);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(toDoAdapter);

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
            selectedDateText.setText("Selected Date: " + selectedDate);
            taskList.clear();
            taskList.addAll(dbHelper.getTasksByDate(selectedDate));
            toDoAdapter.notifyDataSetChanged();
        });

        addButton.setOnClickListener(v -> {
            String task = taskInput.getText().toString();
            if (!task.isEmpty()) {
                Task newTask = new Task(task, selectedDate);
                dbHelper.addTask(newTask);
                taskList.add(newTask);
                toDoAdapter.notifyDataSetChanged();
                taskInput.setText("");
            }
        });
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }
}