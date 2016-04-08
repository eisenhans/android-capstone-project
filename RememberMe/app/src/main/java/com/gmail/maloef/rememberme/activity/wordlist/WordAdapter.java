package com.gmail.maloef.rememberme.activity.wordlist;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gmail.maloef.rememberme.R;
import com.gmail.maloef.rememberme.domain.Word;
import com.gmail.maloef.rememberme.persistence.WordCursor;

import butterknife.Bind;
import butterknife.ButterKnife;

public class WordAdapter extends RecyclerView.Adapter<WordAdapter.ViewHolder> {

    private WordCursor wordCursor;

    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.foreign_word_view) TextView foreignWordView;
        @Bind(R.id.native_word_view) TextView nativeWordView;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public WordAdapter(WordCursor wordCursor) {
        this.wordCursor = wordCursor;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_word_list, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (!wordCursor.moveToPosition(position)) {
            return;
        };
        Word word = wordCursor.peek();
        holder.foreignWordView.setText(word.foreignWord);
        holder.nativeWordView.setText(word.nativeWord);
    }

    @Override
    public int getItemCount() {
        return wordCursor.getCount();
    }
}
