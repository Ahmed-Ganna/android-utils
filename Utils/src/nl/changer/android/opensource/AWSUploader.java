package nl.changer.android.opensource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore.Images.Media;
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
	
	public static void uploadObject( Context ctx, String uri, String contentType, String url ) {
		Log.v( TAG, "#uploadObject uri: " + uri + " contentType: " + contentType + " url: " + url );
		NetworkManager nwMgr = new NetworkManager();
		Bitmap bmp = null;
		
		if( Utils.isImage(contentType) ) {
			try {
				bmp = Media.getBitmap( ctx.getContentResolver(), Uri.parse(uri) );
			} catch ( FileNotFoundException e ) {
				e.printStackTrace();
			} catch ( IOException e ) {
				e.printStackTrace();
			}
		}
		
		// TODO: also check for video and audio types and retrieve the files to
		// upload.
		
		String inputData = "Mr. Jay";
		HashMap<String, Object> outputData = new HashMap<String, Object>();
		nwMgr.postDataToUrl( url, inputData, outputData );
	}

}
