package com.example.admin.lab_50_82;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by Mikhail Valuyskiy on 25.06.2015.
 */
public class ShutdownReceiver extends BroadcastReceiver{
    private static final String TAG = "ShutdownReceiver";
    private static final String MY_PREFS = "computationsParams";

    @Override
    public void onReceive (Context context, Intent intent){
        Log.v(TAG, "Shutting Down..........................");
        if("android.intent.action.ACTION_SHUTDOWN".equals(intent.getAction())) {
            int i = intent.getIntExtra("Counter",1);
            SharedPreferences.Editor editor = context.getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE).edit();
            editor.putInt("Counter",i);
            editor.commit();
            //Power Off
        }
    }
}
