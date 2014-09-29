package net.oschina.app.v2.api.remote;

import net.oschina.app.v2.api.ApiHttpClient;
import net.oschina.app.v2.utils.TDevice;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class BaseApi {
	public static final int DEF_PAGE_SIZE = TDevice.getPageSize();
	private static final String CLIENT_ID = "F6QtiYRetdUEwsYKYvNR";
	private static final String REDIRECT_URI = "http://my.oschina.net/u/142883";

	/**
	 * <table cellspacing="0" bordercolor="#dee7e6" border="1" style="border-collapse:collapse;border: 1px solid #dee7e6;">
	 * <tbody>
	 * <tr>
	 * <th width="91px"></th>
	 * <th width="33px">必选</th>
	 * <th width="100px">类型及范围</th>
	 * <th width="531px">说明</th>
	 * <th width="66px">默认值</th>
	 * </tr>
	 * <tr>
	 * <td style="font-weight:bold;">client_id</td>
	 * <td>true</td>
	 * <td>string</td>
	 * <td>OAuth2客户ID</td>
	 * <td></td>
	 * </tr>
	 * <tr style="background-color:#f6fcfc;">
	 * <td style="font-weight:bold;">response_type</td>
	 * <td>true</td>
	 * <td>string</td>
	 * <td>返回数据类型</td>
	 * <td>code</td>
	 * </tr>
	 * <tr>
	 * <td style="font-weight:bold;">redirect_uri</td>
	 * <td>true</td>
	 * <td>string</td>
	 * <td>回调地址</td>
	 * <td></td>
	 * </tr>
	 * <tr style="background-color:#f6fcfc;">
	 * <td style="font-weight:bold;">state</td>
	 * <td>false</td>
	 * <td>string</td>
	 * <td>可选参数</td>
	 * <td></td>
	 * </tr>
	 * </tbody>
	 * </table>
	 * 
	 * @param handler
	 */
	public static void authorize(AsyncHttpResponseHandler handler) {
		RequestParams params = new RequestParams();
		params.put("client_id", CLIENT_ID);
		params.put("response_type", "code");
		params.put("redirect_uri", REDIRECT_URI);
		ApiHttpClient.get("action/oauth2/authorize", params, handler);
	}

}
