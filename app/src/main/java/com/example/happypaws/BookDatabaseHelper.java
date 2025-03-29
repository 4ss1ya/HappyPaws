package com.example.happypaws;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class BookDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "books_and_medical.db"; // Объединенная БД
    private static final int DATABASE_VERSION = 3;  // Увеличиваем версию базы данных

    public BookDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            // Создание таблицы Animals
            String createTableAnimals = "CREATE TABLE IF NOT EXISTS Animals (" +
                    "animal_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT, " +
                    "species TEXT, " +
                    "age INTEGER);";
            db.execSQL(createTableAnimals);

            // Создание таблицы Books
            String createTableBooks = "CREATE TABLE IF NOT EXISTS Books (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "pet_id INTEGER, " +
                    "title TEXT, " +
                    "description TEXT, " +
                    "date TEXT, " +
                    "FOREIGN KEY(pet_id) REFERENCES Animals(animal_id) ON DELETE CASCADE);";
            db.execSQL(createTableBooks);

            // Создание таблицы MedicalRecords
            String createMedicalRecordsTable = "CREATE TABLE IF NOT EXISTS MedicalRecords (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "animal_id INTEGER NOT NULL, " +
                    "procedure_type TEXT NOT NULL, " +
                    "description TEXT, " +
                    "date TEXT, " +
                    "FOREIGN KEY(animal_id) REFERENCES Animals(animal_id) ON DELETE CASCADE);";
            db.execSQL(createMedicalRecordsTable);

        } catch (Exception e) {
            Log.e("DatabaseError", "Error creating tables", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 3) {
            // Проводим необходимые миграции, например, добавляем новые столбцы или таблицы
            try {
                // Пример миграции: добавляем новый столбец в таблицу Animals (если необходимо)
                db.execSQL("ALTER TABLE Animals ADD COLUMN new_column_name TEXT;");

                // Удаляем старые таблицы, если это необходимо
                db.execSQL("DROP TABLE IF EXISTS MedicalRecords");
                db.execSQL("DROP TABLE IF EXISTS Books");
                db.execSQL("DROP TABLE IF EXISTS Animals");

                // Пересоздаем все таблицы
                onCreate(db);
            } catch (Exception e) {
                Log.e("DatabaseError", "Error during onUpgrade", e);
            }
        }
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.execSQL("PRAGMA foreign_keys = ON;"); // Включаем проверку внешних ключей
    }
}