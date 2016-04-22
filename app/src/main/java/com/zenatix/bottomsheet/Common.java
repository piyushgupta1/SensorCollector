package com.zenatix.bottomsheet;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.preference.PreferenceManager;
import android.util.Log;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

/**
 * Created by RC on 4/15/2016.
 * For Truss
 */
public class Common extends Application {

    public static final String ACTION_FOO = "com.zenatix.bottomsheet.action.publish";
    public static SQLiteHelper database;
    static VolleySingleton volley;
    public static SharedPreferences prefs;
    public static SharedPreferences.Editor editor;
    public static int send_at_once=100;
    public static String device_name="/android_1/";
    public static long dataSendRate=30000;
    public static UsbSerialDevice serialPort;
    public static ArrayList<Meter> meterArrayList= new ArrayList<>();
    public static final CountDownLatch read_lock=new CountDownLatch(1);


    @Override
    public void onCreate() {
        super.onCreate();
        database = new SQLiteHelper(this);
        DatabaseManager.initializeInstance(database);
        volley = VolleySingleton.getInstance(this);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        editor = prefs.edit();
        String[] param_list={"Power", "PowerFactor", "VoltageLL","Voltage", "Current", "Frequency", "PowerRPhase", "PowerFactorRPhase","VoltageRYPhase",
                "VoltageRPhase","CurrentRPhase","PowerYPhase", "PowerFactorYPhase","VoltageYBPhase", "VoltageYPhase",
                "CurrentYPhase", "PowerBPhase","PowerFactorBPhase","VoltageBRPhase", "VoltageBPhase", "CurrentBPhase",
                "ApparentEnergy", "Energy"};
        int[] register_list={2, 6, 8, 10, 12, 14, 18, 22, 24, 26, 28, 32, 36, 38, 40, 42, 46, 50, 52, 54, 56, 58, 60};
        Meter meter= new Meter("device_id_2","read",param_list,register_list);
        meterArrayList.add(meter);
        UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        for (UsbDevice device : deviceList.values()) {
            if (true) {
                UsbDeviceConnection connection = usbManager.openDevice(device);
                serialPort = UsbSerialDevice.createUsbSerialDevice(device, connection);
                if (serialPort != null) {
                    if (serialPort.open()) { //Set Serial Connection Parameters.
                        serialPort.setBaudRate(9600);
                        serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
                        serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
                        serialPort.setParity(UsbSerialInterface.PARITY_NONE);
                        serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
                    } else {
                        Log.d("SERIAL", "PORT NOT OPEN");
                    }
                } else {
                    Log.d("SERIAL", "PORT IS NULL");
                }
            }
        }
    }
}
