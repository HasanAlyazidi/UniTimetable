package com.highestweb.apps.unitimetable;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;

/**
 * Created by HasanYazidi on 4/10/2015.
 */
public class Profile {

    Database db;
    Activity activity;

    // profile data
    public String profileStudentID = null;
    public String profileName = null;
    public String profileProgram = null;
    public String profileSemester = null;
    public String profilePicturePath = null;

    Profile(Activity activity)
    {
        this.activity = activity;

        // open database
        db = new Database(activity.getApplicationContext());

        // get profile data from the database, then store them in class variables
        this.getData();
    }

    // check if profile has been setup, if so it returns true
    public boolean isSetup()
    {

        // get the data of profile
        Cursor dbCursor = db.sqlLiteDatabase.rawQuery("SELECT " + Database.KEY_ID + " FROM  " + Database.TABLE_PROFILE, null);

        boolean returnValue;
        if (dbCursor.moveToFirst() && dbCursor.getCount() == 1)
        {
            returnValue = true; // profile is setup
        }
        else
        {
            returnValue = false; // profile is not setup
        }
        dbCursor.close();

        return returnValue;
    }

    /*
    * inserting a profile
    */
    public long saveProfile(String name, String studentID, String program, String semester, String picturePath) {

        ContentValues values = new ContentValues();
        values.put(Database.KEY_PROFILE_NAME, name);
        values.put(Database.KEY_PROFILE_STUDENT_ID, studentID);
        values.put(Database.KEY_PROFILE_PROGRAM, program);
        values.put(Database.KEY_PROFILE_SEMESTER, semester);
        values.put(Database.KEY_PROFILE_PICTURE_PATH, picturePath);
        values.put(Database.KEY_DATE, Database.getDateTime());

        long returnValue;

        // insert the profile if it is already saved, otherwise, edit the profile
        if (isSetup())
        {
            /*
                update the row
                update() returns the number of affected rows
                ref: http://developer.android.com/reference/android/database/sqlite/SQLiteDatabase.html#update%28java.lang.String,%20android.content.ContentValues,%20java.lang.String,%20java.lang.String[]%29
            */

            returnValue = db.sqlLiteDatabase.update(Database.TABLE_PROFILE, values, "id = ?", new String[] { String.valueOf(1) });

        }
        else
        {
            /*
                insert the row
                insert() returns -1 if not inserted, otherwise returns the ID of just inserted row
                ref: http://stackoverflow.com/questions/27863609/android-sqlite-check-if-inserted-new-value
             */

            returnValue = db.sqlLiteDatabase.insert(Database.TABLE_PROFILE, null, values);

        }

        return returnValue;
    }

    // get profile data from the database, then store them in class variables
    public void getData(){

        Cursor dbCursor = db.sqlLiteDatabase.rawQuery("SELECT * FROM " + Database.TABLE_PROFILE + " WHERE id = 1", null);

        if (dbCursor.moveToFirst() && dbCursor.getCount() == 1)
        {
            this.profileStudentID = dbCursor.getString(dbCursor.getColumnIndex(Database.KEY_PROFILE_STUDENT_ID));
            this.profileName = dbCursor.getString(dbCursor.getColumnIndex(Database.KEY_PROFILE_NAME));
            this.profileProgram = dbCursor.getString(dbCursor.getColumnIndex(Database.KEY_PROFILE_PROGRAM));
            this.profileSemester = dbCursor.getString(dbCursor.getColumnIndex(Database.KEY_PROFILE_SEMESTER));
            this.profilePicturePath = dbCursor.getString(dbCursor.getColumnIndex(Database.KEY_PROFILE_PICTURE_PATH));
        }
        else
        {
            this.profileStudentID = "";
            this.profileName = "";
            this.profileProgram = "";
            this.profileSemester = "";
            this.profilePicturePath = "";
        }
        dbCursor.close();

    }

    // get profile data: student id
    public String getStudentID(){
        return this.profileStudentID;
    }

    // get profile data: name
    public String getName(){
        return this.profileName;
    }

    // get profile data: program
    public String getProgram(){
        return this.profileProgram;
    }

    // get profile data: semester
    public String getSemester(){
        return this.profileSemester;
    }

    // get profile data: picture path
    public String getPicturePath(){
        return this.profilePicturePath;
    }



}
