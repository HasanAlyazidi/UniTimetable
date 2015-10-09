package com.highestweb.apps.unitimetable;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddTableActivity extends Activity {

    String selectedColor = "black"; // default color

    Button buttonColorBlack, buttonColorGreen, buttonColorRed, buttonColorYellow, buttonColorBlue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_table);

        // color buttons
        buttonColorBlack = (Button) findViewById(R.id.buttonColorBlack);
        buttonColorBlack.setOnClickListener(onClickListener);

        buttonColorGreen = (Button) findViewById(R.id.buttonColorGreen);
        buttonColorGreen.setOnClickListener(onClickListener);

        buttonColorRed = (Button) findViewById(R.id.buttonColorRed);
        buttonColorRed.setOnClickListener(onClickListener);

        buttonColorYellow = (Button) findViewById(R.id.buttonColorYellow);
        buttonColorYellow.setOnClickListener(onClickListener);

        buttonColorBlue = (Button) findViewById(R.id.buttonColorBlue);
        buttonColorBlue.setOnClickListener(onClickListener);

        // button: cancel
        Button buttonCancel = (Button) findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // button: add
        Button buttonAdd = (Button) findViewById(R.id.buttonAdd);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // get inputs data
                EditText edittextTableName = (EditText) findViewById(R.id.edittextTableName);
                String tableName = edittextTableName.getText().toString();

                if (tableName.isEmpty() || selectedColor.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please fulfil the data", Toast.LENGTH_LONG).show();
                }
                else // data are ok, save data
                {

                    // class: Tables, access everything related to tables
                    Tables tables = new Tables(AddTableActivity.this);

                    // add data
                    long returnDB = tables.add(tableName, selectedColor);

                    // check if data has been saved
                    if (returnDB != -1) // saved
                    {
                        // finish this activity then go to main activity
                        finish();

                        Toast.makeText(getApplicationContext(), "Data has been saved", Toast.LENGTH_LONG).show();
                    }
                    else // not saved
                    {
                        Toast.makeText(getApplicationContext(), "Data has not been saved", Toast.LENGTH_LONG).show();
                    }

                }

            }
        });


    }

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

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.buttonColorBlack:
                    setColor("black", v);
                    break;

                case R.id.buttonColorGreen:
                    setColor("green", v);
                    break;

                case R.id.buttonColorRed:
                    setColor("red", v);
                    break;

                case R.id.buttonColorYellow:
                    setColor("yellow", v);
                    break;

                case R.id.buttonColorBlue:
                    setColor("blue", v);
                    break;
            }

        }
    };

    public void setColor(String colorName, View viewSelected)
    {
        // 1. clear buttons texts
        buttonColorBlack.setText("");
        buttonColorGreen.setText("");
        buttonColorRed.setText("");
        buttonColorYellow.setText("");
        buttonColorBlue.setText("");

        // 2. mark the selected button
        ((Button) viewSelected).setText("●");

        // 3. store selected color
        selectedColor = colorName;
    }

}
