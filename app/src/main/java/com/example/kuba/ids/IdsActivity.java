package com.example.kuba.ids;

import android.content.Context;
import android.content.Intent;
import android.net.VpnService;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

/**
 * Second activity providing server connection and communication.
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

   // private MainRunnable mRun;

    private TextView mAllTimeHighText;
    private TextView mAllTimeMediumText;
    private TextView mAllTimeLowText;
    private TextView mAllTimeTotalText;
    private TextView mLast72HighText;
    private TextView mLast72MediumText;
    private TextView mLast72LowText;
    private TextView mLast72TotalText;
    private TextView mLast24HighText;
    private TextView mLast24MediumText;
    private TextView mLast24LowText;
    private TextView mLast24TotalText;
    private LinearLayout alertLinearLayout;


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

        mAllTimeHighText = (TextView) findViewById(R.id.all_time_high);
        mAllTimeMediumText = (TextView) findViewById(R.id.all_time_med);
        mAllTimeLowText = (TextView) findViewById(R.id.all_time_low);
        mAllTimeTotalText = (TextView) findViewById(R.id.all_time_total);
        mLast72HighText = (TextView) findViewById(R.id.last_72_high);
        mLast72MediumText = (TextView) findViewById(R.id.last_72_med);
        mLast72LowText = (TextView) findViewById(R.id.last_72_low);
        mLast72TotalText = (TextView) findViewById(R.id.last_72_total);
        mLast24HighText = (TextView) findViewById(R.id.last_24_high);
        mLast24MediumText = (TextView) findViewById(R.id.last_24_med);
        mLast24LowText = (TextView) findViewById(R.id.last_24_low);
        mLast24TotalText = (TextView) findViewById(R.id.last_24_total);
        alertLinearLayout = (LinearLayout) findViewById(R.id.server_view_alert_linear_layout);

        //mRun = new MainRunnable();
        mServer = new ClientServer(mHost, mPortNumber);
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
     */

    public void connectButtonOnClick(View view) {

        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = new Toast(context);

        Log.d("IdsActivity", "starting VpnService");
        Intent intent = VpnService.prepare(getApplicationContext());
        if (intent != null) {
            startActivityForResult(intent, 0);
        } else {
            onActivityResult(0, RESULT_OK, null);
        }



        /*
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
        */
    }

    public void disconnectButtonOnClick(View view) throws IOException {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = new Toast(context);

        if (!mServer.isAlive())
            Toast.makeText(context, "Server Connection is already terminated", duration).show();
        else
        {
            mServer.writeLine("CLOSING"); //TODO
            mServer.close();
        }
    }

    public void finishButtonOnClick(View view) {
        if (!mServer.isAlive())
            mServer.close();
        finish();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Intent intent = new Intent(this, VpnServiceExtended.class);
            startService(intent);
        }
    }
}
