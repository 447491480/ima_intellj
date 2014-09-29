package net.oschina.app.v2.api;

import net.oschina.app.v2.AppContext;

public class ApiClientHelper {

	public static String getUserAgent(AppContext appContext) {
		StringBuilder ua = new StringBuilder("OSChina.NET");
		ua.append('/' + appContext.getPackageInfo().versionName + '_'
				+ "29");// App版本 appContext.getPackageInfo().versionCode
		ua.append("/Android");// 手机系统平台
		ua.append("/" + android.os.Build.VERSION.RELEASE);// 手机系统版本
		ua.append("/" + android.os.Build.MODEL); // 手机型号
		ua.append("/" + appContext.getAppId());// 客户端唯一标识
		return ua.toString();
	}
}
