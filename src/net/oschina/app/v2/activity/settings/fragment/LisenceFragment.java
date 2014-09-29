package net.oschina.app.v2.activity.settings.fragment;

import net.oschina.app.v2.base.BaseFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.tonlin.osc.happy.R;
import com.umeng.analytics.MobclickAgent;

public class LisenceFragment extends BaseFragment {

	private static final String LISENCE_SCREEN = "lisence_screen";

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.v2_fragment_lisence, container,
				false);
		WebView webView = (WebView) view.findViewById(R.id.webview);
		webView.loadUrl("file:///android_asset/licenses.html");
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(LISENCE_SCREEN);
		MobclickAgent.onResume(getActivity());
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(LISENCE_SCREEN);
		MobclickAgent.onPause(getActivity());
	}
}
