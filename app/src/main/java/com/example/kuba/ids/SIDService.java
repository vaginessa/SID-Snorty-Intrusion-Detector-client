package com.example.kuba.ids;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.net.MalformedURLException;
import java.security.KeyManagementException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.ListIterator;

/**
 * Created by Kuba on 2015-04-29.
 */
public class SIDService extends Service{
    private static final String LOG_TAG = "SIDService.class";
    private ArrayList<RequestElement> mRequestList;
    private RequestElement mCurrentRequestElement;

    // TODO DOES IT MAKE SENS?
    public final IBinder mBinder = new RequestBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class RequestBinder extends Binder {
        public SIDService getService(){
            return SIDService.this;
        }
    }

    public void onCreate() {
        mRequestList = new ArrayList<RequestElement>();
        //mDbHelper = new ServerDbAdapter(this);
        //mDbHelper.open();
    }


    public void openConnection() throws IOException {
        if(mCurrentRequestElement.mClient == null){
            mCurrentRequestElement.mClient = new ClientServer(mCurrentRequestElement.mHostText,mCurrentRequestElement.mPortInt);
            mCurrentRequestElement.mClient.open();
        }
    }

    public void closeConnection(){
        if(mCurrentRequestElement.mClient != null){
            mCurrentRequestElement.mClient.close();
            mCurrentRequestElement.mClient = null;
        }
    }

/*
    @Override
    public void onStart(Intent intent, int startId) {
        Long rowId = intent.getExtras().getLong(ROW_ID_TAG);

        Boolean foundRequestElement = false;
        ListIterator<RequestElement> requestListIterator = requestList.listIterator();
        while(requestListIterator.hasNext()){
            mCurrentRequestElement = requestListIterator.next();
            if(mCurrentRequestElement.mRowId == rowId){
                foundRequestElement = true;
                break;
            }
        }
        if(!foundRequestElement){
            requestListIterator.add(new RequestElement());
            mCurrentRequestElement = requestListIterator.previous();
            mCurrentRequestElement.mRowId = rowId;
        }

        if (mCurrentRequestElement.mRowId != null) {
            Cursor server = mDbHelper.fetch(mCurrentRequestElement.mRowId);
            mCurrentRequestElement.mHostText = server.getString(server
                    .getColumnIndexOrThrow(ServerDbAdapter.KEY_HOST));
            mCurrentRequestElement.mPortInt = server.getInt(server
                    .getColumnIndexOrThrow(ServerDbAdapter.KEY_PORT));
            mCurrentRequestElement.mUsernameText = server.getString(server
                    .getColumnIndexOrThrow(ServerDbAdapter.KEY_USERNAME));
            mCurrentRequestElement.mPasswordText = server.getString(server
                    .getColumnIndexOrThrow(ServerDbAdapter.KEY_PASSWORD));
            fetchServerHashes(server);
        }
    }

*/

}
