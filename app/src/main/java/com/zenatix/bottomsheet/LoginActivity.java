package com.zenatix.bottomsheet;

import android.app.Activity;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class LoginActivity extends AppCompatActivity implements UsbSerialInterface.UsbReadCallback {

    public Activity activity;
    String TAG = "DUDE";
    AlarmManager alarmManager;
    private TextView textView1;
    private ScheduledExecutorService scheduler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        activity = this;
        textView1 = (TextView) findViewById(R.id.textView1);
        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    do_stuff3();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        final Intent intent = new Intent(activity, DataPublish.class);
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate
                (new Runnable() {
                    public void run() {
                        // call refresh service
                        startService(intent);
                    }
                }, 1, 5, TimeUnit.MINUTES);
    }

    public void do_stuff() {
        UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        for (UsbDevice device : deviceList.values()) {
            if (true) {
                UsbDeviceConnection connection = usbManager.openDevice(device);
                UsbSerialDevice serialPort = UsbSerialDevice.createUsbSerialDevice(device, connection);
                if (serialPort != null) {
                    if (serialPort.open()) { //Set Serial Connection Parameters.
                        serialPort.setBaudRate(9600);
                        serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
                        serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
                        serialPort.setParity(UsbSerialInterface.PARITY_NONE);
                        serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
                        serialPort.read(this);
                    } else {
                        Log.d("SERIAL", "PORT NOT OPEN");
                    }
                } else {
                    Log.d("SERIAL", "PORT IS NULL");
                }
            }
        }
    }

    private void do_stuff3() {
        File file = new File(this.getExternalFilesDir(null), "test.txt");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        SensorCollector sensorCollector = null;
        try {
            sensorCollector = new SensorCollector(this, file);
            sensorCollector.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // remove later
        if(scheduler!=null){
            scheduler.shutdown();
            scheduler=null;
        }
    }

    @Override
    public void onReceivedData(byte[] bytes) {
        String value = "";
        try {
            value = new String(bytes, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i(TAG, "onReceivedData: " + value);
        printLog(value);
    }

    private void printLog(final String value) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView1.setText(textView1.getText().toString() + "\n" + value);
            }
        });
    }
}