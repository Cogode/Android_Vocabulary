package com.example.vocabulary.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vocabulary.R;
import com.example.vocabulary.adapter.MyRecyclerViewAdapter;
import com.example.vocabulary.domain.Word;
import com.example.vocabulary.util.DBUtil;
import com.example.vocabulary.util.MySQLiteOpenHelper;

import java.util.ArrayList;

public class RawWordsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MyRecyclerViewAdapter adapter;
    private MySQLiteOpenHelper dbHelper = new MySQLiteOpenHelper(this, "vocabulary", null, 1);
    private ArrayList<Word> words;
    private Word longClickWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_raw_words);
        init();
    }

    private void init() {
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("生词本");
        }

        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        words = DBUtil.searchAllWords(dbHelper, "RawWord");
        adapter = new MyRecyclerViewAdapter(words, view -> {
            String name = ((TextView) view.findViewById(R.id.textView)).getText().toString().split("\n")[0];
            Word clickWord = DBUtil.searchWordByName(dbHelper, "RawWord", name);
            View dialog = LayoutInflater.from(this).inflate(R.layout.dialog_word_info, null, false);
            TextView wordName = dialog.findViewById(R.id.info_wordName);
            TextView wordMeaning = dialog.findViewById(R.id.info_wordMeaning);
            TextView wordSentence = dialog.findViewById(R.id.info_wordSentence);
            wordName.setText(clickWord.getName());
            wordMeaning.setText(clickWord.getMeaning());
            wordSentence.setText(clickWord.getSentence());
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(dialog)
                    .setPositiveButton("确定", null)
                    .show();
        }, view -> registerForContextMenu(view), view -> {
            String q = ((TextView) ((View) view.getParent()).findViewById(R.id.textView)).getText().toString().split("\n")[0];
            String langType = "en";
            String appKey = "73b1fe362d1d8800";
            String salt = String.valueOf(System.currentTimeMillis());
            String sign = MainActivity.getDigest(appKey + q + salt + "JruZ6G2UgubkcaSfdSVL9GthEmW9BNf1");
            String path = "https://openapi.youdao.com/ttsapi?q=" + q + "&langType=" + langType + "&appKey=" + appKey + "&salt=" + salt + "&sign=" + sign;
            Uri uri = Uri.parse(path);
            try {
                MediaPlayer.create(RawWordsActivity.this, uri).start();
            } catch(Exception e) {
                Toast.makeText(RawWordsActivity.this, "播放失败", Toast.LENGTH_SHORT).show();
            }
        });
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(0);
    }

    private void toRawWordsActivity() {
        Intent intent = new Intent(RawWordsActivity.this, RawWordsActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.raw:
                toRawWordsActivity();
                break;
            case R.id.addWord:
                View addDialog = LayoutInflater.from(this).inflate(R.layout.dialog_add_word, null, false);
                EditText wordName = addDialog.findViewById(R.id.add_wordName);
                EditText wordMeaning = addDialog.findViewById(R.id.add_wordMeaning);
                EditText wordSentence = addDialog.findViewById(R.id.add_wordSentence);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setView(addDialog)
                        .setPositiveButton("确定", (dialogInterface, i) -> {
                            if(wordName.getText().toString().equals(""))
                                Toast.makeText(RawWordsActivity.this, "名称不能为空", Toast.LENGTH_SHORT).show();
                            else if(wordMeaning.getText().toString().equals(""))
                                Toast.makeText(RawWordsActivity.this, "释义不能为空", Toast.LENGTH_SHORT).show();
                            else {
                                Word word = new Word();
                                word.setName(wordName.getText().toString());
                                word.setMeaning(wordMeaning.getText().toString());
                                word.setSentence(wordSentence.getText().toString());
                                if(DBUtil.add(dbHelper, "Word", word))
                                    Toast.makeText(RawWordsActivity.this, wordName.getText().toString() + " 已加入词库", Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(RawWordsActivity.this, "添加失败，请重试", Toast.LENGTH_SHORT).show();
                            }
                        }).setNegativeButton("取消", null);
                builder.create().show();
                break;
            case R.id.help:
                View helpDialog = LayoutInflater.from(RawWordsActivity.this).inflate(R.layout.dialog_help, null, false);
                AlertDialog.Builder helpBuilder = new AlertDialog.Builder(RawWordsActivity.this);
                helpBuilder.setView(helpDialog).setPositiveButton("确定", null).create().show();
                break;
            case R.id.exit:
                System.exit(0);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.contentmenu_raw, menu);
        String wordName = ((TextView) view.findViewById(R.id.textView)).getText().toString().split("\n")[0];
        for(int i = 0; i < words.size(); i ++) {
            if(wordName.equals(words.get(i).getName())) {
                longClickWord = words.get(i);
                break;
            }
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.deleteRaw:
                View deleteDialog = LayoutInflater.from(this).inflate(R.layout.dialog_delete, null, false);
                TextView dialogHead = deleteDialog.findViewById(R.id.dialog_head);
                dialogHead.setText("是否从生词本中删除 " + longClickWord.getName());
                AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(this);
                deleteBuilder.setView(deleteDialog)
                        .setPositiveButton("确定", (dialogInterface, i) -> {
                            if(DBUtil.delete(dbHelper, "RawWord", longClickWord.getName())) {
                                Toast.makeText(RawWordsActivity.this, longClickWord.getName() + " 已从生词本中删除", Toast.LENGTH_SHORT).show();
                                DBUtil.refreshWords(dbHelper, "RawWord", words);
                                adapter.notifyDataSetChanged();
                            }
                        }).setNegativeButton("取消", null);
                deleteBuilder.create().show();
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        DBUtil.refreshWords(dbHelper, "RawWord", words);
        adapter.notifyDataSetChanged();
    }
}