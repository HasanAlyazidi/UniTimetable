package com.highestweb.apps.unitimetable;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class SubjectsActivity extends Activity {

    // show subjects of this table id
    int tableID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subjects);

        //+ get the data that are sent by the previous activity +\\
        Bundle intentExtras = getIntent().getExtras();
        if (intentExtras != null)
        {
            String QueryType = intentExtras.getString("QueryType");
            int QueryValue = intentExtras.getInt("QueryValue");

            if ( QueryType.equals("ShowSubjects") ) // from activity: MainActivity
            {
                tableID = QueryValue;
            }
        }
        //- get the data that are sent by the previous activity -\\

        LinearLayout layoutNoData = (LinearLayout) findViewById(R.id.layoutNoData);
        LinearLayout layoutPage = (LinearLayout) findViewById(R.id.layoutPage);

        // button: add subject
        Button buttonAddSubject = (Button) findViewById(R.id.buttonAddSubject);
        buttonAddSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // go to add subject activity
                Intent IntentActivityToGo = new Intent(getBaseContext(), AddSubjectActivity.class);
                IntentActivityToGo.putExtra("QueryType", "AddSubject");
                IntentActivityToGo.putExtra("QueryValue", tableID); // table id
                startActivity(IntentActivityToGo);

            }
        });

        // class: Subjects, access everything related to subjects
        Subjects subjects = new Subjects(this, tableID);

        if (subjects.isAvailable())
        {
            // hide no data layout
            layoutNoData.setVisibility(View.GONE);

            // show data of the table
            subjects.list();
        }
        else
        {
            // hide page layout
            layoutPage.setVisibility(View.GONE);
        }

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

    // reload the activity every time the user comes back again to the activity to refresh the data
    @Override
    public void onRestart()
    {
        super.onRestart();
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0 ,0);
    }


    /*
    todo: check if this is useful
    // handle mobile back button
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            CommonCommands.backButton(this, MainActivity.class);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    */


}
