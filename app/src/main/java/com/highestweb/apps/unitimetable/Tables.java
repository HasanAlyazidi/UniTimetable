package com.highestweb.apps.unitimetable;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by HasanYazidi on 4/10/2015.
 */
public class Tables {

    Database db;
    Activity activity;

    Tables(Activity activity)
    {
        this.activity = activity;

        // open database
        db = new Database(activity.getApplicationContext());
    }

    // check if there are tables
    public boolean isAvailable()
    {

        Cursor dbCursor = db.sqlLiteDatabase.rawQuery("SELECT " + Database.KEY_ID + " FROM  " + Database.TABLE_TABLES, null);

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
    * inserting a table
    */
    public long add(String name, String color) {

        ContentValues values = new ContentValues();
        values.put(Database.KEY_TABLES_NAME, name);
        values.put(Database.KEY_TABLES_COLOR, color);
        values.put(Database.KEY_DATE, Database.getDateTime());

        /*
            insert the row
            insert() returns -1 if not inserted, otherwise returns the ID of just inserted row
            ref: http://stackoverflow.com/questions/27863609/android-sqlite-check-if-inserted-new-value
         */

        return db.sqlLiteDatabase.insert(Database.TABLE_TABLES, null, values);
    }

    /*
    * delete a table:
    *   returns the number of affected rows:
    *       0           :   no rows deleted
    *       1 or more   :   rows deleted
    *
    */
    public void delete(int tID) {

        // delete the table
        int affectedRows = db.sqlLiteDatabase.delete(Database.TABLE_TABLES, Database.KEY_ID + "=?", new String[]{String.valueOf(tID)});

        // if table deleted, delete its subjects
        if (affectedRows == 1)
            affectedRows += db.sqlLiteDatabase.delete(Database.TABLE_SUBJECTS, Database.KEY_SUBJECTS_TABLEID + "=?", new String[]{String.valueOf(tID)});

        // show message if operation is done
        if (affectedRows > 0)
            Toast.makeText(activity, "Deleted", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(activity, "Not Deleted", Toast.LENGTH_LONG).show();

        // refresh the tables
        list();
    }

    // count how many subjects in a table
    public int getCountSubjects(int id) {

        Cursor dbCursor = db.sqlLiteDatabase.rawQuery("SELECT " + Database.KEY_ID + " FROM " + Database.TABLE_SUBJECTS + " WHERE " + Database.KEY_SUBJECTS_TABLEID + " = " + id, null);
        int count = dbCursor.getCount();
        dbCursor.close();

        return count;
    }

    public void list(){

        LinearLayout layoutNoData = (LinearLayout) activity.findViewById(R.id.layoutNoData);
        LinearLayout layoutPage = (LinearLayout) activity.findViewById(R.id.layoutPage);

        if (isAvailable())
        {
            // hide no data layout
            layoutNoData.setVisibility(View.GONE);
        }
        else
        {
            // hide page layout
            layoutPage.setVisibility(View.GONE);

            // stop executing this method, because there is no data
            return;
        }

        Cursor dbCursor = db.sqlLiteDatabase.rawQuery("SELECT * FROM " + Database.TABLE_TABLES, null);

        /*
            List of data:
                adapterData[?][0] = id
                adapterData[?][1] = name
                adapterData[?][2] = color
        */
        final String adapterData[][] = new String[dbCursor.getCount()][3];

        int currentArrayIndex = 0;

        while ( dbCursor.moveToNext() )
        {
            adapterData[currentArrayIndex][0] = dbCursor.getString(dbCursor.getColumnIndex(Database.KEY_ID));
            adapterData[currentArrayIndex][1] = dbCursor.getString(dbCursor.getColumnIndex(Database.KEY_TABLES_NAME));
            adapterData[currentArrayIndex][2] = dbCursor.getString(dbCursor.getColumnIndex(Database.KEY_TABLES_COLOR));

            currentArrayIndex++;
        }
        dbCursor.close();

        // tables list
        final ListView listviewTables = (ListView) activity.findViewById(R.id.listviewTables);
        listviewTables.setAdapter(new ItemsAdapter(activity, adapterData));

        // on click item
        listviewTables.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                Log.e("ID", "" + adapterData[position][0]);
                Log.e("Name", "" + adapterData[position][1]);

                // go to the activity that shows table's subjects
                Intent IntentActivityToGo = new Intent(activity, SubjectsActivity.class);
                IntentActivityToGo.putExtra("QueryType", "ShowSubjects");
                IntentActivityToGo.putExtra("QueryValue", Integer.parseInt(adapterData[position][0]));
                activity.startActivity(IntentActivityToGo);

                Toast.makeText(activity, "ID: " + adapterData[position][0] + " | Name: " + adapterData[position][1], Toast.LENGTH_LONG).show();
            }

        });

        // on list view item long press
        listviewTables.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView parent, View view, final int position, long id) {

                //
                optionsMenu(adapterData[position][1], Integer.parseInt(adapterData[position][0]));

                return true;
            }
        });



    }

    private void optionsMenu(String dialogTitle, final int tID) {

        final String OPTION_EDIT = "Edit";
        final String OPTION_DELETE = "Delete";
        final String OPTION_CANCEL = "Cancel";
        final CharSequence[] options = { OPTION_EDIT, OPTION_DELETE, OPTION_CANCEL};

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(activity);
        builder.setTitle(dialogTitle);
        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals(OPTION_EDIT))
                {
                    Toast.makeText(activity, OPTION_EDIT, Toast.LENGTH_LONG).show();
                }
                else if (options[item].equals(OPTION_DELETE))
                {

                    // show confirmation dialog
                    AlertDialog.Builder builderDelete = new AlertDialog.Builder(activity);

                    builderDelete.setTitle(OPTION_DELETE);
                    builderDelete.setMessage("Are you sure you want to delete the table and its subjects?");

                    builderDelete.setPositiveButton("Yes, Delete", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {

                            // delete the selected table
                            delete(tID);

                        }

                    });

                    builderDelete.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog alert = builderDelete.create();
                    alert.show();

                }
                else if (options[item].equals(OPTION_CANCEL))
                {
                    Toast.makeText(activity, OPTION_CANCEL, Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }

            }

        });

        builder.show();

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
            TextView textviewCount;
            View viewColor;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            final ViewHolder holder;

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.listview_tables, null);
                holder = new ViewHolder();

                holder.textviewName = (TextView) convertView.findViewById(R.id.textviewName);
                holder.textviewCount = (TextView) convertView.findViewById(R.id.textviewCount);
                holder.viewColor = convertView.findViewById(R.id.viewColor);

                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            // fill data
            holder.textviewName.setText(lis[position][1]);

            // show how many subjects in each table
            int count = getCountSubjects(Integer.parseInt(lis[position][0]));
            String countSubject;

            if (count == 0)
                countSubject = "No Subjects";
            else if (count == 1)
                countSubject = "1 Subject";
            else
                countSubject = count + " Subjects";

            holder.textviewCount.setText(countSubject);

            // set item color
            switch (lis[position][2]) {
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
