package com.ubica.docshare;

import com.ubica.docshare.AddFolderActivity;
import com.ubica.docshare.GetDocumentActivity;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {
	
    public final static String EXTRA_MESSAGE = "com.ubica.docshare.MESSAGE";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void sync(View view){
		Intent intent = new Intent(this, GetDocumentActivity.class);
		startActivity(intent);

	}
	
	public void addFolder(View view) {
		Intent intent = new Intent(this, AddFolderActivity.class);
		startActivity(intent);
	}

}
