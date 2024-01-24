package com.example.finalproject.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLite extends SQLiteOpenHelper {
    private static final String DB_NAME = "final_project.db";
    private static final int DB_VERSION = 1;

    public SQLite(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableCart = "CREATE TABLE Carts(" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "ID_Product TEXT NOT NULL," +
                "Quality INTEGER NOT NULL" +
                ")";
        db.execSQL(createTableCart);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
