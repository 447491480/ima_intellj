package net.oschina.app.v2.activity.settings.fragment;

import net.oschina.app.v2.activity.settings.view.ChangeLogDialog;
import net.oschina.app.v2.base.BaseFragment;
import net.oschina.app.v2.utils.TDevice;
import net.oschina.app.v2.utils.UIHelper;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tonlin.osc.happy.R;
import com.umeng.analytics.MobclickAgent;

public class AboutFragment extends BaseFragment {

	private static final String ABOUT_SCREEN = "about_screen";

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.v2_fragment_about, container,
				false);
		initViews(view);
		return view;
	}

	private void initViews(View view) {
		view.findViewById(R.id.ly_author).setOnClickListener(this);
		view.findViewById(R.id.ly_osc_git).setOnClickListener(this);
		view.findViewById(R.id.ly_lisence).setOnClickListener(this);
		view.findViewById(R.id.ly_change_log).setOnClickListener(this);
		
		TextView tvVersionName = (TextView) view
				.findViewById(R.id.tv_version_name);
		tvVersionName.setText(TDevice.getVersionName());
	}

	@Override
	public void onClick(View v) {
		final int id = v.getId();
		if (id == R.id.ly_lisence) {
			UIHelper.showLisence(getActivity());
		} else if (id == R.id.ly_author) {
			UIHelper.showUrlRedirect(getActivity(),
					getString(R.string.author_center));
		} else if (id == R.id.ly_osc_git) {
			UIHelper.showUrlRedirect(getActivity(),
					getString(R.string.osc_git_path));
		} else if(id == R.id.ly_change_log){
			ChangeLogDialog dialog = new ChangeLogDialog(getActivity());
			dialog.show();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(ABOUT_SCREEN);
		MobclickAgent.onResume(getActivity());
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(ABOUT_SCREEN);
		MobclickAgent.onPause(getActivity());
	}
}
