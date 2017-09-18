package com.mrerror.tm.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.mrerror.tm.R;
import com.mrerror.tm.connection.NetworkConnection;
import com.mrerror.tm.models.Word;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import static com.mrerror.tm.R.id.word;

public class WordsRecyclerViewAdapter extends RecyclerView.Adapter<WordsRecyclerViewAdapter.ViewHolder> {

    private ArrayList<Word> mValues;
    public static TextToSpeech ttobj;
    private Context mContext;
    char mFromWhere;
    BankWordDelete mBankWordDelete;

    public  interface  BankWordDelete{

        public void onDleteFromBankWord(int postion);
    }

    public WordsRecyclerViewAdapter(ArrayList<Word> items,char fromWhere) {
        mValues = items;
        mFromWhere=fromWhere;
    }
    public WordsRecyclerViewAdapter(ArrayList<Word> items,char fromWhere,BankWordDelete bankWordDelete) {
        mValues = items;
        mFromWhere=fromWhere;
        mBankWordDelete = bankWordDelete;
    }
    public void onChange(ArrayList<Word> newItems)
    {mValues=newItems;}
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
    public void onBindViewHolder(final ViewHolder holder,  int position) {

     if(mFromWhere=='w') {
         holder.mItem = mValues.get(position);
         holder.mWordBank.setVisibility(View.GONE);
         holder.mWordView.setVisibility(View.VISIBLE);
         holder.mWordView.setText(mValues.get(position).getWord());
        holder.mWordView.setChecked(holder.mItem.ismHasFav());
     }else {

             holder.mItem = mValues.get(position);
             holder.mWordView.setVisibility(View.GONE);
             holder.mWordBank.setVisibility(View.VISIBLE);
             holder.mWordBank.setText(mValues.get(position).getWord());

         holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
             @Override
             public boolean onLongClick(View v) {
                 PopupMenu menu= new PopupMenu(mContext,holder.mView.findViewById(R.id.word));
                 menu.inflate(R.menu.bank_word_delete_option);
                 menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                     @Override
                     public boolean onMenuItemClick(MenuItem item) {
                         switch (item.getItemId()){
                            case R.id.delete_word:
                            {
                               mBankWordDelete.onDleteFromBankWord(holder.getLayoutPosition());
                                break;
                            }
                            default:
                                break;
                        }
                         return false;
                     }
                 });
                 menu.show();
                 return true;}
         });


     }
        holder.mTranslation.setText("Click to see the translation");


    }

    @Override
    public int getItemCount() {

        return mValues.size();

    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final CheckBox mWordView;
        final ImageView mListenView;
        final TextView mTranslation;
        final  TextView mWordBank;

        Word mItem;

        ViewHolder(View view) {
            super(view);

            mView = view;
            mWordBank= (TextView) view.findViewById(R.id.WordsBank_textView);
            mWordView = (CheckBox) view.findViewById(word);
            mTranslation = (TextView) view.findViewById(R.id.translation);
            mListenView = (ImageView) view.findViewById(R.id.listen);

          final   SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(mContext);


            mWordView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    NetworkConnection.url=mContext.getString(R.string.domain)+"/api/study/wordtoggle/"+mItem.getWordId()+"/"+sp.getInt("id",0);;
                  new NetworkConnection(new NetworkConnection.OnCompleteFetchingData() {
                      @Override
                      public void onCompleted(String result) throws JSONException {
                          JSONObject obj= new JSONObject(result);
                          Boolean toggle=obj.getBoolean("toggle");
                          mWordView.setChecked(toggle);

                      }

                      @Override
                      public void onError(String error) {

                      }
                  }).getDataAsJsonObject(mContext);
                }
            });

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
