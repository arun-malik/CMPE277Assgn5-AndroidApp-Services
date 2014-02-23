package com.cmpe277Assgn5.services;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import com.cmpe277Assgn5.services.ListPdfFilesActivity.PdfDownloadFileBroadcastReceiver;
import com.cmpe277Assgn5.utility.Constants;

public class BindIntentService extends IntentService {

	private static final String ACTICITY_TAG = "BindIntentService";

	public BindIntentService(String name) {
		super(name);
		Log.i(ACTICITY_TAG,Constants.ON_CONSTRUCTOR_CALL);
	}
	
	public BindIntentService() {
		super("BindIntentService");
		Log.i(ACTICITY_TAG,Constants.ON_CONSTRUCTOR_CALL);
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		Log.i(ACTICITY_TAG,Constants.ON_HANDLER_INTENT);

		String downloadFileCount = intent.getStringExtra("DownLoadFiletext"); 
		ArrayList<String> listOfFilePath = intent.getStringArrayListExtra("filePath");

		for(int i=0;i< listOfFilePath.size();i++) {

			try {

				URL urlFilePath = new URL(listOfFilePath.get(i));


				BufferedReader inputStream = new BufferedReader(new InputStreamReader(urlFilePath.openStream()));

				//FileOutputStream fileOutputStream = openFileOutput("/sdcard/"+downloadFileCount+i, getBaseContext().MODE_PRIVATE);

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
				callBackIntentToBroadcast.putExtra("fileCount", downloadFileCount + Integer.toString(i +1));
				sendBroadcast(callBackIntentToBroadcast);


			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
