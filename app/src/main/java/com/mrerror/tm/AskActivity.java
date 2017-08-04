package com.mrerror.tm;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Random;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.mrerror.tm.R.id.img_layout;

public class AskActivity extends AppCompatActivity implements  IPickResult {

    private EditText question;
    SharedPreferences sp ;
    int user_id;
    // Recorder
    boolean start_state = false;
    boolean play_state = false;
    boolean selectToAttachRecording = false;
    LinearLayout recordLayout;
    ImageButton buttonStart, buttonPlayLastRecordAudio,
            buttonDeleteLastRecording ;
    String AudioSavePathInDevice = null;
    MediaRecorder mediaRecorder ;
    Random random ;
    String RandomAudioFileName = "ABCDEFGHIJKLMNOP";
    public static final int RequestPermissionCode = 1;
    MediaPlayer mediaPlayer ;
    /// image
    RelativeLayout imgLayout;
    ImageView selected_img ;
    PickResult mSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Have a question?");
        setSupportActionBar(toolbar);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        user_id = sp.getInt("id",0);
        Button ask = (Button) findViewById(R.id.send);
        question = (EditText) findViewById(R.id.question);
        ask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean canBeSent = false;
                if (question.getText().toString().length() > 7 ) {
                    canBeSent = true;
                }else{
                    if(AudioSavePathInDevice!=null||mSelected!=null){
                        canBeSent = true;
                    }else{
                        Toast.makeText(AskActivity.this, "check your question! text must be more than 7 characters or include one media atleast", Toast.LENGTH_SHORT).show();
                    }
                }
                if(canBeSent) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            HttpClient httpclient = new DefaultHttpClient();
                            HttpPost httpost = new HttpPost("http://educationplatform.pythonanywhere.com/api/asks/create/");

                            MultipartEntity entity = new MultipartEntity();
                            try {
                                entity.addPart("user", new StringBody(String.valueOf(user_id)));
                                entity.addPart("question", new StringBody(question.getText().toString()));
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            if(AudioSavePathInDevice!=null)
                                entity.addPart("file_sender", new FileBody(new File(AudioSavePathInDevice)));
                            if(mSelected!=null)
                                entity.addPart("image_sender", new FileBody(new File(mSelected.getPath())));
//                    entity.addPart("myAudioFile", new FileBody(audioFile));

                            httpost.setEntity(entity);
                            HttpResponse response;
                            try {
                                response = httpclient.execute(httpost);
                                String responseBody = EntityUtils.toString(response.getEntity());
                                Log.e("Respone",responseBody);
                                AskActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        AlertDialog.Builder dialog = new AlertDialog.Builder(AskActivity.this);
                                        dialog.setTitle("Ask question!");
                                        dialog.setMessage("Your question has been delivered!");
                                        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                AskActivity.this.finish();
                                            }
                                        });
                                        dialog.setCancelable(false);
                                        dialog.setNegativeButton("Another question?", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                question.setText("");
                                                selected_img = null;
                                                imgLayout.setVisibility(View.GONE);
                                                AudioSavePathInDevice = null;
                                                recordLayout.setVisibility(View.GONE);
                                                dialog.dismiss();
                                            }
                                        });
                                        dialog.show();
                                    }
                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    String url = "http://educationplatform.pythonanywhere.com/api/asks/create/";
