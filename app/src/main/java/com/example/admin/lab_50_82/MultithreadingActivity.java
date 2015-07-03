package com.example.admin.lab_50_82;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;


public class MultithreadingActivity extends ActionBarActivity {

    private static final String TAG = "Multithreading";
    private Button launchAsyncTask_;
    private Button launchThread_;
    private Button launchThreadViaHandler_;
    private ListView listView_;
    private ArrayAdapter<String> adapter_;
    private ArrayList<String> list_;
    private Handler handler_;
    private int j_;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multithreading);

        launchAsyncTask_ = (Button) findViewById(R.id.add_in_async_task);
        launchThread_ = (Button) findViewById(R.id.add_in_thread_directly);
        launchThreadViaHandler_ = (Button) findViewById(R.id.add_in_thread_via_handler);

        String[] values = new String[]{getResources().getString(R.string.item_label)};
        listView_ = (ListView) this.findViewById(R.id.list_view);

        list_ = new ArrayList<String>(Arrays.asList(values));
        adapter_ = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list_);

        listView_.setAdapter(adapter_);

        handler_ = new Handler() {
            public void handleMessage(android.os.Message message) {
                updateAdapter(message.what);
                if (isItTenthItem(message.what)) {
                    onOffButtons(true);
                }
            };
        };

        launchAsyncTask_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list_.clear();
                list_.add(getResources().getString(R.string.added_label));
                adapter_.notifyDataSetChanged();
                new addItemViaAsyncTask().execute();
                onOffButtons(false);
            }
        });

        launchThread_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list_.clear();
                list_.add(getResources().getString(R.string.added_label));
                adapter_.notifyDataSetChanged();
                addItem();
                onOffButtons(false);
            }
        });

        launchThreadViaHandler_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list_.clear();
                list_.add(getResources().getString(R.string.added_label));
                adapter_.notifyDataSetChanged();
                addItemUsingHandler();
                onOffButtons(false);
            }
        });
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
                onOffButtons(true);
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
                                onOffButtons(true);
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
                    try {
                        Thread.sleep(1000);
                        handler_.sendEmptyMessage(i);
                    } catch (InterruptedException e) {
                        Log.e(TAG, e.toString());
                    }
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
        list_.clear();
        list_.add(value);
        adapter_.notifyDataSetChanged();
    }

    /**
     * Checks is counter == 10 or not
     * @return True if counter == 10, false otherwise
     */
    private boolean isItTenthItem(int counter) {
        if (counter != 10) {
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
    private void onOffButtons(boolean on) {
        if (on) {
            // On all buttons
            launchAsyncTask_.setEnabled(on);
            launchThread_.setEnabled(on);
            launchThreadViaHandler_.setEnabled(on);
        } else {
            // Off all buttons
            launchAsyncTask_.setEnabled(on);
            launchThread_.setEnabled(on);
            launchThreadViaHandler_.setEnabled(on);
        }
    }
}
