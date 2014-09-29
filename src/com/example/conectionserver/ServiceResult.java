package com.example.conectionserver;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class ServiceResult extends Service {
    boolean isRunning = true;
    JSONArray jArray;
	String result = null;
	InputStream is = null;
	StringBuilder sb=null;
	Intent myFilteredResponse;
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		myFilteredResponse = new Intent("matos.action.GOSERVICE3");
	}
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		Log.e ("<<MyService3-onStart>>", "I am alive-3!");
		// we place the slow work of the service in its own thread 
		// so the caller is not hung up waiting for us
		Thread triggerService = new Thread ( new Runnable(){
			long startingTime = System.currentTimeMillis();
			long tics = 0;
			public void run() {
				while (isRunning) {
					String url = "http://10.0.2.2/reports/result.php";
					String result = "";
					try{
					    HttpClient httpclient = new DefaultHttpClient();
					    HttpPost httppost = new HttpPost(url);
		//			    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					    HttpResponse response = httpclient.execute(httppost);
					    HttpEntity entity = response.getEntity();
					    is = entity.getContent();
					}catch(Exception e){
				        Log.e("log_tag", "Error in http connection"+e.toString());
					}
					try{
				        BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF-8"),8);
				        sb = new StringBuilder();
				        sb.append(reader.readLine() + "\n");
				        String line="0";
				        while ((line = reader.readLine()) != null) {
				            sb.append(line + "\n");
				        }
				        is.close();
				        result=sb.toString();
			        }catch(Exception e){
			            Log.e("log_tag", "Error converting result "+e.toString()+"\n"+url+"\n"+result);
			        }
					String dataString = "";
					String res="";
					if (result!=null && result != "") {
						res = result.substring(1);
					} else return ;
					try{
						jArray = new JSONArray(res);
						JSONObject json_data=null;
						if (jArray.length() == 0 ) return;
					     // resultInvoice.n
						for(int i=0;i<jArray.length();i++){
							json_data = jArray.getJSONObject(i);
							dataString += json_data.getString("id") + " " + json_data.getString("name") + " " + json_data.getString("join_date") + "\n"; 
						}
					    myFilteredResponse.putExtra("serviceData", dataString);
					    sendBroadcast(myFilteredResponse);
					    Thread.sleep(5000); 
//					    Toast.makeText(getBaseContext(), "", Toast.LENGTH_LONG).show();
					}
					catch(Exception e1){
						e1.printStackTrace();
						Log.e("Service",e1.toString() +"\n" +url+"\n"+result); 
					}
				}// end while
			}//run
		});
		triggerService.start();
	}//onStart
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.e ("<<MyService3-onDestroy>>", "I am dead-3");
		isRunning = false;
	}//onDestroy

}//MyService3
