package com.highestweb.apps.unitimetable;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Created by HasanYazidi on 4/10/2015.
 */
public class Subjects {

    Database db;
    Activity activity;

    int tableID;

    Subjects(Activity activity, int tID)
    {
        this.activity = activity;

        // open database
        db = new Database(activity.getApplicationContext());

        this.tableID = tID;
    }

    // check if there are subjects
    public boolean isAvailable()
    {

        Cursor dbCursor = db.sqlLiteDatabase.rawQuery("SELECT " + Database.KEY_ID + " FROM  " + Database.TABLE_SUBJECTS + " WHERE " + Database.KEY_SUBJECTS_TABLEID + " = " + tableID, null);

        boolean returnValue;
        if (dbCursor.moveToFirst() && dbCursor.getCount() > 0)
        {
            returnValue = true; // available
        }
        else
        {
            returnValue = false; // unavailable
        }
        dbCursor.close();

        return returnValue;
    }

    /*
    * inserting a subject
    */
    public long add(String name, String day, String start, String end, String place, String teacher, String color) {

        ContentValues values = new ContentValues();
        values.put(Database.KEY_SUBJECTS_NAME, name);
        values.put(Database.KEY_SUBJECTS_DAY, day);
        values.put(Database.KEY_SUBJECTS_START, start);
        values.put(Database.KEY_SUBJECTS_END, end);
        values.put(Database.KEY_SUBJECTS_PLACE, place);
        values.put(Database.KEY_SUBJECTS_TEACHER, teacher);
        values.put(Database.KEY_SUBJECTS_COLOR, color);
        values.put(Database.KEY_SUBJECTS_TABLEID, tableID);
        values.put(Database.KEY_DATE, Database.getDateTime());

        /*
            insert the row
            insert() returns -1 if not inserted, otherwise returns the ID of just inserted row
            ref: http://stackoverflow.com/questions/27863609/android-sqlite-check-if-inserted-new-value
         */

        return db.sqlLiteDatabase.insert(Database.TABLE_SUBJECTS, null, values);
    }

