/***
 * Collection of key names that can be used anywhere like JSON parsing of JSON has those keys, native storage in key-values etc.
 * ***/

package nl.changer;

public class GlobalConstants {
	
	public static final String API_OUTPUT_STATUS = "status";
	public static final String API_OUTPUT_STATUS_SUCCESS = "success";
	public static final String API_OUTPUT_STATUS_ERROR = "error";
	public static final String API_OUTPUT_STATUS_MESSAGE = "message";
	
	public static final String KEY_STATUS = "status";
	public static final int ERROR_NONE = 0;
	public static final int ERROR_UNKNOWN = 1;
	public static final int ERROR_NETWORK_TIMEOUT = 2;
	
	public static final String KEY_ACCESS_TOKEN = "access_token";
	/****
	 * Heroku dis-allowed custom headers without X in them.
	 * So we started sending both of them to the server.
	 * */
	public static final String KEY_X_ACCESS_TOKEN = "X-Access-Token";
	
	public static final String KEY_TOKEN = "token";
	public static final String KEY_USERNAME = "username";
	public static final String KEY_NAME = "name";
}