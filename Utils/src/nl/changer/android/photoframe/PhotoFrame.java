package nl.changer.android.photoframe;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import nl.changer.GlobalConstants;
import nl.changer.android.opensource.MimeType;
import nl.changer.android.opensource.NetworkManager;
import nl.changer.android.opensource.R;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class PhotoFrame extends BasePhotoFrame {

	private static final String TAG = PhotoFrame.class.getSimpleName();
	
	protected String mUrl;
	protected byte[] mImageData;
	
	private Handler mHandler = new Handler( new Handler.Callback() {
		
		@Override
		public boolean handleMessage(Message msg) {
			
			switch (msg.what) {
				case GlobalConstants.SUCCESS:
					showImage();
					break;

				case GlobalConstants.FAIL:
					Log.v( TAG, "#handleMessage Failed to download the image from the server" );
					break;

				default:
					break;
			}	// end switch
			
			return true;
		}
	});

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		
		View ui = inflater.inflate(R.layout.photo_frame, null);
		
		return ui;
	}
	
	@Override
	public void onAttach( Activity activity ) {
		super.onAttach(activity);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		// Log.v(TAG, "#onStart url: " + mUrl);
	}
	
	/***
	 * Sets the image url & starts retrieving the image data from the server
	 * */
	public void setUrl(String url) {
		mUrl = url;
		
		retrieveImage();
	}
	
	protected void retrieveImage() {
		
		Thread t = new Thread( new Runnable() {
			
			@Override
			public void run() {
				HashMap<String, Object> outputData = new HashMap<String, Object>();
				mImageData = getFromUrl( mUrl, outputData );
				
				if( mImageData != null )
					mHandler.sendEmptyMessage(GlobalConstants.SUCCESS);
				else
					mHandler.sendEmptyMessage(GlobalConstants.FAIL);
			}	// end run
		});
		
		Log.v( TAG, "#retrieveImage starting thread" );
		t.start();

	}
	
	protected void showImage() {
		ImageView photo = (ImageView) getView().findViewById(R.id.photo);
        Bitmap bm = BitmapFactory.decodeByteArray( mImageData, 0, mImageData.length );
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

        photo.setImageBitmap(bm);
	}
	
	/***
	 * 
	 * ***/
	private byte[] getFromUrl(String urlStr, HashMap<String, Object> outputData) {
		
		HttpURLConnection conn = null;
		URL url = null;
		byte[]  responseData = null;
		NetworkManager nwMgr = new NetworkManager();
		
		try {
			url = new URL(urlStr);
			Log.v( TAG, "#getFromUrl url: " + url );
		} catch ( MalformedURLException e ) {
			e.printStackTrace();
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		
		try {
  			conn = (HttpURLConnection) url.openConnection();
  			
  			nwMgr.executeHttpGet( conn, MimeType.APPLICATION_JSON );
	  		
  			responseData = nwMgr.readStreamToBytes( conn.getInputStream() );
	  		
		} catch ( FileNotFoundException e ) {
			e.printStackTrace();
			Log.e( TAG, "#getFromUrl FileNotFoundException while making an API call. Reason: " + e.getMessage() );
			Log.v( TAG, "#getFromUrl FileNotFoundException Error from the errorStream. Reason: " + nwMgr.readStream(conn.getErrorStream()) );
		} catch ( Exception e ) {
			e.printStackTrace();
			Log.e( TAG, "#getFromUrl Exception while making an API call. Reason: " + e.getMessage() );
			Log.v( TAG, "#getFromUrl Exception Error from the errorStream. Reason: " + nwMgr.readStream( conn.getErrorStream() ) );
		} finally {
			
			try {
				Log.d( TAG, "#getFromUrl responseCode: " + conn.getResponseCode() );
				outputData.put( GlobalConstants.API_OUTPUT_STATUS, conn.getResponseCode() );
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			conn.disconnect();
		}
		
		return responseData;
	}

	/***
	 * Sets the image data & starts retrieving the image data from the server
	 * ***/
	public void setImageData( byte[] imageData ) {
		mImageData = imageData;
		
		retrieveImage();
	}
	
}