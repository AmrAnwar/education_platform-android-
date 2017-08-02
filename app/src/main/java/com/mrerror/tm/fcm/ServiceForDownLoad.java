package com.mrerror.tm.fcm;

import android.app.DownloadManager;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.mrerror.tm.DownloadReciver;

/**
 * Created by kareem on 8/2/2017.
 */

public class ServiceForDownLoad extends Service {


    public ServiceForDownLoad() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        registerReceiver(DownloadReciver.getInctance(),new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        System.out.println("hello from service");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(DownloadReciver.getInctance());
        System.out.println("hello from the end of the service");
    }


    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }
}
