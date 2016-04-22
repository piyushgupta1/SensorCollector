package com.zenatix.bottomsheet;

import android.util.Log;

import com.felhr.usbserial.UsbSerialInterface;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by RC on 4/19/2016.
 * For Truss
 */
public class Meter implements UsbSerialInterface.UsbReadCallback {

    String base_address="3840";
    int no_of_register=30;
    String _id;
    String operation;
    String[] params;
    int[] registers;
    ArrayList<String> values_bytes = new ArrayList<>();

    public Meter(String _id, String operation, String[] params, int[] registers) {
        this._id = _id;
        this.operation = operation;
        this.params = params;
        this.registers = registers;
        for (String param : params) {
            values_bytes.add("float");
        }
    }



    @Override
    public void onReceivedData(byte[] bytes) {
        if (bytes.length != (read_data_length())) {
            Log.i("Meter","onReceivedData : I AM HERE Data length "+bytes.length);
            return;
        }
        read_data_values(Arrays.copyOfRange(bytes, 2, bytes.length - 1));
        try {
            Common.serialPort.read(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Common.read_lock.countDown();
    }

    synchronized void get_data() {
        Log.i("Meter", "get_data : I AM HERE");
        sendDataUSB(get_init_string());
    }

    synchronized void sendDataUSB(String data) {
        if (Common.serialPort != null) {
            if (Common.serialPort.open()) { //Set Serial Connection Parameters.
                Common.serialPort.write(data.getBytes(Charset.defaultCharset()));
                Common.serialPort.read(this);
            } else {
                Common.serialPort.setBaudRate(9600);
                Common.serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
                Common.serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
                Common.serialPort.setParity(UsbSerialInterface.PARITY_NONE);
                Common.serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
            }
        } else {
            Log.d("SERIAL", "PORT IS NULL");
            //remove later
            Common.read_lock.countDown();
        }
    }

    public void read_data_values(byte[] bytes) {
        ArrayList<SensorData> all_data = new ArrayList<>();
        int data_read = 0;
        for (int i = 0; i < bytes.length; i++) {
            String value = values_bytes.get(0);
            double converted_double = 0;
            switch (value) {
                case "float":
                    converted_double = data_convert(i, Arrays.copyOfRange(bytes, data_read, data_read + 3));
                    all_data.add(new SensorData(get_name_of_stream(i), System.currentTimeMillis(), converted_double));
                    data_read = +4;
                    break;
                case "int":
                    converted_double = data_convert(i, Arrays.copyOfRange(bytes, data_read, data_read + 3));
                    all_data.add(new SensorData(get_name_of_stream(i), System.currentTimeMillis(), converted_double));
                    data_read = +4;
                    break;
            }
        }
        Common.database.insert_Ard_sensor_Data(all_data);
    }

    public int read_data_length() {
        int length = 0;
        for (String value : values_bytes) {
            switch (value) {
                case "float":
                    length += 4;
                    break;
                case "int":
                    length += 4;
                    break;
            }
        }
        return length;
    }

    public double data_convert(int index, byte[] values) {
        double data = 0;
        String value = values_bytes.get(index);
        switch (value) {
            case "float":
                data = ByteBuffer.wrap(values).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                break;
            case "int":
                data = ByteBuffer.wrap(values).order(ByteOrder.LITTLE_ENDIAN).getInt();
                break;
        }
        Log.i("TAG", "data_convert: data converted "+data);
        return data;
    }

    public String get_name_of_stream(int index) {
        StringBuilder s = new StringBuilder();
        s.append("/");
        s.append(_id);
        s.append("/");
        s.append(params[index]);
        return s.toString();
    }

    public String get_init_string() {
        StringBuilder stringBuilder= new StringBuilder();
        stringBuilder.append("/operation:").append(operation).append("HoldingRegister");
        stringBuilder.append("/id:").append(2);
        stringBuilder.append("/m_startAddress:").append(base_address);
        stringBuilder.append("/m_length:").append(no_of_register);
        return stringBuilder.toString();
    }
}
