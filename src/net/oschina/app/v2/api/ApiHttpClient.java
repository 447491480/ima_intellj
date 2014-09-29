package net.oschina.app.v2.api;

import java.util.Locale;

import net.oschina.app.v2.AppContext;
import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class ApiHttpClient {
	// http://www.oschina.net/action/oauth2/authorize?response_type=code&client_id=F6QtiYRetdUEwsYKYvNR&state=xyz&redirect_uri=http://my.oschina.net/u/142883
	public final static String HOST = "www.oschina.net";
	private static String API_URL;
	public static String ASSET_URL = "http://media-cache.pinterest.com/%s";
	public static String ATTRIB_ASSET_URL = "http://passets-ec.pinterest.com/%s";
	public static String DEFAULT_API_URL;
	public static final String DELETE = "DELETE";
	public static String DEV_API_URL = "http://www.oschina.net/%s";
	public static final String GET = "GET";
	public static final String POST = "POST";
	public static final String PUT = "PUT";
	public static AsyncHttpClient client;

	static {
		API_URL = DEV_API_URL;
	}

	public ApiHttpClient() {
	}

	public static AsyncHttpClient getHttpClient() {
		return client;
	}

	public static void cancelAll(Context context) {
		client.cancelRequests(context, true);
	}

	public static void clearUserCookies(Context context) {
		// (new HttpClientCookieStore(context)).a();
	}

	public static void delete(String partUrl, AsyncHttpResponseHandler handler) {
		client.delete(getAbsoluteApiUrl(partUrl), handler);
		log(new StringBuilder("DELETE ").append(partUrl).toString());
	}

	public static void get(String partUrl, AsyncHttpResponseHandler handler) {
		client.get(getAbsoluteApiUrl(partUrl), handler);
		log(new StringBuilder("GET ").append(partUrl).toString());
	}

	public static void get(String partUrl, RequestParams params,
			AsyncHttpResponseHandler handler) {
		client.get(getAbsoluteApiUrl(partUrl), params, handler);
		log(new StringBuilder("GET ").append(partUrl).append("&")
				.append(params).toString());
	}

	public static String getAbsoluteApiUrl(String partUrl) {
		String url = String.format(API_URL, partUrl);
		Log.d("BASE_CLIENT", "request:" + url);
		return url;
	}

	public static String getApiUrl() {
		return API_URL;
	}

	public static String getAssetUrl(String url) {
		if (url.indexOf("/") == 0)
			url = url.substring(1);
		if (!url.contains("http")) {
			url = String.format(ASSET_URL, url);
		}
		return url;
	}

	public static String getAttributionAssetUrl(String url) {
		if (url.indexOf("/") == 0)
			url = url.substring(1);
		return String.format(ATTRIB_ASSET_URL, url);
	}

	public static void getDirect(String url, AsyncHttpResponseHandler handler) {
		new AsyncHttpClient().get(url, handler);
		log(new StringBuilder("GET ").append(url).toString());
	}

	public static void log(String log) {
		Log.d("BaseApi", log);
	}

	public static void post(String partUrl, AsyncHttpResponseHandler handler) {
		client.post(getAbsoluteApiUrl(partUrl), handler);
		log(new StringBuilder("POST ").append(partUrl).toString());
	}

	public static void post(String partUrl, RequestParams params,
			AsyncHttpResponseHandler handler) {
		client.post(getAbsoluteApiUrl(partUrl), params, handler);
		log(new StringBuilder("POST ").append(partUrl).append("&")
				.append(params).toString());
	}

	public static void put(String partUrl, AsyncHttpResponseHandler handler) {
		client.put(getAbsoluteApiUrl(partUrl), handler);
		log(new StringBuilder("PUT ").append(partUrl).toString());
	}

	public static void put(String partUrl, RequestParams params,
			AsyncHttpResponseHandler handler) {
		client.put(getAbsoluteApiUrl(partUrl), params, handler);
		log(new StringBuilder("PUT ").append(partUrl).append("&")
				.append(params).toString());
	}

	public static void resetApiUrl() {
		setApiUrl(DEFAULT_API_URL);
	}

	public static void setApiUrl(String apiUrl) {
		API_URL = apiUrl;
	}

	public static void setHttpClient(AsyncHttpClient c) {
		client = c;
		client.addHeader("Accept-Language", Locale.getDefault().toString());
		client.addHeader("Host", HOST);
		client.addHeader("Connection", "Keep-Alive");
		setUserAgent(ApiClientHelper.getUserAgent(AppContext.instance()));
		//setUserAgent("OSChina.NET/1.0.0.4_29/Android/4.4.4/Nexus 4/1cd6bd26-fe78-4fbd-8bcf-1dd4d121ef1d");
	}

	public static void setUserAgent(String userAgent) {
		client.setUserAgent(userAgent);
	}

	public static void setCookie(String cookie) {
		client.addHeader("Cookie", cookie);
	}

	private static String appCookie;

	public static void cleanCookie() {
		appCookie = "";
	}

	public static String getCookie(AppContext appContext) {
		if (appCookie == null || appCookie == "") {
			appCookie = appContext.getProperty("cookie");
		}
		return appCookie;
	}
}
