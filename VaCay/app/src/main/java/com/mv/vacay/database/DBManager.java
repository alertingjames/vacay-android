package com.mv.vacay.database;

/**
 * Created by a on 5/7/2017.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.mv.vacay.models.UserEntity;

import java.util.ArrayList;

public class DBManager {

    private DatabaseHelper dbHelper;

    private Context context;

    private SQLiteDatabase database;

    public DBManager(Context c) {
        context = c;
    }

    public DBManager open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public void insert(String name, String email, String photo, String messages) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.NAME, name);
        contentValue.put(DatabaseHelper.EMAIL, email);
        contentValue.put(DatabaseHelper.PHOTO, photo);
        contentValue.put(DatabaseHelper.MESSAGES, messages);
        database.insert(DatabaseHelper.TABLE_NAME, null, contentValue);
    }

    public Cursor fetch() {
        String[] columns = new String[] { DatabaseHelper._ID, DatabaseHelper.NAME, DatabaseHelper.EMAIL, DatabaseHelper.PHOTO };
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public int update(long _id, String name, String email, String photo, String messages) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.NAME, name);
        contentValues.put(DatabaseHelper.EMAIL, email);
        contentValues.put(DatabaseHelper.PHOTO, photo);
        contentValues.put(DatabaseHelper.MESSAGES, messages);
        int i = database.update(DatabaseHelper.TABLE_NAME, contentValues, DatabaseHelper._ID + " = " + _id, null);
        return i;
    }

    public void delete(long _id) {
        database.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper._ID + "=" + _id, null);
    }

    public ArrayList<UserEntity> getAllMembers() {

        ArrayList<UserEntity> userEntities = new ArrayList<>();

        String[] columns = new String[] {
                DatabaseHelper._ID, DatabaseHelper.NAME, DatabaseHelper.EMAIL,DatabaseHelper.PHOTO, DatabaseHelper.MESSAGES};

        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, columns, null, null, null, null, null,null);

        if (cursor.moveToFirst()) {

            do {
                UserEntity user = new UserEntity();
                user.set_idx(Integer.parseInt(cursor.getString(0)));
                user.set_name(cursor.getString(1));
                user.set_email(cursor.getString(2));
                user.set_photoUrl(cursor.getString(3));
                user.set_num(cursor.getString(4));
                userEntities.add(0,user);

            } while (cursor.moveToNext());
        }

        cursor.close();

        return userEntities;
    }

}
