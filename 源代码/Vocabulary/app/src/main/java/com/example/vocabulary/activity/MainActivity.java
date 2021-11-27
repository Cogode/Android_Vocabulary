package com.example.vocabulary.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vocabulary.R;
import com.example.vocabulary.adapter.MyRecyclerViewAdapter;
import com.example.vocabulary.domain.Word;
import com.example.vocabulary.fragment.WordContentFragment;
import com.example.vocabulary.util.DBUtil;
import com.example.vocabulary.util.MySQLiteOpenHelper;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private MySQLiteOpenHelper dbHelper = new MySQLiteOpenHelper(this, "vocabulary", null, 1);
    private ArrayList<Word> words = new ArrayList<>();
    private RecyclerView recyclerView;
    private MyRecyclerViewAdapter adapter;
    private Word longClickWord;
    private EditText searchText;
    private Button searchBtn;
    private Intent intent;
    private Button sentenceBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        if(DBUtil.getNumberOf(dbHelper, "Word") == 0)
            DBUtil.init(dbHelper);

        intent = getIntent();
        Word word = (Word) intent.getSerializableExtra("word");
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            sentenceBtn = findViewById(R.id.sentence_btn);
            sentenceBtn.setOnClickListener(view -> {
                String q = ((TextView) ((View) view.getParent()).findViewById(R.id.wordSentence)).getText().toString().split("例句：")[1];
                String langType = "en";
                String appKey = "73b1fe362d1d8800";
                String salt = String.valueOf(System.currentTimeMillis());
                String sign = getDigest(appKey + q + salt + "JruZ6G2UgubkcaSfdSVL9GthEmW9BNf1");
                String path = "https://openapi.youdao.com/ttsapi?q=" + q + "&langType=" + langType + "&appKey=" + appKey + "&salt=" + salt + "&sign=" + sign;
                Uri uri = Uri.parse(path);
                try {
                    MediaPlayer.create(MainActivity.this, uri).start();
                } catch(Exception e) {
                    Toast.makeText(MainActivity.this, "播放失败", Toast.LENGTH_SHORT).show();
                }
            });
            if(word != null) {
                WordContentFragment content = (WordContentFragment) getSupportFragmentManager().findFragmentById(R.id.wordContentFragment);
                content.refresh(word);
                sentenceBtn.setVisibility(View.VISIBLE);
            }
        }
        else {
            if(word != null)
                toWordContentActivity(word);
        }

        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        words = DBUtil.searchAllWords(dbHelper, "Word");
        adapter = new MyRecyclerViewAdapter(words, view -> {
            String name = ((TextView) view.findViewById(R.id.textView)).getText().toString().split("\n")[0];
            Word clickWord = DBUtil.searchWordByName(dbHelper, "Word", name);
            if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                WordContentFragment content = (WordContentFragment) (getSupportFragmentManager().findFragmentById(R.id.wordContentFragment));
                content.refresh(clickWord);
                sentenceBtn.setVisibility(View.VISIBLE);
            }
            else
                toWordContentActivity(clickWord);
            intent.putExtra("word", clickWord);
        }, view -> registerForContextMenu(view), view -> {
            String q = ((TextView) ((View) view.getParent()).findViewById(R.id.textView)).getText().toString().split("\n")[0];
            String langType = "en";
            String appKey = "73b1fe362d1d8800";
            String salt = String.valueOf(System.currentTimeMillis());
            String sign = getDigest(appKey + q + salt + "JruZ6G2UgubkcaSfdSVL9GthEmW9BNf1");
            String path = "https://openapi.youdao.com/ttsapi?q=" + q + "&langType=" + langType + "&appKey=" + appKey + "&salt=" + salt + "&sign=" + sign;
            Uri uri = Uri.parse(path);
            try {
                MediaPlayer.create(MainActivity.this, uri).start();
            } catch(Exception e) {
                Toast.makeText(MainActivity.this, "播放失败", Toast.LENGTH_SHORT).show();
            }
        });
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(0);

        searchText = findViewById(R.id.searchText);
        searchBtn = findViewById(R.id.searchBtn);
        searchBtn.setOnClickListener(view -> {
            String searchInfo = searchText.getText().toString();
            if(searchInfo.equals("")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("请输入搜索内容！")
                        .setPositiveButton("确定", null)
                        .show();
            }
            else
                toSearchWordsActivity(searchInfo);
        });
    }

    public static String getDigest(String string) {
        if (string == null) {
            return null;
        }
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        byte[] btInput = string.getBytes(StandardCharsets.UTF_8);
        try {
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            mdInst.update(btInput);
            byte[] md = mdInst.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for(byte byte0 : md) {
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch(NoSuchAlgorithmException e) {
            return null;
        }
    }

    private void toWordContentActivity(Word word) {
        Intent intent = new Intent(MainActivity.this, WordContentActivity.class);
        intent.putExtra("word", word);
        startActivity(intent);
    }

    private void toSearchWordsActivity(String searchInfo) {
        Intent intent = new Intent(MainActivity.this, SearchWordsActivity.class);
        intent.putExtra("searchInfo", searchInfo);
        startActivity(intent);
    }

    private void toRawWordsActivity() {
        Intent intent = new Intent(MainActivity.this, RawWordsActivity.class);
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
                                Toast.makeText(MainActivity.this, "名称不能为空", Toast.LENGTH_SHORT).show();
                            else if(wordMeaning.getText().toString().equals(""))
                                Toast.makeText(MainActivity.this, "释义不能为空", Toast.LENGTH_SHORT).show();
                            else {
                                Word word = new Word();
                                word.setName(wordName.getText().toString());
                                word.setMeaning(wordMeaning.getText().toString());
                                word.setSentence(wordSentence.getText().toString());
                                if(DBUtil.add(dbHelper, "Word", word)) {
                                    Toast.makeText(MainActivity.this, wordName.getText().toString() + " 已加入词库", Toast.LENGTH_SHORT).show();
                                    DBUtil.refreshWords(dbHelper, "Word", words);
                                    adapter.notifyDataSetChanged();
                                }
                                else
                                    Toast.makeText(this, "添加失败，请重试", Toast.LENGTH_SHORT).show();
                            }
                        }).setNegativeButton("取消", null);
                builder.create().show();
                break;
            case R.id.help:
                View helpDialog = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_help, null, false);
                AlertDialog.Builder helpBuilder = new AlertDialog.Builder(MainActivity.this);
                helpBuilder.setView(helpDialog).setPositiveButton("确定", null).create().show();
                break;
            case R.id.exit:
                System.exit(0);
        }
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.contentmenu_word, menu);
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
            case R.id.addToRaw:
                if(DBUtil.add(dbHelper, "RawWord", longClickWord))
                    Toast.makeText(MainActivity.this, longClickWord.getName() + " 已加入生词本", Toast.LENGTH_SHORT).show();
                break;
            case R.id.edit:
                View editDialog = LayoutInflater.from(this).inflate(R.layout.dialog_edit_word, null, false);
                TextView wordName = editDialog.findViewById(R.id.edit_wordName);
                EditText wordMeaning = editDialog.findViewById(R.id.edit_wordMeaning);
                EditText wordSentence = editDialog.findViewById(R.id.edit_wordSentence);
                wordName.setText(longClickWord.getName());
                wordMeaning.setText(longClickWord.getMeaning());
                wordSentence.setText(longClickWord.getSentence());
                AlertDialog.Builder editBuilder = new AlertDialog.Builder(this);
                editBuilder.setView(editDialog)
                        .setPositiveButton("确定", (dialogInterface, which) -> {
                            if(wordMeaning.getText().toString().equals(""))
                                Toast.makeText(MainActivity.this, "释义不能为空", Toast.LENGTH_SHORT).show();
                            else {
                                Word editedWord = new Word();
                                editedWord.setName(wordName.getText().toString());
                                editedWord.setMeaning(wordMeaning.getText().toString());
                                editedWord.setSentence(wordSentence.getText().toString());
                                if(DBUtil.update(dbHelper, "Word", editedWord)) {
                                    Toast.makeText(MainActivity.this, longClickWord.getName() + " 修改成功", Toast.LENGTH_SHORT).show();
                                    DBUtil.refreshWords(dbHelper, "Word", words);
                                    adapter.notifyDataSetChanged();
                                    if(intent.hasExtra("word")) {
                                        if(longClickWord.getName().equals(((Word) intent.getSerializableExtra("word")).getName())) {
                                            intent.putExtra("word", editedWord);
                                            if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                                                WordContentFragment content = (WordContentFragment) getSupportFragmentManager().findFragmentById(R.id.wordContentFragment);
                                                content.refresh(editedWord);
                                            }
                                        }
                                    }
                                    longClickWord = editedWord;
                                }
                            }
                        }).setNegativeButton("取消", null);
                editBuilder.create().show();
                DBUtil.refreshRawWords(dbHelper);
                break;
            case R.id.delete:
                View deleteDialog = LayoutInflater.from(this).inflate(R.layout.dialog_delete, null, false);
                TextView dialogHead = deleteDialog.findViewById(R.id.dialog_head);
                dialogHead.setText("是否从词库中删除 " + longClickWord.getName());
                AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(this);
                deleteBuilder.setView(deleteDialog)
                        .setPositiveButton("确定", (dialogInterface, i) -> {
                            if(DBUtil.delete(dbHelper, "Word", longClickWord.getName())) {
                                Toast.makeText(MainActivity.this, longClickWord.getName() + " 已从词库中删除", Toast.LENGTH_SHORT).show();
                                DBUtil.refreshWords(dbHelper, "Word", words);
                                adapter.notifyDataSetChanged();
                                if(intent.hasExtra("word")) {
                                    if(longClickWord.getName().equals(((Word) intent.getSerializableExtra("word")).getName())) {
                                        intent.removeExtra("word");
                                        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                                            WordContentFragment content = (WordContentFragment) getSupportFragmentManager().findFragmentById(R.id.wordContentFragment);
                                            content.refresh(null);
                                            sentenceBtn.setVisibility(View.INVISIBLE);
                                        }
                                    }
                                }
                                longClickWord = null;
                            }
                        }).setNegativeButton("取消", null);
                deleteBuilder.create().show();
                DBUtil.refreshRawWords(dbHelper);
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        DBUtil.refreshWords(dbHelper, "Word", words);
        adapter.notifyDataSetChanged();
    }
}