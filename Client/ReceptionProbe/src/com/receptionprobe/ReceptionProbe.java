/*
 * ReceptionProbe Android Application
 * Author: Amit Bawer
 * Last Update: 28 July 2012
 */

package com.receptionprobe;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;

import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.gsm.GsmCellLocation;
import android.telephony.TelephonyManager; 
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

/*
 * Main Activity
 */
public class ReceptionProbe extends Activity
{	
	//const for data collection web url
	private static final String POST_URL = "http://geocellmap.appspot.com/new";

	//const for exit menu
	private static final int EXIT = 1;
	
	//const for maximal number of lines in display
	private static final int MAX_DISPLAY_LINES = 17;
			
	//signal level handlers	 
	TelephonyManager        phoneStateManager;
	MyPhoneStateListener    phoneStateListener;
	
	//location handlers
	LocationManager 		locationManager;
	LocationListener 		locationListener;
	
	//data  
	int 					sigLevel=99;	//signal level (valid values: 0-31, 99 = unknown)
	int 					cellID=-1; 		//tower ID
	double 					x=0.0;			//location x-coordinate
	double 					y=0.0; 			//location y-coordinate
	
	//text view object handler
	TextView				tv;
   
	/*
	 * Method for sending collected data to web server   
	 */
   public void postSignalLocation()
   {
	   //skip update if data is invalid
	   if(x == 0.0 || y == 0.0 || sigLevel == 99 )
		   return;
	   	   	   	
	   //set http post message with collected data as name-value pairs
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost request = new HttpPost(POST_URL);
        List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
        postParameters.add(new BasicNameValuePair("cid", Integer.toString(cellID)));
        postParameters.add(new BasicNameValuePair("siglevel", Integer.toString(sigLevel)));
        postParameters.add(new BasicNameValuePair("latitude", Double.toString(x)));
        postParameters.add(new BasicNameValuePair("longitude",Double.toString(y)));
        
  		  //send data      
  		  try {
  			  	UrlEncodedFormEntity entity = new UrlEncodedFormEntity(postParameters);
  		        request.setEntity(entity);
  		           
  		        httpClient.execute(request);   
  		      		    		  	  		
  		  } catch (Exception e) {
  			  addText("Caught exception while sending data to server");
  		  }finally{
  			  addText("x="+x+",y="+y+",level="+sigLevel+",CID="+cellID);
  		  }  			  		  
   }
	
   /*
    * Adds line of text on top of a text view
    */
   public void addText(String line)
   {
	   String temp = tv.getText().toString();
	   temp = line + "\n" + temp;
	   //if exceeded number of displayed line, truncate last line
	   if(tv.getLineCount() >= MAX_DISPLAY_LINES)
	   {
		   temp = temp.substring(0,temp.lastIndexOf("\n"));
	   }
	   //set text view
	   tv.setText(temp);
	   tv.setFocusable(false);		   
   }
   /*
    * override of android app create method
    * @see android.app.Activity#onCreate(android.os.Bundle)
    */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_main);
							
			//initialize the location and phone state listeners
			locationListener	= new mylocationlistener();
		    phoneStateListener  = new MyPhoneStateListener();
		    phoneStateManager   = ( TelephonyManager )getSystemService(Context.TELEPHONY_SERVICE);
		    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);			  			   
		  
			final Button btStart = (Button) this.findViewById(R.id.Button1);
			final Button btStop = (Button) this.findViewById(R.id.Button2);
			tv = (TextView) this.findViewById(R.id.textView1);
			
			//set text view fonts and colors
			tv.setTypeface(null, Typeface.BOLD);
			tv.setBackgroundColor(Color.BLACK);
			tv.setTextSize(20);
			tv.setTextColor(Color.WHITE);
			//set buttons initial state and shading
			btStop.setEnabled(false);
			btStart.setEnabled(true);
			btStart.getBackground().setAlpha(255);
			btStop.getBackground().setAlpha(127);
			addText("Server:" + POST_URL);
			//anonymous class method for Start button
			btStart.setOnClickListener(new OnClickListener()
				{
					public void onClick(View view)
						{
							btStart.setEnabled(false);
							btStop.setEnabled(true);
															
							btStart.getBackground().setAlpha(127);
							btStop.getBackground().setAlpha(255);							
													
							addText("Application Started");
							 
							//update every 1000 milliseconds and only if changed location by at least 10 meters
						    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1 , locationListener);
						    
						    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1 , locationListener);
						    
						    phoneStateManager.listen(phoneStateListener ,PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
						}
				});
			//anonymous class method for Stop button 
			btStop.setOnClickListener(new OnClickListener()
				{
					public void onClick(View view)
						{
							btStart.setEnabled(true);
							btStop.setEnabled(false);							
							addText("Application Stopped");
							btStart.getBackground().setAlpha(255);
							btStop.getBackground().setAlpha(127);
							//stop listening for location and signal updates
							locationManager.removeUpdates(locationListener);
						    phoneStateManager.listen(phoneStateListener ,PhoneStateListener.LISTEN_NONE);								

						}
				});

	}//End of onCreate
	
	/*
	 * override for android app options menu creation 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
			super.onCreateOptionsMenu(menu);
			menu.add(0, EXIT, 0, "Exit");
			return true;
	}
	/*
	 * override for android app options menu selection 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
			switch (item.getItemId())
				{

				case EXIT:
					finish();
					return (true);
				}

			return (super.onOptionsItemSelected(item));
	}	 
   /*
    * class for phone state listener
    */
    private class MyPhoneStateListener extends PhoneStateListener
    {
      // Get the Signal strength from the provider, each time there is an update
      @Override
      public void onSignalStrengthsChanged(SignalStrength signalStrength)
      {
         super.onSignalStrengthsChanged(signalStrength);	       
         sigLevel = signalStrength.getGsmSignalStrength();
         GsmCellLocation gsmInfo = (GsmCellLocation) phoneStateManager.getCellLocation();
     	 cellID = gsmInfo.getCid()/10; // cut off sector ID
     	 postSignalLocation();
      }

    };// End of phone state listener 
    
    /*
     * class for location listener
     */
    private class mylocationlistener implements LocationListener {
        @Override
	        public void onLocationChanged(Location location) {
	            if (location != null) {		          	                
	            	x = location.getLatitude();
	            	y = location.getLongitude();
	            	GsmCellLocation gsmInfo = (GsmCellLocation) phoneStateManager.getCellLocation();
	            	cellID = gsmInfo.getCid()/10; //cut off sector ID
	            	postSignalLocation();
	            }
	        }
	        @Override
	        public void onProviderDisabled(String provider) {
	        }
	        @Override
	        public void onProviderEnabled(String provider) {
	        }
	        @Override
	        public void onStatusChanged(String provider, int status, Bundle extras) {
	        }
        }// End of location listener
}

