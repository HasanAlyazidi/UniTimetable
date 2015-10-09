package com.highestweb.apps.unitimetable;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddSubjectActivity extends FragmentActivity {

    String selectedColor = "black"; // default color

    Button buttonColorBlack, buttonColorGreen, buttonColorRed, buttonColorYellow, buttonColorBlue;

    // add subject to this table id
    int tableID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_subject);

        //+ get the data that are sent by the previous activity +\\
        Bundle intentExtras = getIntent().getExtras();
        if (intentExtras != null)
        {
            String QueryType = intentExtras.getString("QueryType");
            int QueryValue = intentExtras.getInt("QueryValue");

            if ( QueryType.equals("AddSubject") ) // from activity: SubjectsActivity
            {
                tableID = QueryValue;
            }
        }
        //- get the data that are sent by the previous activity -\\

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

        final EditText edittextSubjectStart = (EditText) findViewById(R.id.edittextSubjectStart);
        edittextSubjectStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // get the time from the dialog
                DialogFragment timePickerFragment = TimePickerFragment.newInstance(edittextSubjectStart.getId());
                timePickerFragment.show(getSupportFragmentManager(), "timePicker");

            }
        });

       final EditText edittextSubjectEnd = (EditText) findViewById(R.id.edittextSubjectEnd);
        edittextSubjectEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // get the time from the dialog
                DialogFragment timePickerFragment = TimePickerFragment.newInstance(edittextSubjectEnd.getId());
                timePickerFragment.show(getSupportFragmentManager(), "timePicker");

            }
        });

        //+ inserting days spinner data +\\
        final String[] weekDays = {"Day", "Saturday", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};

        final Spinner spinnerDays = (Spinner) findViewById(R.id.spinnerDays);
        spinnerDays.setOnItemSelectedListener(new placeholderOnItemSelectedListener());

        ArrayAdapter<String> adapterSpinnerGender = new ArrayAdapter<String>(this, R.layout.spinner_dropdown_item, weekDays);
        adapterSpinnerGender.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerDays.setAdapter(adapterSpinnerGender);
        spinnerDays.setSelection(0);
        //- inserting days spinner data -\\

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
                EditText edittextSubjectName = (EditText) findViewById(R.id.edittextSubjectName);
                EditText edittextSubjectStart = (EditText) findViewById(R.id.edittextSubjectStart);
                EditText edittextSubjectEnd = (EditText) findViewById(R.id.edittextSubjectEnd);
                EditText edittextSubjectPlace = (EditText) findViewById(R.id.edittextSubjectPlace);
                EditText edittextSubjectTeacher = (EditText) findViewById(R.id.edittextSubjectTeacher);

                // get string inputs data
                String subjectName = edittextSubjectName.getText().toString();
                String subjectDay = weekDays[spinnerDays.getSelectedItemPosition()];
                String subjectStart = edittextSubjectStart.getText().toString();
                String subjectEnd = edittextSubjectEnd.getText().toString();
                String subjectPlace = edittextSubjectPlace.getText().toString();
                String subjectTeacher = edittextSubjectTeacher.getText().toString();

                Log.e("subjectName", subjectName);
                Log.e("subjectDay", subjectDay);
                Log.e("subjectStart", subjectStart);
                Log.e("subjectEnd", subjectEnd);
                Log.e("subjectPlace", subjectPlace);
                Log.e("subjectTeacher", subjectTeacher);
                Log.e("selectedColor", selectedColor);
                Log.e("tableID", "" + tableID);

                if (subjectName.isEmpty() || subjectDay.equals("Day") || subjectStart.isEmpty() || subjectEnd.isEmpty() || subjectPlace.isEmpty() || subjectTeacher.isEmpty() || selectedColor.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please fulfil the data", Toast.LENGTH_LONG).show();
                } else // data are ok, save data
                {

                    // class: Subjects, access everything related to subjects
                    Subjects subjects = new Subjects(AddSubjectActivity.this, tableID);

                    // add data
                    long returnDB = subjects.add(subjectName, subjectDay, subjectStart, subjectEnd, subjectPlace, subjectTeacher, selectedColor);

                    // check if data has been saved
                    if (returnDB != -1) // saved
                    {
                        // finish this activity then go to subjects activity
                        finish();

                        Toast.makeText(getApplicationContext(), "Data has been saved", Toast.LENGTH_LONG).show();
                    } else // not saved
                    {
                        Toast.makeText(getApplicationContext(), "Data has not been saved", Toast.LENGTH_LONG).show();
                    }

                }

            }
        });

    } // onCreate

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

    // make first item of the spinner looks like a placeholder (gray colored)
    private class placeholderOnItemSelectedListener implements AdapterView.OnItemSelectedListener{

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            // set first item as placeholder (gray colored)
            if (position == 0)
                ((TextView) parent.getChildAt(0)).setTextColor(Color.parseColor("#5e7072"));
            else
                ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

        EditText toSetTimeTo;

        public final static String EXTRA_RESOURCE = "editviewResource";

        public static TimePickerFragment newInstance(int resource)
        {
            // 1. data will be sent to the class
            Bundle bundle = new Bundle(1);
            bundle.putInt(EXTRA_RESOURCE, resource);

            // 2. return the new instance of the class with arguments
            TimePickerFragment dialogFragment = new TimePickerFragment();
            dialogFragment.setArguments(bundle);
            return dialogFragment;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
        }

        // Do something with the time chosen by the user
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            Calendar mCalendar = Calendar.getInstance();
            mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            mCalendar.set(Calendar.MINUTE, minute);

            SimpleDateFormat mSDF;

            // get the time based on system date format
            if (DateFormat.is24HourFormat(getActivity())) // 24 hours
            {
                mSDF = new SimpleDateFormat("HH:mm", Locale.US);
            }
            else // 12 hours
            {
                mSDF = new SimpleDateFormat("hh:mm a", Locale.US);
            }

            // get resource id of the view that will show the time
            int viewResourceID = getArguments().getInt(EXTRA_RESOURCE);

            // set the time
            toSetTimeTo = (EditText) getActivity().findViewById(viewResourceID);
            toSetTimeTo.setText(mSDF.format(mCalendar.getTime()));

        }
    }

}
