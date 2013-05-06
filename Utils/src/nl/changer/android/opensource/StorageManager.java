package nl.changer.android.opensource;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class StorageManager {
	
	private static final String TAG = StorageManager.class.getSimpleName();
	
	protected Context mContext;
	
	protected SharedPreferences mSettings;
	protected Editor mEditor;
	
	public StorageManager(Context ctx, String prefFileName) {
		mContext = ctx;
		
		mSettings = mContext.getSharedPreferences(prefFileName, Context.MODE_PRIVATE);
		mEditor = mSettings.edit();
	}
	
	public void setValue(String key, String value) {
		mEditor.putString(key, value);
		mEditor.commit();
	}
	
	public void setValue(String key, int value) {
		mEditor.putInt(key, value);
		mEditor.commit();
	}
	
	public void setValue(String key, double value) {
		setValue( key, Double.toString(value) );
	}
	
	/****
	 * Gets the value from the settings stored natively on the device.
	 * @param defaultValue Default value for the key, if one is not found.
	 * **/
	public String getValue(String key, String defaultValue) {
		String value = defaultValue;
		
		value = mSettings.getString(key, defaultValue);
		
		return value;
	}
	
	public int getIntValue(String key, int defaultValue) {
		int value = defaultValue;
		
		value = mSettings.getInt(key, defaultValue);
		
		return value;
	}
	
	/****
	 * Gets the value from the settings stored natively on the device.
	 * 
	 * @param defValue Default value for the key, if one is not found.
	 * **/
	public boolean getValue( String key, boolean defValue ) {
		boolean value = defValue;
		
		value = mSettings.getBoolean(key, defValue);
		
		return value;
	}

}
