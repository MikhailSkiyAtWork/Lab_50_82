package com.example.admin.lab_50_82;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Button for launching activity for Lab #56
        Button launchActivity50Button = (Button) findViewById(R.id.launch_50_activity_button);

        launchActivity50Button.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  Intent intent = new Intent(MainActivity.this, MultithreadingActivity.class);
                  startActivity(intent);
              }
          }
        );

        // Button for launching activity for Lab #57
        Button launcher82 = (Button) findViewById(R.id.launch_82_activity_button);

        launcher82.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                 // Intent intent = new Intent(MainActivity.this, ActivityForLab57.class);
                 // startActivity(intent);
              }
          }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
}
