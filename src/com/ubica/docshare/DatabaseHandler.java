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

	private static final String DATABASE_NAME = "ubica";
	private static final int DATABASE_VERSION = 1;
	private static final String TABLE_FILES = "files";

	 // Files Table Columns names
    private static final String KEY_NAME = "name";
    private static final String KEY_FOLDER = "folder";
    private static final String KEY_DATE = "dateModified";

	
	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub
		String CREATE_TABLE = "CREATE TABLE" + TABLE_FILES + "(" + KEY_FOLDER + 
				"TEXT NOT NULL," + KEY_NAME + "TEXT NOT NULL UNIQUE PRIMARY KEY," +
				KEY_DATE + "TIMESTAMP DEFAULT CORRENT_TIMESTAMP NOT NULL)";
		arg0.execSQL(CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		arg0.execSQL("DROP TABLE IF EXISTS " + TABLE_FILES);
        onCreate(arg0);
		
	}
	
	public void addFile(MyFile file) {
		SQLiteDatabase db = this.getWritableDatabase();
		 
	    ContentValues values = new ContentValues();
	    values.put(KEY_NAME, file.getName()); // Name
	    values.put(KEY_FOLDER, file.getFolder()); // Folder
	    values.put(KEY_DATE, file.getDate().toString()); // Folder
	    
	    // Inserting Row
	    db.insert(TABLE_FILES, null, values);
	    db.close(); // Closing database connection
	}
	
	public List<MyFile> getFiles() throws ParseException {
		List<MyFile> files = new ArrayList<MyFile>();
	    // Select All Query
	    String selectQuery = "SELECT  * FROM " + TABLE_FILES;
	 
	    SQLiteDatabase db = this.getWritableDatabase();
	    Cursor cursor = db.rawQuery(selectQuery, null);
	 
	    // looping through all rows and adding to list
	    if (cursor.moveToFirst()) {
	        do {
	            MyFile file = new MyFile();
	            file.setFolder(cursor.getString(0));
	            file.setName(cursor.getString(1));
	            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	            file.setDate(dateFormat.parse(cursor.getString(2)));
	            files.add(file);
	        } while (cursor.moveToNext());
	    }
	 
	    // return files
	    return files;
	}
	
	public List<String> getFolders(){
		List<String> folders = new ArrayList<String>();
	    // Select All Query
	    String selectQuery = "SELECT" + KEY_FOLDER + "FROM " + TABLE_FILES;
	 
	    SQLiteDatabase db = this.getWritableDatabase();
	    Cursor cursor = db.rawQuery(selectQuery, null);
	 
	    // looping through all rows and adding to list
	    if (cursor.moveToFirst()) {
	        do {
	            folders.add(cursor.getString(0));
	        } while (cursor.moveToNext());
	    }
	 
	    // return files
	    return folders;
	}

	public int updateFile(MyFile file) {
	    SQLiteDatabase db = this.getWritableDatabase();
	 
	    ContentValues values = new ContentValues();
	    values.put(KEY_DATE, file.getDate().toString());
	 
	    // updating row
	    return db.update(TABLE_FILES, values, KEY_NAME + " = ?",
	            new String[] { String.valueOf(file.getName()) });
	}

	public MyFile getFile(String name) throws ParseException {
		String selectQuery = "SELECT  * FROM " + TABLE_FILES + "WHERE name=" + name;
		 
	    SQLiteDatabase db = this.getWritableDatabase();
	    Cursor cursor = db.rawQuery(selectQuery, null);
	    	 
	    MyFile file = new MyFile();
	    file.setFolder(cursor.getString(0));
	    file.setName(cursor.getString(1));
	    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	    file.setDate(dateFormat.parse(cursor.getString(2)));
	    return file;
	}
}
