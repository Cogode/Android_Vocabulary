package com.example.vocabulary.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.vocabulary.R;
import com.example.vocabulary.domain.Word;

public class WordContentFragment extends Fragment {
    private TextView wordName;
    private TextView wordMeaning;
    private TextView wordSentence;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_right, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        wordName = view.findViewById(R.id.wordName);
        wordMeaning = view.findViewById(R.id.wordMeaning);
        wordSentence = view.findViewById(R.id.wordSentence);
    }

    public void refresh(Word word) {
        if(word != null) {
            wordName.setText(word.getName());
            wordMeaning.setText("释义：" + word.getMeaning());
            String sentence = "例句：";
            if(word.getSentence().equals(""))
                sentence += "暂无例句";
            else
                sentence += word.getSentence();
            wordSentence.setText(sentence);
        }
        else {
            wordName.setText("");
            wordMeaning.setText("");
            wordSentence.setText("");
        }
    }
}
