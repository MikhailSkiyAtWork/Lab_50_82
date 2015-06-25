package com.example.admin.lab_50_82;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

public class ComputationService extends Service {

    private ComputationService context_ = this;
    private Notification notification_ = new Notification();
    private NotificationManager notificatoinManager_;
    private Notification.Builder builder_;
    private long[] vibratePattern_ = {0, 200, 200, 300};
    private static final int MY_NOTIFICATION_ID = 1;
    private AsyncTask task_;
    private int precision_;
    private BroadcastReceiver receiver = null;
    private Intent shutdownIntent = null;
    private int i = 0;

    public static final String PREFS_NAME = "MyPrefsFile";

    public ComputationService() {
    }

    public void onCreate() {
        super.onCreate();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {

        // Initialize receiver
        IntentFilter filter = new IntentFilter(Intent.ACTION_SHUTDOWN);
        shutdownIntent = new Intent(this,ShutdownReceiver.class);
        shutdownIntent.setAction(intent.ACTION_SHUTDOWN);

        // TODO Get value from preferences before put!!!!
        shutdownIntent.putExtra("Counter",i);

        receiver = new ShutdownReceiver();
        registerReceiver(receiver,filter);

        int precision = intent.getIntExtra(getResources().getString(R.string.precision), 1);

        task_ = new startComputation().execute(precision);


        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        if (task_ != null) {
            if (!task_.isCancelled()) {
                task_.cancel(true);
                Log.v("Cancel TASK", "Cancel TASK");
            }
        }
        Log.v("Destroy", "Service was destroed");
        unregisterReceiver(receiver);
        super.onDestroy();
        stopSelf();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        // throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    public class startComputation extends AsyncTask<Integer, Integer, Boolean> {

        @Override
        protected void onPreExecute() {
            // Prepare notification, set parameters
            Intent intent = new Intent(context_, PiActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(context_, 0, intent, 0);

            notificatoinManager_ = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            builder_ = new Notification.Builder(
                    getApplicationContext())
                    .setTicker((getResources().getString(R.string.in_progress)) + PiActivity.precision_)
                    .setContentTitle((getResources().getString(R.string.in_progress)) + PiActivity.precision_)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setSmallIcon(android.R.drawable.stat_sys_warning);
        }

        @Override
        protected Boolean doInBackground(Integer... params) {

            SharedPreferences sharedpreferences = getSharedPreferences("ShutdownReceiver", Context.MODE_PRIVATE);
            int counter = sharedpreferences.getInt("Counter",0);

            precision_ = params[0];
            if (counter!=0){
                i = counter;
            } else i = 1;

            // Computation of PI
            float pi = 0;
            float sum = 0;
            int tenthPart = precision_ / 10;
            int total = 0;
            for ( ; i < precision_; i += 8) {
                sum += (float) 1 / i - (float) 1 / (i + 2) + (float) 1 / (i + 4) - (float) 1 / (i + 6);
                Log.v("Current sum", Float.toString(sum));
                // Update ProgressBar ten times
                if (i > total) {
                    publishProgress(i);
                    total = total + tenthPart;
                }
            }
            pi = sum * 4;
            Log.v("The value of PI", Float.toString(pi));

            // If everything is OK return true
            if (pi > 0) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        protected void onProgressUpdate(Integer... value) {
            builder_.setProgress(precision_, value[0], false);
            notificatoinManager_.notify(1, builder_.build());
        }

        @Override
        protected void onPostExecute(Boolean succes) {
            if (succes) {
                launchNotification();
                PiActivity.holdStartButton();

            }
        }

        private float computePi(int precision) {
            float pi = 0;
            float sum = 0;
            for (int i = 1; i < precision; i += 8) {
                sum += (float) 1 / i - (float) 1 / (i + 2) + (float) 1 / (i + 4) - (float) 1 / (i + 6);
                Log.v("Current sum", Float.toString(sum));

            }
            pi = sum * 4;
            return pi;
        }

        private void launchNotification() {
            Intent intent = new Intent(context_, PiActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(context_, 0, intent, 0);

            Notification.Builder notificationBuilder = new Notification.Builder(
                    getApplicationContext())
                    .setTicker(getResources().getString(R.string.success_message) + PiActivity.precision_)
                    .setSmallIcon(android.R.drawable.stat_sys_warning)
                    .setAutoCancel(true)
                    .setContentTitle(getResources().getString(R.string.success_label))
                    .setContentIntent(pendingIntent)
                    .setContentText(getResources().getString(R.string.success_message) + PiActivity.precision_)
                    .setVibrate(vibratePattern_);

            // Pass the Notification to the NotificationManager:
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(MY_NOTIFICATION_ID,
                    notificationBuilder.build());
        }
    }
}
