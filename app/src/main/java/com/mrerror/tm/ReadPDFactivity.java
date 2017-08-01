package com.mrerror.tm;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.mrerror.tm.dataBases.Contract;
import com.mrerror.tm.dataBases.ModelAnswerDbHelper;
import com.mrerror.tm.fragments.ModelAnswerFragment;
import com.mrerror.tm.models.ModelAnswer;

import java.io.File;
import java.net.URI;

import uk.co.senab.photoview.PhotoViewAttacher;

import static android.widget.Toast.makeText;

public class ReadPDFactivity extends AppCompatActivity implements OnPageChangeListener {

    ImageView imageView;
    PDFView pdfView;
    SQLiteDatabase db;
    ImageButton go;
    PhotoViewAttacher ph;
    EditText eGoToPage;
    int currPage;
    LinearLayout mGoToPageLayout;
    ModelAnswer mModelAnswer;
    DownloadManager downloadManager;
    long reference;
    ModelAnswerDbHelper dbHelper;
    Toast toast;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_pdfactivity);
        dbHelper= new ModelAnswerDbHelper(this);

        go= (ImageButton) findViewById(R.id.gotopage);
        eGoToPage=(EditText)findViewById(R.id.edit_for_page_number);
        mGoToPageLayout= (LinearLayout) findViewById(R.id.goPage);
        eGoToPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eGoToPage.setCursorVisible(true);
            }
        });
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(eGoToPage.length()!=0) {
                    String pageNumberString = eGoToPage.getText().toString();

                    int pageNumber = Integer.parseInt(pageNumberString);
                    pageNumber--;
                    pdfView.jumpTo(pageNumber);
                    eGoToPage.setSelection(eGoToPage.length());
                    eGoToPage.setCursorVisible(true);

                }
            }
        });
        imageView= (ImageView) findViewById(R.id.ImageViewWithzoom);
        pdfView = (PDFView) findViewById(R.id.pdfView);

        Intent m=getIntent();
         mModelAnswer= (ModelAnswer) m.getSerializableExtra("obj");
        show(mModelAnswer);

        //download section


        BroadcastReceiver receiver =new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action=intent.getAction();
                if(DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action))
                {
                    DownloadManager.Query req_query= new DownloadManager.Query();
                    req_query.setFilterById(reference);
                    Cursor c= downloadManager.query(req_query);

                    if(c.moveToFirst()){
                        int coulmIndex=c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                        if (DownloadManager.STATUS_SUCCESSFUL==c.getInt(coulmIndex)){
                            String uriString= c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));

                            String filelocation=c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
                            System.out.println(filelocation);
                            String extision= getMimeType(filelocation);
                            mModelAnswer.setFilePath(uriString);
                            mModelAnswer.setFileLocal(filelocation);
                            mModelAnswer.setFileExtention(extision);

                            saveToDataBase(uriString,mModelAnswer);
                            show(mModelAnswer);

                        }
                    }

                }
            }
        };

      registerReceiver(receiver,new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));


        dbHelper.close();
    }


    private  void show(ModelAnswer item){
        if(mModelAnswer.getDwonload()) {

            File file = new File(URI.create(mModelAnswer.getFileLocal()).getPath());

            if (!file.exists()) {

                deleteFromDataBase(mModelAnswer.getFilePath());
            } else {
                if (mModelAnswer.getFileExtention().equals("pdf")) {
                    imageView.setVisibility(View.GONE);
                    mGoToPageLayout.setVisibility(View.VISIBLE);
                    pdfView.setVisibility(View.VISIBLE);
                    pdfView.fromUri(Uri.parse(mModelAnswer.getFilePath()))
                            .onPageChange(this)
                            .enableAntialiasing(true)
                            .spacing(2)
                            .load();
                } else {
                    pdfView.setVisibility(View.GONE);
                    mGoToPageLayout.setVisibility(View.GONE);
                    imageView.setVisibility(View.VISIBLE);
                    imageView.setImageURI(Uri.parse(mModelAnswer.getFilePath()));
                    ph=new PhotoViewAttacher(imageView);
                }

            }
        }else {downLoad(mModelAnswer);}

    }
    private void deleteFromDataBase(String filepath){
      db = dbHelper.getWritableDatabase();
        makeText(this, "file not exist any more download it again ", Toast.LENGTH_SHORT).show();
        String selection = Contract.TableForModelAnswer.COLUMN_FILE_PATH + " LIKE ?";
        String[] selectionArgs = { filepath };
        db.delete(Contract.TableForModelAnswer.TABLE_NAME, selection, selectionArgs);

        db.close();
        dbHelper.close();
        ModelAnswerFragment.shouldResume=true;
        finish();
    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        eGoToPage.setCursorVisible(false);
        eGoToPage.setSelection(eGoToPage.length());
        currPage= page;
        currPage++;
        eGoToPage.setText(String.valueOf(currPage));

        if(toast !=null){
            toast.cancel();
        }
        toast=  Toast.makeText(this, currPage+"/"+pageCount, Toast.LENGTH_SHORT);

        toast.show();
    }
    public  void downLoad(ModelAnswer modelAnswer ){

        downloadManager= (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri= Uri.parse(modelAnswer.getFileUrl());
        DownloadManager.Request request= new DownloadManager.Request(uri);
        request.setVisibleInDownloadsUi(true);
//        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        reference= downloadManager.enqueue(request);
    }
    public  void saveToDataBase(String uri,ModelAnswer modelAnswer){

//        Toast.makeText(this, uri+ " "+ modelAnswer.getId(), Toast.LENGTH_SHORT).show();
        SQLiteDatabase db = dbHelper.getWritableDatabase();


        ContentValues values = new ContentValues();
        values.put(Contract.TableForModelAnswer._ID, modelAnswer.getId());
        values.put(Contract.TableForModelAnswer.COLUMN_FILE_PATH,uri.toString());
        values.put(Contract.TableForModelAnswer.COLUMN_EXTENSION,modelAnswer.getFileExtention());
        values.put(Contract.TableForModelAnswer.COLUMN_NOTE,modelAnswer.getNote());
        values.put(Contract.TableForModelAnswer.COLUMN_TITLE,modelAnswer.getTitle());
        values.put(Contract.TableForModelAnswer.COLUMN_TYPE,modelAnswer.getType());
        values.put(Contract.TableForModelAnswer.COLUMN_FILE_LOCATION,modelAnswer.getFileLocal());


        long check=  db.insert(Contract.TableForModelAnswer.TABLE_NAME,null,values);
//        db.delete(Contract.TableForModelAnswer.TABLE_NAME,null,null);

        if(check>=0)
            makeText(this, "Done add to your device ", Toast.LENGTH_SHORT).show();
        else
            makeText(this, "Error", Toast.LENGTH_SHORT).show();

    }
    public  String getMimeType(String url) {

        String extension = MimeTypeMap.getFileExtensionFromUrl(url);

        return extension;
    }


}
