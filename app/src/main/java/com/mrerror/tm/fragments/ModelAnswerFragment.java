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
import android.widget.CheckBox;
import android.widget.Toast;

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

public class ModelAnswerFragment extends Fragment implements NetworkConnection.OnCompleteFetchingData, ModelAnswerAdapter.OnModelAnswerClick {


    ModelAnswerAdapter mAdabter;
    ModelAnswerDbHelper mDbHelper;
    OnItemClick mOnclick;
    ModelAnswer refrence;

    CheckBox cExam;
    CheckBox cSheet;
    CheckBox cOther;
    String nextURl;
   String url = "http://educationplatform.pythonanywhere.com/api/answers/";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mOnclick = (OnItemClick) context;
        } catch (ClassCastException e) {
            throw new ClassCastException();
        }
    }

    HashMap<Integer, ModelAnswer> itemInDataBase;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        itemInDataBase = new HashMap<>();
        mDbHelper = new ModelAnswerDbHelper(getContext());
        initializeDb();
    }

    @Override
    public void onModelAnserClicked(ModelAnswer modelAnswer) {
        mOnclick.onItemClickLestiner(modelAnswer);
    }

    public interface OnItemClick {

        public void onItemClickLestiner(ModelAnswer item);
    }
//here I get items from data base in hashmap to check after that when i get data from server if I donwload it or not

    ArrayList<ModelAnswer> arryWithOutNetAll;
    ArrayList<ModelAnswer>exams;
    ArrayList<ModelAnswer> sheets;
    ArrayList<ModelAnswer>others;

    public void initializeDb() {
        arryWithOutNetAll = new ArrayList<>();
        exams= new ArrayList<>();
        sheets= new ArrayList<>();
        others= new ArrayList<>();

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String[] projection = {
                Contract.TableForModelAnswer._ID,
                Contract.TableForModelAnswer.COLUMN_FILE_PATH,
                Contract.TableForModelAnswer.COLUMN_EXTENSION,
                Contract.TableForModelAnswer.COLUMN_TYPE,
                Contract.TableForModelAnswer.COLUMN_TITLE,
                Contract.TableForModelAnswer.COLUMN_NOTE};

        Cursor cursor = db.query(
                Contract.TableForModelAnswer.TABLE_NAME,
                projection, null, null, null, null, null);
        while (cursor.moveToNext()) {
            refrence = new ModelAnswer();
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(Contract.TableForModelAnswer._ID));
            refrence.setId(id);
            String filePath = cursor.getString(cursor.getColumnIndex(Contract.TableForModelAnswer.COLUMN_FILE_PATH));
            refrence.setFilePath(filePath);
            String fileExtesion = cursor.getString(cursor.getColumnIndex(Contract.TableForModelAnswer.COLUMN_EXTENSION));
            refrence.setFileExtention(fileExtesion);
            String title = cursor.getString(cursor.getColumnIndex(Contract.TableForModelAnswer.COLUMN_TITLE));
            refrence.setTitle(title);
            String note = cursor.getString(cursor.getColumnIndex(Contract.TableForModelAnswer.COLUMN_NOTE));
            refrence.setNote(note);
            String type = cursor.getString(cursor.getColumnIndex(Contract.TableForModelAnswer.COLUMN_TYPE));
            refrence.setType(type);

            if(refrence.getType().equals("Exam")) exams.add(refrence);
            if(refrence.getType().equals("Sheet"))sheets.add(refrence);
            if (refrence.getType().equals("others"))others.add(refrence);

            arryWithOutNetAll.add(refrence);
            itemInDataBase.put(id, refrence);
        }
        cursor.close();

    }

    public ModelAnswerFragment() {
    }
LoadMoreData loadMoreData;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_modelanswer_with_checbox, container, false);

        cExam= (CheckBox) rootView.findViewById(R.id.exams);
        cSheet=(CheckBox) rootView.findViewById(R.id.sheet);
        cOther=(CheckBox) rootView.findViewById(R.id.other);


        cExam.setOnClickListener(check);
        cSheet.setOnClickListener(check);
        cOther.setOnClickListener(check);
        loadMoreData=new LoadMoreData() {
            @Override
            public void loadMorData() {

                if(!nextURl.equals("null")){
                    getData(nextURl);
                    Toast.makeText(getContext(),nextURl , Toast.LENGTH_SHORT).show();
                }

            }
        };
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.model_answerlist);
        mAdabter = new ModelAnswerAdapter(arryWithOutNetAll, this,loadMoreData);
        mAdabter.inilaize(exams,sheets,others);
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager( linearLayoutManager);
        getData(url);
        recyclerView.setAdapter(mAdabter);
        return rootView;
    }
    ArrayList<ModelAnswer> examAndsheets;
    ArrayList<ModelAnswer> examAndOther;
    ArrayList<ModelAnswer> sheetAndOther;
   View.OnClickListener check= new View.OnClickListener() {
       @Override
       public void onClick(View v) {

           if(cExam.isChecked()&&cSheet.isChecked()){
             if(examAndsheets==null) { examAndsheets  =new ArrayList<>();
               examAndsheets.addAll(exams);
               examAndsheets.addAll(sheets);}
               mAdabter.addNewData(examAndsheets);
           }else if(cExam.isChecked()&&cOther.isChecked()){

               if(examAndOther==null) { examAndOther  =new ArrayList<>();
                   examAndOther.addAll(exams);
                   examAndOther.addAll(others);}

               mAdabter.addNewData(examAndOther);}
           else if(cSheet.isChecked()&&cOther.isChecked()){

               if(sheetAndOther==null) { sheetAndOther  =new ArrayList<>();
                   sheetAndOther.addAll(exams);
                   sheetAndOther.addAll(others);}

               mAdabter.addNewData(sheetAndOther);}
           else if(cExam.isChecked()){
               mAdabter.addNewData(exams);
           }
else if(cSheet.isChecked()){mAdabter.addNewData(sheets);}
           else if(cOther.isChecked()){mAdabter.addNewData(others);}else {mAdabter.addNewData(arryWithOutNetAll);}


       }
   };

    private void getData(String url) {
        NetworkConnection.url = url;
        new NetworkConnection(this).getDataAsJsonObject(getContext());
    }

    @Override
    public void onCompleted(String result) throws JSONException {

        JSONObject newsObj = new JSONObject(result);
         nextURl =newsObj.getString("next");

        JSONArray resultArray = newsObj.getJSONArray("results");
        for (int i = 0; i < resultArray.length(); i++) {
             refrence = new ModelAnswer();
            JSONObject obj = resultArray.getJSONObject(i);
            int id = obj.getInt("id");
            if (itemInDataBase.keySet().contains(id)) {
                continue;
            }
            refrence.setId(id);
            refrence.setTitle(obj.getString("title"));
            refrence.setFileUrl(obj.getString("file"));
            refrence.setNote(obj.getString("note"));
            refrence.setType(obj.getString("type"));

            if(refrence.getType().equals("Exam")) exams.add(refrence);
            if(refrence.getType().equals("Sheet"))sheets.add(refrence);
            if (refrence.getType().equals("others"))others.add(refrence);

            arryWithOutNetAll.add(refrence);
        }
        mAdabter.inilaize(exams,sheets,others);
        mAdabter.addNewData(arryWithOutNetAll);

    }


}
