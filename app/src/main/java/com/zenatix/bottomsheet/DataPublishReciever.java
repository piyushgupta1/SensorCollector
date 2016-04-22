package com.zenatix.bottomsheet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class DataPublishReciever extends BroadcastReceiver {
    public DataPublishReciever() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Log.i("DataPublishReciever","onReceive : I AM HERE");
        context.startService(new Intent(context, DataPublish.class));
    }
}
