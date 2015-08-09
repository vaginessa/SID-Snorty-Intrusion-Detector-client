package com.example.kuba.ids;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.VpnService;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Enumeration;

/**
 * Created by Kuba on 2015-07-06.
 * MIN API = 14
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class VpnServiceExtended extends VpnService implements Handler.Callback, Runnable {
    private static final String TAG = "VpnService";

    private Handler mHandler;
    private Thread mThread;
    private Builder mBuilder = new Builder();
    private ParcelFileDescriptor mInterface;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // The handler is only used to show messages.
        if (mHandler == null) {
            mHandler = new Handler(this);
        }

        // Stop the previous session by interrupting the thread.
        if (mThread != null) {
            mThread.interrupt();
        }

        // Start a new session by creating a new thread.
        Log.i(TAG, "Starting a thread");
        mThread = new Thread(this, "VpnThread");
        mThread.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (mThread != null) {
            mThread.interrupt();
        }
    }

    @Override
    public boolean handleMessage(Message message) {
        if (message != null) {
            Toast.makeText(this, message.what, Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    @Override
    public synchronized void run() {
        Log.i(TAG, "running vpnService");
        try {

            Log.i(TAG, "runVpnConnection starting");
            runVpnConnection();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Got " + e.toString());
        } finally {
            try {
                mInterface.close();
            } catch (Exception e) {
                // ignore
            }
            mInterface = null;

            mHandler.sendEmptyMessage(R.string.disconnected);
            Log.i(TAG, "Exiting");
        }
    }

    private boolean runVpnConnection() throws Exception {

       // configure();

        int error;

        Log.i(TAG, "runVpnConnection started");
        ByteBuffer byteBuffer = ByteBuffer.allocate(256);

        mBuilder.setMtu(1500);
        mInterface = mBuilder.setSession("SID_VPNService")
                .addAddress("10.0.0.2", 32)
                //.addDnsServer("8.8.8.8")
                .addRoute("0.0.0.0", 0)
                .establish();
        //b. Packets to be sent are queued in this input stream.
        FileInputStream in = new FileInputStream(
                mInterface.getFileDescriptor());
        //b. Packets received need to be written to this output stream.
        FileOutputStream out = new FileOutputStream(
                mInterface.getFileDescriptor());


        Log.i(TAG, "in.available() = " + in.available());
        error = in.read(byteBuffer.array());
        Log.i(TAG, "in.read() error = " + error);
        Log.i(TAG, "in.read() = " + byteBuffer.toString());
        out.write(byteBuffer.array());
        Log.i(TAG, "out.read() error = " + error);
        Thread.sleep(5000);


        return true;


            //c. The UDP channel can be used to pass/get ip package to/from server
        //DatagramChannel tunnel = DatagramChannel.open();
            // Connect to the server, localhost is used for demonstration only.
        //tunnel.connect(new InetSocketAddress("127.0.0.1", 8087));
            //d. Protect this socket, so package send by it will not be feedback to the vpn service.
        //protect(tunnel.socket());

/*
        while(true) {
            byteBuffer.clear();
            final SocketAddress from = tunnel.receive(byteBuffer);
            byteBuffer.flip();

            Log.i(TAG, String.format("Received %d bytes from %s", byteBuffer.limit(), from));


        }
*/
        /*
        FileInputStream in = new FileInputStream(mInterface.getFileDescriptor());
        FileOutputStream out = new FileOutputStream(mInterface.getFileDescriptor());

        DatagramChannel tunnel = DatagramChannel.open();
        // Connect to the server, localhost is used for demonstration only.
        tunnel.connect(new InetSocketAddress("127.0.0.1", 8087));
        //d. Protect this socket, so package send by it will not be feedback to the vpn service.
        protect(tunnel.socket());
        // Allocate the buffer for a single packet.
        ByteBuffer packet = ByteBuffer.allocate(32767);

        // We keep forwarding packets till something goes wrong.
        while (true) {
            // Assume that we did not make any progress in this iteration.
            boolean idle = true;

            // Read the outgoing packet from the input stream.
            int length = in.read(packet.array());
            if (length > 0) {

                Log.i(TAG, "packet received");


                while (packet.hasRemaining()) {
                    Log.i(TAG, "" + packet.get());
                    //System.out.print((char) packet.get());
                }
                packet.limit(length);
                //  tunnel.write(packet);
                packet.clear();

                // There might be more outgoing packets.
                idle = false;
            }
            Thread.sleep(50);
        }
        */
    }

    public String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    Log.i(TAG, "****** INET ADDRESS ******");
                    Log.i(TAG, "address: " + inetAddress.getHostAddress());
                    Log.i(TAG, "hostname: " + inetAddress.getHostName());
                    Log.i(TAG, "address.toString(): " + inetAddress.getHostAddress().toString());
                    if (!inetAddress.isLoopbackAddress()) {
                        //IPAddresses.setText(inetAddress.getHostAddress().toString());
                        Log.i(TAG, "IS NOT LOOPBACK ADDRESS: " + inetAddress.getHostAddress().toString());
                        return inetAddress.getHostAddress().toString();
                    } else {
                        Log.i(TAG, "It is a loopback address");
                    }
                }
            }
        } catch (SocketException ex) {
            String LOG_TAG = null;
            Log.e(LOG_TAG, ex.toString());
        }

        return null;
    }


    /*
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void configure() throws Exception {
        // If the old interface has exactly the same parameters, use it!
        if (mInterface != null) {
            Log.i(TAG, "Using the previous interface");
            return;
        }

        // Configure a builder while parsing the parameters.

        Log.i(TAG, "Configuring");
        mBuilder.addAddress("192.168.0.1", 24);
        mBuilder.addRoute("0.0.0.0", 0);
        mBuilder.addDnsServer("8.8.8.8");
        try {
            mInterface.close();
        } catch (Exception e) {
            // ignore
        }

        Log.i(TAG, "Establishing");
        mInterface = mBuilder.establish();
        Log.i(TAG, "Eestablishment done");

    }

    */
}