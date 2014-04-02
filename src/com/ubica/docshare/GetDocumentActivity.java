package com.ubica.docshare;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ubica.docshare.DatabaseHandler;
import com.ubica.docshare.JSONFunction;
import com.ubica.docshare.MainActivity;
import com.ubica.docshare.MyFile;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.support.v4.app.NavUtils;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
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
	
	String home = "http://142.103.25.29/UbicaUpload/";
	
	String message = "Download in Progress";
		
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    // Get the message from the intent
	    Intent intent = getIntent();
	    String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
	    
//	    Check remote DB for changes
		JSONObject jsonobject;
		jsonobject = JSONFunction.getJSONfromURL(home + "connect.php");
		
		try { // get list of files to change
			List<String> files = this.parseJSON(jsonobject);
			
//			download each file
			for(int m=0; m< files.size(); m++) {
				 String document = home + files.get(m);
				 String[] parts = document.split("/");
				 
				 preferenceManager = PreferenceManager.getDefaultSharedPreferences(this);
				 downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
					
				 Uri documentUri = Uri.parse(document);
				 
				 DownloadManager.Request request = new DownloadManager.Request(documentUri);
					
				long download_id = downloadManager.enqueue(request);
				Editor PrefEdit = preferenceManager.edit();
			    PrefEdit.putLong(document, download_id);
				PrefEdit.commit();
				
				Date today = new Date();
				Timestamp now = new Timestamp(today.getTime());
				//	update local entries
				MyFile newFile = new MyFile(parts[0], parts[1], now);
				DatabaseHandler db = new DatabaseHandler(this);
				db.updateFile(newFile);
				
			}
		} catch (JSONException e1) {
			e1.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

	    // Create the text view
	    TextView textView = new TextView(this);
	    textView.setTextSize(40);
	    textView.setText(message);

	    // Set the text view as the activity layout
	    setContentView(textView);
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
	
	public List<String> parseJSON(JSONObject jsonobject) throws JSONException, ParseException {
		DatabaseHandler db = new DatabaseHandler(this);
		List<String> folders = db.getFolders();
		
		List<String> download = new ArrayList<String>();
		
		for(int i=0; i<folders.size(); i++) {
			JSONArray curr = jsonobject.getJSONArray(folders.get(i));
			
			// go through each array and check if local is old
			for(int j=0; j<curr.length(); j++) {
				JSONObject each = curr.getJSONObject(j);
				String name = each.getString("Name");
				int version = each.getInt("Version");
				Timestamp timestamp = toTimestamp(each.getString("DateModified"));
				// if it is old, then put into download array
				MyFile file = db.getFile(name);
				if (timestamp.compareTo(file.getDate()) > 0 ) {
					download.add(folders.get(i) + "/" + name);
				}
			}
		}
		
		return download;
	}
	
	@SuppressLint("SimpleDateFormat")
	public Timestamp toTimestamp(String string) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return (Timestamp) dateFormat.parse(string);
		
	}


}
