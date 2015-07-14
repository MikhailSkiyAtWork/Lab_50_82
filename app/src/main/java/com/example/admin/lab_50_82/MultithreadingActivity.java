package com.example.admin.lab_50_82;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;


public class MultithreadingActivity extends Activity{

    private static final int FIRST_ITEM = 0;
    private static final int TENTH_ITEM = 10;

    private static final String TAG = "Multithreading";
    private Button launchAsyncTaskButton_;
    private Button launchThreadButton_;
    private Button launchThreadViaHandlerButton_;
    private ListView listView_;

    private ArrayAdapter<String> adapter_;
    String[] values_;
    private Handler handler_;
    private int j_;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multithreading);

        launchAsyncTaskButton_ = (Button) findViewById(R.id.add_in_async_task);
        launchThreadButton_ = (Button) findViewById(R.id.add_in_thread_directly);
        launchThreadViaHandlerButton_ = (Button) findViewById(R.id.add_in_thread_via_handler);

        values_ = new String[]{getResources().getString(R.string.item_label)};
        listView_ = (ListView) this.findViewById(R.id.list_view);


        adapter_ = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, values_);

        listView_.setAdapter(adapter_);

        handler_ = new Handler() {
            public void handleMessage(android.os.Message message) {
                updateAdapter(message.what);
                if (isItTenthItem(message.what)) {
                    setButtonsEnabled(true);
                }
            }

            ;
        };
    }

    public void launchTask(View v) {
        switch (v.getId()) {
            case (R.id.add_in_thread_directly):
                prepareList();
                addItem();
                break;
            case (R.id.add_in_thread_via_handler):
                prepareList();
                addItemUsingHandler();
                break;
            case (R.id.add_in_async_task):
                prepareList();
                new addItemViaAsyncTask().execute();
                break;
            default:
                break;
        }
    }

    public class addItemViaAsyncTask extends AsyncTask<String, Integer, Void> {

        @Override
        protected Void doInBackground(String... params) {
            for (int i = 1; i <= 10; i++) {
                try {
                    Thread.sleep(1000);
                    publishProgress(i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... value) {
            updateAdapter(value[0]);
            if (isItTenthItem(value[0])) {
                setButtonsEnabled(true);
            }
        }
    }

    public void addItem() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (j_ = 0; j_ < 10; j_++) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Log.e(TAG, e.toString());
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateAdapter(j_);
                            if (isItTenthItem(j_)) {
                                setButtonsEnabled(true);
                            }
                        }
                    });
                }
            }
        }).start();
    }

    public void addItemUsingHandler() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 1; i <= 10; i++) {
                        handler_.sendMessageDelayed(handler_.obtainMessage(i),1000);
                }
            }
        }).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_multithreading, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateAdapter(int number) {
        String value = Integer.toString(number);
        values_[FIRST_ITEM] = value;
        adapter_.notifyDataSetChanged();
    }

    /**
     * Checks is counter == 10 or not
     * @return True if counter == 10, false otherwise
     */
    private boolean isItTenthItem(int counter) {
        if (counter != TENTH_ITEM) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * On or Off all buttons, depends on "On" value
     * If on=true, so switch all buttons in On condition
     * If on=false, so switch all buttons in Off condition
     */
    private void setButtonsEnabled(boolean on) {
        if (on) {
            // On all buttons
            launchAsyncTaskButton_.setEnabled(on);
            launchThreadButton_.setEnabled(on);
            launchThreadViaHandlerButton_.setEnabled(on);
        } else {
            // Off all buttons
            launchAsyncTaskButton_.setEnabled(on);
            launchThreadButton_.setEnabled(on);
            launchThreadViaHandlerButton_.setEnabled(on);
        }
    }

    private void prepareList(){
        setButtonsEnabled(false);
    }
}