//                    new NetworkConnection(AskActivity.this).postData(AskActivity.this, url, new String[]{"user", "question"},
//                            new String[]{String.valueOf(user_id), question.getText().toString()});



                }
            }
        });


        // Recorder
        recordLayout = (LinearLayout) findViewById(R.id.record_layout);
        imgLayout = (RelativeLayout) findViewById(img_layout);
        buttonStart = (ImageButton) findViewById(R.id.start_rec_btn);
        buttonPlayLastRecordAudio = (ImageButton) findViewById(R.id.play_rec_btn);
        buttonDeleteLastRecording = (ImageButton)findViewById(R.id.remove_rec_btn);
        selected_img = (ImageView)findViewById(R.id.selected_img);

        buttonPlayLastRecordAudio.setEnabled(false);

        random = new Random();

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!start_state) {
                    if (checkPermission()) {
                        AudioSavePathInDevice =
                                Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                                        CreateRandomAudioFileName(5) + "AudioRecording.3gp";

                        MediaRecorderReady();

                        try {
                            mediaRecorder.prepare();
                            mediaRecorder.start();
                        } catch (IllegalStateException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        start_state = true;
                        buttonDeleteLastRecording.setEnabled(false);
                        buttonPlayLastRecordAudio.setEnabled(false);
                        buttonStart.setImageResource(R.drawable.ic_stop);
                        Toast.makeText(AskActivity.this, "Recording started",
                                Toast.LENGTH_LONG).show();
                    } else {
                        requestPermission();
                    }

                }else{
                    mediaRecorder.stop();
                    buttonStart.setImageResource(R.drawable.ic_record);
                    start_state = false;
                    buttonStart.setEnabled(true);
                    buttonDeleteLastRecording.setEnabled(true);
                    buttonPlayLastRecordAudio.setEnabled(true);
                    Toast.makeText(AskActivity.this, "Recording Completed",
                            Toast.LENGTH_LONG).show();
                }
            }
        });


        buttonPlayLastRecordAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) throws IllegalArgumentException,
                    SecurityException, IllegalStateException {
                if(!play_state) {
                    buttonStart.setEnabled(false);
                    buttonDeleteLastRecording.setEnabled(false);
                    buttonPlayLastRecordAudio.setImageResource(R.drawable.ic_stop);
                    play_state = true;
                    mediaPlayer = new MediaPlayer();
                    try {
                        mediaPlayer.setDataSource(AudioSavePathInDevice);
                        mediaPlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    mediaPlayer.start();
                    Toast.makeText(AskActivity.this, "Recording Playing",
                            Toast.LENGTH_LONG).show();
                }else{
                    buttonStart.setEnabled(true);
                    buttonDeleteLastRecording.setEnabled(true);
                    buttonPlayLastRecordAudio.setImageResource(R.drawable.ic_play);
                    play_state = false;
                    buttonPlayLastRecordAudio.setEnabled(true);

                    if(mediaPlayer != null){
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        MediaRecorderReady();
                    }
                }
            }
        });

        buttonDeleteLastRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(AskActivity.this, "clicked", Toast.LENGTH_SHORT).show();
                File file = new File(AudioSavePathInDevice);
                boolean deleted = file.delete();
                if(deleted){
                    Toast.makeText(AskActivity.this, "the file is deleted", Toast.LENGTH_SHORT).show();
                    AudioSavePathInDevice = null;
                }
            }
        });
    }

    //Recorder
    public void MediaRecorderReady(){
        mediaRecorder=new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(AudioSavePathInDevice);
    }

    public String CreateRandomAudioFileName(int string){
        StringBuilder stringBuilder = new StringBuilder( string );
        int i = 0 ;
        while(i < string ) {
            stringBuilder.append(RandomAudioFileName.
                    charAt(random.nextInt(RandomAudioFileName.length())));

            i++ ;
        }
        return stringBuilder.toString();
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(AskActivity.this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length> 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(AskActivity.this, "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(AskActivity.this,"Permission Denied",Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }

    public void useRecord(View view) {
        if(!selectToAttachRecording) {
            recordLayout.setVisibility(View.VISIBLE);
            selectToAttachRecording = true;
        }else{
            recordLayout.setVisibility(View.GONE);
            selectToAttachRecording = false;
        }
    }

    public void attachImg(View view) {
        imgLayout.setVisibility(View.VISIBLE);
        PickImageDialog.build(new PickSetup()).show(this);
    }

    @Override
    public void onPickResult(PickResult r) {
        if (r.getError() == null) {
            //If you want the Uri.
            //Mandatory to refresh image from Uri.
            //getImageView().setImageURI(null);

            //Setting the real returned image.
            //getImageView().setImageURI(r.getUri());

            //If you want the Bitmap.
            mSelected = r;
            selected_img.setImageBitmap(r.getBitmap());

            //Image path
            //r.getPath();
        } else {
            //Handle possible errors
            //TODO: do what you have to do with r.getError();
            Toast.makeText(this, r.getError().getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void deleteImg(View view) {
        imgLayout.setVisibility(View.GONE);
        mSelected = null;
        selected_img.setImageResource(R.drawable.ic_close);
    }
}
