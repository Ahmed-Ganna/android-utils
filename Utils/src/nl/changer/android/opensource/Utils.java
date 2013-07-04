package nl.changer.android.opensource;

import java.io.BufferedReader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class Utils {
	
	private static final String TAG = Utils.class.getSimpleName();
	
	protected static Context mContext;
	
	ProgressDialog mProgressDialog;

	/***
	 * @param ctx Activity Context. Any other context will break the app.
	 * ***/
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
	 * This method must preferably called from a UI thread.
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

	/***
	 * Gets the version name of the application.
	 * For e.g. 1.9.3
	 * ***/
	public static String getApplicationVersionNumber() {
		
		String versionName = null;
		
		try {
			 versionName = mContext.getPackageManager().getPackageInfo( mContext.getPackageName(), 0 ).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
		// Log.v( TAG, "#getApplicationVersionNumber versionName: " + versionName );
		
		return versionName;
	}
	
	/***
	 * Gets the version code of the application.
	 * For e.g. Maverick Meerkat or 2013050301
	 * ***/
	public int getApplicationVersionCode() {
		
		int versionCode = 0;
		
		try {
			versionCode = mContext.getPackageManager().getPackageInfo( mContext.getPackageName(), 0 ).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
		return versionCode;
	}
	
	/***
	 * Get the version number of the Android OS
	 * For e.g. 2.3.4 or 4.1.2
	 ***/
	public static String getOSVersion() {	
		return Build.VERSION.RELEASE;
	}
	
	
    /**
     * Checks if the service with the given name is currently running on the device.
     * **/
    public boolean isServiceRunning(String serviceName) {
        ActivityManager manager = (ActivityManager) mContext.getSystemService(mContext.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (service.service.getClassName().equals(serviceName)) {
            	// Log.i(TAG, "#isServiceAlreadyRunning " + serviceName + " is already running.");
                return true;
            }
        }
        
        return false;
    }
    
	/***
	 * Get the device unique id called IMEI.
	 * Sometimes, this returns 00000000000000000 for the rooted devices.
	 * ***/
	public String getDeviceImei() {
		
		TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getDeviceId();

	}
    
    /***
     * Share an application over the social network like Facebook, Twitter etc.
     * @param sharingMsg Message to be pre-populated when the 3rd party app dialog opens up.
     * @param emailSubject Message that shows up as a subject while sharing through email.
     * @param title Title of the sharing options prompt. For e.g. "Share via" or "Share using"
     * ***/
	public void share(String sharingMsg, String emailSubject, String title) {
		Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
		
		sharingIntent.setType("text/plain");
		sharingIntent.putExtra(Intent.EXTRA_TEXT, sharingMsg);
		sharingIntent.putExtra(Intent.EXTRA_SUBJECT, emailSubject);
		
		mContext.startActivity( Intent.createChooser( sharingIntent, title ) );
	}
    
    /***
     * Check the type of data connection that is currently available on
     * the device. 
     * @return <code>ConnectivityManager.TYPE_*</code> as a type of
     * internet connection on the device. Returns -1 in case of error or none of 
     * <code>ConnectivityManager.TYPE_*</code> is found.
     * ***/
	public int getDataConnectionType() {
		
		ConnectivityManager connMgr =  (ConnectivityManager) mContext.getSystemService( Context.CONNECTIVITY_SERVICE );
		
		if( connMgr != null && connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE) != null ) {
			if ( connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected() )
	        	return ConnectivityManager.TYPE_MOBILE;
	        
	        if ( connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected() )
	        	return ConnectivityManager.TYPE_WIFI;
	        else
	        	return -1;
		}else
			return -1;
	}
	
	/***
	 * Checks if the input parameter is a valid email.
	 * ***/
	public boolean isValidEmail(String email) {
		final String emailPattern = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
		Matcher matcher;
		Pattern pattern = Pattern.compile(emailPattern);
		
		matcher = pattern.matcher(email);
		
		if( matcher != null )
			return matcher.matches();
		else
			return false;
	}
	
	/***
	 * Capitalize a each word in the string.
	 * ***/
	public static String capitalizeString(String string) {
	  char[] chars = string.toLowerCase().toCharArray();
	  boolean found = false;
	  for (int i = 0; i < chars.length; i++) {
	    if (!found && Character.isLetter(chars[i])) {
	      chars[i] = Character.toUpperCase(chars[i]);
	      found = true;
	    } else if (Character.isWhitespace(chars[i]) || chars[i]=='.' || chars[i]=='\'') { // You can add other chars here
	      found = false;
	    }
	  }
	  return String.valueOf(chars);
	}
	
    /***
     * 
     * ***/
	public void tileBackground(int layoutIdOfRootView, int resIdOfTile) {
    	
    	try {
    		//Tiling the background.
        	Bitmap bmp = BitmapFactory.decodeResource(mContext.getResources(), resIdOfTile);
        	// deprecated constructor call
            // BitmapDrawable bitmapDrawable = new BitmapDrawable(bmp);
        	BitmapDrawable bitmapDrawable = new BitmapDrawable( mContext.getResources(), bmp);
            bitmapDrawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
            View view = ((Activity) mContext).findViewById( layoutIdOfRootView );
            
            if( view == null )
            	throw new NullPointerException("View to which the tile has to be applied should not be null");
            else
            	setBackground( view, bitmapDrawable);
            		
		} catch (Exception e) {
			Log.e(TAG, "#tileBackground Exception while tiling the background of the view");
		}
	}
	
	/***
	 * Sets the passed-in drawable parameter as a background to the 
	 * passed in target parameter in an SDK independent way. This
	 * is the recommended way of setting background rather
	 * than using native background setters provided by {@link View}
	 * class 
	 * 
	 * @param target View to set background to.
	 * @param drawable background image
	 * ***/
	@SuppressLint("NewApi")
	public void setBackground(View target, Drawable drawable) {
		if( Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
    		target.setBackgroundDrawable(drawable);
    	} else {
    		target.setBackground(drawable);
    	}
	}
    
    public void tileBackground(int layoutId, View viewToTileBg, int resIdOfTile) {
    	
    	try {
            //Tiling the background.
        	Bitmap bmp = BitmapFactory.decodeResource(mContext.getResources(), resIdOfTile);
        	// deprecated constructor
            // BitmapDrawable bitmapDrawable = new BitmapDrawable(bmp);
        	BitmapDrawable bitmapDrawable = new BitmapDrawable( mContext.getResources(), bmp);
            bitmapDrawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
            View view = viewToTileBg.findViewById(layoutId);
            
            if( view != null )
            	setBackground(view, bitmapDrawable);
            
		} catch (Exception e) {
			Log.e(TAG, "#tileBackground Exception while tiling the background of the view");
		}
	}
    
	public boolean isDatabasePresent(String packageName, String dbName) {
        SQLiteDatabase checkDB = null;
        try {
            checkDB = SQLiteDatabase.openDatabase( "/data/data/" + packageName + "/databases/" + dbName, null, SQLiteDatabase.OPEN_READONLY );
            checkDB.close();
        } catch (SQLiteException e) {
            // database doesn't exist yet.
        	e.printStackTrace();
        	Log.e(TAG, "The database does not exist.");
        } catch ( Exception e) {
        	e.printStackTrace();
        	Log.e(TAG, "Exception ");
        }
        
        boolean isDbPresent = checkDB != null ? true : false;
        
        return isDbPresent;
    }
	
	/***
	 * Get the file path from the MediaStore.Images.Media Content URI
	 * 
	 * @param mediaContentUri Content URI pointing to a row of {@link MediaStore.Images.Media}
	 * ***/
	public String getRealPathFromURI(Uri mediaContentUri) {

		Cursor cur = null;
		String path = null;
		
		try {
			String[] proj = { MediaStore.Images.Media.DATA };
	        cur = mContext.getContentResolver().query( mediaContentUri, proj, null, null, null );
	        
	        if( cur != null && cur.getCount() != 0 ) {
	        	cur.moveToFirst();	
	        }
	        
	        path = cur.getString( cur.getColumnIndexOrThrow(MediaStore.Images.Media.DATA) );
	        
	        // Log.v( TAG, "#getRealPathFromURI Path: " + path );
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if( cur != null && cur.isClosed() )
				cur.close();
		}
        
        return path;
    }
	
	public static ArrayList<String> toStringArray( JSONArray jsonArr ) {
		
		if( jsonArr == null || jsonArr.length() == 0 )
			return null;
		
		ArrayList<String> stringArray = new ArrayList<String>();
		
		for(int i = 0, count = jsonArr.length(); i< count; i++) {
		    try {
		        String str = jsonArr.getString(i);
		        stringArray.add(str);
		    } catch (JSONException e) {
		        e.printStackTrace();
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
		}
		
		// Log.v( TAG, "#toStringArray stringArr: " + stringArray );
		
		return stringArray;
	}
	
	public static JSONArray toJSONArray(ArrayList<String> stringArr) {
		JSONArray jsonArr = new JSONArray();
		
		for (int i = 0; i < stringArr.size(); i++) {
			String value = stringArr.get(i);
			jsonArr.put(value);	
		}
		
		return jsonArr;
	}
	
	/***
	 * Writes the given image to the external storage of the device.
	 * @return Path of the image file that has been written.
	 * ***/
	public static String writeImage(byte[] imageData) {
		
		OutputStream imageFileOS;
		
		File dir = new File ( Environment.getExternalStorageDirectory() + "/atemp" );
		dir.mkdirs();
		File f = new File( dir, "photograph.jpeg" );

		try {
		   imageFileOS = new FileOutputStream(f);
		   imageFileOS.write(imageData);
		   imageFileOS.flush();
		   imageFileOS.close();
		} catch ( FileNotFoundException e ) {
			e.printStackTrace();
		} catch ( IOException e ) {
			e.printStackTrace();
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		
		String filePath = f.getAbsolutePath();
		
		return filePath;
	}
	
	/***
	 * Get the name of the application that has been defined in AndroidManifest.xml
	 * ***/
	public static String getApplicationName() {
		final PackageManager packageMgr = mContext.getPackageManager();
		ApplicationInfo appInfo;
		
		try {
		    appInfo = packageMgr.getApplicationInfo( mContext.getPackageName(), PackageManager.SIGNATURE_MATCH );
		} catch (final NameNotFoundException e) {
		    appInfo = null;
		}
		
		final String applicationName = (String) (appInfo != null ? packageMgr.getApplicationLabel(appInfo) : "(unknown)");
		
		return applicationName;
	}
}