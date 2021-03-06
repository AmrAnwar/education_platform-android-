package com.mrerror.tm;


import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
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
import java.util.HashMap;

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
    static long reference;
    ModelAnswerDbHelper dbHelper;
    Toast toast;


    public static HashMap<Integer, Long> checkid = new HashMap<>();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_pdfactivity);
        dbHelper = new ModelAnswerDbHelper(this);

        go = (ImageButton) findViewById(R.id.gotopage);
        eGoToPage = (EditText) findViewById(R.id.edit_for_page_number);
        mGoToPageLayout = (LinearLayout) findViewById(R.id.goPage);
        eGoToPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eGoToPage.setCursorVisible(true);
            }
        });
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (eGoToPage.length() != 0) {
                    String pageNumberString = eGoToPage.getText().toString();

                    int pageNumber = Integer.parseInt(pageNumberString);
                    pageNumber--;
                    pdfView.jumpTo(pageNumber);
                    eGoToPage.setSelection(eGoToPage.length());
                    eGoToPage.setCursorVisible(true);

                }
            }
        });
        imageView = (ImageView) findViewById(R.id.ImageViewWithzoom);
        pdfView = (PDFView) findViewById(R.id.pdfView);

        Intent m = getIntent();
        mModelAnswer = (ModelAnswer) m.getSerializableExtra("obj");

        show(mModelAnswer);

        dbHelper.close();
    }


    private void show(ModelAnswer item) {

        if (mModelAnswer.getDwonload()) {


            String filepath = Environment.getExternalStorageDirectory().getPath();
            System.out.println(filepath);

            File file = new File(filepath + "/TM_DownLoad/" + mModelAnswer.getFileName());

            if (!file.isFile()) {
                deleteFromDataBase(mModelAnswer.getFilePath());
            } else {
                if (mModelAnswer.getFileExtention().equals("pdf")) {
                    imageView.setVisibility(View.GONE);
                    mGoToPageLayout.setVisibility(View.VISIBLE);
                    pdfView.setVisibility(View.VISIBLE);

                    Uri mUri = Uri.parse(mModelAnswer.getFilePath());
                    pdfView.fromUri(mUri)
                            .onPageChange(this)
                            .load();
                } else {
                    pdfView.setVisibility(View.GONE);
                    mGoToPageLayout.setVisibility(View.GONE);
                    imageView.setVisibility(View.VISIBLE);
                    imageView.setImageURI(Uri.parse(mModelAnswer.getFilePath()));
                    ph = new PhotoViewAttacher(imageView);
                }

            }
        } else {


            if (!checkid.keySet().contains(mModelAnswer.getId())) {
                ModelAnswerFragment.shouldResume = false;
                Uri checkUri = Uri.parse(mModelAnswer.getFileUrl());

                downLoad(checkUri);
                Toast.makeText(this, "Wait....", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void deleteFromDataBase(String filepath) {
        db = dbHelper.getWritableDatabase();
        makeText(this, "file not exist any more download it again ", Toast.LENGTH_SHORT).show();
        String selection = Contract.TableForModelAnswer.COLUMN_FILE_PATH + " LIKE ?";
        String[] selectionArgs = {filepath};
        db.delete(Contract.TableForModelAnswer.TABLE_NAME, selection, selectionArgs);

        db.close();
        dbHelper.close();

        ModelAnswerFragment.shouldResume = true;

        finish();
    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        eGoToPage.setCursorVisible(false);
        eGoToPage.setSelection(eGoToPage.length());
        currPage = page;
        currPage++;
        eGoToPage.setText(String.valueOf(currPage));

        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(this, currPage + "/" + pageCount, Toast.LENGTH_SHORT);

        toast.show();
    }


    public void downLoad(Uri uri) {


        File direct = new File(Environment.getExternalStorageDirectory(),
                "/TM_DownLoad");
        if (!direct.exists()) {
            direct.mkdirs();
        }

        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setVisibleInDownloadsUi(true);
        request.setDestinationInExternalPublicDir("/TM_DownLoad", mModelAnswer.getFileName());
        if (reference != 0) {

            for (int m : checkid.keySet()) {

                if (checkid.get(m) == reference) {
                    downloadManager.remove(checkid.get(m));
                    checkid.remove(m);
                }
            }
        }
        reference = downloadManager.enqueue(request);
        DownloadReciver.setReciver(mModelAnswer, reference, downloadManager);
        checkid.put(mModelAnswer.getId(), reference);
    }


}
