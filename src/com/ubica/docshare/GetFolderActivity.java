package com.ubica.docshare;

import java.sql.Timestamp;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ubica.docshare.AddFolderActivity;
import com.ubica.docshare.DatabaseHandler;
import com.ubica.docshare.JSONFunction;
import com.ubica.docshare.MyFile;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.preference.PreferenceManager;

public class GetFolderActivity extends Activity {
	
	SharedPreferences preferenceManager;
	DownloadManager downloadManager;
	ProgressDialog progress;
	
	String home = "http://142.103.25.29/UbicaUpload/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
	    
	    // Get the pass from the intent
	    Intent intent = getIntent();
	    String pass = intent.getStringExtra(AddFolderActivity.EXTRA_MESSAGE);
	    
	    // check pass if its correct
		JSONObject jsonobject;
		jsonobject = JSONFunction.getJSONfromURL(home + "keys.php");
				
		for(int i=0; i<jsonobject.length(); i++) {
			try {
				JSONObject entry = jsonobject.getJSONObject("key");
				String masterPass = entry.getString("pass");
				String db = entry.getString("table");
				if (masterPass == pass) {
					// download
					
					downloadFolder(db);
				}
				else {
					// move onto next
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

    }


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.get_folder, menu);
		return true;
	}
    
    @SuppressLint("NewApi")
	public void downloadFolder(String table) throws JSONException {
    	JSONObject jsonobject;
		jsonobject = JSONFunction.getJSONfromURL(home + "connect.php"); 
		JSONArray curr = jsonobject.getJSONArray(table);
		
		for(int j=0; j<curr.length(); j++) {
			JSONObject each = curr.getJSONObject(j);
			String name = each.getString("Name");
	    	String document = home + table + "/" + name;
	    	
	    	preferenceManager = PreferenceManager.getDefaultSharedPreferences(this);
			downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
				
			Uri documentUri = Uri.parse(document);
			 
			DownloadManager.Request request = new DownloadManager.Request(documentUri);
				
			long download_id = downloadManager.enqueue(request);
			Editor PrefEdit = preferenceManager.edit();
		    PrefEdit.putLong(document, download_id);
			PrefEdit.commit();
			
			// add entry to local db
			Date today = new Date();
			Timestamp now = new Timestamp(today.getTime());
			MyFile newFile = new MyFile(table, name, now);
			DatabaseHandler db = new DatabaseHandler(this);
			db.addFile(newFile);

		}
		 
		
    }
    
}