package com.example.happypaws;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "app.db";
    private static final int DATABASE_VERSION = 2; // Увеличили версию!

    public UserDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS People (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "username TEXT UNIQUE NOT NULL, " +
                "password TEXT NOT NULL);");

        db.execSQL("CREATE TABLE IF NOT EXISTS Animals (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "species TEXT NOT NULL, " +
                "birth_date TEXT, " +
                "gender TEXT, " +
                "owner_id INTEGER NOT NULL, " +
                "FOREIGN KEY(owner_id) REFERENCES People(id) ON DELETE CASCADE);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Добавляем таблицу Animals, если она не существует
            db.execSQL("CREATE TABLE IF NOT EXISTS Animals (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL, " +
                    "species TEXT NOT NULL, " +
                    "birth_date TEXT, " +
                    "gender TEXT, " +
                    "owner_id INTEGER NOT NULL, " +
                    "FOREIGN KEY(owner_id) REFERENCES People(id) ON DELETE CASCADE);");
        }
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.execSQL("PRAGMA foreign_keys=ON;");
    }
}