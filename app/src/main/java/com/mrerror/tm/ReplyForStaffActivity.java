package com.mrerror.tm;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mrerror.tm.connection.NetworkConnection;
import com.mrerror.tm.models.QuestionForStaff;

import org.json.JSONException;

public class ReplyForStaffActivity extends AppCompatActivity implements NetworkConnection.OnCompleteFetchingData {

    private EditText reply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply_for_staff);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final QuestionForStaff questionForStaff = getIntent().getParcelableExtra("question");
        Button replyBtn = (Button) findViewById(R.id.send);
        TextView question = (TextView) findViewById(R.id.question);
        TextView username = (TextView) findViewById(R.id.username);
        question.setText(questionForStaff.getQuestion());
        username.setText("By : "+questionForStaff.getUsername());
        reply = (EditText) findViewById(R.id.reply);
        replyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reply.getText().toString().length() <= 1) {
                    Toast.makeText(ReplyForStaffActivity.this, "Very short reply!", Toast.LENGTH_SHORT).show();
                } else {
                    String url = questionForStaff.getLinkToEdit();
                    new NetworkConnection(ReplyForStaffActivity.this).putData(ReplyForStaffActivity.this,url,
                            new String[]{"question","replay"},new String[]{questionForStaff.getQuestion(),reply.getText().toString()});
                }
            }
        });
    }

    @Override
    public void onCompleted(String result) throws JSONException {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Answer a question!");
        dialog.setMessage("Your reply has been delivered!");
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                ReplyForStaffActivity.this.finish();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }
}
