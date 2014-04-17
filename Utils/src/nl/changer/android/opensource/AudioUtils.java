package nl.changer.android.opensource;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.media.MediaRecorder;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.text.TextUtils;
import android.util.Log;

public class AudioUtils {
	
	private static final String TAG = AudioUtils.class.getSimpleName();
	
	private static String mFileName;
	private static MediaRecorder mRecorder = new MediaRecorder();
	
	public static void startRecordingAudio(Context ctx) {
		
		// mRecorder = new MediaRecorder();
        mRecorder.setAudioSource( MediaRecorder.AudioSource.MIC );
        mRecorder.setOutputFormat( MediaRecorder.OutputFormat.MPEG_4 );
        // Winamp does not play the 
        // audio recorded MP3 audio.
        mFileName = Utils.getStorageDirectory( ctx, "application-utils/audio" ).getAbsolutePath() + "/" + new Date().getTime() + ".mp3";
        mRecorder.setOutputFile( mFileName );
        mRecorder.setAudioEncoder( MediaRecorder.AudioEncoder.AAC );

        try {
            mRecorder.prepare();
        } catch ( IllegalStateException e ) {
            Log.e(TAG, " failed" + e.getMessage() );
        } catch ( IOException e ) {
        	Log.e(TAG, " failed" + e.getMessage() );
        } catch ( Exception e ) {
        	Log.e(TAG, " failed" + e.getMessage() );
        }

        mRecorder.start();
	}
	
	public static Uri stopRecordingAudio( Context ctx) {
		
		mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        
        return saveAudio(ctx);
	}
	
	/****
	 * Insert an audio into {@link Media} content provider of the device.
	 * @return The media content Uri to the newly created audio, or null if failed for any reason.
	 * ***/
	public static Uri writeAudioToMedia( Context ctx, File audioFile ) {
		  ContentValues values = new ContentValues();
		  values.put( MediaStore.MediaColumns.DATA, audioFile.getAbsolutePath() );
		  values.put( MediaStore.MediaColumns.TITLE, "Name Of Your File" );
		  values.put( MediaStore.MediaColumns.MIME_TYPE, "audio/mpeg" );
		  values.put( MediaStore.MediaColumns.SIZE, audioFile.length() );
		  values.put( MediaStore.Audio.Media.ARTIST, "Artist Name" );
		  values.put( MediaStore.Audio.Media.IS_RINGTONE, false );
		  // Now set some extra features it depend on you
		  values.put( MediaStore.Audio.Media.IS_NOTIFICATION, false );
		  values.put( MediaStore.Audio.Media.IS_ALARM, false );
		  values.put( MediaStore.Audio.Media.IS_MUSIC, false );
		  
		  Uri uri = MediaStore.Audio.Media.getContentUriForPath( audioFile.getAbsolutePath() );
		  Log.v( TAG, "#writeAudioToMedia uri: " + uri + " absPath: " + audioFile.getAbsolutePath() );
		  Uri uri2 = ctx.getContentResolver().insert( uri, values );
		  Log.v( TAG, "#writeAudioToMedia uri2: " + uri2 );
		  
		  if( uri2 == null || TextUtils.isEmpty(uri2.toString()) )
			  Log.w(TAG, "Something went wrong while inserting data to content resolver");
		  
		  return uri2;
	}
	
	private static Uri saveAudio(Context ctx) {
		return Utils.writeAudioToMedia(ctx, new File(mFileName ) );
	}
}
