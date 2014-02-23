package com.cmpe277Assgn5.services;

import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import com.cmpe277Assgn5.R;
import com.cmpe277Assgn5.services.LocalService.LocalServiceBinder;
import com.cmpe277Assgn5.utility.Constants;

public class ListImageFilesActivity extends Activity {

	private static final String ACTIVITY_NAME= "ListPdfFilesActivity";
	
	ImageDownloadFileBroadcastReceiver imageDownloadReceiver;
	ArrayList<String> URLs;
	EditText img1, img2, img3, img4, img5 ;
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
		setContentView(R.layout.imgdownload_activity);

		IntentFilter intentFilter = new IntentFilter(ImageDownloadFileBroadcastReceiver.PROCESS_RESPONSE);		
		intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
		imageDownloadReceiver = new ImageDownloadFileBroadcastReceiver();
		registerReceiver(imageDownloadReceiver, intentFilter);


	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.image_file,menu);
		return true;
	}

	@SuppressLint("InlinedApi")
	public void startImageDownload(View view){
		URLs = new ArrayList<String>();
		
		img1 = (EditText) findViewById(R.id.editImage1);
		img2 = (EditText) findViewById(R.id.editImage2);
		img3 = (EditText) findViewById(R.id.editImage3);
		img4 = (EditText) findViewById(R.id.editImage4);
		img5 = (EditText) findViewById(R.id.editImage5);


		if(img1.getText() != null ) {
			URLs.add(img1.getText().toString());
		}

		if(img2.getText() != null ) {
			URLs.add(img2.getText().toString());
		}

		if(img3.getText() != null) {
			URLs.add(img3.getText().toString());
		}

		if(img4.getText() != null ) {
			URLs.add(img4.getText().toString());
		}

		if(img5.getText() != null ) {
			URLs.add(img5.getText().toString());
		}

		RadioGroup rdgServiceSelection = (RadioGroup) findViewById(R.id.rdgImage);
		int selectedServiceRdBtn = rdgServiceSelection.getCheckedRadioButtonId();

		if (selectedServiceRdBtn != -1){

			if (selectedServiceRdBtn == R.id.rdImgLocalServiceWithAutoCreate){ //normal local service -- bind auto create

				Intent intent = new Intent(this, LocalService.class);
				intent.putStringArrayListExtra("filePath", URLs);
				intent.putExtra("DownLoadFiletext", "Number_Of_IMAGE_Files_Downloaded_");
				bindService(intent, conn, Context.BIND_AUTO_CREATE);
				startService(intent);

			} 
			else if (selectedServiceRdBtn == R.id.rdImgLocalServiceWithBindActivity){ //normal local service -- bind with activity

				Intent intent = new Intent(this, LocalService.class);
				intent.putStringArrayListExtra("filePath", URLs);
				intent.putExtra("DownLoadFiletext", "Number_Of_IMAGE_Files_Downloaded_");
				bindService(intent, conn, Context.BIND_ADJUST_WITH_ACTIVITY);
				startService(intent);

			} 

			else if(selectedServiceRdBtn == R.id.rdImgBindServiceSync) {  //Bind Intent Service --Sync

				Intent intent = new Intent(getBaseContext(), BindIntentService.class); 
				intent.putStringArrayListExtra("filePath", URLs);
				intent.putExtra("DownLoadFiletext", "Number_Of_IMAGE_Files_Downloaded_");
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
			intentForLocalService.putExtra("DownLoadFiletext", "Number_Of_IMAGE_Files_Downloaded_");
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
		RadioGroup radioGroup = (RadioGroup) findViewById(R.id.rdgImage);
		int id = radioGroup.getCheckedRadioButtonId();
		if (id != -1){
			if (id == R.id.rdImgLocalServiceWithBindActivity || id == R.id.rdImgLocalServiceWithAutoCreate){
				Log.i(ACTIVITY_NAME, Constants.ON_DESTROY);
				unbindService(conn);
			} 
		}

		this.unregisterReceiver(imageDownloadReceiver);

		super.onDestroy();
	}

	public class ImageDownloadFileBroadcastReceiver extends BroadcastReceiver{

		public static final String PROCESS_RESPONSE = "com.serviceApp.intent.action.PROCESS_RESPONSE";

		@Override 
		public void onReceive(Context context, Intent intent)
		{  
//			String receiverResponseFileCount = intent.getStringExtra("fileCount");
//			Toast.makeText(getBaseContext(), receiverResponseFileCount, Toast.LENGTH_LONG).show(); 
		}


	}

}
