package com.bahaso.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bahaso.model.UserCurrentCourse;

import java.util.ArrayList;
import java.util.List;

public class DB_UserCourses extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "usercourse";
    private static final String TABLE_COURSE = "courses";

    // Contacts Table Columns names
    private static final String KEY_ID_COURSE = "id_course";
    private static final String KEY_NAME_COURSE = "name_course";
    private static final String KEY_ICON_COURSE = "icon_source";
    private static final String KEY_TOTAL_SCORE = "total_score";
    private static final String KEY_TOTAL_POINT = "total_point";
    private static final String KEY_SCORE_COURSE = "score_course";
    private static final String KEY_POINT_COURSE = "point_course";
    private static final String KEY_PROGRESS_COURSE = "progress_course";

    public DB_UserCourses(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_COURSE_TABLE = "CREATE TABLE " + TABLE_COURSE + "("
                + KEY_ID_COURSE + " INTEGER PRIMARY KEY,"
                + KEY_NAME_COURSE + " TEXT,"
                + KEY_ICON_COURSE + " BLOB,"
                + KEY_TOTAL_SCORE + " TEXT,"
                + KEY_TOTAL_POINT + " TEXT,"
                + KEY_SCORE_COURSE + " INTEGER,"
                + KEY_POINT_COURSE + " INTEGER,"
                + KEY_PROGRESS_COURSE + " REAL" + ")";

        db.execSQL(CREATE_COURSE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSE);
        // Create tables again
        onCreate(db);
    }

    public void addCourse2DB(UserCurrentCourse ucc) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID_COURSE, ucc.getCourseID()); //0
        values.put(KEY_NAME_COURSE, ucc.getCourseName()); //1
        values.put(KEY_ICON_COURSE, ucc.getCourseImg()); //2
        values.put(KEY_TOTAL_SCORE, ucc.getTotalScore()); //3
        values.put(KEY_TOTAL_POINT, ucc.getTotalPoint()); //4

        values.put(KEY_SCORE_COURSE, ucc.getCourseScore()); //5
        values.put(KEY_POINT_COURSE, ucc.getCoursePoint()); //7
        values.put(KEY_PROGRESS_COURSE, ucc.getCourseProgress()); //8

        // Inserting Row
        db.insert(TABLE_COURSE, null, values);
        db.close(); // Closing database connection

    }

    public UserCurrentCourse getSingleCourse(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_COURSE, new String[] { KEY_ID_COURSE,
                        KEY_NAME_COURSE, KEY_ICON_COURSE, KEY_TOTAL_SCORE, KEY_TOTAL_POINT,
                        KEY_SCORE_COURSE, KEY_POINT_COURSE, KEY_PROGRESS_COURSE }, KEY_ID_COURSE + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        UserCurrentCourse course = new UserCurrentCourse(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3),
                cursor.getString(4), cursor.getInt(5), cursor.getInt(6), cursor.getDouble(7));

        // return contact
        return course;
    }

    public List<UserCurrentCourse> getAllUserCourse() {
        List<UserCurrentCourse> courseList = new ArrayList<UserCurrentCourse>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_COURSE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                UserCurrentCourse ucc = new UserCurrentCourse();
                ucc.setCourseID(Integer.parseInt(cursor.getString(0)));
                ucc.setCourseName(cursor.getString(1));
                ucc.setCourseImg(cursor.getString(2));
                ucc.setTotalScore(cursor.getString(3));
                ucc.setTotalPoint(cursor.getString(4));
                ucc.setCourseScore(cursor.getInt(5));
                ucc.setCoursePoint(cursor.getInt(6));
                ucc.setCourseProgress(cursor.getDouble(7));
                // Adding contact to list
                courseList.add(ucc);
            } while (cursor.moveToNext());
        }
        // return contact list
        return courseList;
    }

}
