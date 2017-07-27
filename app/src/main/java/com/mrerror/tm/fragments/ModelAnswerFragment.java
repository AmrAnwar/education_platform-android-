package com.mrerror.tm.fragments;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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


    public ModelAnswerAdapter mAdabter;
    ModelAnswerDbHelper mDbHelper;
    OnItemClick mOnclick;
    ModelAnswer refrence;
    public static boolean shouldResume = false;
    HashMap<Integer, ModelAnswer> itemInDataBase;
    ArrayList<ModelAnswer> arryWithOutNetAll;
    ArrayList<ModelAnswer> exams;
    ArrayList<ModelAnswer> sheets;
    ArrayList<ModelAnswer> others;
    RecyclerView recyclerView;
    CheckBox cExam;
    CheckBox cSheet;
    CheckBox cOther;
    String nextURl = "";
    String url = "http://educationplatform.pythonanywhere.com/api/answers/";
    String urlExamOnly = "http://educationplatform.pythonanywhere.com/api/answers/?type=b";
    String urlSheetOnly = "http://educationplatform.pythonanywhere.com/api/answers/?type=a";
    String urlOtherOnly = "http://educationplatform.pythonanywhere.com/api/answers/?type=c";
    String urlNextFilter = "";

    int countAll = 0;
    int countExam = 0;
    int countSheet = 0;
    int countOther = 0;
    int scrolFalg = 0;
    SwipeRefreshLayout swipeRefreshLayout;
    ArrayList<ModelAnswer> examAndsheets;
    ArrayList<ModelAnswer> examAndOther;
    ArrayList<ModelAnswer> sheetAndOther;


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
public boolean isOnline() {
    ConnectivityManager cm =
            (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo netInfo = cm.getActiveNetworkInfo();
    return netInfo != null && netInfo.isConnectedOrConnecting();
}

    public void initializeDb() {
        itemInDataBase = new HashMap<>();
        arryWithOutNetAll = new ArrayList<>();
        if (exams == null) exams = new ArrayList<>();
        if (sheets == null) sheets = new ArrayList<>();
        if (others == null) others = new ArrayList<>();
        if (examAndsheets == null) examAndsheets = new ArrayList<>();
        if (examAndOther == null) examAndOther = new ArrayList<>();
        if (sheetAndOther == null) sheetAndOther = new ArrayList<>();

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String[] projection = {
                Contract.TableForModelAnswer._ID,
                Contract.TableForModelAnswer.COLUMN_FILE_PATH,
                Contract.TableForModelAnswer.COLUMN_EXTENSION,
                Contract.TableForModelAnswer.COLUMN_TYPE,
                Contract.TableForModelAnswer.COLUMN_TITLE,
                Contract.TableForModelAnswer.COLUMN_NOTE,
                Contract.TableForModelAnswer.COLUMN_FILE_LOCATION};

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
            String location=cursor.getString(cursor.getColumnIndex(Contract.TableForModelAnswer.COLUMN_FILE_LOCATION));
            refrence.setFileLocal(location);

            if (refrence.getType().equals("Exam"))
            { exams.add(refrence);
                examAndOther.add(refrence);
                examAndsheets.add(refrence);
            }
            else if (refrence.getType().equals("Sheet"))
            {sheets.add(refrence);
                examAndsheets.add(refrence);
                sheetAndOther.add(refrence);
            }
            else if (refrence.getType().equals("others"))
            {  others.add(refrence);
                examAndOther.add(refrence);
                sheetAndOther.add(refrence);
            }

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

         swipeRefreshLayout= (SwipeRefreshLayout) rootView.findViewById(R.id.refresher);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
              if(isOnline()) { reFresh();}else {
                  Toast.makeText(getContext(), "No InterNet", Toast.LENGTH_SHORT).show();
                  swipeRefreshLayout.setRefreshing(false);
              }
            }
        });

        cExam = (CheckBox) rootView.findViewById(R.id.exams);
        cSheet = (CheckBox) rootView.findViewById(R.id.sheet);
        cOther = (CheckBox) rootView.findViewById(R.id.other);
        cExam.setOnClickListener(check);
        cSheet.setOnClickListener(check);
        cOther.setOnClickListener(check);
        loadMoreData = new LoadMoreData() {
            @Override
            public void loadMorData(String url) {

                getData(url);
            }
        };
        recyclerView = (RecyclerView) rootView.findViewById(R.id.model_answerlist);
        mAdabter = new ModelAnswerAdapter(arryWithOutNetAll, this);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                int x = linearLayoutManager.findLastCompletelyVisibleItemPosition();

                if (x % 3 == 0 && x >= 3 && x > scrolFalg && arryWithOutNetAll.size() < countAll && !nextURl.equals("null")) {
                    if ((cExam.isChecked() || cSheet.isChecked() || cOther.isChecked()) &&
                            (!urlNextFilter.equals("null") && !urlNextFilter.equals(""))) {
                        loadMoreData.loadMorData(urlNextFilter);
                        scrolFalg = x;
                    } else {
                        loadMoreData.loadMorData(nextURl);

                        scrolFalg = x;
                    }
                }
            }
        });


        getData(url);
        recyclerView.setAdapter(mAdabter);

        return rootView;
    }


    View.OnClickListener check = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (cExam.isChecked() && cOther.isChecked() && cSheet.isChecked()) {
                urlNextFilter = "";
                upDateUi(arryWithOutNetAll);
            } else if (cExam.isChecked() && cSheet.isChecked()) {
                urlNextFilter = "";
                upDateUi(examAndsheets);
            } else if (cExam.isChecked() && cOther.isChecked()) {

                urlNextFilter = "";
                upDateUi(examAndOther);
            } else if (cSheet.isChecked() && cOther.isChecked()) {
                urlNextFilter = "";
                upDateUi(sheetAndOther);
            } else if (cExam.isChecked()) {

                if (arryWithOutNetAll.size() < countAll) {
                    getData(urlExamOnly);
                    scrolFalg = 0;
                }
                upDateUi(exams);
            } else if (cSheet.isChecked()) {
                if (arryWithOutNetAll.size() < countAll) {
                    getData(urlSheetOnly);
                    scrolFalg = 0;
                }
                upDateUi(sheets);
            } else if (cOther.isChecked()) {
                if (arryWithOutNetAll.size() < countAll) {
                    getData(urlOtherOnly);
                    scrolFalg = 0;
                }
                upDateUi(others);

            } else {
                upDateUi(arryWithOutNetAll);
            }
        }
    };


    private void upDateUi(ArrayList<ModelAnswer> items){
        mAdabter.addNewData(items);
        mAdabter.notifyDataSetChanged();
        recyclerView.invalidate();

    }

    private void getData(String url) {
        NetworkConnection.url = url;
        new NetworkConnection(this).getDataAsJsonObject(getContext());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (shouldResume) {
            reFresh();
            shouldResume = false;
        }

    }

    private void reFresh(){
        exams=null;
        sheets=null;
        others=null;
        scrolFalg = 0;
        examAndsheets = null;
        examAndOther = null;
        sheetAndOther = null;
        nextURl="";
        urlNextFilter="";
        initializeDb();
        if (cExam.isChecked() && !cOther.isChecked() && !cSheet.isChecked()) {
            getData(urlExamOnly);
            }else if(cSheet.isChecked() && !cOther.isChecked() && !cExam.isChecked()){
            getData(urlSheetOnly);
        }else if(cOther.isChecked() && !cSheet.isChecked() && !cExam.isChecked()){
            getData(urlOtherOnly);
        }else {
        getData(url);
    }

    }

    @Override
    public void onCompleted(String result) throws JSONException {

        JSONObject newsObj = new JSONObject(result);
        swipeRefreshLayout.setRefreshing(false);

        if (cExam.isChecked() && !cOther.isChecked() && !cSheet.isChecked()) {
            urlNextFilter = newsObj.getString("next");
            countExam = newsObj.getInt("count");
        } else if (cSheet.isChecked() && !cOther.isChecked() && !cExam.isChecked()) {
            urlNextFilter = newsObj.getString("next");
            countSheet = newsObj.getInt("count");
        } else if (cOther.isChecked() && !cSheet.isChecked() && !cExam.isChecked()) {
            urlNextFilter = newsObj.getString("next");
            countOther = newsObj.getInt("count");
        } else {
            nextURl = newsObj.getString("next");
            countAll = newsObj.getInt("count");
        }


        JSONArray resultArray = newsObj.getJSONArray("results");
        for (int i = 0; i < resultArray.length(); i++) {
            refrence = new ModelAnswer();
            JSONObject obj = resultArray.getJSONObject(i);
            int id = obj.getInt("id");
            if (itemInDataBase.keySet().contains(id) && itemInDataBase.get(id).getDwonload()) {

                continue;
            } else if (itemInDataBase.keySet().contains(id)) {


                continue;

            } else {
                refrence.setId(id);
                refrence.setTitle(obj.getString("title"));
                refrence.setFileUrl(obj.getString("file"));
                refrence.setNote(obj.getString("note"));
                refrence.setType(obj.getString("type"));

                if (refrence.getType().equals("Exam"))
                { exams.add(refrence);
                    examAndOther.add(refrence);
                    examAndsheets.add(refrence);
                }
               else if (refrence.getType().equals("Sheet"))
                {sheets.add(refrence);
                    examAndsheets.add(refrence);
                    sheetAndOther.add(refrence);
                }
              else if (refrence.getType().equals("others"))
                {  others.add(refrence);
                    examAndOther.add(refrence);
                    sheetAndOther.add(refrence);
                }

                arryWithOutNetAll.add(refrence);
                itemInDataBase.put(id, refrence);
            }
        }

        if (cExam.isChecked() && cSheet.isChecked()) {
            if(examAndsheets.size()<4&&arryWithOutNetAll.size()<countAll){
                if(!nextURl.equals("null")&&!nextURl.equals(""))
                loadMoreData.loadMorData(nextURl);}
          else {

                upDateUi(examAndsheets);
            }
        } else if (cExam.isChecked() && cOther.isChecked()) {

            if(examAndOther.size()<4&&arryWithOutNetAll.size()<countAll)
            {
                if(!nextURl.equals("null")&&!nextURl.equals(""))
                    loadMoreData.loadMorData(nextURl);
            }else
            {upDateUi(examAndOther);}

        } else if (cSheet.isChecked() && cOther.isChecked()) {

            if(sheetAndOther.size()<4&&arryWithOutNetAll.size()<countAll) {
                if(!nextURl.equals("null")&&!nextURl.equals(""))
                    loadMoreData.loadMorData(nextURl);}
            else {
                upDateUi(sheetAndOther);
            }
        } else if (cExam.isChecked() && !cOther.isChecked() && !cSheet.isChecked()) {

upDateUi(exams);
        } else if (cSheet.isChecked() && !cOther.isChecked() && !cExam.isChecked()) {
          upDateUi(sheets);
        } else if (cOther.isChecked() && !cSheet.isChecked() && !cExam.isChecked()) {
            upDateUi(others);

        } else {
          upDateUi(arryWithOutNetAll);
        }


    }


}
