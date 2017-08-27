package com.mrerror.tm;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mrerror.tm.models.QuestionForStaff;
import com.squareup.picasso.Picasso;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.mrerror.tm.Inbox.replyListener;

//import org.apache.http.entity.mime.MultipartEntity;
//import org.apache.http.entity.mime.content.FileBody;
//import org.apache.http.entity.mime.content.StringBody;

public class ReplyForStaffActivity extends AppCompatActivity implements IPickResult {

    private EditText reply;
    // Recorder
    boolean start_state = false;
    boolean play_state = false;
    boolean selectToAttachRecording = false;
    LinearLayout recordLayout;
    ImageButton buttonStart, buttonPlayLastRecordAudio,
            buttonDeleteLastRecording, playBtn;
    String AudioSavePathInDevice = null;
    MediaRecorder mediaRecorder;
    Random random;
    String RandomAudioFileName = "ABCDEFGHIJKLMNOP";
    public static final int RequestPermissionCode = 1;
    MediaPlayer mediaPlayer;
    /// image
    RelativeLayout imgLayout;
    ImageView selected_img;
    ImageView studentImg;
    LinearLayout studentImgLayout;
    ProgressBar loadingImg;

    PickResult mSelected;
    private MediaPlayer mediaPlayer2;
    private QuestionForStaff questionForStaff;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply_for_staff);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        questionForStaff = getIntent().getParcelableExtra("question");
        Button replyBtn = (Button) findViewById(R.id.send);
        TextView question = (TextView) findViewById(R.id.question);
        TextView username = (TextView) findViewById(R.id.username);
        question.setText(questionForStaff.getQuestion());
        username.setText("By : " + questionForStaff.getUsername());
        reply = (EditText) findViewById(R.id.reply);
        replyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    if (start_state) {
                        mediaRecorder.stop();
                        buttonStart.setImageResource(R.drawable.ic_record);
                        start_state = false;
                        buttonStart.setEnabled(true);
                        buttonDeleteLastRecording.setEnabled(true);
                        buttonPlayLastRecordAudio.setEnabled(true);
                    }
                    progressDialog = new ProgressDialog(ReplyForStaffActivity.this);
                    progressDialog.setMessage("Sending ...");
                    progressDialog.show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            HttpClient httpclient = new DefaultHttpClient();
                            HttpPut httput = new HttpPut(questionForStaff.getLinkToEdit());

                            MultipartEntity entity = new MultipartEntity();
                            try {
                                entity.addPart("question", new StringBody(questionForStaff.getQuestion()));
                                entity.addPart("replay", new StringBody(reply.getText().toString()));
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            if (AudioSavePathInDevice != null)
                                entity.addPart("file_staff", new FileBody(new File(AudioSavePathInDevice)));
                            if (mSelected != null)
                                entity.addPart("image_staff", new FileBody(new File(mSelected.getPath())));
//                    entity.addPart("myAudioFile", new FileBody(audioFile));

                            httput.setEntity(entity);
                            HttpResponse response;
                            try {
                                response = httpclient.execute(httput);
                                String responseBody = EntityUtils.toString(response.getEntity());
                                Log.e("Respone", responseBody);
                                progressDialog.dismiss();
                                ReplyForStaffActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        AlertDialog.Builder dialog = new AlertDialog.Builder(ReplyForStaffActivity.this);
                                        dialog.setTitle("Answer a question!");
                                        dialog.setMessage("Your reply has been delivered!");
                                        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                replyListener.onReply();
                                                ReplyForStaffActivity.this.finish();
                                            }
                                        });
                                        dialog.setCancelable(false);
                                        dialog.show();
                                    }
                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
