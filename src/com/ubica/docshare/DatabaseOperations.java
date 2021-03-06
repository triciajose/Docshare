package com.ubica.docshare;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DatabaseOperations {
	
	private DatabaseHandler dbhandler;
	private SQLiteDatabase database;
	private String[] MYFILE_TABLE_COLUMNS = { DatabaseHandler.KEY_FOLDER, DatabaseHandler.KEY_NAME, DatabaseHandler.KEY_VERSION};

	public DatabaseOperations(Context context) {
		dbhandler = new DatabaseHandler(context);
	}
	
//	open the database
	public void open() {
		database = dbhandler.getWritableDatabase();
	}
	
//	close the database
	public void close() {
		dbhandler.close();
	}
	
//	add file to the database
//	params: file
	public void addFile(MyFile file) {
		 
	    ContentValues values = new ContentValues();
	    values.put(DatabaseHandler.KEY_NAME, file.getName()); // Name
	    values.put(DatabaseHandler.KEY_FOLDER, file.getFolder()); // Folder
	    values.put(DatabaseHandler.KEY_VERSION, file.getVersion()); // Date
	    
	    // Inserting Row
	    database.insert(DatabaseHandler.TABLE_FILES, null, values);
	}
	
//	return all files in the database
	public List<MyFile> getFiles() throws ParseException {
		List<MyFile> files = new ArrayList<MyFile>();
	    // Select All Query
	    String selectQuery = "SELECT * FROM " + DatabaseHandler.TABLE_FILES;
	 
	    Cursor cursor = database.rawQuery(selectQuery, null);
	    
	    // looping through all rows and adding to list
	    if (cursor.moveToFirst()) {
	        do {
	            MyFile file = new MyFile();
	            file.setFolder(cursor.getString(0));
	            file.setName(cursor.getString(1));
	            file.setVersion(cursor.getInt(2));
	            files.add(file);
	        } while (cursor.moveToNext());
	    }
	 
	    // return files
	    return files;
	}
	
//	get all unique folders in database
	public List<String> getFolders(){
		List<String> folders = new ArrayList<String>();
	    // Select All Query
	    String selectQuery = "SELECT DISTINCT " + DatabaseHandler.KEY_FOLDER + " FROM " + DatabaseHandler.TABLE_FILES;
	 
	    Cursor cursor = database.rawQuery(selectQuery, null);
	    // looping through all rows and adding to list
	    if (cursor.moveToFirst()) {
	        do {
	            folders.add(cursor.getString(0));
	        } while (cursor.moveToNext());
	    }
	 
	    // return files
	    return folders;
	}

//	update file in database
//	params: file
	public int updateFile(MyFile file) {
	 
	    ContentValues values = new ContentValues();
	    
//	    values.put(DatabaseHandler.KEY_NAME, file.getName()); // Name
//	    values.put(DatabaseHandler.KEY_FOLDER, file.getFolder()); // Folder
	    values.put(DatabaseHandler.KEY_VERSION, file.getVersion());
	 
	    // updating row
	    return database.update(DatabaseHandler.TABLE_FILES, values, DatabaseHandler.KEY_NAME + " = ?",
	            new String[] { String.valueOf(file.getName()) });
	}

//	get file in the database
//	params: file name
	public MyFile getFile(String name) throws ParseException {
		String selectQuery = "SELECT DISTINCT * FROM " + DatabaseHandler.TABLE_FILES + " WHERE " + DatabaseHandler.KEY_NAME + " = ?";
		Cursor cursor = database.rawQuery(selectQuery, new String[] {name});
	    MyFile file = new MyFile();
	    if (cursor.moveToFirst()) {
		    file.setFolder(cursor.getString(0));
		    file.setName(cursor.getString(1));
            file.setVersion(cursor.getInt(2));
	    }
	    return file;
	}
	
}
