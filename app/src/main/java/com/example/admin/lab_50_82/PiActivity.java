package com.example.admin.lab_50_82;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;


public class PiActivity extends ActionBarActivity {

    private final static int THOUSAND = 1000;
    private final static int MILLION = 1000000;
    private Spinner spinner_;
    private ArrayAdapter<String> spinnerAdapter_;

    public static boolean active = false;
    public static Button startButton;
    public static Button cancelButton;
    public static String precision_;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pi);

        final TreeMap<String, Integer> precisionValues = new TreeMap<String, Integer>();
        {
            precisionValues.put(getResources().getString(R.string._32K), 32 * THOUSAND);
            precisionValues.put(getResources().getString(R.string._64K), 64 * THOUSAND);
            precisionValues.put(getResources().getString(R.string._128K), 128 * THOUSAND);
            precisionValues.put(getResources().getString(R.string._256K), 256 * THOUSAND);
            precisionValues.put(getResources().getString(R.string._512K), 512 * THOUSAND);
            precisionValues.put(getResources().getString(R.string._1M), 1 * MILLION);
            precisionValues.put(getResources().getString(R.string._2M), 2 * MILLION);
            precisionValues.put(getResources().getString(R.string._4M), 4 * MILLION);
            precisionValues.put(getResources().getString(R.string._8M), 8 * MILLION);
            precisionValues.put(getResources().getString(R.string._16M), 16 * MILLION);
        }

        List<String> precisions = new ArrayList<String>();
        precisions.addAll(precisionValues.keySet());

        spinnerAdapter_ = new ArrayAdapter<String>(
                this,
                R.layout.support_simple_spinner_dropdown_item,
                precisions
        );
        spinner_ = (Spinner) this.findViewById(R.id.precisions_spinner);
        spinner_.setAdapter(spinnerAdapter_);
        spinner_.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                precision_ = parent.getItemAtPosition(pos).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

        // Starting service
        startButton = (Button) findViewById(R.id.start_button);
        cancelButton = (Button) findViewById(R.id.cancel_button);
        startButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   holdCancelButton();
                   startService(new Intent(getApplicationContext(), ComputationService.class).putExtra(getResources().getString(R.string.precision), precisionValues.get(precision_)));
               }
           }
        );

        // Finishing service
        cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holdStartButton();
                    stopService(new Intent(getApplicationContext(), ComputationService.class));
                }
            }
        );
    }

    @Override
    public void onStart(){
        super.onStart();
        active = true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pi, menu);
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

    @Override
    public void onStop(){
        super.onStop();
        active = false;
    }

    /**
     * Sets startButton Visible and Cancel buttin invisible
     */
    public static void holdStartButton() {
        startButton.setVisibility(View.VISIBLE);
        cancelButton.setVisibility(View.INVISIBLE);
    }

    /**
     * Sets cancelButton Visible and startButton Invisible
     */
    public static void holdCancelButton() {
        startButton.setVisibility(View.INVISIBLE);
        cancelButton.setVisibility(View.VISIBLE);
    }
}
