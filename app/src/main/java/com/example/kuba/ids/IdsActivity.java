package com.example.kuba.ids;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Second activity providing server connection and communication.
 * @param mSocket
 * @param mHost
 * @param mPortNumber
 *
 * @param mServerEnabled    It says if the {@link mSocket} is connected.
 * @param mServerCloseReq   It says if the closure of {@link mSocket} was requested.
 * @param mServerConnTrial  It says if the connection trial still lasts.
 */
public class IdsActivity extends ActionBarActivity {
    private Socket mSocket;

    private Thread mServerThread = null;
    private TextView mHostTextV;
    private TextView mPortTextV;

    private Button mConnectButton;
    private Button mDisconnectButton;

    private String mHost;
    private String mPortString;
    private int mPortNumber;


    private volatile boolean mServerEnabled = false;
    private volatile boolean mServerCloseReq = false;
    private volatile boolean mServerConnTrial = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ids);

        mHostTextV = (TextView) findViewById(R.id.host);
        mPortTextV = (TextView) findViewById(R.id.port);
        mConnectButton = (Button) findViewById(R.id.connect);
        mDisconnectButton = (Button) findViewById(R.id.disconnect);

        mDisconnectButton.setEnabled(mServerEnabled);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mPortString = extras.getString("PORT");
            mHost = extras.getString("HOST");
            mPortNumber = Integer.parseInt(mPortString);
        }
        mHostTextV.setText("Host: " + mHost);
        mPortTextV.setText("Port: " + mPortString);
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
    class ClientThread implements Runnable {
        private BufferedReader mBuffInput;
        private String mReadString;

        @Override
        public void run() {
            try {
                mServerEnabled = false;
                mServerConnTrial = true;

                mSocket = new Socket();
                mSocket.connect(new InetSocketAddress(mHost,mPortNumber), 1000);
                //communication trial with timeout set to 1000ms

                if (mSocket.isConnected()) {
                    mServerEnabled = true;
                    mServerConnTrial = false;
                    Log.i("ClientThread","before BufferedReader");
                    mBuffInput = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
                    while ((mServerCloseReq == false) || (((mReadString = mBuffInput.readLine()) != null))) {
                        Log.i("ClientThread","Server Says: " + mReadString + "\n");
                    }
                    Log.i("ClientThread","after while: mServerCloseReq = " + mServerCloseReq);
                    Log.i("ClientThread","before mBuffInput.close()");
                    mBuffInput.close();
                    mSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                mServerEnabled = false;
                mServerConnTrial = false;
                mServerCloseReq = false;
            }
        }
    }


    /**
     *
     *
     */
    public void connectButtonOnClick(View view) {

        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = new Toast(context);

        if (mServerThread == null || mServerThread.isAlive() == false) {
            mServerThread = new Thread(new ClientThread());
            mServerThread.start();
            mServerConnTrial = true;
        }
        while (mServerConnTrial) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (mServerEnabled) {
            toast.makeText(context, "Server Connection established", duration).show();
            mConnectButton.setEnabled(!mServerEnabled);
            mDisconnectButton.setEnabled(mServerEnabled);

            try {
                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream())), true);
                out.println("Connection established, thread id = " + mServerThread.getId());
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            toast.makeText(context, "Server Connection failed", duration).show();
        }

    }

    public void disconnectButtonOnClick(View view) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = new Toast(context);

        if ((mSocket == null) || (mSocket.isClosed()) || (!mServerEnabled))
            toast.makeText(context, "Connection is already terminated", duration).show();
        else if (mServerConnTrial)
            toast.makeText(context, "Connection termination failed: server connection trial lasts", duration).show();
        else
            {
                toast.makeText(context, "Connection termination starts", duration).show();
                mServerCloseReq = true;

                while (mServerCloseReq) {
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.i("disconnectButtonOnClick", "waiting 500\n" +
                            "mServerCloseReq = " + mServerCloseReq +
                            "\nmServerEnabled = " + mServerEnabled +
                            "\nmServerConnTrial = " + mServerConnTrial);
                }

                if (!mServerCloseReq) {
                    mConnectButton.setEnabled(!mServerEnabled);
                    mDisconnectButton.setEnabled(mServerEnabled);
                    toast.makeText(context, "Connection termination succeeded", duration).show();
                }
            }
    }

    public void finishButtonOnClick(View view) {
        finish();
    }
}
