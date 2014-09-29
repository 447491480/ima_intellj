package net.oschina.app.v2.api;

import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.client.HttpResponseException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.JsonHttpResponseHandler;

public class BaseApiResponseHandler extends JsonHttpResponseHandler {

	public BaseApiResponseHandler() {
	}

	public String getBaseUrl() {
		return _baseUrl;
	}

	public String getMethod() {
		return _method;
	}

	public void onFailure(Throwable throwable, ApiResponse response) {
	}

//	public void onFailure(Throwable throwable, String s) {
//		try {
//			onFailure(
//					throwable,
//					new ApiResponse(new JSONObject(String.format(
//							"{\"data\":\"%s\"}", s))));
//		} catch (JSONException e) {
//			onFailure(throwable, new ApiResponse(null));
//		}
//	}
//
//	public void onFailure(Throwable throwable, JSONArray array) {
//		try {
//			onFailure(
//					throwable,
//					new ApiResponse(new JSONObject(String.format(
//							"{\"data\":\"%s\"}", array))));
//		} catch (JSONException e) {
//			onFailure(throwable, new ApiResponse(null));
//		}
//	}
//
//	public void onFailure(Throwable throwable, JSONObject obj) {
//		ApiResponse apiresponse = new ApiResponse(obj);
//		onFailure(throwable, apiresponse);
//	}
	
	@Override
	public void onFailure(int statusCode, Header[] headers,
			Throwable throwable, JSONArray errorResponse) {
		try {
			onFailure(
					throwable,
					new ApiResponse(new JSONObject(String.format(
							"{\"data\":\"%s\"}", errorResponse))));
		} catch (JSONException e) {
			onFailure(throwable, new ApiResponse(null));
		}
	}
	
	@Override
	public void onFailure(int statusCode, Header[] headers,
			Throwable throwable, JSONObject errorResponse) {
		ApiResponse ar = new ApiResponse(errorResponse);
		onFailure(throwable, ar);
	}
	
	public void onSuccess(ApiResponse response) {
	}

	@Override
	public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
		try {
			onSuccess(new ApiResponse(new JSONObject(String.format(
					"{\"data\":\"%s\"}", response))));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void onSuccess(JSONObject obj) {
		onSuccess(new ApiResponse(obj));
	}

	public void setBaseUrl(String baseUrl) {
		_baseUrl = baseUrl;
	}

	public void setMethod(String method) {
		_method = method;
	}

	protected String _baseUrl;
	protected String _method;

	class ApiException extends Exception {
		final BaseApiResponseHandler handler;
		private ApiResponse response;
		private int statusCode;
		private String errorMessage;

		private ApiException(ApiResponse apiresponse, Throwable throwable) {
			handler = BaseApiResponseHandler.this;
			response = new ApiResponse(null);
			statusCode = -1;
			errorMessage = "";
			response = apiresponse;
			if (throwable instanceof HttpResponseException) {
				statusCode = ((HttpResponseException) throwable)
						.getStatusCode();
				errorMessage = throwable.getLocalizedMessage();
			}
		}

		public String getMessage() {
			ArrayList<String> list = new ArrayList<String>();
			if (response != null) {
				list.add(String.valueOf(statusCode));
				list.add(errorMessage);
				list.add(handler._baseUrl);
				list.add(String.valueOf(response.getErrorCode()));
				list.add(response.getMessage());
			}
			return StringUtils.join(list, ": ");
		}
	}
}
