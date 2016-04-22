package com.zenatix.bottomsheet;

/**
 * Created by RC on 4/16/2016.
 * For Truss
 */
public class SensorData {
    String name;
    long timeStamp;
    double values;


    public SensorData(String name, long timeStamp, double values) {
        this.name = name;
        this.timeStamp = timeStamp;
        this.values = values;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public double getValues() {
        return values;
    }

    public void setValues(float values) {
        this.values = values;
    }
}
