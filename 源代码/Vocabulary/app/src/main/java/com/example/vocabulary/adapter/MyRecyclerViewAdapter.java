package com.example.vocabulary.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vocabulary.R;
import com.example.vocabulary.domain.Word;

import java.util.ArrayList;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {
    private ArrayList<Word> words;
    private OnItemClickListener itemClickListener;
    private OnItemLongClickListener longClickListener;
    private OnButtonClickListener buttonClickListener;

    public interface OnItemClickListener {
        void onClick(View view);
    }

    public interface OnItemLongClickListener {
        void onLongClick(View view);
    }

    public interface OnButtonClickListener {
        void onClick(View view);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final View view;
        private final TextView textView;
        private final Button button;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView.findViewById(R.id.parent_view);
            textView = itemView.findViewById(R.id.textView);
            button = itemView.findViewById(R.id.button);
        }
    }

    public MyRecyclerViewAdapter(ArrayList<Word> words, OnItemClickListener itemClickListener, OnItemLongClickListener longClickListener, OnButtonClickListener buttonClickListener) {
        this.words = words;
        this.itemClickListener = itemClickListener;
        this.longClickListener = longClickListener;
        this.buttonClickListener = buttonClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.word_row_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Word word = words.get(position);
        holder.textView.setText(word.getName() + "\n" + word.getMeaning());
        holder.view.setOnClickListener(view -> itemClickListener.onClick(view));
        longClickListener.onLongClick(holder.view);
        holder.button.setOnClickListener(view -> buttonClickListener.onClick(view));
    }

    @Override
    public int getItemCount() {
        return words.size();
    }
}
