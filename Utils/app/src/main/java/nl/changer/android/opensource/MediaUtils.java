package nl.changer.android.opensource;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Video;
import android.util.Log;

import java.io.File;

public class MediaUtils {

    private static final String TAG = MediaUtils.class.getSimpleName();

    /**
     * *
     * Get runtime duration of media such as audio or video in milliseconds
     * **
     */
    public static long getDuration(Context ctx, Uri mediaUri) {
        Cursor cur = ctx.getContentResolver().query(mediaUri, new String[]{Video.Media.DURATION}, null, null, null);
        long duration = -1;

        try {
            if (cur != null && cur.getCount() > 0) {
                while (cur.moveToNext()) {
                    duration = cur.getLong(cur.getColumnIndex(Video.Media.DURATION));

                    if (duration == 0)
                        Log.w(TAG, "#getMediaDuration The image size was found to be 0. Reason: UNKNOWN");

                }    // end while
            } else if (cur.getCount() == 0) {
                Log.e(TAG, "#getMediaDuration cur size is 0. File may not exist");
            } else
                Log.e(TAG, "#getMediaDuration cur is null");
        } finally {
            if (cur != null && !cur.isClosed())
                cur.close();
        }

        return duration;
    }

    /**
     * Checks if the parameter {@link android.net.Uri} is a Media content uri.
     * **
     */
    public static boolean isMediaContentUri(Uri uri) {

        // TODO: move to MediaUtils.
        if (!uri.toString().contains("content://media/")) {
            Log.w(TAG, "#isContentUri The uri is not a media content uri");
            return false;
        } else
            return true;
    }

    /**
     * Generates uri with "content://" scheme for a given image file.
     *
     * @param context
     * @param imageFile
     * @return Content uri for the parameter image file. Returns null if
     * the image file is not found.
     */
    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        return getImageContentUri(context, filePath);
    }

    /**
     * Generates uri with "content://" scheme for a given image file path.
     *
     * @param context
     * @param absoluteImageFilePath Absolute path of the image file on the disk
     * @return Content uri for the parameter image file. Returns null if
     * the image file is not found.
     */
    public static Uri getImageContentUri(Context context, String absoluteImageFilePath) {
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.Media._ID}, MediaStore.Images.Media.DATA + "=? ", new String[]{absoluteImageFilePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id);
        } else {
            File imageFile = new File(absoluteImageFilePath);
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, absoluteImageFilePath);
                return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }
}
