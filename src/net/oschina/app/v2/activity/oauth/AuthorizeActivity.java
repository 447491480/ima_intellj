package net.oschina.app.v2.activity.oauth;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.tonlin.osc.happy.R;

public class AuthorizeActivity extends ActionBarActivity {
	private static final String AUTHROIZE_URL = "http://www.oschina.net/action/oauth2/authorize?response_type=code&client_id=F6QtiYRetdUEwsYKYvNR&state=xyz&redirect_uri=http://my.oschina.net/u/142883";
	private WebView mWebView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.v2_activity_authorize);

		mWebView = (WebView) findViewById(R.id.webview);
		WebSettings settings = mWebView.getSettings();
		settings.setJavaScriptEnabled(true);
		mWebView.loadUrl(AUTHROIZE_URL);
		mWebView.setWebViewClient(new AuthorizeClient());
	}

	class AuthorizeClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			return super.shouldOverrideUrlLoading(view, url);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
		}
	}
}
