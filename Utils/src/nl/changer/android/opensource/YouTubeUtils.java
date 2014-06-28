package nl.changer.android.opensource;

import android.text.TextUtils;

public class YouTubeUtils {

/*default.jpg -   default
https://i1.ytimg.com/vi/<insert-youtube-video-id-here>/mqdefault.jpg - medium 
https://i1.ytimg.com/vi/<insert-youtube-video-id-here>/hqdefault.jpg - high
https://i1.ytimg.com/vi/<insert-youtube-video-id-here>/sddefault.jpg
*/	
	public static final String THUMBNAIL_QUALITY_DEFAULT = "default";
	public static final String THUMBNAIL_QUALITY_MQ = "mqdefault";
	public static final String THUMBNAIL_QUALITY_HQ = "hqdefault";
	public static final String THUMBNAIL_QUALITY_SD = "sddefault";
	
	public static String createVideoUrl(String videoId) {
		
		if(TextUtils.isEmpty(videoId)) {
			throw new IllegalArgumentException("Video ID cannot be null or blank");	
		}
		
		return "http://youtube.com/watch?v=" + videoId;
	}
	
	/***
	 * Creates thubmnail url for a given video ID.
	 * 
	 * @param videoId
	 * @param quality 
	 ****/
	public static String createThumbnailUrl(String videoId, String quality) {
		
		if(quality == null) {
			quality = THUMBNAIL_QUALITY_DEFAULT;
		}
		
		if(!quality.equalsIgnoreCase(THUMBNAIL_QUALITY_DEFAULT) &&
				!quality.equalsIgnoreCase(THUMBNAIL_QUALITY_MQ) &&
				!quality.equalsIgnoreCase(THUMBNAIL_QUALITY_HQ) && 
				!quality.equalsIgnoreCase(THUMBNAIL_QUALITY_SD)) {
			throw new IllegalArgumentException("Invalid quality thumbnail requested");
		}
		
		return "http://img.youtube.com/vi/" + videoId + "/" + quality + ".jpg";
	}
}
