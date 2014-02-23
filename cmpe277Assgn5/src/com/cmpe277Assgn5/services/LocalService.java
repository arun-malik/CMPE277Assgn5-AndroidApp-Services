package com.cmpe277Assgn5.services;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.os.StrictMode;
import android.util.Log;

import com.cmpe277Assgn5.services.ListPdfFilesActivity.PdfDownloadFileBroadcastReceiver;
import com.cmpe277Assgn5.utility.Constants;

public class LocalService extends Service {

	private static final String ACTICITY_TAG = "BindIntentService";
	final IBinder binderObjectLocalServiceBinder = new LocalServiceBinder();

	@TargetApi(9)
	public LocalService() {
		super();
		Log.i(ACTICITY_TAG, Constants.ON_CONSTRUCTOR_CALL);

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
	}


	@Override
	public void onCreate() {
		Log.i(ACTICITY_TAG, Constants.ON_CREATE);
	}


	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(ACTICITY_TAG, Constants.ON_START_COMMAND);


		String downloadFileCount = intent.getStringExtra("DownLoadFiletext");

		ArrayList<String> listOfFilePath = intent.getStringArrayListExtra("filePath");

		for(int i=0;i< listOfFilePath.size();i++) {

			try {

				URL urlFilePath = new URL(listOfFilePath.get(i));

				BufferedReader inputStream = new BufferedReader(new InputStreamReader(urlFilePath.openStream()));

				//FileOutputStream fileOutputStream =  new FileOutputStream(Environment.getExternalStorageDirectory().getPath()+downloadFileCount+i); //openFileOutput("/sdcard/"+downloadFileCount+i, getBaseContext().MODE_PRIVATE);
				FileOutputStream fileOutputStream =  new FileOutputStream("/sdcard/"+downloadFileCount+i); //openFileOutput("/sdcard/"+downloadFileCount+i, getBaseContext().MODE_PRIVATE);

				char[] characterBuffer=new char[255];

				while ((inputStream.read(characterBuffer)) != -1) {
					fileOutputStream.write(characterBuffer.toString().getBytes());
				}

				inputStream.close();

				fileOutputStream.close();

				Intent callBackIntentToBroadcast = new Intent();
				callBackIntentToBroadcast.setAction(PdfDownloadFileBroadcastReceiver.PROCESS_RESPONSE);
				callBackIntentToBroadcast.addCategory(Intent.CATEGORY_DEFAULT);
				callBackIntentToBroadcast.putExtra("fileCount", downloadFileCount+i);
				sendBroadcast(callBackIntentToBroadcast);


			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return Service.START_STICKY;
	}


	@Override
	public void onDestroy() {
		Log.i(ACTICITY_TAG, "Service onDestroy");
	}

	@Override
	public IBinder onBind(Intent intent) {
		return binderObjectLocalServiceBinder;
	}

	public class LocalServiceBinder extends Binder{
		public LocalService getService(){
			return LocalService.this;
		}
	}

}
