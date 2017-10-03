package com.mrerror.tm;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mrerror.tm.adapter.ExamsRecyclerViewAdapter;
import com.mrerror.tm.adapter.TestsRecyclerViewAdapter;
import com.mrerror.tm.connection.NetworkConnection;
import com.mrerror.tm.models.Test;
import com.mrerror.tm.models.TestChoices;
import com.mrerror.tm.models.TestComplete;
import com.mrerror.tm.models.TestDialog;
import com.mrerror.tm.models.TestMistake;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.mrerror.tm.connection.NetworkConnection.url;

public class ExamsActivity extends AppCompatActivity implements NetworkConnection.OnCompleteFetchingData {
    TextView blankText;
    ProgressBar mProgressBar;
    String no_list = "List_is_empty";
    private String mTestUrl = null;
    private ArrayList<Test> mTestsList = null;
    private ArrayList<String> mTestsTitlesList = null;
    private ExamsRecyclerViewAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exams);
        getSupportActionBar().setTitle("Exams");
        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
        blankText = (TextView)findViewById(R.id.no_list_net);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mTestsList = new ArrayList<>();
        mTestsTitlesList = new ArrayList<>();

        adapter = new ExamsRecyclerViewAdapter(mTestsTitlesList, mTestsList);
        recyclerView.setAdapter(adapter);
        getData();

        // Set the adapter
    }
    private void getData() {
        if (isOnline()) {
            mProgressBar.setVisibility(View.VISIBLE);
            blankText.setVisibility(View.GONE);

            url = getString(R.string.domain)+"/api/study/mcq/";
            new NetworkConnection(this).getDataAsJsonObject(this);
        } else {
            mProgressBar.setVisibility(View.GONE);
            blankText.setText(getString(R.string.no_interNet));
            blankText.setVisibility(View.VISIBLE);

        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public void onCompleted(String result) throws JSONException {
        JSONObject testsObj = new JSONObject(result);
        if (mTestsList.size() > 0)
            mTestsList = new ArrayList<>();
        if (mTestsTitlesList.size() > 0)
            mTestsTitlesList = new ArrayList<>();
        JSONArray testsJsonArray = testsObj.getJSONArray("results");
        for (int i = 0; i < testsJsonArray.length(); i++) {
            JSONObject testObj = testsJsonArray.getJSONObject(i);
            String testTitle = testObj.getString("title");
            mTestsTitlesList.add(testTitle);
            JSONArray choicesArrObj = testObj.getJSONArray("choices");
            ArrayList<TestChoices> testChoicesArrayList = null;
            for (int j = 0; j < choicesArrObj.length(); j++) {
                if (testChoicesArrayList == null)
                    testChoicesArrayList = new ArrayList<>();
                JSONObject choiceObj = choicesArrObj.getJSONObject(j);
                TestChoices choice = new TestChoices();
                choice.setQuestion(choiceObj.getString("question"));
                choice.setChoice1(choiceObj.getString("choice_one"));
                choice.setChoice2(choiceObj.getString("choice_two"));
                choice.setChoice3(choiceObj.getString("choice_three"));
                choice.setAnswer(choiceObj.getString("answer"));
                testChoicesArrayList.add(choice);
            }
            JSONArray completeArrObj = testObj.getJSONArray("complete");
            ArrayList<TestComplete> testCompleteArrayList = null;
            for (int j = 0; j < completeArrObj.length(); j++) {
                if (testCompleteArrayList == null)
                    testCompleteArrayList = new ArrayList<>();
                JSONObject completeObj = completeArrObj.getJSONObject(j);
                TestComplete complete = new TestComplete(completeObj.getString("description"), completeObj.getString("answer"));
                testCompleteArrayList.add(complete);
            }
            JSONArray dialogArrObj = testObj.getJSONArray("dialog");
            ArrayList<TestDialog> testDialogArrayList = null;
            for (int j = 0; j < dialogArrObj.length(); j++) {
                if (testDialogArrayList == null)
                    testDialogArrayList = new ArrayList<>();
                JSONObject dialogObj = dialogArrObj.getJSONObject(j);
                TestDialog dialog = new TestDialog(dialogObj.getString("description"), dialogObj.getString("first_speaker")
                        , dialogObj.getString("second_speaker"), dialogObj.getString("location"));
                testDialogArrayList.add(dialog);
            }
            JSONArray mistakeArrObj = testObj.getJSONArray("mistake");
            ArrayList<TestMistake> testMistakeArrayList = null;
            for (int j = 0; j < mistakeArrObj.length(); j++) {
                if (testMistakeArrayList == null)
                    testMistakeArrayList = new ArrayList<>();
                JSONObject mistakeObj = mistakeArrObj.getJSONObject(j);
                TestMistake mistake = new TestMistake(mistakeObj.getString("description"), mistakeObj.getString("replace"),
                        mistakeObj.getString("answer"));
                testMistakeArrayList.add(mistake);
            }
            Test test = new Test(testTitle, testChoicesArrayList, testCompleteArrayList, testDialogArrayList, testMistakeArrayList);
            mTestsList.add(test);
        }
        Log.e("list exams",mTestsList.size()+"");
        adapter.notifyDataSetChanged();
        mProgressBar.setVisibility(View.GONE);
        blankText.setVisibility(View.GONE);


    }

    @Override
    public void onError(String error) {
        mProgressBar.setVisibility(View.GONE);
        blankText.setText("an error happened ");
        blankText.setVisibility(View.VISIBLE);
    }
}
