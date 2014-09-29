package net.oschina.app.v2.api.remote;

import net.oschina.app.v2.api.ApiHttpClient;

import com.loopj.android.http.AsyncHttpResponseHandler;

public class OtherApi {
	private static final String ENG_API = "http://open.iciba.com/dsapi";

	public static void getDailyEnglish(AsyncHttpResponseHandler handler) {
		ApiHttpClient.getDirect(ENG_API, handler);
	}
}
