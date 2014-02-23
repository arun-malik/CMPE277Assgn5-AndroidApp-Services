package com.cmpe277Assgn5.services;

import java.util.ArrayList;
import com.cmpe277Assgn5.services.LocalService.LocalServiceBinder;
import com.cmpe277Assgn5.utility.Constants;
import com.cmpe277Assgn5.R;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

public class ListTextFilesActivity extends Activity {
	
	private static final String ACTIVITY_NAME= "ListPdfFilesActivity";
	

	TextBroadcastReceiver txtDownloadReceiver;
	ArrayList<String> URLs;
	EditText txt1, txt2, txt3, txt4, txt5 ;
	protected LocalService downloadLocalService;
	
	protected ServiceConnection conn = new ServiceConnection(){
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.i(ACTIVITY_NAME, Constants.ON_SERVICE_ATTACHED);

			LocalServiceBinder localServiceBinderObject = (LocalServiceBinder) service;
			downloadLocalService = localServiceBinderObject.getService();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.i(ACTIVITY_NAME,Constants.ON_SERVICE_DISCONNECTED);

			downloadLocalService = null;
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.txtdownload_activity);
		
		IntentFilter intentFilter = new IntentFilter(TextBroadcastReceiver.PROCESS_RESPONSE);		
		intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
		txtDownloadReceiver = new TextBroadcastReceiver();
		registerReceiver(txtDownloadReceiver, intentFilter);


	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.text, menu);
		return true;
	}


	@SuppressLint("InlinedApi")
	public void startTextDownload(View view){

		URLs = new ArrayList<String>();
		
		txt1 = (EditText) findViewById(R.id.text1);
		txt2 = (EditText) findViewById(R.id.text2);
		txt3 = (EditText) findViewById(R.id.text3);
		txt4 = (EditText) findViewById(R.id.text4);
		txt5 = (EditText) findViewById(R.id.text5);

		if(txt1.getText() != null ) {
			URLs.add(txt1.getText().toString());
		}

		if(txt2.getText() != null ) {
			URLs.add(txt2.getText().toString());
		}

		if(txt3.getText() != null) {
			URLs.add(txt3.getText().toString());
		}

		if(txt4.getText() != null ) {
			URLs.add(txt4.getText().toString());
		}

		if(txt5.getText() != null ) {
			URLs.add(txt5.getText().toString());
		}


		RadioGroup rdgServiceSelection = (RadioGroup) findViewById(R.id.rdgText);
		int selectedServiceRdBtn = rdgServiceSelection.getCheckedRadioButtonId();

		if (selectedServiceRdBtn != -1){

			if (selectedServiceRdBtn == R.id.rdTxtLocalServiceWithAutoCreate){ //normal local service -- bind auto create

				Intent intent = new Intent(this, LocalService.class);
				intent.putStringArrayListExtra("filePath", URLs);
				intent.putExtra("DownLoadFiletext", "Number_Of_TXT_Files_Downloaded_");
				bindService(intent, conn, Context.BIND_AUTO_CREATE);
				startService(intent);

			} 
			else if (selectedServiceRdBtn == R.id.rdTxtLocalServiceWithActivity){ //normal local service -- bind with activity

				Intent intent = new Intent(this, LocalService.class);
				intent.putStringArrayListExtra("filePath", URLs);
				intent.putExtra("DownLoadFiletext", "Number_Of_TXT_Files_Downloaded_");
				bindService(intent, conn, Context.BIND_ADJUST_WITH_ACTIVITY);
				startService(intent);

			} 

			else if(selectedServiceRdBtn == R.id.rdTxtBindSync) {  //Bind Intent Service --Sync

				Intent intent = new Intent(getBaseContext(), BindIntentService.class); 
				intent.putStringArrayListExtra("filePath", URLs);
				intent.putExtra("DownLoadFiletext", "Number_Of_TXT_Files_Downloaded_");
				startService(intent);   

			} else {

				new downloadFiles().execute("");    /// Bind Tntent Service -- With Async Task
			}
		}



	}

	private class downloadFiles extends AsyncTask<String, Void, String> {
		@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
		@Override
		protected String doInBackground(String... str) {

			Intent intentForLocalService = new Intent(getBaseContext(), LocalService.class);
			intentForLocalService.putStringArrayListExtra("filePath", URLs);
			intentForLocalService.putExtra("DownLoadFiletext", "Number_Of_TXT_Files_Downloaded_");
			startService(intentForLocalService);
			return null;
		}

		@Override
		protected void onPostExecute(String result) {

		}
	}

	public void goBackToMainActivity(View view) {
		this.finish();
	}

	@Override
	public void onDestroy() {
		RadioGroup radioGroup = (RadioGroup) findViewById(R.id.rdgText);
		int id = radioGroup.getCheckedRadioButtonId();
		if (id != -1){
			if (id == R.id.rdTxtLocalServiceWithAutoCreate || id == R.id.rdTxtLocalServiceWithActivity){
				Log.i(ACTIVITY_NAME, Constants.ON_DESTROY);
				unbindService(conn);
			} 
		}

		this.unregisterReceiver(txtDownloadReceiver);

		super.onDestroy();
	}

	public class TextBroadcastReceiver extends BroadcastReceiver{

		public static final String PROCESS_RESPONSE = "com.serviceApp.intent.action.PROCESS_RESPONSE";

		@Override 
		public void onReceive(Context context, Intent intent)
		{ 
			String receiverResponseFileCount = intent.getStringExtra("fileCount");
			Toast.makeText(getBaseContext(), receiverResponseFileCount, Toast.LENGTH_LONG).show(); 
		}


	}

}


