package com.example.vocabulary.util;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.vocabulary.domain.Word;

import java.util.ArrayList;

public class DBUtil {
    public static void init(MySQLiteOpenHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", "apple");
        values.put("meaning", "苹果");
        values.put("sentence", "This is an apple.");
        db.insert("Word", null, values);
        values.put("name", "banana");
        values.put("meaning", "香蕉");
        values.put("sentence", "This is a banana.");
        db.insert("Word", null, values);
        values.put("name", "fruit");
        values.put("meaning", "水果");
        values.put("sentence", "This is a fruit.");
        db.insert("Word", null, values);
        values.put("name", "orange");
        values.put("meaning", "橘子");
        values.put("sentence", "This is an orange.");
        db.insert("Word", null, values);
        values.put("name", "pear");
        values.put("meaning", "梨");
        values.put("sentence", "This is a pear.");
        db.insert("Word", null, values);
        values.put("name", "pineapple");
        values.put("meaning", "菠萝");
        values.put("sentence", "This is a pineapple.");
        db.insert("Word", null, values);
        values.put("name", "strawberry");
        values.put("meaning", "草莓");
        values.put("sentence", "This is a strawberry.");
        db.insert("Word", null, values);
        values.put("name", "watermelon");
        values.put("meaning", "西瓜");
        values.put("sentence", "This is a watermelon.");
        db.insert("Word", null, values);
    }

    public static ArrayList<Word> searchAllWords(MySQLiteOpenHelper dbHelper, String table) {
        ArrayList<Word> words = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + table, null);
        try {
            if(cursor != null) {
                while(cursor.moveToNext()) {
                    Word word = new Word();
                    int name = cursor.getColumnIndex("name");
                    int meaning = cursor.getColumnIndex("meaning");
                    int sentence = cursor.getColumnIndex("sentence");
                    word.setName(cursor.getString(name));
                    word.setMeaning(cursor.getString(meaning));
                    word.setSentence(cursor.getString(sentence));
                    words.add(word);
                }
            }
        } catch(Exception e) {
            Log.i("TAG", "数据查询失败...");
        }
        return words;
    }

    public static ArrayList<Word> searchWordsLikeInfo(MySQLiteOpenHelper dbHelper, String table, String info) {
        ArrayList<Word> words = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + table + " where name like ? or meaning like ? or sentence like ?",
                new String[] { "%" + info + "%", "%" + info + "%", "%" + info + "%" });
        try {
            if(cursor != null) {
                while(cursor.moveToNext()) {
                    Word word = new Word();
                    int name = cursor.getColumnIndex("name");
                    int meaning = cursor.getColumnIndex("meaning");
                    int sentence = cursor.getColumnIndex("sentence");
                    word.setName(cursor.getString(name));
                    word.setMeaning(cursor.getString(meaning));
                    word.setSentence(cursor.getString(sentence));
                    words.add(word);
                }
            }
        } catch(Exception e) {
            Log.i("TAG", "数据查询失败...");
        }
        return words;
    }

    public static Word searchWordByName(MySQLiteOpenHelper dbHelper, String table, String wordName) {
        Word word = null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + table + " where name = ?", new String[] { wordName });
        try {
            if(cursor != null) {
                if(cursor.moveToNext()) {
                    word = new Word();
                    int name = cursor.getColumnIndex("name");
                    int meaning = cursor.getColumnIndex("meaning");
                    int sentence = cursor.getColumnIndex("sentence");
                    word.setName(cursor.getString(name));
                    word.setMeaning(cursor.getString(meaning));
                    word.setSentence(cursor.getString(sentence));
                }
            }
        } catch(Exception e) {
            Log.i("TAG", "数据查询失败...");
        }
        return word;
    }

    public static boolean add(MySQLiteOpenHelper dbHelper, String table, Word word) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", word.getName());
        values.put("meaning", word.getMeaning());
        values.put("sentence", word.getSentence());
        try {
            db.insert(table, null, values);
        } catch(Exception e) {
            Log.i("TAG", "数据插入失败...");
            return false;
        }
        return true;
    }

    public static boolean delete(MySQLiteOpenHelper dbHelper, String table, String name) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            db.delete(table, "name = ?", new String[] { name });
        } catch(Exception e) {
            Log.i("TAG", "数据删除失败...");
            return false;
        }
        return true;
    }

    public static boolean update(MySQLiteOpenHelper dbHelper, String table,  Word word) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("meaning", word.getMeaning());
            values.put("sentence", word.getSentence());
            db.update(table, values, "name = ?", new String[] { word.getName() });
        } catch(Exception e) {
            Log.i("TAG", "数据修改失败...");
            return false;
        }
        return true;
    }

    public static void refreshWords(MySQLiteOpenHelper dbHelper, String table, ArrayList<Word> words) {
        ArrayList<Word> newWords = searchAllWords(dbHelper, table);
        words.clear();
        for(int i = 0; i < newWords.size(); i ++)
            words.add(newWords.get(i));
    }

    public static int getNumberOf(MySQLiteOpenHelper dbHelper, String table) {
        ArrayList<Word> words = searchAllWords(dbHelper, table);
        return words.size();
    }

    public static void refreshRawWords(MySQLiteOpenHelper dbHelper) {
        ArrayList<Word> rawWords = searchAllWords(dbHelper, "RawWord");
        for(int i = 0; i < rawWords.size(); i ++) {
            Word word = searchWordByName(dbHelper, "Word", rawWords.get(i).getName());
            if(word != null)
                update(dbHelper, "RawWord", word);
            else
                delete(dbHelper, "RawWord", rawWords.get(i).getName());
        }
    }
}
