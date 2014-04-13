package com.ubica.docshare;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ubica.docshare.AddFolderActivity;
import com.ubica.docshare.DatabaseHandler;
import com.ubica.docshare.JSONFunction;
import com.ubica.docshare.MyFile;

import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.util.JsonWriter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.support.v4.app.NavUtils;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.preference.PreferenceManager;

public class GetFolderActivity extends Activity {
	
	SharedPreferences preferenceManager;
	DownloadManager downloadManager;
	ProgressDialog progress;
	DatabaseOperations database;
	
	String home = "http://142.103.25.29/UbicaUpload/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	
    	super.onCreate(savedInstanceState);
		 
    	// Get the pass from the intent
	    Intent intent = getIntent();
	    String pass = intent.getStringExtra(AddFolderActivity.EXTRA_MESSAGE);
	    
	    // check pass if its correct
	    JSONArray jsonarray;
	    try {
			jsonarray = new JSONFunction().execute(home + "keys.php").get();
			
			for(int i=0; i<jsonarray.length(); i++) {
				try {
					JSONObject entry = jsonarray.getJSONObject(i);
					JSONArray val = entry.getJSONArray("key");
					String masterPass = val.getJSONObject(0).getString("pass");
					String db = val.getJSONObject(0).getString("table");
					if (masterPass.equals(pass)) {
						// download
						Log.d("Done:", "almost");
						downloadFolder(db);
						Log.d("Done:", "yes");
					}
					else {
						// move onto next
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ExecutionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    Intent intent1 = new Intent(this, MainActivity.class);
		startActivity(intent1);

    }


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.get_folder, menu);
		return true;
	}
    
    @SuppressLint("NewApi")
	public void downloadFolder(String table) throws InterruptedException, ExecutionException, JSONException, IOException {
    	JSONArray jsonarray;
		jsonarray = new JSONFunction().execute(home + "connect.php").get();
//		 initialize local JSONObject to handle local data
		JSONArray localarray = new JSONArray();
		JSONObject local = new JSONObject();
		local.put(table, localarray);
		
		for(int i=0; i<jsonarray.length(); i++) {
			try {
				JSONObject entry = jsonarray.getJSONObject(i);
				JSONArray val = entry.optJSONArray(table);
				if (val != null) {
					for(int j=0; j<val.length(); j++) {
						String name = val.getJSONObject(j).getString("Name");
						String document = home + table + "/" + name;
						Log.d("document:", document);
						
						File direct = new File(Environment.getExternalStorageDirectory()
				                + "UbiCA");
						
						if (!direct.exists()) {
				            direct.mkdirs();
				        }
						
						DownloadManager.Request request = new DownloadManager.Request(Uri.parse(document));
						request.setTitle(name);
						// 
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						    request.allowScanningByMediaScanner();
						    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
						}
						if (Environment.getExternalStorageState() == null) {
				            File directory = new File(Environment.getExternalStorageDirectory()
				                    + "/UbiCA/" + table);				            
				            
				            // if no directory exists, create new directory
				            if (!directory.exists()) {
				                directory.mkdir();
				            }
				            Log.d("path", directory.getAbsolutePath());
						}
						request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, name);
//						request.setDestinationInExternalPublicDir("/UbiCA/", table + "/" + name);

						
						// get download service and enqueue file
						DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
						manager.enqueue(request);
						
//						 add entry to local db
						Date today = new Date();
						Log.d("Date:", today.toString());
						Timestamp now = new Timestamp(today.getTime());
						Log.d("Timestamp:", now.toString());
						MyFile newFile = new MyFile(table, name, now);
						
						database = new DatabaseOperations(this);
					    database.open();
						database.addFile(newFile);
					}
				}
				else {
//					do nothing
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	
    }
    
}