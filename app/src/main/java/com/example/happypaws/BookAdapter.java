package com.example.happypaws;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private List<Book> books;

    public BookAdapter(List<Book> books) {
        this.books = books;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_book, parent, false);  // Убедитесь, что у вас есть layout item_book.xml
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = books.get(position);
        holder.textViewBook.setText(book.getText());  // Убедитесь, что у вас есть метод getText() в Book
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    // Метод для обновления данных в адаптере
    public void updateData(List<Book> newBooks) {
        this.books.clear();  // Очищаем старые данные
        this.books.addAll(newBooks);  // Добавляем новые данные
        notifyDataSetChanged();  // Уведомляем адаптер об изменении
    }

    static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView textViewBook;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewBook = itemView.findViewById(R.id.textViewBook);  // Убедитесь, что у вас есть элемент с этим id
        }
    }
}