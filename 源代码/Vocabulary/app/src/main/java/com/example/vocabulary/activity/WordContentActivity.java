package com.example.vocabulary.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vocabulary.R;
import com.example.vocabulary.domain.Word;
import com.example.vocabulary.util.DBUtil;
import com.example.vocabulary.util.MySQLiteOpenHelper;

public class WordContentActivity extends AppCompatActivity {
    private TextView wordName;
    private TextView wordMeaning;
    private TextView wordSentence;
    private MySQLiteOpenHelper dbHelper = new MySQLiteOpenHelper(this, "vocabulary", null, 1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_content);
        init();
    }

    private void init() {
        Intent intent = getIntent();
        Word word = (Word) intent.getSerializableExtra("word");

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            toMainActivity(word);
        else {
            ActionBar actionBar = getSupportActionBar();
            if(actionBar != null) {
                actionBar.setHomeButtonEnabled(true);
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setTitle("");
            }
            wordName = findViewById(R.id.wordName);
            wordMeaning = findViewById(R.id.wordMeaning);
            wordSentence = findViewById(R.id.wordSentence);
            wordName.setText(word.getName());
            wordMeaning.setText("释义：" + word.getMeaning());
            String sentence = "例句：";
            if(word.getSentence().equals(""))
                sentence += "暂无例句";
            else
                sentence += word.getSentence();
            wordSentence.setText(sentence);
            Button button = findViewById(R.id.button);
            button.setOnClickListener(view -> {
                String q = ((TextView) ((View) view.getParent()).findViewById(R.id.wordName)).getText().toString();
                String langType = "en";
                String appKey = "73b1fe362d1d8800";
                String salt = String.valueOf(System.currentTimeMillis());
                String sign = MainActivity.getDigest(appKey + q + salt + "JruZ6G2UgubkcaSfdSVL9GthEmW9BNf1");
                String path = "https://openapi.youdao.com/ttsapi?q=" + q + "&langType=" + langType + "&appKey=" + appKey + "&salt=" + salt + "&sign=" + sign;
                Uri uri = Uri.parse(path);
                try {
                    MediaPlayer.create(WordContentActivity.this, uri).start();
                } catch(Exception e) {
                    Toast.makeText(WordContentActivity.this, "播放失败", Toast.LENGTH_SHORT).show();
                }
            });
            Button sentenceButton = findViewById(R.id.sentence_btn);
            sentenceButton.setOnClickListener(view -> {
                String q = ((TextView) ((View) view.getParent()).findViewById(R.id.wordSentence)).getText().toString().split("例句：")[1];
                String langType = "en";
                String appKey = "73b1fe362d1d8800";
                String salt = String.valueOf(System.currentTimeMillis());
                String sign = MainActivity.getDigest(appKey + q + salt + "JruZ6G2UgubkcaSfdSVL9GthEmW9BNf1");
                String path = "https://openapi.youdao.com/ttsapi?q=" + q + "&langType=" + langType + "&appKey=" + appKey + "&salt=" + salt + "&sign=" + sign;
                Uri uri = Uri.parse(path);
                try {
                    MediaPlayer.create(WordContentActivity.this, uri).start();
                } catch(Exception e) {
                    Toast.makeText(WordContentActivity.this, "播放失败", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void toMainActivity(Word word) {
        Intent returnIntent = new Intent(WordContentActivity.this, MainActivity.class);
        returnIntent.putExtra("word", word);
        startActivity(returnIntent);
    }

    private void toRawWordsActivity() {
        Intent intent = new Intent(WordContentActivity.this, RawWordsActivity.class);
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
                                Toast.makeText(WordContentActivity.this, "名称不能为空", Toast.LENGTH_SHORT).show();
                            else if(wordMeaning.getText().toString().equals(""))
                                Toast.makeText(WordContentActivity.this, "释义不能为空", Toast.LENGTH_SHORT).show();
                            else {
                                Word word = new Word();
                                word.setName(wordName.getText().toString());
                                word.setMeaning(wordMeaning.getText().toString());
                                word.setSentence(wordSentence.getText().toString());
                                if(DBUtil.add(dbHelper, "Word", word))
                                    Toast.makeText(WordContentActivity.this, wordName.getText().toString() + "添加成功", Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(WordContentActivity.this, "添加失败，请重试", Toast.LENGTH_SHORT).show();
                            }
                        }).setNegativeButton("取消", null);
                builder.create().show();
                break;
            case R.id.help:
                View helpDialog = LayoutInflater.from(WordContentActivity.this).inflate(R.layout.dialog_help, null, false);
                AlertDialog.Builder helpBuilder = new AlertDialog.Builder(WordContentActivity.this);
                helpBuilder.setView(helpDialog).setPositiveButton("确定", null).create().show();
                break;
            case R.id.exit:
                System.exit(0);
        }
        return super.onOptionsItemSelected(item);
    }
}