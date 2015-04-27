package com.example.kuba.ids;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Second activity providing server connection and communication.
 * @param mSocket
 * @param mHost
 * @param mPortNumber
 */
public class IdsActivity extends ActionBarActivity {
    private ClientServer mServer = null;
    private TextView mHostTextV;
    private TextView mPortTextV;

    private Button mConnectButton;
    private Button mDisconnectButton;

    private String mHost;
    private String mPortString;
    private int mPortNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ids);

        mHostTextV = (TextView) findViewById(R.id.host);
        mPortTextV = (TextView) findViewById(R.id.port);
        mConnectButton = (Button) findViewById(R.id.connect);
        mDisconnectButton = (Button) findViewById(R.id.disconnect);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mPortString = extras.getString("PORT");
            mHost = extras.getString("HOST");
            mPortNumber = Integer.parseInt(mPortString);
        }
        mHostTextV.setText("Host: " + mHost);
        mPortTextV.setText("Port: " + mPortString);

        if (mServer == null || mServer.isAlive() == false)
            mServer = new ClientServer(mPortString, mPortNumber);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ids, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Runnable class with communication trial to specific IP/Port
     * @param mReadString read line from {@link mSocket}
     */

    public void connectButtonOnClick(View view) {

        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = new Toast(context);

        mServer.open();
        if (mServer.isAlive()) {
            toast.makeText(context, "Server Connection established", duration).show();
            mConnectButton.setEnabled(!mServer.isAlive());
            mDisconnectButton.setEnabled(mServer.isAlive());

            try {
                mServer.writeLine("trololol");
                List<String> strings = new ArrayList<String>();
                strings = mServer.executeSimpleCommand("sadasdsad");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            toast.makeText(context, "Server Connection failed", duration).show();
        }
    }

    public void disconnectButtonOnClick(View view) throws IOException {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = new Toast(context);

        if (!mServer.isAlive())
            toast.makeText(context, "Server Connection is already terminated", duration).show();
        else
        {
            mServer.writeLine("Spierdalam stond!");
            mServer.close();
        }
    }

    public void finishButtonOnClick(View view) {
        if (!mServer.isAlive())
            mServer.close();
        finish();
    }
}
