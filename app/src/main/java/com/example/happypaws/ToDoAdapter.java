package com.example.happypaws;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder> {
    private List<Task> taskList;
    private DatabaseHelper dbHelper;
    private String selectedDate;

    public ToDoAdapter(List<Task> taskList, DatabaseHelper dbHelper, String selectedDate) {
        this.taskList = taskList;
        this.dbHelper = dbHelper;
        this.selectedDate = selectedDate;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_todo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.textViewTask.setText(task.getText());

        holder.buttonDelete.setOnClickListener(v -> {
            dbHelper.deleteTask(task.getId());
            taskList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, taskList.size());
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTask;
        Button buttonDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTask = itemView.findViewById(R.id.textViewTask);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }
    }
}