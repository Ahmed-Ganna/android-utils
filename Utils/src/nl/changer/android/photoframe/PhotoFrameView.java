package nl.changer.android.photoframe;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

import nl.changer.GlobalConstants;
import nl.changer.android.opensource.MimeType;
import nl.changer.android.opensource.NetworkManager;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.protocol.HTTP;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;

public class PhotoFrameView extends ImageView {

	private static final String TAG = PhotoFrameView.class.getSimpleName();
	
	Context mContext;
	
	protected String mUrl;
	protected byte[] mImageData;
	
	public PhotoFrameView(Context context) {
		super(context);
		mContext = context;
	}
	
	public PhotoFrameView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
	}

	public PhotoFrameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}
	
	/***
	 * Sets the image url & starts retrieving the image data from the server
	 * */
	public void setUrl(String url) {
		
		mUrl = url;
		
		// retrive image data
		// asynchronously and then show
		retrieveImage();
	}
	
	/***
	 * Sets the image data & starts retrieving the image data from the server
	 * ***/
	public void setImageData( byte[] imageData ) {
		mImageData = imageData;
		
		// image data is available,
		// no need to retrieve it from
		// the server
		showImage();
	}
	
	protected void retrieveImage() {
		
		if( mUrl == null )
			throw new NullPointerException("Image URL has not been set. Use setUrl()");
		
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
	
	/***
	 * 
	 * ***/
	private byte[] getFromUrl(String urlStr, HashMap<String, Object> outputData) {
		
		HttpURLConnection conn = null;
		URL url = null;
		byte[]  responseData = null;
		NetworkManager nwMgr = new NetworkManager();
		
		try {
			url = new URL( urlStr );
			Log.v( TAG, "#getFromUrl url: " + url );
		} catch ( MalformedURLException e ) {
			e.printStackTrace();
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		
		try {
  			conn = (HttpURLConnection) url.openConnection();
  			
  			executeHttpGet( conn );
	  		
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
	
	public void executeHttpGet(HttpURLConnection conn) {
		
		// caution, doing setDoOutput = true will convert
		// this GET request into a POST request and you will
		// end up debugging for long time.
		
		conn.setDoInput(true);
		conn.setInstanceFollowRedirects(true);
		
		try {
			conn.setRequestMethod( HttpGet.METHOD_NAME );
		} catch ( ProtocolException e ) {
			e.printStackTrace();
		} catch ( Exception e ) {
			e.printStackTrace();
		} 
		
		conn.setRequestProperty( "Accept-Charset", "UTF-8" );
		conn.setRequestProperty(HTTP.USER_AGENT,"Mozilla/5.0 ( compatible ) ");
		conn.setRequestProperty("charset", "utf-8");
  		conn.setUseCaches(false);
	}
	
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
	
	protected void showImage() {
        Bitmap bm = BitmapFactory.decodeByteArray( mImageData, 0, mImageData.length );
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);

        setImageBitmap(bm);
	}

}