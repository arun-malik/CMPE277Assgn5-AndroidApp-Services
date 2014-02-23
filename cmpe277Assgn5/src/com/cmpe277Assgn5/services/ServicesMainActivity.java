package com.cmpe277Assgn5.services;

import com.cmpe277Assgn5.R;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;

public class ServicesMainActivity extends Activity {

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
	
	public void getPDFFiles(View view){
		Intent intent = new Intent(this, ListPdfFilesActivity.class); 
        startActivity(intent);
	}
	
	public void getImageFiles(View view){
		Log.v("Main Activity","Calling Image Files Download");
		Intent intent = new Intent(this, ListImageFilesActivity.class); 
        startActivity(intent);
	}
	
	public void getTextFiles(View view){
		Intent intent = new Intent(this, ListTextFilesActivity.class); 
        startActivity(intent);
	}
	
	public void closeApp(View view){
		this.finish();
	}

}
