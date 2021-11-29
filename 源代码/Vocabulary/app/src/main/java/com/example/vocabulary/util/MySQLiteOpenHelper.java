package com.example.vocabulary.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {
    private String createWordSQL = "create table Word (" +
                                    "name text primary key," +
                                    "meaning text not null," +
                                    "sentence text)";

    private String createRawWordSQL = "create table RawWord (" +
                                    "name text primary key," +
                                    "meaning text not null," +
                                    "sentence text)";

    public MySQLiteOpenHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(createWordSQL);
        sqLiteDatabase.execSQL(createRawWordSQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }
}
