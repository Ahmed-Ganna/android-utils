package nl.changer.android.opensource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;

import org.apache.http.client.methods.HttpPut;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.provider.MediaStore.Audio;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.Video;
import android.util.Log;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.ResponseHeaderOverrides;

public class AWSUploader {

	private static final String TAG = AWSUploader.class.getSimpleName();
	
	private String mAccessKeyId;
	private String mSecretKey;
	private String mBucketName;
	
	public AWSUploader( String accessKeyId, String secretKey, String bucketName ) {
		mAccessKeyId = accessKeyId;
		mSecretKey = secretKey;
		mBucketName = bucketName;
	}

	/**** 
	 * Uploads the image to AWS S3 and returns the URL of the uploaded image.
	 ****/
	public URL uploadPhotograph( final String filePath ) {

		Log.v( TAG, "#uploadPhotograph filePath: " + filePath );

		if( filePath == null ) {
			Log.v( TAG, "#uploadPhotograph Fatal error. The filePath of the image to be uploaded on S3 is null" );
			return null;
		}

		URL url = null;

		// if( ! Constants.IS_EMULATOR ) {

		AmazonS3Client s3Client = new AmazonS3Client( new BasicAWSCredentials( mAccessKeyId, mSecretKey ) );

		// This is same as used by the iOS application
		String bucketName = mBucketName;

		// prefix the name with the platform name,
		// in this case 'android'
		// to make them distinguishable on AWS
		String fileName = "image_file" + new Date().getTime() + ".jpeg";
		
		if( !s3Client.doesBucketExist( bucketName ) ) {
			s3Client.createBucket( bucketName );
		}	// end if

		PutObjectRequest por = new PutObjectRequest( bucketName, fileName, new File( filePath) );
		s3Client.putObject( por );
		// Log.v( TAG, "#uploadPhotograph probably image uploading has finished " );

		// getting the URL
		ResponseHeaderOverrides override = new ResponseHeaderOverrides();
		override.setContentType( MimeType.IMAGE_JPEG );

		GeneratePresignedUrlRequest urlRequest = new GeneratePresignedUrlRequest( bucketName, fileName );
		urlRequest.setExpiration( new Date( System.currentTimeMillis() + 3600 ) );  // Added an hour's worth of milliseconds to the current time.
		urlRequest.setResponseHeaders( override );

		url = s3Client.generatePresignedUrl( urlRequest );

		// strip off the query string from the URL
		url = Utils.getPathFromUrl( url );

		return url;
	}
	
	/***
	 * Uploads the object pointed to by the Uri parameter to AWS S3.
	 * 
	 * @param ctx
	 * @param uri
	 * @param contentType
	 * @param url Signed URL to upload to AWS
	 * ****/
	public static void uploadObject( Context ctx, Uri uri, String contentType, String url ) {
		
		// TODO: check if the parameters are null or invalid.
		// TODO: uri validity
		Log.v( TAG, "#uploadObject uri: " + uri + " contentType: " + contentType + " url: " + url );
		NetworkManager nwMgr = new NetworkManager();
		Bitmap bmp = null;
		byte[] data = null;
		
		if( Utils.isImage(contentType) ) {
			// content://media/external/images/media/45490
			try {
				
				bmp = BitmapFactory.decodeStream( ctx.getContentResolver().openInputStream( uri ) );
				
				if( bmp != null )
					Log.v( TAG, "#uploadObject bmp.w: " + bmp.getWidth() + " bmp.h: " + bmp.getHeight() + " size: " + Utils.toMegaBytes(bmp.getByteCount()) + " MB" );
				
				
				bmp = Utils.compressImage( bmp, 8 );
				Log.v( TAG, "#uploadObject bmp.w: " + bmp.getWidth() + " bmp.h: " + bmp.getHeight() + " size: " + Utils.toMegaBytes(bmp.getByteCount()) + " MB" );
				
				data = Utils.toBytes(bmp);
			} catch ( FileNotFoundException e ) {
				e.printStackTrace();
			} catch ( Exception e ) {
				e.printStackTrace();
			}
		} else if( Utils.isAudio(contentType) ) {
			// android.provider.MediaStore.Audio.
			// TODO:
		} else if( Utils.isVideo(contentType) ) {
			// video uri = "content://media/external/video/media/45492"
			data = getMediaData( ctx, uri );
		}
		
		// TODO: also check for video and audio types and retrieve the files to
		// upload.
		Log.v(TAG, "#uploadObject uploading...");
		HashMap<String, Object> outputData = new HashMap<String, Object>();
		uploadObjectToAWS( url, contentType, data, outputData );
		
		Log.v(TAG, "#uploadObject uploading FINISHED");
	}
	
	private static byte[] getMediaData( Context ctx, Uri uri ) {
		
		// TODO: check if uri does not have 'media' in it,
		// it is possibly wrong media content URI.
		
		Cursor cur = ctx.getContentResolver().query( uri, new String[]{ Media.DATA }, null, null, null );
		byte[]  data = null;
		
		if( cur != null && cur.getCount() > 0 ) {
			while( cur.moveToNext() ) {
				String path = cur.getString( cur.getColumnIndex(Video.Media.DATA) );
				
				try {
					File f = new File(path);
					FileInputStream fis = new FileInputStream(f);
					data = NetworkManager.readStreamToBytes( fis );
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch ( Exception e) {
					e.printStackTrace();
				}
				
				// Log.v( TAG, "#getVideoData byte.size: " + data.length );
			}	// end while
		} else
			Log.e(TAG, "#getVideoData cur is null or blank" );
		
		return data;
	}
	
	public static void uploadObjectToAWS(String urlStr, String contentType, Object inputData, HashMap<String, Object> outputData) {
		
		URL url = null;
		try {
			url = new URL(urlStr);
			Log.v(TAG, "#uplaodObject url: " + url );
		} catch ( MalformedURLException e ) {
			e.printStackTrace();
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		
		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection) url.openConnection();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		connection.setDoOutput(true);
		connection.setRequestProperty( HTTP.CONTENT_TYPE, contentType );
		connection.setRequestProperty( "Accept", "*/*" );
		try {
			connection.setRequestMethod( HttpPut.METHOD_NAME );
		} catch (ProtocolException e) {
			e.printStackTrace();
		}
		
		OutputStreamWriter out = null;
		try {
			out = new OutputStreamWriter( connection.getOutputStream() );
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if( out != null ) {
			if( inputData != null && inputData.toString().length() > 0 ) {
				
				try {
					if( inputData instanceof JSONObject || inputData instanceof JSONArray || inputData instanceof String )	
							out.write( inputData.toString() );
					else if ( inputData instanceof byte[] ) {
						byte[] buffer = (byte[]) inputData;
						out.write( new String(buffer) );
					}
					
					out.flush();
			  		out.close();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}	
		} else
			Log.w(TAG, "#uploadObject out stream is null");
		
		try {
			int responseCode = connection.getResponseCode();
			Log.v( TAG, "#uploadObject resCode: " + responseCode );
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