//                    String url = questionForStaff.getLinkToEdit();
//                    new NetworkConnection(ReplyForStaffActivity.this).putData(ReplyForStaffActivity.this,url,
//                            new String[]{"question","replay"},new String[]{questionForStaff.getQuestion(),reply.getText().toString()});

                }

        });
        // play record from student
        playBtn = (ImageButton) findViewById(R.id.play_btn);

        if (questionForStaff.getStudentRec().length() > 5) {
            playBtn.setVisibility(View.VISIBLE);
        }
        // Recorder
        recordLayout = (LinearLayout) findViewById(R.id.record_layout);
        imgLayout = (RelativeLayout) findViewById(R.id.img_layout);
        buttonStart = (ImageButton) findViewById(R.id.start_rec_btn);
        buttonPlayLastRecordAudio = (ImageButton) findViewById(R.id.play_rec_btn);
        buttonDeleteLastRecording = (ImageButton) findViewById(R.id.remove_rec_btn);
        selected_img = (ImageView) findViewById(R.id.selected_img);
        studentImg = (ImageView) findViewById(R.id.questionImg);
        studentImgLayout = (LinearLayout) findViewById(R.id.st_image_layout);
        loadingImg = (ProgressBar) findViewById(R.id.loading_img);
        studentImg.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                try
                {
                    Toast.makeText(ReplyForStaffActivity.this, "downloading ...", Toast.LENGTH_SHORT).show();
                    Log.e("long click","clicked");
                    Bitmap bmp = null;
                    URL url = new URL(questionForStaff.getStudentImg());
                    URLConnection conn = url.openConnection();
                    bmp = BitmapFactory.decodeStream(conn.getInputStream());
                    File f = new File(Environment.getExternalStorageDirectory(),System.currentTimeMillis() + ".jpg");
                    if(f.exists())
                        f.delete();
                    f.createNewFile();
                    Bitmap bitmap = bmp;
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                    byte[] bitmapdata = bos.toByteArray();
                    FileOutputStream fos = new FileOutputStream(f);
                    fos.write(bitmapdata);
                    fos.flush();
                    fos.close();
                    Toast.makeText(ReplyForStaffActivity.this, "downloaded!", Toast.LENGTH_SHORT).show();
                    Log.e("long click","downloaded");

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                return true;
            }
        });
        if (questionForStaff.getStudentImg().length() > 4) {
            studentImgLayout.setVisibility(View.VISIBLE);
            Picasso.with(this).load(questionForStaff.getStudentImg()).into(studentImg, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                loadingImg.setVisibility(View.GONE);
                }

                @Override
                public void onError() {

                }
            });
        }
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
                        Toast.makeText(ReplyForStaffActivity.this, "Recording started",
                                Toast.LENGTH_LONG).show();
                    } else {
                        requestPermission();
                    }

                } else {
                    mediaRecorder.stop();
                    buttonStart.setImageResource(R.drawable.ic_record);
                    start_state = false;
                    buttonStart.setEnabled(true);
                    buttonDeleteLastRecording.setEnabled(true);
                    buttonPlayLastRecordAudio.setEnabled(true);
                    Toast.makeText(ReplyForStaffActivity.this, "Recording Completed",
                            Toast.LENGTH_LONG).show();
                }
            }
        });


        buttonPlayLastRecordAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) throws IllegalArgumentException,
                    SecurityException, IllegalStateException {
                if (!play_state) {
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
                    Toast.makeText(ReplyForStaffActivity.this, "Recording Playing",
                            Toast.LENGTH_LONG).show();
                } else {
                    buttonStart.setEnabled(true);
                    buttonDeleteLastRecording.setEnabled(true);
                    buttonPlayLastRecordAudio.setImageResource(R.drawable.ic_play);
                    play_state = false;
                    buttonPlayLastRecordAudio.setEnabled(true);

                    if (mediaPlayer != null) {
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
                Toast.makeText(ReplyForStaffActivity.this, "clicked", Toast.LENGTH_SHORT).show();
                File file = new File(AudioSavePathInDevice);
                boolean deleted = file.delete();
                if (deleted) {
                    Toast.makeText(ReplyForStaffActivity.this, "the file is deleted", Toast.LENGTH_SHORT).show();
                    AudioSavePathInDevice = null;
                }
            }
        });

    }

    //Recorder
    public void MediaRecorderReady() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(AudioSavePathInDevice);
    }

    public String CreateRandomAudioFileName(int string) {
        StringBuilder stringBuilder = new StringBuilder(string);
        int i = 0;
        while (i < string) {
            stringBuilder.append(RandomAudioFileName.
                    charAt(random.nextInt(RandomAudioFileName.length())));

            i++;
        }
        return stringBuilder.toString();
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(ReplyForStaffActivity.this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(ReplyForStaffActivity.this, "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(ReplyForStaffActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
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
        if (!selectToAttachRecording) {
            recordLayout.setVisibility(View.VISIBLE);
            selectToAttachRecording = true;
        } else {
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

    boolean playing = false;

    public void playRec(View view)  {
        if (!playing) {
            mediaPlayer2 = new MediaPlayer();
            Log.e("link",questionForStaff.getStudentRec());
            try {
                mediaPlayer2.setDataSource(questionForStaff.getStudentRec());
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaPlayer2.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    playBtn.setImageResource(R.drawable.ic_play);
                    playing = false;
                }
            });
            try {
                mediaPlayer2.prepare();

            mediaPlayer2.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {

                    mediaPlayer2.start();
                }
            });
            playBtn.setImageResource(R.drawable.ic_stop);
            playing = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            playBtn.setImageResource(R.drawable.ic_play);
            mediaPlayer2.stop();
            playing = false;
        }
    }

    public interface onReply {
        void onReply();
    }

    @Override
    protected void onPause() {
        try{
            mediaPlayer2.stop();
        }catch (Exception e){

        }
        try{
            mediaPlayer.stop();
        }catch (Exception e){

        }
        super.onPause();
    }
}

