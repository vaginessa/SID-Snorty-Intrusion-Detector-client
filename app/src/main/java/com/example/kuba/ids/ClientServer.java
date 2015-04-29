package com.example.kuba.ids;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kuba on 2015-04-27.
 */
public class ClientServer {
    private Socket mSocket;
    private String mHost;
    private int mPortNumber;
    private boolean mAlive;

    private BufferedReader mBuffInput;
    private OutputStream mOutput;
    private String mReadString;

    public ClientServer(String host, int port){
        mHost = host;
        mPortNumber = port;
        mAlive = false;
        mSocket = new Socket();
    }

    public void open() {
        try {
            InetSocketAddress socketAddress = new InetSocketAddress(mHost, mPortNumber);
            mSocket.connect(socketAddress, 3000);
            if (mSocket.isConnected()) {
                mBuffInput = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
                mOutput = mSocket.getOutputStream();
                mAlive = true;
            }
            else throw new IOException("Socket is closed.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            mBuffInput.close();
            mOutput.close();
            mSocket.close();
            mAlive = false;
        }   catch (Exception e) {
            e.printStackTrace();
        }
        mBuffInput = null;
        mSocket = null;
        mSocket = null;
    }

    public String readBuffer(int length) throws IOException{
        char[] charBuffer = new char[length];
        mBuffInput.read(charBuffer, 0, length);
        String readRet = new String(charBuffer);
        return readRet;
    }


    private void checkLine(String line) throws Exception
    {
        if (line.length() < 1)
            throw new Exception("SMTP response is 0 length");

        // TODO
        // CZYLI line[0] != 0, why?

        char c = line.charAt(0);
        if ((c == '4') || (c == '5')) {
            throw new Exception(line);
        }
    }

    public String readLine() throws IOException {
        StringBuffer sb = new StringBuffer();
        int d;
        while ((d = mBuffInput.read()) != -1) {
            if (((char)d) == '\r') {
                continue;
            } else
                if (((char)d) == '\n')
                    break;
                else
                    sb.append((char)d);
        }
        String ret = sb.toString();
        return ret;
    }

    public void writeLine(String s) throws IOException {
        try{
            mOutput.write(s.getBytes());
            mOutput.write('\r');
            mOutput.write('\n');
            mOutput.flush();
        } catch (IOException e){
            close();
            open();
            writeLine(s);
        }
    }

    /*
    public void sendRequest(String[] requestLines) throws IOException{
        try {
            for(String requestLine : requestLines){
                writeLine(requestLine);
            }
            writeLine("");
        } catch (IOException e){
            throw new IOException(e.toString());
        }
    }
    */

    public List<String> executeSimpleCommand(String command) throws IOException, Exception {
        List<String> results = new ArrayList<String>();
        if (command != null)
            writeLine(command);

        boolean cont = false;
        do {
            String line = readLine();
            checkLine(line);
            if (line.length() > 4)
            {
                results.add(line.substring(4));
                if (line.charAt(3) == '-')
                    cont = true;
                else
                    cont = false;
            }
        } while (cont);
        return results;
    }

    public BufferedReader getInputStream(){
        return mBuffInput;
    }

    public OutputStream getOutputStream(){
        return mOutput;
    }

    public boolean isAlive() {
        return mAlive;
    }

}
