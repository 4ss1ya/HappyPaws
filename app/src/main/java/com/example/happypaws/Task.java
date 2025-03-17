package com.example.happypaws;

public class Task {
    private int id;
    private String text;
    private String date;

    public Task(String text, String date) {
        this.text = text;
        this.date = date;
    }

    public Task(int id, String text, String date) {
        this.id = id;
        this.text = text;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDate() {
        return date;
    }
}