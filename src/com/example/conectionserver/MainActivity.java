package com.example.conectionserver;

import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {
    TextView txtMsg;
    Button btnStopService;
    ComponentName service;
    Intent intentMyService;
    BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtMsg = (TextView) findViewById(R.id.result);
        
        intentMyService = new Intent(this, ServiceResult.class);
        service = startService(intentMyService);
                
        txtMsg.setText("Application demo connecting to localhost!!\nLe Van Ban");
        btnStopService = (Button) findViewById(R.id.stopService);
        btnStopService.setText("Stop");
        btnStopService.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				try {
					stopService(new Intent(intentMyService) );
					txtMsg.setText("After stoping Service: \n" + 
						service.getClassName());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
        }); 
        IntentFilter mainFilter = new IntentFilter("matos.action.GOSERVICE3");
    	receiver = new MyMainLocalReceiver();
    	registerReceiver(receiver, mainFilter);
    }

    @Override
    protected void onDestroy() {
	    super.onDestroy();
	    try {
	    stopService(intentMyService);
	    unregisterReceiver(receiver);
	    } catch (Exception e) {
	    	Log.e ("MAIN3-DESTROY>>>", e.getMessage() );
	    }
	    Log.e ("MAIN3-DESTROY>>>" , "Adios" );
    } //onDestroy


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    public class MyMainLocalReceiver extends BroadcastReceiver {
    	@Override
    	public void onReceive(Context localContext, Intent callerIntent) {
	    	String serviceData = callerIntent.getStringExtra("serviceData");
	    	Log.e ("MAIN>>>", serviceData + " -receiving data " 
				+ SystemClock.elapsedRealtime() );
	    	String now = "\n" + serviceData + " --- " 
	    	           + new Date().toLocaleString();
//	    	Date a = new Date();
//	    	txtMsg.append(now);
	    	Toast.makeText(getBaseContext(), serviceData, Toast.LENGTH_LONG).show();
    	}
	}//MyMainLocalReceiver
}
