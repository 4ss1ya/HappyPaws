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

    // Конструктор адаптера, принимающий список книг
    public BookAdapter(List<Book> books) {
        this.books = books;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Создаем новый элемент из layout-файла item_book.xml
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_book, parent, false); // Убедитесь, что layout-файл item_book.xml существует
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        // Получаем текущую книгу по позиции
        Book book = books.get(position);

        // Устанавливаем текст в TextView, предполагая, что у Book есть метод getTitle(), который возвращает название книги
        holder.textViewBook.setText(book.getTitle());  // Убедитесь, что у класса Book есть метод getTitle()
    }

    @Override
    public int getItemCount() {
        return books.size(); // Возвращаем размер списка книг
    }

    // Метод для обновления данных в адаптере
    public void updateData(List<Book> newBooks) {
        this.books.clear();  // Очищаем старые данные
        this.books.addAll(newBooks);  // Добавляем новые данные
        notifyDataSetChanged();  // Уведомляем адаптер, что данные изменились
    }

    // ViewHolder для книги
    static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView textViewBook;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            // Находим TextView по ID в layout-файле item_book.xml
            textViewBook = itemView.findViewById(R.id.textViewBook);  // Убедитесь, что в item_book.xml есть элемент с этим ID
        }
    }
}
