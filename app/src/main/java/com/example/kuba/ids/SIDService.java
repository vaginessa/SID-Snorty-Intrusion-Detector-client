package com.example.kuba.ids;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.ArrayList;

/**
 * Created by Kuba on 2015-04-29.
 */
public class SIDService extends Service{
    private static final String LOG_TAG = "SIDService.class";
    private ArrayList<RequestElement> mRequestList;
    private RequestElement mCurrentRequestElement;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
