package nl.changer.android.opensource;

import java.io.FileNotFoundException;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
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
}
