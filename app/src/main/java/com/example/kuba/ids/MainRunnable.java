package com.example.kuba.ids;

import android.util.Log;

/**
 * Created by Kuba on 2015-04-28.
 */
class Trololo {
    public void Trololo(){
        //"trololo"
                Log.i("asdas", "Asdas");
    }
};


/*
public class MainRunnable implements Runnable {
    private SIDService mReqService;

    public MainRunnable() {

    }

    @Override
    public void run() {
        try {
            mReqService.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void startReqService() {
        Intent newIntent = new Intent(nrbr.getContext(), Request.class);
        newIntent.putExtra(Request.ROW_ID_TAG, nrbr.getRowId());
        this.startService(newIntent);
        nrbr.bindService(newIntent, mConnection, 0);
    }

    public void close(){
        /*
        if(mHasManager == HAS_MANAGER_NO){
            mDbHelper.close();
            if(mBoundRequest != null)
                nrbr.unbindService(mConnection);
        }

    }
}

*/