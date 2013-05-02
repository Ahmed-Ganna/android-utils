package nl.changer.android.opensource;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Base64;
import android.widget.Toast;

public class Utils {
	
	private static final String TAG = Utils.class.getSimpleName();
	
	private Context mContext;
	
	ProgressDialog mProgressDialog;
	
	public Utils(Context ctx) {
		mContext = ctx;
	} 
	
	/***
	 * Shows the message passed in the parameter in the Toast.
	 * 
	 * @param msg Message to be show in the toast. 
	 * ***/
	public void showToast(String msg) {
	    Toast toast = Toast.makeText( mContext, msg, Toast.LENGTH_SHORT );
	    toast.show();
	}
	
	/***
	 * Checks if the Internet connection is available.
	 * @return Returns true if the Internet connection is available. False otherwise.
	 * **/
	public boolean isNetworkAvailable() {
	    ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService( Context.CONNECTIVITY_SERVICE );
	    
	    NetworkInfo networkInfo = cm.getActiveNetworkInfo();
	    
	    // if network is NOT available networkInfo will be null
	    // otherwise check if we are connected
	    if( networkInfo != null && networkInfo.isConnected() ) {
	        return true;
	    }
	    
	    return false;
	}
	
	/***
	 * Checks if the SD Card is mounted on the device.
	 * ***/
	public boolean isSDCARDMounted() {
	    String status = Environment.getExternalStorageState();
	    if( status.equals(Environment.MEDIA_MOUNTED) )
	        return true;
	    return false;
	}
	
	/***
	 * Show an alert dialog with the OK button.
	 * When the user presses OK button, the dialog dismisses.
	 * ***/
	public void showAlertDialog(String title, String body) {
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
	    .setMessage(body)
	    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	            dialog.cancel();
	        }
	    });
		
		if( !TextUtils.isEmpty(title) )
			builder.setTitle(title);
		
	    builder.show();
	}
	
	/***
	 * Serializes the Bitmap to Base64
	 * ***/
	public String toBase64(Bitmap bitmap) {
		String base64Bitmap = null;
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
		byte[] imageBitmap = stream.toByteArray();
		base64Bitmap = Base64.encodeToString(imageBitmap, Base64.DEFAULT);
		 
		return base64Bitmap;
	}
	
	
	/***
	 * Converts the passed in drawable to Bitmap
	 * representation
	 * ***/
	public Bitmap drawableToBitmap( Drawable drawable ) {
		
		if( drawable == null ) {
			throw new NullPointerException("Drawable to convert should NOT be null");
		}
		
	    if( drawable instanceof BitmapDrawable ) {
	        return ((BitmapDrawable)drawable).getBitmap();
	    }
	    
	    if( drawable.getIntrinsicWidth() <= 0 && drawable.getIntrinsicHeight() <= 0 ) {
	    	return null;
	    }
	    
	    // Log.d(TAG, "#drawableToBitmap w: " + drawable.getIntrinsicWidth() + " h: " + drawable.getIntrinsicHeight() );

	    Bitmap bitmap = Bitmap.createBitmap( drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Config.ARGB_8888 );
	    Canvas canvas = new Canvas(bitmap); 
	    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
	    drawable.draw(canvas);

	    return bitmap;
	}
	
	/***
	 * Converts the given bitmap to {@linkplain InputStream}.
	 * @throws NullPointerException If the parameter bitmap is null.
	 * ***/
	public InputStream bitmapToInputStream(Bitmap bitmap) throws NullPointerException {
		
		if( bitmap == null )
			throw new NullPointerException("The value of the passed in bitmap cannot be null");
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress( Bitmap.CompressFormat.PNG, 100, baos );
		InputStream inputstream = new ByteArrayInputStream( baos.toByteArray() );
		
		return inputstream;
	}
	
	/***
	 * Show a progress dialog with a spinning animation in it.
	 * 
	 * @param title Title of the progress dialog
	 * @param body Body/Message to be shown in the progress dialog
	 * @param isCancellable True if the dialog can be cancelled on back button press, false otherwise
	 ***/
	public void showProgressDialog(String title, String body, boolean isCancellable) {
		mProgressDialog = ProgressDialog.show( mContext, title, body, true );
		mProgressDialog.setIcon(null);
		mProgressDialog.setCancelable( isCancellable );
	}

	/***
	 * Dismiss the progress dialog if it is visible.
	 * **/
	public void dismissProgressDialog() {
		
		if( mProgressDialog != null )
			mProgressDialog.dismiss();
	}
	
	/***
	 * Read the {@link InputStream} and convert the data received
	 * into the {@link String}
	 * ***/
	public String readStream( InputStream in ) {
		StringBuffer data = null;
		  BufferedReader reader = null;
		  try {
		    reader = new BufferedReader( new InputStreamReader(in) );
		    String line = "";
		    data = new StringBuffer();
		    while( (line = reader.readLine()) != null ) {
		    	data.append(line);
		    }
		    
		  } catch ( IOException e ) {
		    e.printStackTrace();
		  } catch ( Exception e ) {
		    e.printStackTrace();
		  } finally {
			  
		    if( reader != null ) {
		      try {
		    	  reader.close();
		    	  
		    	  if( in != null )
					  in.close();
		      } catch ( IOException e ) {
		    	  e.printStackTrace();
		      } catch ( Exception e ) {
		    	  e.printStackTrace();
		      }
		    }
		  }	// finally
		  
		  if( data == null )
			  return null;
		  else
			  return data.toString();
	}
	
	/***
	 * Scales the image depending upon the display density of the
	 * device.
	 * 
	 * When dealing with the bitmaps of bigger size, this method must be called
	 * from a non-UI thread.
	 * ***/
	public Bitmap scaleDownBitmap( Bitmap photo, int newHeight ) {

		 final float densityMultiplier = getDensityMultiplier();        
	
		 int h = (int) ( newHeight * densityMultiplier );
		 int w = (int) ( h * photo.getWidth() / ((double) photo.getHeight()) );
		 
		 // Log.v( TAG, "#scaleDownBitmap banneredImage w: " + w + " h: " + h );
	
		 photo = Bitmap.createScaledBitmap( photo, w, h, true );
	
		 return photo;
	}
	
	/***
	 * Gives the device independent constant which can be used for scaling images,
	 * manipulating view sizes and changing dimension etc.
	 * ***/
	public float getDensityMultiplier() {
		float densityMultiplier = mContext.getResources().getDisplayMetrics().density;
		return densityMultiplier;
	}
	
	/***
	 * Creates a confirmation dialog that show a pop-up
	 * with Yes-No Button. By default the buttons just dismiss
	 * the dialog.
	 * 
	 * @param message Message to be shown in the dialog.
	 * ***/
	public void showConfirmDialog(String message, DialogInterface.OnClickListener yesListener, DialogInterface.OnClickListener noListener) {

		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		
		if( yesListener == null ) {
			yesListener = new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			};
		}
		
		if( noListener == null ) {
			noListener = new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			};
		}
			
			
			builder.setMessage(message)
			.setPositiveButton("Yes", yesListener)
		    .setNegativeButton("No", noListener)
		    .show();
	}
	

}
