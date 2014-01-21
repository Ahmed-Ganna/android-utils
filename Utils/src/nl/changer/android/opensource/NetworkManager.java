package nl.changer.android.opensource;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;

import nl.changer.GlobalConstants;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.protocol.HTTP;

import android.util.Log;
import android.webkit.MimeTypeMap;

public class NetworkManager {
	
	private static final String TAG = NetworkManager.class.getSimpleName();
	
	/***
	 * Convert {@linkplain InputStream} to byte array.
	 * 
	 * @throws NullPointerException If input parameter inputstream is null
	 * **/
	public byte[] readStreamToBytes(InputStream inputStream) {
		
		if( inputStream == null )
			throw new NullPointerException("InputStream is null");
		
		byte[] bytesData = null;
		  BufferedReader reader = null;
		  try {
		    reader = new BufferedReader( new InputStreamReader(inputStream) );
		    ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		    int nRead;
		    byte[] data = new byte[16384];

		    while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
		    	buffer.write(data, 0, nRead);
		    }

		    buffer.flush();

		    bytesData = buffer.toByteArray();
		    
		    // Log.d( TAG, "#readStream data: " + data );
		  } catch ( IOException e ) {
		    e.printStackTrace();
		  } catch ( Exception e ) {
		    e.printStackTrace();
		  } finally {
			  
		    if( reader != null ) {
		      try {
		    	  reader.close();
		    	  
		    	  if( inputStream != null )
		    		  inputStream.close();
		      } catch ( IOException e ) {
		    	  e.printStackTrace();
		      } catch ( Exception e ) {
		    	  e.printStackTrace();
		      }
		    }
		  }	// finally
		  
