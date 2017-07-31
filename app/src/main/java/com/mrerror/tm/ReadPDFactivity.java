package com.mrerror.tm;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
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

import java.io.File;
import java.net.URI;

import uk.co.senab.photoview.PhotoViewAttacher;

public class ReadPDFactivity extends AppCompatActivity implements OnPageChangeListener {
    ModelAnswerDbHelper dbHelper;

    ImageView imageView;
    PDFView pdfView;
    PhotoViewAttacher ph;
    SQLiteDatabase db;
    ImageButton go;
    EditText eGoToPage;
    int currPage;
    LinearLayout mGoToPageLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
       String filePath= m.getStringExtra("file_path");
        String ext=m.getStringExtra("ext");
        String location=m.getStringExtra("file_loc");
        File file=new File(URI.create(location).getPath());

        if(!file.exists()){

            deleteFromDataBase(filePath);
        }
        else {
            if (ext.equals("pdf")) {
                imageView.setVisibility(View.GONE);
                mGoToPageLayout.setVisibility(View.VISIBLE);
                pdfView.setVisibility(View.VISIBLE);
                pdfView.fromUri(Uri.parse(filePath))
                        .onPageChange(this)
                        .load();

            } else {
                pdfView.setVisibility(View.GONE);
                mGoToPageLayout.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageURI(Uri.parse(filePath));
//                if (imageView.getDrawable() != null) {
//
//                    ph = new PhotoViewAttacher(imageView);
//                } else {
//                    deleteFromDataBase(filePath);
//                }
            }

        }


        dbHelper.close();
    }
    private void deleteFromDataBase(String filepath){
      db = dbHelper.getWritableDatabase();
        Toast.makeText(this, "file not exist any more download it again ", Toast.LENGTH_SHORT).show();
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
        Toast.makeText(this, currPage+"/"+pageCount, Toast.LENGTH_SHORT).show();
    }
}
