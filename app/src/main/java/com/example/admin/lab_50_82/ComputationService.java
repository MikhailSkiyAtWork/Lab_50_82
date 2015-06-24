package com.example.admin.lab_50_82;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

public class ComputationService extends Service {

    ComputationService context = this;

    Notification notification_ = new Notification();

    private long[] vibratePattern_ = {0, 200, 200, 300};
    private static final int MY_NOTIFICATION_ID = 1;
    private int precision_;

    public ComputationService() {
    }

    public void onCreate() {
        super.onCreate();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        int precision = intent.getIntExtra("precision", 1);
        new startComputation().execute(precision);

        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        // throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    public class startComputation extends AsyncTask<Integer, Void, Boolean> {


        @Override
        protected void onPreExecute(){

        }

        @Override
        protected Boolean doInBackground(Integer... params) {

            if ((float) params[0] / 1000000 > 1) {
                precision_ = params[0] / 1000000;
            } else {
                precision_ = params[0] / 1000;
            }
            if (computePi(params[0]) > 0) {
                return true;
            } else {
                return false;
            }
        }

//        @Override
//        protected void onProgressUpdate(Integer... value) {
//            updateAdapter(value[0]);
//            if (isItTenthItem(value[0])) {
//                onOffButtons(true);
//            }
//        }

        @Override
        protected void onPostExecute(Boolean succes) {
            if (succes) {
                launchNotification();
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

        private void launchOnGoingNotification(){

            notification_.contentView.setProgressBar(R.id.notificationLoadingBar, max, progress, false);
            RemoteViews.RemoteView view = new RemoteViews.RemoteView(getPackageName(),R.id.notificationLoadingBar);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
            mBuilder.setOngoing(true)
                    .setContent(R.id.notificationLoadingBar)
        }

        private void launchNotification() {
            Intent intent = new Intent(context, PiActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            Notification.Builder notificationBuilder = new Notification.Builder(
                    getApplicationContext())
                    .setTicker(getResources().getString(R.string.success_message) + precision_ + getResources().getString(R.string.K))
                    .setSmallIcon(android.R.drawable.stat_sys_warning)
                    .setAutoCancel(false)
                    .setContentTitle(getResources().getString(R.string.success_label))
                    .setContentIntent(pendingIntent)
                    .setContentText(getResources().getString(R.string.success_message) + precision_ + getResources().getString(R.string.K))
                    .setVibrate(vibratePattern_);

            // Pass the Notification to the NotificationManager:
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(MY_NOTIFICATION_ID,
                    notificationBuilder.build());
        }
    }
}
