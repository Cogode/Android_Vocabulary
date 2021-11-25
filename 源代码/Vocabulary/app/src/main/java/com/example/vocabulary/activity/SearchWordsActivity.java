package com.example.vocabulary.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.ActivityInfo;
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
import com.example.vocabulary.domain.WordInformation;
import com.example.vocabulary.domain.WordService;
import com.example.vocabulary.util.DBUtil;
import com.example.vocabulary.util.MySQLiteOpenHelper;
import com.example.vocabulary.util.ServiceCreator;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchWordsActivity extends AppCompatActivity {
    private RecyclerView localRecyclerView;
    private RecyclerView netRecyclerView;
    private MyRecyclerViewAdapter localAdapter;
    private MyRecyclerViewAdapter netAdapter;
    private ArrayList<Word> localWords = new ArrayList<>();
    private ArrayList<Word> netWords = new ArrayList<>();
    private Word longClickWord;
    private MySQLiteOpenHelper dbHelper = new MySQLiteOpenHelper(this, "vocabulary", null, 1);
    private String searchInfo;
    private EditText searchText;
    private Button searchBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_words);
        init();
    }

    private void init() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("");
        }
        Intent intent = getIntent();
        searchInfo = intent.getStringExtra("searchInfo");

        localRecyclerView = findViewById(R.id.localRecyclerView);
        LinearLayoutManager localLayoutManager = new LinearLayoutManager(this);
        localRecyclerView.setLayoutManager(localLayoutManager);
        localWords = DBUtil.searchWordsLikeInfo(dbHelper, "Word", searchInfo);
        localAdapter = new MyRecyclerViewAdapter(localWords, view -> {
            String name = ((TextView) view.findViewById(R.id.textView)).getText().toString().split("\n")[0];
            Word clickWord = DBUtil.searchWordByName(dbHelper, "Word", name);
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
                MediaPlayer.create(SearchWordsActivity.this, uri).start();
            } catch(Exception e) {
                Toast.makeText(SearchWordsActivity.this, "播放失败", Toast.LENGTH_SHORT).show();
            }
        });
        localRecyclerView.setAdapter(localAdapter);
        localAdapter.notifyDataSetChanged();
        localRecyclerView.scrollToPosition(0);

        netRecyclerView = findViewById(R.id.netRecyclerView);
        LinearLayoutManager netLayoutManager = new LinearLayoutManager(this);
        netRecyclerView.setLayoutManager(netLayoutManager);
        netAdapter = new MyRecyclerViewAdapter(netWords, view -> {
            String name = ((TextView) view.findViewById(R.id.textView)).getText().toString().split("\n")[0];
            Word clickWord = new Word();
            for(int i = 0; i < netWords.size(); i ++) {
                if(name.equals(netWords.get(i).getName())) {
                    clickWord = netWords.get(i);
                    break;
                }
            }
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
                MediaPlayer.create(SearchWordsActivity.this, uri).start();
            } catch(Exception e) {
                Toast.makeText(SearchWordsActivity.this, "播放失败", Toast.LENGTH_SHORT).show();
            }
        });
        netRecyclerView.setAdapter(netAdapter);

        refreshNetWords();

        searchText = findViewById(R.id.searchText);
        searchBtn = findViewById(R.id.searchBtn);
        searchBtn.setOnClickListener(view -> {
            String info = searchText.getText().toString();
            if(info.equals("")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("请输入搜索内容！")
                        .setPositiveButton("确定", null)
                        .show();
            }
            else
                toSearchWordsActivity(info);
        });
    }

    private void refreshNetWords() {
        try {
            WordService wordService = ServiceCreator.create(WordService.class);
            String q = searchInfo;
            String from, to;
            if(isEnglish(q)) {
                from = "en";
                to = "zh-CHS";
            }
            else {
                from = "zh-CHS";
                to = "en";
            }
            String appKey = "73b1fe362d1d8800";
            String salt = String.valueOf(System.currentTimeMillis());
            String signType = "v3";
            String curtime = String.valueOf(System.currentTimeMillis() / 1000);
            String sign = getDigest(appKey + truncate(q) + salt + curtime + "JruZ6G2UgubkcaSfdSVL9GthEmW9BNf1");
            wordService.getWordData(q, from, to, appKey, salt, sign, signType, curtime).enqueue(new Callback<WordInformation>() {
                @Override
                public void onResponse(Call<WordInformation> call, Response<WordInformation> response) {
                    try {
                        WordInformation information = response.body();
                        if(information != null) {
                            if(information.getErrorCode().equals("0")) {
                                if(from.equals("en")) {
                                    List<WordInformation.SimpleWord> web = information.getWeb();
                                    for(int i = 0; i < web.size(); i++) {
                                        Word word = new Word();
                                        word.setName(web.get(i).getKey());
                                        String meaning = "";
                                        for(int j = 0; j < web.get(i).getValue().size(); j++)
                                            meaning += web.get(i).getValue().get(j) + ";";
                                        meaning = meaning.substring(0, meaning.length() - 1);
                                        word.setMeaning(meaning);
                                        word.setSentence("暂无例句");
                                        netWords.add(word);
                                        netAdapter.notifyDataSetChanged();
                                    }
                                }
                                else {
                                    Word word = new Word();
                                    word.setName(information.getTranslation().get(0));
                                    word.setMeaning(information.getQuery());
                                    word.setSentence("暂无例句");
                                    netWords.add(word);
                                    netAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    } catch(Exception e) {
                        Toast.makeText(SearchWordsActivity.this, "网络查询失败", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<WordInformation> call, Throwable t) {
                    Toast.makeText(SearchWordsActivity.this, "网络查询失败", Toast.LENGTH_SHORT).show();
                }
            });
        } catch(Exception e) {
            Toast.makeText(SearchWordsActivity.this, "网络查询失败", Toast.LENGTH_SHORT).show();
        }
    }

    public static String getDigest(String string) {
        if(string == null) {
            return null;
        }
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        byte[] btInput = string.getBytes(StandardCharsets.UTF_8);
        try {
            MessageDigest mdInst = MessageDigest.getInstance("SHA-256");
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

    public static String truncate(String q) {
        if(q == null) {
            return null;
        }
        int len = q.length();
        return len <= 20 ? q : (q.substring(0, 10) + len + q.substring(len - 10, len));
    }

    public static boolean isEnglish(String s) {
        byte[] bytes = s.getBytes();
        return s.length() == bytes.length;
    }

    private void toSearchWordsActivity(String searchInfo) {
        Intent intent = new Intent(SearchWordsActivity.this, SearchWordsActivity.class);
        intent.putExtra("searchInfo", searchInfo);
        startActivity(intent);
    }

    private void toRawWordsActivity() {
        Intent intent = new Intent(SearchWordsActivity.this, RawWordsActivity.class);
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
                                Toast.makeText(SearchWordsActivity.this, "名称不能为空", Toast.LENGTH_SHORT).show();
                            else if(wordMeaning.getText().toString().equals(""))
                                Toast.makeText(SearchWordsActivity.this, "释义不能为空", Toast.LENGTH_SHORT).show();
                            else {
                                Word word = new Word();
                                word.setName(wordName.getText().toString());
                                word.setMeaning(wordMeaning.getText().toString());
                                word.setSentence(wordSentence.getText().toString());
                                if(DBUtil.add(dbHelper, "Word", word))
                                    Toast.makeText(SearchWordsActivity.this, wordName.getText().toString() + " 已加入词库", Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(SearchWordsActivity.this, "添加失败，请重试", Toast.LENGTH_SHORT).show();
                            }
                        }).setNegativeButton("取消", null);
                builder.create().show();
                break;
            case R.id.help:
                View helpDialog = LayoutInflater.from(SearchWordsActivity.this).inflate(R.layout.dialog_help, null, false);
                AlertDialog.Builder helpBuilder = new AlertDialog.Builder(SearchWordsActivity.this);
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
        try {
            RecyclerView recyclerView = (RecyclerView) view.getParent().getParent();
            if(recyclerView.getId() == R.id.localRecyclerView)
                inflater.inflate(R.menu.contentmenu_search_local, menu);
            else if(recyclerView.getId() == R.id.netRecyclerView)
                inflater.inflate(R.menu.contentmenu_search_net, menu);
        } catch(Exception e) {
            Toast.makeText(SearchWordsActivity.this, "注册菜单失败", Toast.LENGTH_SHORT).show();
        }

        String wordName = ((TextView) view.findViewById(R.id.textView)).getText().toString().split("\n")[0];
        for(int i = 0; i < localWords.size(); i ++) {
            if(wordName.equals(localWords.get(i).getName())) {
                longClickWord = localWords.get(i);
                return;
            }
        }
        for(int i = 0; i < netWords.size(); i ++) {
            if(wordName.equals(netWords.get(i).getName())) {
                longClickWord = netWords.get(i);
                return;
            }
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.addToWord:
                if(DBUtil.add(dbHelper, "Word", longClickWord))
                    Toast.makeText(SearchWordsActivity.this, longClickWord.getName() + " 已加入词库", Toast.LENGTH_SHORT).show();
                break;
            case R.id.addToRaw:
                if(DBUtil.searchWordByName(dbHelper, "Word", longClickWord.getName()) == null)
                    DBUtil.add(dbHelper, "Word", longClickWord);
                if(DBUtil.add(dbHelper, "RawWord", longClickWord))
                    Toast.makeText(SearchWordsActivity.this, longClickWord.getName() + " 已加入生词本", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onContextItemSelected(item);
    }
}