		return bytesData;
	}
	
	/***
	 * Convert {@linkplain InputStream} to byte array.
	 * 
	 * @throws NullPointerException If input parameter inputstream is null
	 * **/
	public String readStream(InputStream inputStream) {
		
		if( inputStream == null )
			throw new NullPointerException("InputStream is null");
		
		StringBuffer data = null;
		BufferedReader reader = null;
		try {
			reader = new BufferedReader( new InputStreamReader(inputStream) );
			String line = "";
		    data = new StringBuffer();
		    while( (line = reader.readLine()) != null ) {
		    	data.append(line);
		    }
	    // Log.d( TAG, "#readStream data: " + data );
		} catch ( IOException e ) {
			e.printStackTrace();
		} catch ( Exception e ) {
			e.printStackTrace();
		} finally {
		  
			if( reader != null ) {
				try {
					reader.close();
	    	  
					if( inputStream != null )
						inputStream.close();
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
	 * Send a GET request. This method must be called from
	 * a non-UI thread.
	 * ***/
	public void executeHttpGet(HttpURLConnection conn) {
		
		// caution, doing setDoOutput = true will convert
		// this GET request into a POST request and you will
		// end up debuggin for long time.
		
		conn.setDoInput(true);
		conn.setInstanceFollowRedirects(true);
		
		try {
			conn.setRequestMethod(HttpGet.METHOD_NAME);
		} catch ( ProtocolException e ) {
			e.printStackTrace();
		} catch ( Exception e ) {
			e.printStackTrace();
		} 
		
		conn.setRequestProperty( HTTP.CONTENT_TYPE, MimeType.APPLICATION_FORM_URLENCODED);
		conn.setRequestProperty("Accept-Charset", "UTF-8");
		conn.setRequestProperty(HTTP.USER_AGENT,"Mozilla/5.0 ( compatible ) ");
		conn.setRequestProperty("charset", "utf-8");
  		conn.setUseCaches(false);
	}
	
	/***
	 * Send a GET request. This method must be called from
	 * a non-UI thread.
	 * 
	 * @param conn Connection object instance
	 * @param acceptHeader value for standard 'Accept' header in the request
	 * ***/
	public void executeHttpGet( HttpURLConnection conn, String acceptHeader ) {
		
		// caution, doing setDoOutput = true will convert
		// this GET request into a POST request and you will
		// end up debuggin for long time.
		conn.setDoInput(true);
		conn.setInstanceFollowRedirects(true);
		
		try {
			conn.setRequestMethod(HttpGet.METHOD_NAME);
		} catch ( ProtocolException e ) {
			e.printStackTrace();
		} catch ( Exception e ) {
			e.printStackTrace();
		} 
		
		conn.setRequestProperty( HTTP.CONTENT_TYPE, MimeType.APPLICATION_FORM_URLENCODED);
		conn.setRequestProperty( "Accept", acceptHeader);
		conn.setRequestProperty( "Accept-Charset", "UTF-8");
		conn.setRequestProperty( HTTP.USER_AGENT,"Mozilla/5.0 ( compatible ) ");
		conn.setRequestProperty( "charset", "utf-8");
  		conn.setUseCaches(false);
	}
	
	/***
	 * Send a POST request. This method must be called from
	 * a non-UI thread.
	 * ***/
	protected void executeHttpPost(HttpURLConnection conn) {
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setInstanceFollowRedirects(true);
		
		try {
			conn.setRequestMethod(HttpPost.METHOD_NAME);
		} catch (ProtocolException e) {
			e.printStackTrace();
		}
		
		conn.setRequestProperty(HTTP.CONTENT_TYPE, MimeType.APPLICATION_FORM_URLENCODED); 
		conn.setRequestProperty("charset", "utf-8");
  		conn.setUseCaches(false);
	}
	
	/***
	 * Send a POST request. This method must be called from
	 * a non-UI thread.
	 * ***/
	protected void executeHttpPut(HttpURLConnection conn) {
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setInstanceFollowRedirects(true);
		
		try {
			conn.setRequestMethod(HttpPut.METHOD_NAME);
		} catch (ProtocolException e) {
			e.printStackTrace();
		}
		
		conn.setRequestProperty(HTTP.CONTENT_TYPE, MimeType.APPLICATION_FORM_URLENCODED); 
		conn.setRequestProperty("charset", "utf-8");
  		conn.setUseCaches(false);
	}
	
	protected void executeHttpPut(HttpURLConnection conn, String mimeType) {
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setInstanceFollowRedirects(true);
		
		try {
			conn.setRequestMethod(HttpPut.METHOD_NAME);
		} catch (ProtocolException e) {
			e.printStackTrace();
		}
		
		conn.setRequestProperty(HTTP.CONTENT_TYPE, mimeType); 
		conn.setRequestProperty("charset", "utf-8");
  		conn.setUseCaches(false);
	}
	
	protected void executeHttpPost(HttpURLConnection conn, String mimeType) {
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setInstanceFollowRedirects(true);
		
		try {
			conn.setRequestMethod(HttpPost.METHOD_NAME);
		} catch (ProtocolException e) {
			e.printStackTrace();
		}
		
		conn.setRequestProperty(HTTP.CONTENT_TYPE, mimeType); 
		conn.setRequestProperty("charset", "utf-8");
  		conn.setUseCaches(false);
	}
	
	/***
	 * Posts the input JSON data to the specified URL. Returns the error message, if any, in the HashMap.
	 * The error message can be retrieve by using {@link HashMap#get(Object)} for the key 'message'
	 * @return Response from the server.
	 * ***/
	protected String putDataToUrl( String url, Object inputData, HashMap<String, Object> outputData ) {
		
		HttpURLConnection conn = null;
		URL urlObj = null;
		String response = null;
		InputStream inputStream = null;
		
		Log.v( TAG, "#putDataToUrl inputData: " + inputData );
		
		try {
			urlObj = new URL(url);
			Log.v( TAG, "#putDataToUrl url: " + urlObj );
		} catch ( MalformedURLException e ) {
			e.printStackTrace();
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		
		try {
  			conn = (HttpURLConnection) urlObj.openConnection();
  			
  			executeHttpPut( conn, MimeType.APPLICATION_JSON );
  			
  			if( inputData != null && inputData.toString().length() > 0 ) {
  				DataOutputStream dos = new DataOutputStream( conn.getOutputStream() );
  		  		dos.writeBytes( inputData.toString() );
  		  		dos.flush();
  		  		dos.close();
  			}
	  		
  			inputStream = conn.getInputStream();
  			
  			if( inputStream != null )
  				response = readStream( inputStream );
	  		
	  		// Log.d( TAG, "#putDataToUrl response: " + response );
	  		
		} catch ( FileNotFoundException e ) {
			Log.e( TAG, "#putDataToUrl FileNotFoundException while making an API call. Reason: " + e.getMessage() );
			
			if( conn.getErrorStream() != null ) {
				String errMsg = readStream( conn.getErrorStream() );
				Log.v( TAG, "#putDataToUrl FileNotFoundException Error from the errorStream. Reason: " + errMsg );
				outputData.put( GlobalConstants.API_OUTPUT_STATUS_MESSAGE, errMsg );	
			}
				
		} catch ( IOException e ) {
			
			Log.e( TAG, "#putDataToUrl IOException while making an API call. Reason: " + e.getMessage() );
			if( conn.getErrorStream() != null ) {
				String errMsg = readStream( conn.getErrorStream() );
				Log.v( TAG, "#putDataToUrl IOException Error from the errorStream. Reason: " + errMsg );
				outputData.put( GlobalConstants.API_OUTPUT_STATUS_MESSAGE, errMsg );				
			}

		} catch ( Exception e ) {
			
			Log.e( TAG, "#putDataToUrl Exception while making an API call. Reason: " + e.getMessage() );
			
			if( conn.getErrorStream() != null ) {
				String errMsg = readStream( conn.getErrorStream() );			
				Log.v( TAG, "#putDataToUrl Exception Error from the errorStream. Reason: " + errMsg );
				outputData.put( GlobalConstants.API_OUTPUT_STATUS_MESSAGE, errMsg );				
			}

		} finally {
			try {
				if( conn != null ) {
					Log.d( TAG, "#putDataToUrl responseCode: " + conn.getResponseCode() );
					outputData.put( GlobalConstants.API_OUTPUT_STATUS_CODE, conn.getResponseCode() );
					outputData.put( GlobalConstants.API_OUTPUT_STATUS_LINE, conn.getResponseMessage() );
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			conn.disconnect();
		}
		
		return response;
	}
}