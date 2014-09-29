package net.oschina.app.v2.utils;

public class AvatarUtils {
	public static final String AVATAR_SIZE_REG = "_[0-9]{1,3}";
	public static final String MIDDLE_SIZE = "_100";
	public static final String LARGE_SIZE = "_200";

	// http://static.oschina.net/uploads/user/63/127726_50.png?t=1390533116000
	public static String getSmallAvatar(String source) {
		return source;
	}

	// http://static.oschina.net/uploads/user/63/127726_100.png?t=1390533116000
	public static String getMiddleAvatar(String source) {
		if (source == null)
			return null;
		return source.replaceAll(AVATAR_SIZE_REG, MIDDLE_SIZE);
	}

	public static String getLargeAvatar(String source) {
		if (source == null)
			return null;
		return source.replaceAll(AVATAR_SIZE_REG, LARGE_SIZE);
	}
}
