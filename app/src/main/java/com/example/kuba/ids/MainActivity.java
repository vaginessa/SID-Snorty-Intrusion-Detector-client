package com.example.kuba.ids;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.net.InetAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * First, main activity with Host IP and Port input.
 * Regexp checking included.
 * TODO Connection Trial included.
 * TODO saving put data
 */
public class MainActivity extends ActionBarActivity {
    private EditText mHost;
    private EditText mPort;
    private String mServerHostText;
    private String mServerPortText;
    private int mServerPortNumber = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHost = (EditText) findViewById(R.id.host);
        mPort = (EditText) findViewById(R.id.port);

        mPort.setText("12121"); // DEFAULT SERVER PORT

        mHost.setText("192.168.0.172"); // Server temporary ip

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void exitButtonOnClick(View view) {
        finish();
    }

    /*
     * Method that start after clicking 'Confirm' button
     * Checks validation of put data by REGEXP
     * TODO connection trial
     */
    public void confirmButtonOnClick(View view) {

        //CHECKING
        mServerHostText = mHost.getText().toString();
        mServerPortText = mPort.getText().toString();

        final Pattern hostIP_Pattern = Pattern.compile("^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$");

        String errorString = "";

        Matcher hostIP_Matcher = hostIP_Pattern.matcher(mServerHostText);
        if(!hostIP_Matcher.matches())
            errorString += "Invalid host. Please make sure you entered a valid host IP.";
        else {
            if(mServerPortText.length() != 0) {
                boolean isNumber = Pattern.matches("[0-9]+", mServerPortText);
                if(isNumber)                    mServerPortNumber = Integer.parseInt(mServerPortText);
            }
            if(mServerPortNumber < 1 || mServerPortNumber > 65535)
                errorString += (errorString == "" ? "" : "\n\n") + "Invalid port. Valid port range is 1-65535.";
            else {
                try {
                    InetAddress serverAddr = InetAddress.getByName(mServerHostText);
                    //Socket socket = new Socket(serverAddr, mServerPortNumber);
                    //socket.close();

                    //TODO validation trial

                } catch (IOException e) {
                    e.printStackTrace();
                    errorString += (errorString == "" ? "" : "\n\n") + "Connection failed. Please make sure your server is up.";
                }
            }

        }
        if(errorString != ""){
            Context context = getApplicationContext();
            Toast toast = Toast.makeText(context, errorString, Toast.LENGTH_SHORT);
            toast.show();
        } else
            nextActivity();
    }

    private void nextActivity() {
        Log.d("nextActivity","start");
        Intent intent =  new Intent(getApplicationContext(), IdsActivity.class);
        intent.putExtra("PORT",mServerPortText);
        intent.putExtra("HOST",mServerHostText);

        Log.d("nextActivity","intent created");
        startActivity(intent);

        finish();
    }
}



