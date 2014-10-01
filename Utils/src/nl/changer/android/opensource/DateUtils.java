
package nl.changer.android.opensource;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.net.ParseException;

/***
 * Provides helper methods for date utilities.
 ***/
public class DateUtils {

	/***
	 * Converts ISO date string to UTC timezone equivalent.
	 * 
	 * @param dateAndTime
	 *            ISO formatted time string.
	 ****/
	public static String getUtcTime(String dateAndTime) {
		Date d = parseDate(dateAndTime);

		String format = "yyyy-MM-dd'T'HH:mm:ss'Z'";
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());

		// Convert Local Time to UTC
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

		return sdf.format(d);
	}

	/****
	 * Parses date string and return a {@link Date} object
	 * 
	 * @return The ISO formatted date object
	 *****/
	public static Date parseDate(String date) {
		StringBuffer sbDate = new StringBuffer();
		sbDate.append(date);
		String newDate = null;
		Date dateDT = null;

		try {
			newDate = sbDate.substring(0, 19).toString();
		} catch (Exception e) {
			e.printStackTrace();
		}

		String rDate = newDate.replace("T", " ");
		String nDate = rDate.replaceAll("-", "/");

		try {
			dateDT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()).parse(nDate);
			// Log.v( TAG, "#parseDate dateDT: " + dateDT );
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return dateDT;
	}

}