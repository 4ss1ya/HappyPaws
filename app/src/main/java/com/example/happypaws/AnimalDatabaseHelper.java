package com.example.happypaws;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.List;
import java.util.ArrayList;

public class AnimalDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "animals.db";
    private static final int DATABASE_VERSION = 5;

    private static final String TABLE_ANIMALS = "animals";
    private static final String COLUMN_ANIMAL_ID = "animal_id";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_ANIMAL_NAME = "name";
    private static final String COLUMN_ANIMAL_TYPE = "type";
    private static final String COLUMN_OWNER_NAME = "owner_name";
    private static final String COLUMN_BIRTH_DATE = "birth_date";
    private static final String COLUMN_GENDER = "gender";

    public AnimalDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createAnimalsTable = "CREATE TABLE " + TABLE_ANIMALS + " (" +
                COLUMN_ANIMAL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USER_ID + " INTEGER, " +
                COLUMN_ANIMAL_NAME + " TEXT, " +
                COLUMN_ANIMAL_TYPE + " TEXT, " +
                COLUMN_OWNER_NAME + " TEXT DEFAULT '', " +
                COLUMN_BIRTH_DATE + " TEXT DEFAULT '', " +
                COLUMN_GENDER + " TEXT DEFAULT '', " +
                "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES users(id) ON DELETE CASCADE);";
        db.execSQL(createAnimalsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("AnimalDatabaseHelper", "Обновление базы животных с версии " + oldVersion + " на " + newVersion);

        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_ANIMALS + " ADD COLUMN " + COLUMN_USER_ID + " INTEGER;");
        }
    }

    // Получение всех животных по ID пользователя
    public List<Animal> getAllAnimalsByUserId(int userId) {
        List<Animal> animals = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_ANIMALS + " WHERE " + COLUMN_USER_ID + " = ?",
                    new String[]{String.valueOf(userId)});

            while (cursor.moveToNext()) {
                Animal animal = new Animal(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ANIMAL_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ANIMAL_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ANIMAL_TYPE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_OWNER_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BIRTH_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GENDER))
                );
                animals.add(animal);
            }
        } catch (Exception e) {
            Log.e("AnimalDatabaseHelper", "Ошибка при получении списка животных", e);
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return animals;
    }

    // Получение животного по ID питомца
    public Animal getAnimalByPetId(int petId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        Animal animal = null;

        try {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_ANIMALS + " WHERE " + COLUMN_ANIMAL_ID + " = ?",
                    new String[]{String.valueOf(petId)});
            if (cursor != null && cursor.moveToFirst()) {
                animal = new Animal(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ANIMAL_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ANIMAL_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ANIMAL_TYPE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_OWNER_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BIRTH_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GENDER))
                );
            }
        } catch (Exception e) {
            Log.e("AnimalDatabaseHelper", "Ошибка при получении питомца по ID", e);
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }

        return animal;
    }
}