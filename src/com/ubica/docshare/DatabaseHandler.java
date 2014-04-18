package com.ubica.docshare;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ubica.docshare.MyFile;

@SuppressLint("SimpleDateFormat")
public class DatabaseHandler extends SQLiteOpenHelper{

	public static final String DATABASE_NAME = "ubica";
	public static final int DATABASE_VERSION = 1;
	public static final String TABLE_FILES = "files";

	 // Files Table Columns names
    public static final String KEY_NAME = "name";
    public static final String KEY_FOLDER = "folder";
    public static final String KEY_VERSION = "version";
    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_FILES + " ( " + KEY_FOLDER + 
			" TEXT NOT NULL, " + KEY_NAME + " TEXT NOT NULL UNIQUE PRIMARY KEY, " +
			KEY_VERSION + " INT DEFAULT 0 NOT NULL);";

	
	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		arg0.execSQL(CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		arg0.execSQL("DROP TABLE IF EXISTS " + TABLE_FILES);
        onCreate(arg0);
		
	}
}
