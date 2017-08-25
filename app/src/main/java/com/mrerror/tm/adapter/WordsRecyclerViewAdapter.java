package com.mrerror.tm.adapter;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mrerror.tm.R;
import com.mrerror.tm.models.Word;

import java.util.ArrayList;
import java.util.Locale;

import static com.mrerror.tm.R.id.word;

public class WordsRecyclerViewAdapter extends RecyclerView.Adapter<WordsRecyclerViewAdapter.ViewHolder> {

    private ArrayList<Word> mValues;
    public static TextToSpeech ttobj;
    private Context mContext;

    public WordsRecyclerViewAdapter(ArrayList<Word> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_words, parent, false);
        ttobj = new TextToSpeech(mContext, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                ttobj.setLanguage(Locale.UK);
            }
        }
        );
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mWordView.setText(mValues.get(position).getWord());
        holder.mTranslation.setText("Click to see the translation");


    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView mWordView;
        final ImageView mListenView;
        final TextView mTranslation;
        Word mItem;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mWordView = (TextView) view.findViewById(word);
            mTranslation = (TextView) view.findViewById(R.id.translation);
            mListenView = (ImageView) view.findViewById(R.id.listen);
            mListenView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ttobj.speak(mItem.getWord(), TextToSpeech.QUEUE_FLUSH, null);
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("idddd", getAdapterPosition() + "");
                    v.setTag("clicked");
                    ((TextView) v.findViewById(R.id.translation)).setText(mItem.getTranslation());
                }
            });
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mWordView.getText() + "'";
        }

    }
}
