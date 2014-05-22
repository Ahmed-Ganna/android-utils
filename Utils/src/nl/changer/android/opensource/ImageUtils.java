package nl.changer.android.opensource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore.Images.Media;
import android.util.Log;

public class ImageUtils {

	private final static String TAG = ImageUtils.class.getSimpleName();
	
	/***
	 * Scales the image depending upon the display density of the
	 * device. Maintains image aspect ratio.
	 * 
	 * When dealing with the bitmaps of bigger size, this method must be called
	 * from a non-UI thread.
	 * ***/
	public static Bitmap scaleDownBitmap( Context ctx, Bitmap source, int newHeight ) {
		final float densityMultiplier = Utils.getDensityMultiplier(ctx);
		
		// Log.v( TAG, "#scaleDownBitmap Original w: " + source.getWidth() + " h: " + source.getHeight() );
		
		int h = (int) ( newHeight * densityMultiplier );
		int w = (int) ( h * source.getWidth() / ((double) source.getHeight()) );
		 
		// Log.v( TAG, "#scaleDownBitmap Computed w: " + w + " h: " + h );
	
		Bitmap photo = Bitmap.createScaledBitmap( source, w, h, true );
		
		// Log.v( TAG, "#scaleDownBitmap Final w: " + w + " h: " + h );
	
		return photo;
	}
	
	/***
	 * Scales the image independently of the screen density of the device. Maintains image aspect ratio.
	 * 
	 * When dealing with the bitmaps of bigger size, this method must be called
	 * from a non-UI thread.
	 * ***/
	public static Bitmap scaleBitmap( Context ctx, Bitmap source, int newHeight) {
		
		// Log.v( TAG, "#scaleDownBitmap Original w: " + source.getWidth() + " h: " + source.getHeight() );
		
		int w = (int) ( newHeight * source.getWidth() / ((double) source.getHeight()) );
		 
		// Log.v( TAG, "#scaleDownBitmap Computed w: " + w + " h: " + newHeight );
	
		Bitmap photo = Bitmap.createScaledBitmap( source, w, newHeight, true );
		
		// Log.v( TAG, "#scaleDownBitmap Final w: " + w + " h: " + newHeight );
	
		return photo;
	}
	
	/***
	 * Scales the image independently of the screen density of the device. Maintains image aspect ratio.
	 * @param uri Uri of the source bitmap
	 ****/
	public static Bitmap scaleDownBitmap( Context ctx, Uri uri, int newHeight ) throws FileNotFoundException, IOException {
		Bitmap original = Media.getBitmap(ctx.getContentResolver(), uri);
		return scaleBitmap(ctx, original, newHeight);
	}
	
	/***
	 * Scales the image independently of the screen density of the device. Maintains image aspect ratio.
	 * @param uri Uri of the source bitmap
	 ****/
	public static Uri scaleDownBitmapForUri( Context ctx, Uri uri, int newHeight ) throws FileNotFoundException, IOException {
		Bitmap original = Media.getBitmap(ctx.getContentResolver(), uri);
		Bitmap bmp = scaleBitmap(ctx, original, newHeight);
		
		Uri destUri = null;
		String uriStr = Utils.writeImageToMedia( ctx, bmp, "", "" );
		
		if( uriStr != null ) {
			destUri = Uri.parse(uriStr);
		}
		
		return destUri;
	}
	
	/***
	 * Rotate the image at the specified uri.
	 * @param uri Uri of the image to be rotated.
	 ****/
	public static Uri rotateImage( Context ctx, Uri uri ) throws FileNotFoundException, IOException {
		
		int invalidOrientation = -1;
		String filePath = Utils.getImagePathForUri(ctx, uri);
		byte[] data = Utils.getMediaData(ctx, uri);
		Uri newUri = null;
		
        try {
            ExifInterface exif = new ExifInterface(filePath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, invalidOrientation);
            Log.d(TAG, "Exif: " + orientation);
            
            if(orientation != invalidOrientation) {
            	Matrix matrix = new Matrix();
                
                if (orientation == 6) {
                    matrix.postRotate(90);
                } else if (orientation == 3) {
                    matrix.postRotate(180);
                }  else if (orientation == 8) {
                    matrix.postRotate(270);
                }
                
                // set some options so the memory is manager properly
                BitmapFactory.Options options = new BitmapFactory.Options();
                // options.inPreferredConfig = Bitmap.Config.RGB_565;		// try to enable this if OutOfMem issue still persists
                options.inPurgeable = true;             
                options.inInputShareable = true;
                
                Bitmap original = BitmapFactory.decodeByteArray(data, 0, data.length, options);
                Bitmap rotatedBitmap = Bitmap.createBitmap(original, 0, 0, original.getWidth(), original.getHeight(), matrix, true); // rotating bitmap
                String newUrl = Media.insertImage(((Activity) ctx).getContentResolver(), rotatedBitmap, "", "");
                
                if(newUrl != null)
                	newUri = Uri.parse(newUrl);	
            }
        } catch (Exception e) {
        	e.printStackTrace();
        }
        
        return newUri;
	}
}
