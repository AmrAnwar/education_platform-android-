package com.mrerror.tm;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mrerror.tm.adapter.WordsRecyclerViewAdapter;
import com.mrerror.tm.connection.NetworkConnection;
import com.mrerror.tm.dataBases.Contract;
import com.mrerror.tm.dataBases.ModelAnswerDbHelper;
import com.mrerror.tm.models.Word;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;

public class WordsBank extends AppCompatActivity {

   EditText eWord,eTranslate;
    Button saveButton;
     ArrayList<Word> myItems;
    WordsRecyclerViewAdapter mAdapter;
    RecyclerView recycle;
    ModelAnswerDbHelper mDbHelper;
    final  private  String STATE_LOCAL="L";
    final  private  String STATE_LOCAL_SERVER="LS";
    final  private  String STATE_DELETE="D";
    ArrayList<Word> state_L;
    ArrayList<Word> state_D;

    HashSet<String> checkForDataFromServerAndSql;
    SharedPreferences sp;
    Toolbar toolbarFlexibleSpace;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_words_bank);

        initView();
        mDbHelper = new ModelAnswerDbHelper(this);
        sp= PreferenceManager.getDefaultSharedPreferences(this);

        if(isOnline()){
            getDataFromSQL();
            sDelete();
            sPost();
            getDataFromServer();

        }else {
            getDataFromSQL();
        }


    }
    private void initView() {

        checkForDataFromServerAndSql=new HashSet<>();

        myItems=new ArrayList<>();
        state_L=new ArrayList<>();
        state_D=new ArrayList<>();
        saveButton= (Button) findViewById(R.id.saveButton);
        eWord= (EditText) findViewById(R.id.addword);
        eTranslate= (EditText) findViewById(R.id.addtranslate);
        recycle= (RecyclerView) findViewById(R.id.words_bank_list);

        WordsRecyclerViewAdapter.BankWordDelete wordDelete=new WordsRecyclerViewAdapter.BankWordDelete() {
            @Override
            public void onDleteFromBankWord(int postion) {
              changeStateToDelete(myItems.get(postion));

            }
        };


        mAdapter = new WordsRecyclerViewAdapter(myItems,'B',wordDelete);
        saveButton.setOnClickListener(onClickListenerButton);
        recycle.setLayoutManager(new LinearLayoutManager(this));
        recycle.setNestedScrollingEnabled(false);
        recycle.setAdapter(mAdapter);
    }




    private  void getDataFromSQL(){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String[] projection = {
                Contract.TableForWrodsBank.COLUMN_WORD,
                Contract.TableForWrodsBank.COLUMN_TRANSLATE,
                Contract.TableForWrodsBank.COLUMN_STATE,
                Contract.TableForWrodsBank._ID
        };
        Cursor cursor = db.query(
                Contract.TableForWrodsBank.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );
        while (cursor.moveToNext()){
            String word=cursor.getString(cursor.getColumnIndex(Contract.TableForWrodsBank.COLUMN_WORD));
            checkForDataFromServerAndSql.add(word);

            String translate=cursor.getString(cursor.getColumnIndex(Contract.TableForWrodsBank.COLUMN_TRANSLATE));
            String state= cursor.getString(cursor.getColumnIndex(Contract.TableForWrodsBank.COLUMN_STATE));
            int id= cursor.getInt(cursor.getColumnIndex(Contract.TableForWrodsBank._ID));
            Word ref=new Word(word,translate);
            if(state.equals(STATE_LOCAL))
            {   state_L.add(ref);
                myItems.add(ref);
            }else if(state.equals(STATE_LOCAL_SERVER)){
                myItems.add(ref);
                ref.setmWordId(id);
            }else if(state.equals(STATE_DELETE)){
                state_D.add(ref);
                ref.setmWordId(id);
            }
        }
        mAdapter.notifyDataSetChanged();
        cursor.close();
    }



    private  void sDelete(){
          if(!state_D.isEmpty()&&isOnline()){
              for(int i=0;i<state_D.size();i++){
                  deleteFromServer(state_D.get(i));
              }
          }
      }
      private  void sPost(){

          if(!state_L.isEmpty()&&isOnline()){
              for(int i=0;i<state_L.size();i++){

                  postToServer(state_L.get(i));
              }

          }

      }
