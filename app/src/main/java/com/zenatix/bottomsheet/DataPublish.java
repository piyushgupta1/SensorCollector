package com.zenatix.bottomsheet;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class DataPublish extends IntentService {
    private ArrayList<SensorData> data= new ArrayList<>();

    public DataPublish() {
        super("DataPublish");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            try {
                handleActionFoo(json_to_dump());
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(final JSONObject data_check) {
        String url = "http://192.168.1.110:9101/add/i6IxgtjxoeaKgqPOO0Hgdo9vllepQnIFjsxI";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Common.database.remove_Sent_data(Common.send_at_once);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                return data_check.toString().getBytes();
            }
        };
        Common.volley.addToRequestQueue(request, "");
    }

    private JSONObject json_to_dump() throws NullPointerException {
        JSONObject main_data_json = null;
        try {
            data.clear();
            data.addAll(Common.database.getAllRecordsSensorData(Common.send_at_once));
//            if(data.size()==0){
//                throw new NullPointerException("NO data to send currently");
//            }
            main_data_json = new JSONObject();
            HashMap<String, JSONArray> arry = new HashMap<>();
            for (int i = 0; i < data.size(); i++) {
                SensorData ind_data = data.get(i);
                if (arry.containsKey(ind_data.getName())) {
                    JSONArray json_array = arry.get(ind_data.getName());
                    JSONArray time_stamp = new JSONArray();
                    time_stamp.put(0, ind_data.getTimeStamp());
                    time_stamp.put(1, ind_data.getValues());
                    json_array.put(json_array.length(), time_stamp);
                    arry.put(ind_data.getName(), json_array);
                } else {
                    JSONArray jsonArray = new JSONArray();
                    JSONArray time_stamp = new JSONArray();
                    time_stamp.put(0, ind_data.getTimeStamp());
                    time_stamp.put(1, ind_data.getValues());
                    jsonArray.put(0, time_stamp);
                    arry.put(ind_data.getName(), jsonArray);
                }
            }
            for (Map.Entry<String, JSONArray> entry : arry.entrySet()) {
                JSONObject ind_data_json = new JSONObject();
                ind_data_json.put("Readings", entry.getValue());
                ind_data_json.put("uuid", UUID.nameUUIDFromBytes(entry.getKey().getBytes()).toString());
                ind_data_json.put("Metadata/SourceName", "Hackathon");
                JSONObject meta_data = new JSONObject();
                meta_data.put("Timezone", "Asia/Kolkata");
                meta_data.put("UnitofMeasure", "");
                meta_data.put("ReadingType", "double");
                ind_data_json.put("Properties", meta_data);
                main_data_json.put(entry.getKey(), ind_data_json);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i("TAG", "json_to_dump: " + main_data_json.toString());
        return main_data_json;
    }
}
