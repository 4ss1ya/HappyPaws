package com.example.happypaws;

public class Book {
    private String title;
    private String description;

    // Конструктор класса
    public Book(String title, String description) {
        this.title = title;
        this.description = description;
    }

    // Геттер для названия книги
    public String getTitle() {
        return title;
    }

    // Геттер для описания книги (если необходимо)
    public String getDescription() {
        return description;
    }
}
