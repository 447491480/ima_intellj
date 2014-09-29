package net.oschina.app.v2.activity.daily;

import net.oschina.app.v2.AppContext;
import net.oschina.app.v2.api.remote.OtherApi;
import net.oschina.app.v2.base.BaseFragment;
import net.oschina.app.v2.model.DailyEnglish;
import net.oschina.app.v2.ui.empty.EmptyLayout;
import net.oschina.app.v2.utils.DateUtil;
import net.oschina.app.v2.utils.TLog;
import net.oschina.app.v2.utils.UIHelper;

import org.apache.http.Header;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;
import com.tonlin.osc.happy.R;

public class DailyEnglishFragment extends BaseFragment {

	private static DailyEnglish mDailyEnglish;
	private TextView mTvContent, mTvNote;
	private ImageView mIvPic;
	private DisplayImageOptions options;
	private TextView mTvTranslation;
	private EmptyLayout mEmptyLayout;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		options = new DisplayImageOptions.Builder().cacheOnDisk(true)
				.cacheInMemory(true).postProcessor(new BitmapProcessor() {

					@Override
					public Bitmap process(Bitmap arg0) {
						return arg0;
					}
				}).build();
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.v2_fragment_daily_english,
				container, false);
		initView(view);
		requestDailyEnglish();
		return view;
	}

	private void initView(View view) {
		mEmptyLayout = (EmptyLayout) view.findViewById(R.id.emptylayout);
		mTvContent = (TextView) view.findViewById(R.id.tv_content);
		mTvNote = (TextView) view.findViewById(R.id.tv_note);
		mTvTranslation = (TextView) view.findViewById(R.id.tv_translation);
		mIvPic = (ImageView) view.findViewById(R.id.iv_pic);
		mIvPic.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mDailyEnglish != null) {
					UIHelper.showImagePreview(getActivity(),
							new String[] { mDailyEnglish.getPictureBig() });
				}
			}
		});
	}

	public void requestDailyEnglish() {
		mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
		String lastDate = AppContext.getLastGetDailyEngDate();
		if (lastDate != null) {
			if (DateUtil.getNow("yyy-MM-dd").compareTo(lastDate) <= 0) {
				mDailyEnglish = AppContext.getDailyEnglish();
				if (mDailyEnglish != null) {
					TLog.log("today english has ready");
					fillUI();
					return;
				}
			}
		}
		OtherApi.getDailyEnglish(new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				AppContext.setLastGetDailyEngDate(DateUtil.getNow("yyy-MM-dd"));
				AppContext.setDailyEnglish(response.toString());
				mDailyEnglish = AppContext.getDailyEnglish();
				if (mDailyEnglish != null) {
					fillUI();
				} else {
					mEmptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					String responseString, Throwable throwable) {
				TLog.log(responseString + "");
				throwable.printStackTrace();
				mEmptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
			}
		});
	}

	private void fillUI() {
		mTvContent.setText(mDailyEnglish.getContent());
		mTvNote.setText(mDailyEnglish.getNote());
		mTvTranslation.setText(mDailyEnglish.getTranslation());
		ImageLoader.getInstance().displayImage(mDailyEnglish.getPicture(),
				mIvPic, options);
		mEmptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
	}
}
