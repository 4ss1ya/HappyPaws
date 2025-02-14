package com.example.happypaws;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

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


        SQLiteDatabase db = getBaseContext()
                .openOrCreateDatabase("app.db", MODE_PRIVATE, null);

        db.execSQL("CREATE TABLE IF NOT EXISTS People ( id_People INTEGER PRIMARY KEY AUTOINCREMENT, id_pet INTEGER, Name TEXT NOT NULL, Number TEXT, Adres TEXT, FOREIGN KEY (id_pet) REFERENCES Pet(id_Pet) ON DELETE SET NULL)");


        db.execSQL("CREATE TABLE IF NOT EXISTS Pet ( id_Pet INTEGER PRIMARY KEY AUTOINCREMENT, Name TEXT NOT NULL, Birthday DATE, Gender TEXT, id_type INTEGER, PHOTO BLOB, FOREIGN KEY (id_type) REFERENCES Type(id_type) ON DELETE SET NULL)");


        db.execSQL("CREATE TABLE IF NOT EXISTS Type ( id_type INTEGER PRIMARY KEY AUTOINCREMENT, NameType TEXT NOT NULL, who TEXT)");


        db.execSQL("CREATE TABLE IF NOT EXISTS Books ( id_Books INTEGER PRIMARY KEY AUTOINCREMENT, pet_id INTEGER, text TEXT, FOREIGN KEY (pet_id) REFERENCES Pet(id_Pet) ON DELETE CASCADE)");

        db.execSQL("CREATE TABLE IF NOT EXISTS vaccine ( id_vaccine INTEGER PRIMARY KEY AUTOINCREMENT, pet_id INTEGER, text TEXT, FOREIGN KEY (pet_id) REFERENCES Pet(id_Pet) ON DELETE CASCADE)");

        db.execSQL("CREATE TABLE IF NOT EXISTS Events ( id_Events INTEGER PRIMARY KEY AUTOINCREMENT, Date DATE NOT NULL, text TEXT, is_done BOOLEAN DEFAULT 0)");

    }

    public void goMapBtn(View view){
        Intent intent = new Intent(this,GoogleMapActivity.class);
        startActivity(intent);
    }
}