package net.oschina.app.v2.api.remote;

import net.oschina.app.v2.api.ApiHttpClient;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class UserApi extends BaseApi {

	public static void login(String username, String password,
			AsyncHttpResponseHandler handler) {
		RequestParams params = new RequestParams();
		params.put("username", username);
		params.put("pwd", password);
		params.put("keep_login", 1);
		
		String loginurl = "action/api/login_validate";
		ApiHttpClient.get(loginurl, params, handler);
	}
	
}
