package com.example.happypaws;

public class Book {
    private String text;

    public Book(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}