package com.ubica.docshare;

import java.io.File;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ubica.docshare.DatabaseHandler;
import com.ubica.docshare.JSONFunction;
import com.ubica.docshare.MainActivity;
import com.ubica.docshare.MyFile;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.support.v4.app.NavUtils;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.preference.PreferenceManager;

@SuppressLint("NewApi")
public class GetDocumentActivity extends Activity {

	SharedPreferences preferenceManager;
	DownloadManager downloadManager;
	ProgressDialog progress;
	DatabaseOperations database;
	
	String home = "http://142.103.25.29/UbicaUpload/";
	String message = "Download in Progress";
		
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    // Get the message from the intent
	    Intent intent = getIntent();
	    String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
	    
//	    Check remote DB for changes
		
		try {
			JSONArray jsonarray; 
			jsonarray = new JSONFunction().execute(home + "connect.php").get();
			
			List<String> files = this.parseJSON(jsonarray);
			
			for(int m=0; m< files.size(); m++) {
				 String document = home + files.get(m);
				 String[] parts = document.split("/");
				 
				 DownloadManager.Request request = new DownloadManager.Request(Uri.parse(document));
					request.setTitle(parts[1]);
					// 
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					    request.allowScanningByMediaScanner();
					    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
					}
					if (Environment.getExternalStorageState() == null) {
			            File directory = new File(Environment.getExternalStorageDirectory()
			                    + "UbiCA/" + parts[0]);
			            Log.d("path", directory.getAbsolutePath());
			            
			            // if no directory exists, create new directory
			            if (!directory.exists()) {
			                directory.mkdir();
			            }
					}
					request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS + "/UbiCA" + parts[0], parts[1]);

					// get download service and enqueue file
					DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
					manager.enqueue(request);
				Date today = new Date();
				Timestamp now = new Timestamp(today.getTime());
				//	update local entries
				MyFile newFile = new MyFile(parts[0], parts[1], now);
				database = new DatabaseOperations(this);
			    database.open();
				database.updateFile(newFile);
				
			}
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Intent intent1 = new Intent(this, MainActivity.class);
		startActivity(intent1);
	}
                                                                                                                                   


	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
	
	public List<String> parseJSON(JSONArray jsonarray) throws JSONException, ParseException {
		database = new DatabaseOperations(this);
		database.open();
		List<String> folders = database.getFolders();
		List<String> download = new ArrayList<String>();
		
		for(int i=0; i<folders.size(); i++) {
			for(int m=0; m<jsonarray.length(); m++) {
				JSONObject curr = jsonarray.getJSONObject(m);
				JSONArray val = curr.optJSONArray(folders.get(i));
				if(val != null) {
					for(int j=0; j<val.length(); j++) {
						String name = val.getJSONObject(j).getString("Name");
						Timestamp date = Timestamp.valueOf(val.getJSONObject(j).getString("DateModified"));
						// if it is old, then put into download array
						if (database.getFile(name).getName() != null) {
							MyFile file = database.getFile(name);
							if (date.compareTo(file.getDate()) > 0 ) {
								download.add(folders.get(i) + "/" + name);
							}
						}
						else {
							download.add(folders.get(i) + "/" + name);
						}
						
					}
				}
			}
		}
		Log.d("download", download.toString());
		return download;
	}

}
