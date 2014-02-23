package com.cmpe277Assgn5.services;

import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
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

public class ListPdfFilesActivity extends Activity {

	private static final String ACTIVITY_NAME= "ListPdfFilesActivity";

	PdfDownloadFileBroadcastReceiver pdfDownloadReceiver;
	ArrayList<String> URLs = new ArrayList<String>();
	EditText pdf1, pdf2, pdf3, pdf4, pdf5 ;
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
		setContentView(R.layout.pdfdownload_activity);

		IntentFilter intentFilter = new IntentFilter(PdfDownloadFileBroadcastReceiver.PROCESS_RESPONSE);		
		intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
		pdfDownloadReceiver = new PdfDownloadFileBroadcastReceiver();
		registerReceiver(pdfDownloadReceiver, intentFilter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.pdf, menu);
		return true;
	}

	@SuppressLint("InlinedApi")
	public void startPDFDownload(View view){
		
		URLs = new ArrayList<String>();

		pdf1 = (EditText) findViewById(R.id.pdf1);
		pdf2 = (EditText) findViewById(R.id.pdf2);
		pdf3 = (EditText) findViewById(R.id.pdf3);
		pdf4 = (EditText) findViewById(R.id.pdf4);
		pdf5 = (EditText) findViewById(R.id.pdf5);

		if(pdf1.getText() != null ) {
			URLs.add(pdf1.getText().toString());
		}

		if(pdf2.getText() != null ) {
			URLs.add(pdf2.getText().toString());
		}

		if(pdf3.getText() != null) {
			URLs.add(pdf3.getText().toString());
		}

		if(pdf4.getText() != null ) {
			URLs.add(pdf1.getText().toString());
		}

		if(pdf5.getText() != null ) {
			URLs.add(pdf5.getText().toString());
		}


		RadioGroup rdgServiceSelection = (RadioGroup) findViewById(R.id.rdgPdf);
		int selectedServiceRdBtn = rdgServiceSelection.getCheckedRadioButtonId();

		if (selectedServiceRdBtn != -1){

			if (selectedServiceRdBtn == R.id.rdPdfLocalServiceWithAutoCreate){ //normal local service -- bind auto create

				Intent intent = new Intent(this, LocalService.class);
				intent.putStringArrayListExtra("filePath", URLs);
				intent.putExtra("DownLoadFiletext", "Number_Of_PDF_Files_Downloaded_");
				bindService(intent, conn, Context.BIND_AUTO_CREATE);
				startService(intent);

			} 
			else if (selectedServiceRdBtn == R.id.rdpdfLocalServiceWithActivity){ //normal local service -- bind with activity

				Intent intent = new Intent(this, LocalService.class);
				intent.putStringArrayListExtra("filePath", URLs);
				intent.putExtra("DownLoadFiletext", "Number_Of_PDF_Files_Downloaded_");
				bindService(intent, conn, Context.BIND_ADJUST_WITH_ACTIVITY);
				startService(intent);

			} 

			else if(selectedServiceRdBtn == R.id.rdPdfBindSync) {  //Bind Intent Service --Sync

				Intent intent = new Intent(getBaseContext(), BindIntentService.class); 
				intent.putStringArrayListExtra("filePath", URLs);
				intent.putExtra("DownLoadFiletext", "Number_Of_PDF_Files_Downloaded_");
				startService(intent);   

			} else {

				new downloadFiles().execute("");    /// Bind Tntent Service -- With Async Task
			}
		}




	}

	private class downloadFiles extends AsyncTask<String, Void, String> {
	
		protected String doInBackground(String... str) {
			
			Intent intentForLocalService = new Intent(getBaseContext(), LocalService.class);
			intentForLocalService.putStringArrayListExtra("filePath", URLs);
			intentForLocalService.putExtra("DownLoadFiletext", "Number_Of_PDF_Files_Downloaded_");
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
		RadioGroup radioGroup = (RadioGroup) findViewById(R.id.rdgPdf);
		int id = radioGroup.getCheckedRadioButtonId();
		if (id != -1){
			if (id == R.id.rdpdfLocalServiceWithActivity || id == R.id.rdPdfLocalServiceWithAutoCreate ){
				Log.i(ACTIVITY_NAME, Constants.ON_DESTROY);
				unbindService(conn);
			} 
		}

		this.unregisterReceiver(pdfDownloadReceiver);

		super.onDestroy();
	}

	public class PdfDownloadFileBroadcastReceiver extends BroadcastReceiver{

		public static final String PROCESS_RESPONSE = "com.serviceApp.intent.action.PROCESS_RESPONSE";

		@Override 
		public void onReceive(Context context, Intent intent)
		{ 
			//			String receiverResponseFileCount = intent.getStringExtra("fileCount");
			//			Toast.makeText(getBaseContext(), receiverResponseFileCount, Toast.LENGTH_LONG).show(); 
		}


	}

}
