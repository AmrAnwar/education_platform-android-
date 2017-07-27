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


  public   ModelAnswerAdapter mAdabter;
    ModelAnswerDbHelper mDbHelper;
    OnItemClick mOnclick;
    ModelAnswer refrence;
  public static   boolean shouldResume= false;
    HashMap<Integer, ModelAnswer> itemInDataBase;
    ArrayList<ModelAnswer> arryWithOutNetAll;
    ArrayList<ModelAnswer>exams;
    ArrayList<ModelAnswer> sheets;
    ArrayList<ModelAnswer>others;
    RecyclerView recyclerView;
    CheckBox cExam;
    CheckBox cSheet;
    CheckBox cOther;
    String nextURl="";
   String url = "http://educationplatform.pythonanywhere.com/api/answers/";
    String urlExamOnly="http://educationplatform.pythonanywhere.com/api/answers/?type=b";
    String urlSheetOnly="http://educationplatform.pythonanywhere.com/api/answers/?type=a";
    String urlOtherOnly="http://educationplatform.pythonanywhere.com/api/answers/?type=c";
    String urlNextFilter="";

    int countAll=0;
    int countExam=0;
    int countSheet=0;
    int countOther=0;
    int scrolFalg=0;



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mOnclick = (OnItemClick) context;
        } catch (ClassCastException e) {
            throw new ClassCastException();
        }
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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



    public void initializeDb() {
        itemInDataBase = new HashMap<>();
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
            public void loadMorData(String url) {

              Toast.makeText(getContext(),"loading", Toast.LENGTH_SHORT).show();
                   examAndsheets = null;
                   examAndOther = null;
                   sheetAndOther = null;

                       getData(url);



 }
 };
        recyclerView = (RecyclerView) rootView.findViewById(R.id.model_answerlist);
        mAdabter = new ModelAnswerAdapter(arryWithOutNetAll, this);
        mAdabter.inilaize(exams,sheets,others);
        final LinearLayoutManager linearLayoutManager= new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager( linearLayoutManager);

        recyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
            int x=   linearLayoutManager.findLastCompletelyVisibleItemPosition();

                if(x %3==0&& x>=3 &&x>scrolFalg &&arryWithOutNetAll.size()<countAll&&!nextURl.equals("null"))
                { if((cExam.isChecked()||cSheet.isChecked()||cOther.isChecked())&&
                        (!urlNextFilter.equals("null")&&!urlNextFilter.equals("")))
                {
                    loadMoreData.loadMorData(urlNextFilter);
                    scrolFalg=x;
                }

                 else   { loadMoreData.loadMorData(nextURl);
                    System.out.println(x);
                    scrolFalg=x;
                 }
                }
            }
        });


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
           if (cExam.isChecked() && cOther.isChecked() && cSheet.isChecked()) {
               urlNextFilter="";
               mAdabter.addNewData(arryWithOutNetAll);}
           else if (cExam.isChecked() && cSheet.isChecked()) {
               if (examAndsheets == null) {
                   examAndsheets = new ArrayList<>();
                   examAndsheets.addAll(exams);
                   examAndsheets.addAll(sheets);}
               urlNextFilter = "";
               mAdabter.addNewData(examAndsheets);}
           else if (cExam.isChecked() && cOther.isChecked()) {
               if (examAndOther == null) {
                   examAndOther = new ArrayList<>();
                   examAndOther.addAll(exams);
                   examAndOther.addAll(others);
               }
               urlNextFilter="";
               mAdabter.addNewData(examAndOther);
           } else if (cSheet.isChecked() && cOther.isChecked()) {

               if (sheetAndOther == null) {
                   sheetAndOther = new ArrayList<>();
                   sheetAndOther.addAll(sheets);
                   sheetAndOther.addAll(others);
               }
               urlNextFilter="";
               mAdabter.addNewData(sheetAndOther);
           } else if (cExam.isChecked()) {
               if(arryWithOutNetAll.size()<countAll)
               { getData(urlExamOnly);
               scrolFalg=0;}
               mAdabter.addNewData(exams);
           } else if (cSheet.isChecked()) {
               if(arryWithOutNetAll.size()<countAll)
               { getData(urlSheetOnly);
                   scrolFalg=0;}
               mAdabter.addNewData(sheets);
           } else if (cOther.isChecked()) {
               if(arryWithOutNetAll.size()<countAll)
               { getData(urlOtherOnly);
                   scrolFalg=0;}
               mAdabter.addNewData(others);
           } else {
               mAdabter.addNewData(arryWithOutNetAll);
           }
       }
   };





    private void getData(String url) {
        NetworkConnection.url = url;
        new NetworkConnection(this).getDataAsJsonObject(getContext());
    }

    @Override
    public void onResume() {
        super.onResume();
if(shouldResume) {
    Toast.makeText(getContext(), "onResume", Toast.LENGTH_SHORT).show();

    exams=null;
    sheets=null;
    others=null;
    arryWithOutNetAll=null;

    examAndsheets = null;
    examAndOther = null;
    sheetAndOther = null;
    itemInDataBase=null;
    initializeDb();
    getData(url);
    mAdabter.notifyDataSetChanged();
    shouldResume=false;
}

    }

    @Override
    public void onCompleted(String result) throws JSONException {

        JSONObject newsObj = new JSONObject(result);

        if(cExam.isChecked()&&!cOther.isChecked()&&!cSheet.isChecked()){
            urlNextFilter= newsObj.getString("next");
            countExam=newsObj.getInt("count");
        }else if(cSheet.isChecked()&&!cOther.isChecked()&&!cExam.isChecked())
        { urlNextFilter= newsObj.getString("next");
        countSheet=newsObj.getInt("count");}
        else if(cOther.isChecked()&&!cSheet.isChecked()&&!cExam.isChecked()){
            urlNextFilter= newsObj.getString("next");
            countOther=newsObj.getInt("count");
        }else {
            nextURl =newsObj.getString("next");
            countAll=newsObj.getInt("count");}


        JSONArray resultArray = newsObj.getJSONArray("results");
        for (int i = 0; i < resultArray.length(); i++) {
             refrence = new ModelAnswer();
            JSONObject obj = resultArray.getJSONObject(i);
            int id = obj.getInt("id");
            if (itemInDataBase.keySet().contains(id)&&itemInDataBase.get(id).getDwonload()) {
                continue;
            }else if(itemInDataBase.keySet().contains(id)){

                continue;

            }else {
                refrence.setId(id);
                refrence.setTitle(obj.getString("title"));
                refrence.setFileUrl(obj.getString("file"));
                refrence.setNote(obj.getString("note"));
                refrence.setType(obj.getString("type"));

                if (refrence.getType().equals("Exam")) exams.add(refrence);
                if (refrence.getType().equals("Sheet")) sheets.add(refrence);
                if (refrence.getType().equals("others")) others.add(refrence);

                arryWithOutNetAll.add(refrence);
                itemInDataBase.put(id,refrence);
            }
        }
        mAdabter.inilaize(exams,sheets,others);
        if(cExam.isChecked()&&cSheet.isChecked()){
            examAndsheets=null;
            examAndsheets=new ArrayList<>();
            examAndsheets.addAll(exams);
            examAndsheets.addAll(sheets);
            mAdabter.addNewData(examAndsheets);

        }else if(cExam.isChecked()&&cOther.isChecked()){examAndsheets=null;
           examAndOther=null;
            examAndOther=new ArrayList<>();
            examAndOther.addAll(exams);
            examAndOther.addAll(others);
            mAdabter.addNewData(examAndOther);}else if(cSheet.isChecked()&&cOther.isChecked()){
            sheetAndOther=null;
            sheetAndOther=new ArrayList<>();
            sheetAndOther.addAll(sheets);
            sheetAndOther.addAll(others);

        }
        else if(cExam.isChecked()&&!cOther.isChecked()&&!cSheet.isChecked()){
        mAdabter.addNewData(exams);
        }else if(cSheet.isChecked()&&!cOther.isChecked()&&!cExam.isChecked())
        {mAdabter.addNewData(sheets); }
        else if(cOther.isChecked()&&!cSheet.isChecked()&&!cExam.isChecked()){
           mAdabter.addNewData(others);
        }else {
            mAdabter.addNewData(arryWithOutNetAll);
           }


    }


}