private void getDataFromServer(){
    NetworkConnection.url= getString(R.string.domain)+"/api/study/bank/?user="+sp.getInt("id",0);
    new NetworkConnection(new NetworkConnection.OnCompleteFetchingData() {
        @Override
        public void onCompleted(String result) throws JSONException {
            JSONArray array= new JSONArray(result);
            Word ref;
            for(int i=0;i<array.length();i++){

                JSONObject obj= array.getJSONObject(i);
                 String mWord=obj.getString("name");
                if(!checkForDataFromServerAndSql.contains(mWord)) {
                    ref = new Word(mWord, obj.getString("translation"));
                    ref.setmWordId(obj.getInt("id"));
                    addNewWordToTable(ref, STATE_LOCAL_SERVER);
                    checkForDataFromServerAndSql.add(mWord);

                }
            }

            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onError(String error) {

        }
    }).getDataAsJsonArray(this);


}
    //add to sqlLite and if it's online and  data not from server it will send data to server
    private void addNewWordToTable(Word word,String state){

        if(!checkForDataFromServerAndSql.contains(word.getWord())) {
            SQLiteDatabase dp = mDbHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(Contract.TableForWrodsBank.COLUMN_WORD, word.getWord());
            contentValues.put(Contract.TableForWrodsBank.COLUMN_TRANSLATE, word.getTranslation());
            contentValues.put(Contract.TableForWrodsBank.COLUMN_STATE, state);
            if (word.getWordId() != 0)
                contentValues.put(Contract.TableForWrodsBank._ID, word.getWordId());

            Long id = dp.insert(Contract.TableForWrodsBank.TABLE_NAME, null, contentValues);
            if (id < 0) {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            } else {

                myItems.add(word);
                checkForDataFromServerAndSql.add(word.getWord());
                Toast.makeText(this, "Done Added To Device", Toast.LENGTH_SHORT).show();

                mAdapter.notifyDataSetChanged();
                if (state.equals(STATE_LOCAL) && isOnline()) {
                    postToServer(word);
                }
            }
        }else {
            Toast.makeText(this, "You have enter this word once if you need to add it again " +
                            "change last letter with _1 example and example_1 ",
                    Toast.LENGTH_LONG).show();

        }
    }


    View.OnClickListener onClickListenerButton=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String mWord="";
            String mTranslate="";
            if(!eWord.getText().toString().isEmpty()){
                mWord  = eWord.getText().toString();}
            else {
                Toast.makeText(WordsBank.this, "Enter English please", Toast.LENGTH_SHORT).show();
                return;}
            if(!eTranslate.getText().toString().isEmpty()) {
                mTranslate = eTranslate.getText().toString();
            }else {
                Toast.makeText(WordsBank.this, "Enter Arabic please", Toast.LENGTH_SHORT).show();
                return; }


            addNewWordToTable(new Word(mWord,mTranslate),STATE_LOCAL);
            mAdapter.notifyDataSetChanged();
            eWord.setText("");
            eTranslate.setText("");

        }
    };


    private  void  postToServer(final Word word){


        NetworkConnection connection=new NetworkConnection(new NetworkConnection.OnCompleteFetchingData() {
            @Override
            public void onCompleted(String result) throws JSONException {
                SQLiteDatabase db=mDbHelper.getWritableDatabase();

                try{
                    String encoding= URLEncoder.encode(result,"ISO-8859-1");
                    result= URLDecoder.decode(encoding,"UTF-8");

                }catch (UnsupportedEncodingException e){

                }


                JSONObject obj=new JSONObject(result);
                int wordid= obj.getInt("id");
                word.setmWordId(wordid);
                String wordString= obj.getString("name");
                Log.d("userID",obj.getString("user"));
                ContentValues values= new ContentValues();
                values.put(Contract.TableForWrodsBank._ID,wordid);
                values.put(Contract.TableForWrodsBank.COLUMN_STATE,STATE_LOCAL_SERVER);
                String selection = Contract.TableForWrodsBank.COLUMN_WORD + " LIKE ?";
                String[] selectionArgs = { wordString };


                int num=     db.update(Contract.TableForWrodsBank.TABLE_NAME,values,selection,selectionArgs);
                if(num<0){
                    Toast.makeText(WordsBank.this, "Error", Toast.LENGTH_SHORT).show();
                }else {

                    state_L.remove(word);
                }
            }

            @Override
            public void onError(String error) {

            }
        } );

        String url=getString(R.string.domain)+"/api/study/bank/";
        int id=sp.getInt("id",0);
        connection.postData(this,url,new String[]{"user","name","translation"}
                ,new String[]{String.valueOf(id),word.getWord(),word.getTranslation()});
    }




    private  void deleteFromServer(final Word word){
        if(word.getWordId()>0) {
            deletefromDataBase(word);
            String url = getString(R.string.domain) + "/api/study/bank/" + word.getWordId() + "/?user=" + sp.getInt("id", 0);
            new NetworkConnection(new NetworkConnection.OnCompleteFetchingData() {
                @Override
                public void onCompleted(String result) throws JSONException {


                }

                @Override
                public void onError(String error) {

                }
            }).deleteData(this, url);
        }else {

            deletefromDataBase(word);

        }

    }

    private void deletefromDataBase(Word word) {
        SQLiteDatabase db=mDbHelper.getWritableDatabase();
        String selection = Contract.TableForWrodsBank.COLUMN_WORD + " LIKE ?";
        String[] selectionArgs = { word.getWord() };
        db.delete(Contract.TableForWrodsBank.TABLE_NAME, selection, selectionArgs);
        state_D.remove(word);
    }

    private  void changeStateToDelete(Word word){
        SQLiteDatabase db= mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Contract.TableForWrodsBank.COLUMN_STATE, STATE_DELETE);
        String selection = Contract.TableForWrodsBank.COLUMN_WORD + " LIKE ?";
        String[] selectionArgs = { word.getWord() };
        int count = db.update(
                Contract.TableForWrodsBank.TABLE_NAME ,
                values,
                selection,
                selectionArgs);
        if(count<0){
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }else {
            state_D.add(word);
            if(isOnline()){
                deleteFromServer(word);
            }
            myItems.remove(word);
            mAdapter.notifyDataSetChanged();
            Toast.makeText(this, "Done delete from device", Toast.LENGTH_SHORT).show();
        }


    }


    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

}
