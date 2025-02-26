package com.example.happypaws;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private SQLiteDatabase db;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = getBaseContext().openOrCreateDatabase("app.db", MODE_PRIVATE, null);
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        // Создаем таблицу, если ее нет
        db.execSQL("CREATE TABLE IF NOT EXISTS People ( id_People INTEGER PRIMARY KEY AUTOINCREMENT, id_pet INTEGER, Name TEXT NOT NULL, Number TEXT, Adres TEXT, username TEXT UNIQUE NOT NULL, password TEXT NOT NULL, FOREIGN KEY (id_pet) REFERENCES Pet(id_Pet) ON DELETE SET NULL)");

        db.execSQL("CREATE TABLE IF NOT EXISTS Pet ( id_Pet INTEGER PRIMARY KEY AUTOINCREMENT, Name TEXT NOT NULL, Birthday DATE, Gender TEXT, id_type INTEGER, PHOTO BLOB, FOREIGN KEY (id_type) REFERENCES Type(id_type) ON DELETE SET NULL)");

        db.execSQL("CREATE TABLE IF NOT EXISTS Type ( id_type INTEGER PRIMARY KEY AUTOINCREMENT, NameType TEXT NOT NULL, who TEXT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS Books ( id_Books INTEGER PRIMARY KEY AUTOINCREMENT, pet_id INTEGER, text TEXT, FOREIGN KEY (pet_id) REFERENCES Pet(id_Pet) ON DELETE CASCADE)");

        db.execSQL("CREATE TABLE IF NOT EXISTS vaccine ( id_vaccine INTEGER PRIMARY KEY AUTOINCREMENT, pet_id INTEGER, text TEXT, FOREIGN KEY (pet_id) REFERENCES Pet(id_Pet) ON DELETE CASCADE)");

        db.execSQL("CREATE TABLE IF NOT EXISTS Events ( id_Events INTEGER PRIMARY KEY AUTOINCREMENT, Date DATE NOT NULL, text TEXT, is_done BOOLEAN DEFAULT 0)");
    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (!isLoggedIn) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }


    public void logoutUser(View view) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        startActivity(new Intent(this, RegisterActivity.class));
        finish();
    }

    public void goMapBtn(View view){
        Intent intent = new Intent(this, GoogleMapActivity.class);
        startActivity(intent);
    }
}