    public void list(){

        Cursor dbCursor = db.sqlLiteDatabase.rawQuery("SELECT * FROM " + Database.TABLE_SUBJECTS + " WHERE " + Database.KEY_SUBJECTS_TABLEID + " = " + tableID, null);

        /*
            List of data:
                adapterData[?][0] = id
                adapterData[?][1] = name
                adapterData[?][2] = day
                adapterData[?][3] = start
                adapterData[?][4] = end
                adapterData[?][5] = place
                adapterData[?][6] = teacher
                adapterData[?][7] = color
        */
        final String adapterData[][] = new String[dbCursor.getCount()][8];

        int currentArrayIndex = 0;

        while ( dbCursor.moveToNext() )
        {
            adapterData[currentArrayIndex][0] = dbCursor.getString(dbCursor.getColumnIndex(Database.KEY_ID));
            adapterData[currentArrayIndex][1] = dbCursor.getString(dbCursor.getColumnIndex(Database.KEY_SUBJECTS_NAME));
            adapterData[currentArrayIndex][2] = dbCursor.getString(dbCursor.getColumnIndex(Database.KEY_SUBJECTS_DAY));
            adapterData[currentArrayIndex][3] = dbCursor.getString(dbCursor.getColumnIndex(Database.KEY_SUBJECTS_START));
            adapterData[currentArrayIndex][4] = dbCursor.getString(dbCursor.getColumnIndex(Database.KEY_SUBJECTS_END));
            adapterData[currentArrayIndex][5] = dbCursor.getString(dbCursor.getColumnIndex(Database.KEY_SUBJECTS_PLACE));
            adapterData[currentArrayIndex][6] = dbCursor.getString(dbCursor.getColumnIndex(Database.KEY_SUBJECTS_TEACHER));
            adapterData[currentArrayIndex][7] = dbCursor.getString(dbCursor.getColumnIndex(Database.KEY_SUBJECTS_COLOR));

            currentArrayIndex++;
        }
        dbCursor.close();

        // subject list
        final ListView listviewSubjects = (ListView) activity.findViewById(R.id.listviewSubjects);
        listviewSubjects.setAdapter(new ItemsAdapter(activity, adapterData));

        // OnClick
        listviewSubjects.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                Log.e("ID", "" + adapterData[position][0]);
                Log.e("Name", "" + adapterData[position][1]);

                /*
                // go to the activity that shows table's subjects
                Intent IntentActivityToGo = new Intent(activity, SubjectsActivity.class);
                IntentActivityToGo.putExtra("QueryType", "ShowSubjects");
                IntentActivityToGo.putExtra("QueryValue", Integer.parseInt(adapterData[position][0]));
                activity.startActivity(IntentActivityToGo);
                */


                Toast.makeText(activity, "ID: " + adapterData[position][0] + " | Name: " + adapterData[position][1], Toast.LENGTH_LONG).show();
            }

        });

        // page title
        TextView textviewPageTitle = (TextView) activity.findViewById(R.id.textviewPageTitle);
        textviewPageTitle.setText(getTableName() + " - Subjects");

    }

    // get table data: name
    public String getTableName(){

        Cursor dbCursor = db.sqlLiteDatabase.rawQuery("SELECT " + Database.KEY_TABLES_NAME + " FROM " + Database.TABLE_TABLES + " WHERE id = " + tableID, null);

        String returnValue;
        if (dbCursor.moveToFirst())
        {
            returnValue = dbCursor.getString(dbCursor.getColumnIndex(Database.KEY_TABLES_NAME));
        }
        else
        {
            returnValue = "";
        }
        dbCursor.close();

        return returnValue;
    }


    public class ItemsAdapter extends BaseAdapter
    {
        private Context context;
        private String[][] lis;

        public ItemsAdapter(Context c, String[][] li)
        {
            context = c;
            lis = li;
        }

        public int getCount() {
            return lis.length;
        }
        public Object getItem(int position) {
            return position;
        }
        public long getItemId(int position) {
            return position;
        }

        public class ViewHolder {
            TextView textviewName;
            TextView textviewDay;
            TextView textviewTime;
            TextView textviewPlace;
            TextView textviewTeacher;
            View viewColor;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            final ViewHolder holder;

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.listview_subjects, null);
                holder = new ViewHolder();

                holder.textviewName = (TextView) convertView.findViewById(R.id.textviewName);
                holder.textviewDay = (TextView) convertView.findViewById(R.id.textviewDay);
                holder.textviewTime = (TextView) convertView.findViewById(R.id.textviewTime);
                holder.textviewPlace = (TextView) convertView.findViewById(R.id.textviewPlace);
                holder.textviewTeacher = (TextView) convertView.findViewById(R.id.textviewTeacher);
                holder.viewColor = convertView.findViewById(R.id.viewColor);

                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            // fill data
            holder.textviewName.setText(lis[position][1]);
            holder.textviewDay.setText(lis[position][2]);
            holder.textviewTime.setText(lis[position][3] + " - " + lis[position][4]);
            holder.textviewPlace.setText(lis[position][5]);
            holder.textviewTeacher.setText(lis[position][6]);

            // set item color
            switch (lis[position][7]) {
                case "black":
                    holder.viewColor.setBackgroundColor(Color.parseColor("#282828"));
                    break;
                case "green":
                    holder.viewColor.setBackgroundColor(Color.parseColor("#68e801"));
                    break;
                case "red":
                    holder.viewColor.setBackgroundColor(Color.parseColor("#ea0000"));
                    break;
                case "yellow":
                    holder.viewColor.setBackgroundColor(Color.parseColor("#f9f604"));
                    break;
                case "blue":
                    holder.viewColor.setBackgroundColor(Color.parseColor("#00a5ea"));
                    break;
            }

            return convertView ;
        }
    } // - class: ItemsAdapter

}
