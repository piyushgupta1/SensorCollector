/*
 *  ZENATIX CONFIDENTIAL
 * __________________
 *
 * [2016] Zenatix Solutions Private Limited
 * All Rights Reserved.
 * NOTICE:  All information contained herein is, and remains
 * the property of Zenatix Solutions Private Limited and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Zenatix Solutions Private Limited
 * and its suppliers and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Zenatix Solutions Private Limited.
 */

package com.zenatix.bottomsheet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * Created by RC on 11/19/2015.
 * For Truss
 */
public class SQLiteHelper extends SQLiteOpenHelper {

    /**
     * The constant DEVICE_INFO.
     */
    private static final String SENSOR_DATA = "SENSOR_DATA";
    private static final String DATA_CHECK = "DATA_CHECK";


    /**
     * The constant DATABASE_VERSION.
     */
    private static final int DATABASE_VERSION = 1;
    /**
     * The constant DATABASE_NAME.
     */
    private static final String DATABASE_NAME = "D0_i_noe_you.db";

    /**
     * Instantiates a new Sq lite helper.
     *
     * @param context
     *         the context
     */
    public SQLiteHelper(Context context) {
        super(context, context.getExternalFilesDir(null).getAbsolutePath() + "/" + DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table SENSOR_DATA" +
                "( ID INTEGER PRIMARY KEY," +
                "NAME VARCHAR, " +
                "TIMESTAMP INTEGER," +
                "VALUE VARCHAR," +
                "SENT INTEGER" +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + SENSOR_DATA);
        onCreate(db);
    }

    public void insert_Accel_data(String name, long time, float[] value, boolean sensor_val) {
        SQLiteDatabase database = DatabaseManager.getInstance().openDatabase();
        database.beginTransactionNonExclusive();
        Log.i("SQLiteHelper","insert_Accel_data : I AM HERE");
        try {
            ContentValues values = new ContentValues();
            if (value.length == 3) {
                for (int i = 0; i < value.length; i++) {
                    StringBuilder string = new StringBuilder();
                    string.append(name).append("/");
                    switch (i) {
                        case 0:
                            string.append("x");
                            break;
                        case 1:
                            string.append("y");
                            break;
                        case 2:
                            string.append("z");
                            break;
                    }
                    values.put("NAME", string.toString());
                    values.put("VALUE", String.valueOf(value[i]));
                    values.put("TIMESTAMP", time);
                    values.put("SENT", 0);
                    database.insert(SENSOR_DATA, null, values);
                }
            } else if (value.length == 2) {
                for (int i = 0; i < value.length; i++) {
                    StringBuilder string = new StringBuilder();
                    string.append(name).append("/");
                    switch (i) {
                        case 0:
                            string.append("lat");
                            break;
                        case 1:
                            string.append("long");
                            break;
                    }
                    values.put("NAME", string.toString());
                    values.put("VALUE", String.valueOf(value[i]));
                    values.put("TIMESTAMP", time);
                    values.put("SENT", 0);
                    database.insert(SENSOR_DATA, null, values);
                }
            } else {
                values.put("NAME", name);
                values.put("VALUE", String.valueOf(value[0]));
                values.put("TIMESTAMP", time);
                values.put("SENT", 0);
                database.insert(SENSOR_DATA, null, values);
            }
            database.setTransactionSuccessful(); // marks a commit
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.endTransaction();
        }
        DatabaseManager.getInstance().closeDatabase();

    }

    /**
     * Util function
     * Convert Timeseries to json to string
     *
     * @param list_timeSeries
     *         timeseries from a widget
     * @return String to dump in DB
     */
    private String listOfMap_to_string(ArrayList<LinkedHashMap<String, String>> list_timeSeries) {
        JSONArray array = new JSONArray();
        for (int i = 0; i < list_timeSeries.size(); i++) {
            JSONObject json1 = new JSONObject(list_timeSeries.get(i));
            array.put(json1);
        }
        return array.toString();
    }

    /**
     * Util function
     * Convert String to json to Timeseries
     *
     * @param json_from_db
     *         String retrieved from Json
     * @return Converted Timeseries
     */
    private ArrayList<LinkedHashMap<String, String>> string_to_map_timeSeries(String json_from_db) {
        ArrayList<LinkedHashMap<String, String>> time_series = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(json_from_db);
            for (int j = 0; j < jsonArray.length(); j++) {
                JSONObject one_series = jsonArray.getJSONObject(j);
                LinkedHashMap<String, String> map = new LinkedHashMap<>();
                Iterator keys = one_series.keys();
                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    map.put(key, one_series.getString(key));
                }
                time_series.add(map);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return time_series;
    }

    /**
     * Util function
     * Converts the HashMap to String
     *
     * @param hashMap_param
     *         parameter hashMap of widget
     * @return String to dump in Db
     */
    private String hashMapToString(HashMap hashMap_param) {
        JSONObject json1 = new JSONObject(hashMap_param);
        return json1.toString();
    }

    public ArrayList<SensorData> getAllRecordsSensorData(int send_at_once) {
        SQLiteDatabase database = DatabaseManager.getInstance().openDatabase();
        ArrayList<SensorData> sensorDatas = new ArrayList<>();
        Cursor cursor = database.query(SENSOR_DATA, null, null, null, null, null, "ID", String.valueOf(send_at_once));
        if (cursor.getCount() > 0) {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                sensorDatas.add(new SensorData(cursor.getString(1), cursor.getLong(2), Double.valueOf(cursor.getString(3))));
            }
        }
        cursor.close();
        DatabaseManager.getInstance().closeDatabase();
        return sensorDatas;
    }

    public void remove_Sent_data(int send_at_once) {
        SQLiteDatabase database = DatabaseManager.getInstance().openDatabase();
        database.beginTransactionNonExclusive();
        try {
            Log.i("SQLiteHelper", "remove_Sent_data : I AM HERE");
            String sql = "DELETE FROM " + SENSOR_DATA + " WHERE ID IN (SELECT ID FROM " + SENSOR_DATA + " LIMIT " + send_at_once + ");";
            database.execSQL(sql);
            database.setTransactionSuccessful();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            database.endTransaction();
        }
        DatabaseManager.getInstance().closeDatabase();
    }

    public void insert_Ard_sensor_Data(ArrayList<SensorData> all_data) {
        SQLiteDatabase database = DatabaseManager.getInstance().openDatabase();
        database.beginTransactionNonExclusive();
        try {
            for (SensorData sens_Data : all_data) {
                ContentValues values = new ContentValues();
                values.put("NAME", sens_Data.getName());
                values.put("VALUE", String.valueOf(sens_Data.getValues()));
                values.put("TIMESTAMP", sens_Data.getTimeStamp());
                values.put("SENT", 0);
                Log.i("TAG", "insert_Ard_sensor_Data: "+sens_Data.getName());
                Log.i("TAG", "insert_Ard_sensor_Data: "+sens_Data.getValues());
                database.insert(SENSOR_DATA, null, values);
            }

            database.setTransactionSuccessful(); // marks a commit
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.endTransaction();
        }
        DatabaseManager.getInstance().closeDatabase();

    }
}
