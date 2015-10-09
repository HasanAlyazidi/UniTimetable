package com.highestweb.apps.unitimetable;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class MainActivity extends Activity {

    // class: Tables, access everything related to tables
    Tables tables;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // button: add table
        Button buttonAddTable = (Button) findViewById(R.id.buttonAddTable);
        buttonAddTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // go to add table activity
                Intent IntentActivityToGo = new Intent(getBaseContext(), AddTableActivity.class);
                startActivity(IntentActivityToGo);

            }
        });

        // show list view data
        tables = new Tables(this);
        tables.list();

    } // - onCreate

    /* Get the x and y position after the button is draw on screen
    (It's important to note that we can't get the position in the onCreate(),
    because at that stage most probably the view isn't drawn yet, so it will return (0, 0))
    */
    @Override
    protected void onResume() {
        super.onResume();

        // apply common commands
        CommonCommands.apply(this);

    }

    // refresh the list view every time the user comes back again to the activity
    @Override
    public void onRestart()
    {
        super.onRestart();

        // show list view data
        tables.list();
    }

    /*
    // handle mobile back button
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    */
}
