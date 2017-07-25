package com.mrerror.tm.fragments;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mrerror.tm.R;
import com.mrerror.tm.connection.NetworkConnection;
import com.mrerror.tm.dataBases.Contract;
import com.mrerror.tm.dataBases.ModelAnswerDbHelper;
import com.mrerror.tm.models.ModelAnswer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by kareem on 7/24/2017.
 */

public class ModelAnswerFragment extends Fragment implements NetworkConnection.OnCompleteFetchingData,ModelAnswerAdapter.OnModelAnswerClick{
      ArrayList<ModelAnswer> mModelArray;

    ModelAnswerAdapter mAdabter;
    ModelAnswerDbHelper mDbHelper;
    OnItemClick mOnclick;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
     try {
         mOnclick = (OnItemClick) context;
     }catch (ClassCastException e){throw new ClassCastException();}
    }

    HashMap<Integer,String> filePathInDataBase;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        filePathInDataBase=new HashMap<>();
        mDbHelper=new ModelAnswerDbHelper(getContext());
        initializeDb();
    }

    @Override
    public void onModelAnserClicked(ModelAnswer modelAnswer) {
        mOnclick.onItemClickLestiner(modelAnswer);
    }

    public  interface  OnItemClick{

        public void onItemClickLestiner(ModelAnswer item);
    }
//here I get items from data base in hashmap to check after that when i get data from server if I donwload it or not
    public void initializeDb(){

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String[] projection = {
                Contract.TableForModelAnswer._ID,
                Contract.TableForModelAnswer.COLUMN_FILE_PATH,};

        Cursor cursor = db.query(
                Contract.TableForModelAnswer.TABLE_NAME,
                projection,null,null,null,null,null);
        while(cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(Contract.TableForModelAnswer._ID));
            String filepath=cursor.getString(cursor.getColumnIndex(Contract.TableForModelAnswer.COLUMN_FILE_PATH));
            filePathInDataBase.put(id,filepath);
        }
        cursor.close();

    }

    public ModelAnswerFragment(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_news_list,container,false);
        RecyclerView recyclerView= (RecyclerView) rootView.findViewById(R.id.list);
         mAdabter=new ModelAnswerAdapter(new ArrayList<ModelAnswer>(),this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        getData();
        recyclerView.setAdapter(mAdabter);
        return rootView;
    }

    private void getData( ) {
        String url = "http://educationplatform.pythonanywhere.com/api/answers/";
        NetworkConnection.url = url;
        new NetworkConnection(this).getDataAsJsonObject(getContext());
    }

    @Override
    public void onCompleted(String result) throws JSONException {

        JSONObject newsObj = new JSONObject(result);
        mModelArray = new ArrayList<>();
        JSONArray resultArray = newsObj.getJSONArray("results");
        for(int i = 0 ; i < resultArray.length();i++) {
            ModelAnswer modelAnswer = new ModelAnswer();
            JSONObject obj = resultArray.getJSONObject(i);
            int id= obj.getInt("id");
            if(filePathInDataBase.keySet().contains(id)){
                modelAnswer.setFilePath(filePathInDataBase.get(id));

            }
            modelAnswer.setId(id);
            modelAnswer.setTitle(obj.getString("title"));
            modelAnswer.setFileUrl(obj.getString("file"));
            modelAnswer.setNote(obj.getString("note"));
            modelAnswer.setType(obj.getString("type"));
            mModelArray.add(modelAnswer);
        }
        mAdabter.addNewData(mModelArray);

    }



}
