package com.highestweb.apps.unitimetable;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by HasanYazidi on 2/10/2015.
 * SOURCE: http://www.androidhive.info/2013/09/android-sqlite-database-with-multiple-tables/
 */
public class Database extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "UniTimeTableDatabase";

    // Table Names
    public static final String TABLE_PROFILE = "profile";
    public static final String TABLE_TABLES = "tables";
    public static final String TABLE_SUBJECTS = "subjects";

    // Common column names
    public static final String KEY_ID = "id";
    public static final String KEY_DATE = "date";

    // Profile Table - column names
    public static final String KEY_PROFILE_NAME = "name";
    public static final String KEY_PROFILE_STUDENT_ID = "student_id";
    public static final String KEY_PROFILE_PROGRAM = "program";
    public static final String KEY_PROFILE_SEMESTER = "semester";
    public static final String KEY_PROFILE_PICTURE_PATH = "picture_path";

    // Tables Table - column names
    public static final String KEY_TABLES_NAME = "name";
    public static final String KEY_TABLES_COLOR = "color";

    // Subjects Table - column names
    public static final String KEY_SUBJECTS_NAME = "name";
    public static final String KEY_SUBJECTS_DAY = "day";
    public static final String KEY_SUBJECTS_START = "start";
    public static final String KEY_SUBJECTS_END = "end";
    public static final String KEY_SUBJECTS_PLACE = "place";
    public static final String KEY_SUBJECTS_TEACHER = "teacher";
    public static final String KEY_SUBJECTS_COLOR = "color";
    public static final String KEY_SUBJECTS_TABLEID = "table_id";

    // Profile Create Statements
    private static final String CREATE_TABLE_PROFILE = "CREATE TABLE "
            + TABLE_PROFILE + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_PROFILE_NAME + " TEXT,"
            + KEY_PROFILE_STUDENT_ID + " INTEGER,"
            + KEY_PROFILE_PROGRAM + " TEXT,"
            + KEY_PROFILE_SEMESTER + " TEXT,"
            + KEY_PROFILE_PICTURE_PATH + " TEXT,"
            + KEY_DATE + " DATETIME"
            + ")";

    // tables table create statement
    private static final String CREATE_TABLE_TABLES = "CREATE TABLE " + TABLE_TABLES
            + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_TABLES_NAME + " TEXT,"
            + KEY_TABLES_COLOR + " TEXT,"
            + KEY_DATE + " DATETIME" + ")";

    // subjects table create statement
    private static final String CREATE_TABLE_SUBJECTS = "CREATE TABLE "
            + TABLE_SUBJECTS + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_SUBJECTS_NAME + " text,"
            + KEY_SUBJECTS_DAY + " TEXT,"
            + KEY_SUBJECTS_START + " TEXT,"
            + KEY_SUBJECTS_END + " TEXT,"
            + KEY_SUBJECTS_PLACE + " TEXT,"
            + KEY_SUBJECTS_COLOR + " TEXT,"
            + KEY_SUBJECTS_TEACHER + " TEXT,"
            + KEY_SUBJECTS_TABLEID + " INTEGER,"
            + KEY_DATE + " DATETIME" + ")";

    public SQLiteDatabase sqlLiteDatabase;

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        sqlLiteDatabase = this.getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // creating required tables
        db.execSQL(CREATE_TABLE_PROFILE);
        db.execSQL(CREATE_TABLE_TABLES);
        db.execSQL(CREATE_TABLE_SUBJECTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TABLES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUBJECTS);

        // create new tables
        onCreate(db);
    }

    /**
     * get datetime
     * */
    public static String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    // closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }


}