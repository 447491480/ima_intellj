package net.oschina.app.v2.model;

import net.oschina.app.v2.AppException;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 通过json字符串解析的结果实体类
 * 
 * @author 火蚁（http://my.oschina/LittleDY）
 * @version 1.0
 * @created 2014-03-18
 */
public class JsonResult {

	public final static String NODE_MES = "msg";
	public final static String NODE_ERROR = "error";

	private String message;// 成功消息
	private String errorMes;// 错误消息
	private boolean ok;// 是否成功

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getErrorMes() {
		return errorMes;
	}

	public void setErrorMes(String errorMes) {
		this.errorMes = errorMes;
	}

	public boolean isOk() {
		return ok;
	}

	public void setOk(boolean ok) {
		this.ok = ok;
	}

	public static JsonResult parse(String jsonStr) throws AppException {
		JsonResult jsonResult = new JsonResult();
		try {
			JSONObject jsonObject = new JSONObject(jsonStr);
			// 如果有错误信息则表示不成功
			if (jsonObject.isNull(NODE_ERROR)) {
				jsonResult.setOk(true);
			}
			if (!jsonObject.isNull(NODE_ERROR)) {
				jsonResult.setErrorMes(jsonObject.getString(NODE_ERROR));
			}
			if (!jsonObject.isNull(NODE_MES)) {
				jsonResult.setMessage(jsonObject.getString(NODE_MES));
			}
		} catch (JSONException e) {
			// 抛出一个json解析错误的异常
			throw AppException.json(e);
		}
		return jsonResult;
	}
}
