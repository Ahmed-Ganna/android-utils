package nl.changer.android.opensource;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.protocol.HTTP;

public class NetworkManager {
	
	private static final String TAG = NetworkManager.class.getSimpleName();
	
	/***
	 * Convert {@linkplain InputStream} to byte array.
	 * 
	 * @throws NullPointerException If input parameter inputstream is null
	 * **/
	protected byte[] readStreamToBytes(InputStream inputStream) {
		
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
	protected String readStream(InputStream inputStream) {
		
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
	protected void executeHttpGet(HttpURLConnection conn) {
		
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
		
		conn.setRequestProperty( HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded");
		conn.setRequestProperty(HTTP.USER_AGENT,"Mozilla/5.0 ( compatible ) ");
		conn.setRequestProperty("Accept-Charset", "UTF-8");
		conn.setRequestProperty("charset", "utf-8");
  		conn.setUseCaches(false);
	}
	
	/***
	 * Send a GET request. This method must be called from
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
		
		conn.setRequestProperty(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded"); 
		conn.setRequestProperty("charset", "utf-8");
  		conn.setUseCaches(false);
	}

}
