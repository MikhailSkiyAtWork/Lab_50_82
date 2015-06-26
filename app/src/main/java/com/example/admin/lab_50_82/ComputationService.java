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

import java.text.ParsePosition;

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
    private int i = 0;
    float currentSum = 0;

    // Values for containing saved data
    int savedCurrentProgres;
    int savedPrecision;
    float savedSum;
    int shortPrecision;
    String postfix;


    public ComputationService() {
    }

    public void onCreate() {
        super.onCreate();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v("SERVICE", "START SERVICE");

        // Initialize receiver
        IntentFilter filter = new IntentFilter(Intent.ACTION_SHUTDOWN);
        receiver = new ShutdownReceiver();
        registerReceiver(receiver, filter);

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

            // Before start we should check counter value, and decide to start from 0 or from last point
            getSavedValues();

            if (PiActivity.precision_ == null) {
                postfix = Integer.toString(shortPrecision);
            } else postfix = PiActivity.precision_;

            // Prepare notification, set parameters

            Intent intent = new Intent(context_, PiActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(context_, 0, intent, 0);

            notificatoinManager_ = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            builder_ = new Notification.Builder(
                    getApplicationContext())
                    .setTicker((getResources().getString(R.string.in_progress)) + postfix)
                    .setContentTitle((getResources().getString(R.string.in_progress)) + postfix)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setSmallIcon(android.R.drawable.stat_sys_warning);
        }

        @Override
        protected Boolean doInBackground(Integer... params) {

            if (savedPrecision != 0) {
                precision_ = savedPrecision;
            } else {
                precision_ = params[0];
            }

            // if counter!=0 so we should start from last saved point
            if (savedCurrentProgres != 0) {
                i = savedCurrentProgres;
            } else {
                i = 1;
            }

            // Computation of PI
            float pi = 0;

            if (savedSum != 0) {
                currentSum = savedSum;
            } else {
                currentSum = 0;
            }

            int tenthPart = precision_ / 10;
            int total = 0;
            for (; i < precision_; i += 8) {
                currentSum += (float) 1 / i - (float) 1 / (i + 2) + (float) 1 / (i + 4) - (float) 1 / (i + 6);
                Log.v("Current sum", Float.toString(currentSum));
                // Update ProgressBar ten times
                if (i > total) {
                    publishProgress(i);
                    total = total + tenthPart;
                }
            }
            // Finally calculate the PI
            pi = currentSum * 4;
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

            // Update current progress bar or show it
            builder_.setProgress(precision_, value[0], false);
            notificatoinManager_.notify(1, builder_.build());

            // Save current progress and precision
            saveCurrentValues();
        }

        @Override
        protected void onPostExecute(Boolean succes) {
            if (succes) {
                // Reset the status
                resetProgressValues();
                // Launch notification that operation was finished
                launchNotification();

                // Make Start Button visible if PiActivity is active
                // This check is necessary in case users phone was restarted
                // Because in such case PiActivity was destroed and Buttons will be null
                if (PiActivity.active) {
                    PiActivity.holdStartButton();
                }
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
                    .setTicker(getResources().getString(R.string.success_message) + postfix)
                    .setSmallIcon(android.R.drawable.stat_sys_warning)
                    .setAutoCancel(true)
                    .setContentTitle(getResources().getString(R.string.success_label))
                    .setContentIntent(pendingIntent)
                    .setContentText(getResources().getString(R.string.success_message) + postfix)
                    .setVibrate(vibratePattern_);

            // Pass the Notification to the NotificationManager:
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(MY_NOTIFICATION_ID,
                    notificationBuilder.build());
        }

        private void getSavedValues() {
            SharedPreferences sharedpreferences = getSharedPreferences(getBaseContext().getResources().getString(R.string.progressPreferences), Context.MODE_PRIVATE);
            savedCurrentProgres = sharedpreferences.getInt(getResources().getString(R.string.progresStatus), 0);
            savedPrecision = sharedpreferences.getInt(getResources().getString(R.string.progressMax), 0);
            savedSum = sharedpreferences.getFloat(getResources().getString(R.string.currentSum), 0);
            shortPrecision = sharedpreferences.getInt(getResources().getString(R.string.shortPrecision), 0);
        }

        private void saveCurrentValues() {
            SharedPreferences.Editor editor = getSharedPreferences(getResources().getString(R.string.progressPreferences), Context.MODE_PRIVATE).edit();
            editor.putInt(getResources().getString(R.string.progresStatus), i);
            editor.putInt(getResources().getString(R.string.progressMax), precision_);
            editor.putFloat(getResources().getString(R.string.currentSum), currentSum);
            editor.putInt(getResources().getString(R.string.shortPrecision), precision_);
            editor.commit();
        }

        private void resetProgressValues() {
            SharedPreferences.Editor editor = getSharedPreferences(getResources().getString(R.string.progressPreferences), Context.MODE_PRIVATE).edit();
            editor.putInt(getResources().getString(R.string.progresStatus), 0);
            editor.putInt(getResources().getString(R.string.progressMax), 0);
            editor.putFloat(getResources().getString(R.string.currentSum), 0);
            editor.putInt(getResources().getString(R.string.shortPrecision), 0);
            editor.commit();
        }
    }
}
