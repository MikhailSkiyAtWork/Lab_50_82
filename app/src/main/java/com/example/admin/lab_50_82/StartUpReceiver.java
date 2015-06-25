package com.example.admin.lab_50_82;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Mikhail Valuyskiy on 25.06.2015.
 */
public class StartUpReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving

        // Start Service On Boot Start Up
        Intent service = new Intent(context, ComputationService.class);
        context.startService(service);
        }
}
