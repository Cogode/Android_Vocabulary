package com.example.vocabulary.util;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class MyContentProvider extends ContentProvider {
    private static int WORD_DIR = 0;
    private static int WORD_ITEM = 1;
    private static int RAW_DIR = 2;
    private static int RAW_ITEM = 3;
    private static String authority = "com.example.vocabulary.provider";
    private static UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(authority, "Word", WORD_DIR);
        uriMatcher.addURI(authority, "Word/*", WORD_ITEM);
        uriMatcher.addURI(authority, "RawWord", RAW_DIR);
        uriMatcher.addURI(authority, "RawWord/*", RAW_ITEM);
    }
    private MySQLiteOpenHelper dbHelper;

    public MyContentProvider() {
    }

    @Override
    public boolean onCreate() {
        dbHelper =  new MySQLiteOpenHelper(this.getContext(), "vocabulary", null, 1);
        return true;
    }

    @Override
    public String getType(Uri uri) {
        if(uriMatcher.match(uri) == WORD_DIR)
            return "vnd.android.cursor.dir/vnd.com.example.vocabulary.provider.Word";
        else if(uriMatcher.match(uri) == WORD_ITEM)
            return "vnd.android.cursor.item/vnd.com.example.vocabulary.provider.Word";
        else if(uriMatcher.match(uri) == RAW_DIR)
            return "vnd.android.cursor.dir/vnd.com.example.vocabulary.provider.RawWord";
        else if(uriMatcher.match(uri) == RAW_ITEM)
            return "vnd.android.cursor.item/vnd.com.example.vocabulary.provider.RawWord";
        else
            throw new IllegalArgumentException("Unknown Uri:" + uri);
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if(uriMatcher.match(uri) == WORD_DIR)
            db.insert("Word", null, values);
        else if(uriMatcher.match(uri) == RAW_DIR)
            db.insert("RawWord", null, values);
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if(uriMatcher.match(uri) == WORD_DIR || uriMatcher.match(uri) == WORD_ITEM)
            return db.delete("Word", selection, selectionArgs);
        else if(uriMatcher.match(uri) == RAW_DIR || uriMatcher.match(uri) == RAW_ITEM)
            return db.delete("RawWord", selection, selectionArgs);
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if(uriMatcher.match(uri) == WORD_DIR || uriMatcher.match(uri) == WORD_ITEM)
            return db.update("Word", values, selection, selectionArgs);
        else if(uriMatcher.match(uri) == RAW_DIR || uriMatcher.match(uri) == RAW_ITEM)
            return db.update("RawWord", values, selection, selectionArgs);
        return 0;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if(uriMatcher.match(uri) == WORD_DIR)
            return db.query("Word", projection, selection, selectionArgs, null, null, sortOrder);
        else if(uriMatcher.match(uri) == WORD_ITEM) {
            String name = uri.getPathSegments().get(1);
            return db.query("Word", projection, "name = ?", new String[] { name }, null, null, sortOrder);
        }
        else if(uriMatcher.match(uri) == RAW_DIR)
            return db.query("RawWord", projection, selection, selectionArgs, null, null, sortOrder);
        else if(uriMatcher.match(uri) == RAW_ITEM) {
            String name = uri.getPathSegments().get(1);
            return db.query("RawWord", projection, "name = ?", new String[] { name }, null, null, sortOrder);
        }
        else
            throw new IllegalArgumentException("Unknown Uri:" + uri);
    }
}