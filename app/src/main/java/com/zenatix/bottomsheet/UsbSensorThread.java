package com.zenatix.bottomsheet;

import android.util.Log;

/**
 * Created by RC on 4/21/2016.
 * For Truss
 */
public class UsbSensorThread extends Thread {
    private String TAG = "SensorThread";



    public UsbSensorThread() {
    }

    @Override
    public void run() {
        super.run();
        Log.i("UsbSensorThread", "run : I AM HERE");
        for (Meter meter : Common.meterArrayList) {
            Log.i("UsbSensorThread", "onTick : I AM HERE");
            meter.get_data();
            try {
                Common.read_lock.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void interrupt() {
        super.interrupt();
        Log.i("UsbSensorThread", "interrupt : I AM HERE");
    }
}
