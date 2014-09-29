package net.oschina.app.v2.activity.news;

import java.lang.ref.WeakReference;

import net.oschina.app.v2.activity.blog.fragment.BlogDetailFragment;
import net.oschina.app.v2.activity.news.fragment.EmojiFragmentControl;
import net.oschina.app.v2.activity.news.fragment.NewsDetailFragment;
import net.oschina.app.v2.activity.news.fragment.ToolbarFragment;
import net.oschina.app.v2.activity.news.fragment.ToolbarFragmentControl;
import net.oschina.app.v2.activity.question.fragment.QuestionDetailFragment;
import net.oschina.app.v2.activity.software.fragment.SoftwareDetailFragment;
import net.oschina.app.v2.activity.tweet.fragment.TweetDetailFragment;
import net.oschina.app.v2.base.BaseActivity;
import net.oschina.app.v2.base.BaseFragment;
import net.oschina.app.v2.emoji.EmojiFragment;
import net.oschina.app.v2.utils.TDevice;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;

import com.tonlin.osc.happy.R;

/**
 * 新闻资讯详情
 * 
 * @author william_sim
 * 
 */
public class DetailActivity extends BaseActivity implements
		ToolbarEmojiVisiableControl {

	public static final int DISPLAY_NEWS = 0;
	public static final int DISPLAY_BLOG = 1;
	public static final int DISPLAY_SOFTWARE = 2;
	public static final int DISPLAY_QUESTION = 3;
	public static final int DISPLAY_TWEET = 4;
	public static final String BUNDLE_KEY_DISPLAY_TYPE = "BUNDLE_KEY_DISPLAY_TYPE";
	// private static final String DETAIL_SCREEN = "detail_screen";

	private View mViewEmojiContaienr, mViewToolBarContaienr;
	private WeakReference<BaseFragment> mFragment, mEmojiFragment;

	@Override
	protected int getLayoutId() {
		return R.layout.v2_activity_detail;
	}

	@Override
	protected boolean hasBackButton() {
		return true;
	}

	@Override
	protected int getActionBarTitle() {
		return R.string.actionbar_title_detail;
	}

	@Override
	protected int getActionBarCustomView() {
		return R.layout.v2_actionbar_custom_detail;
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		super.init(savedInstanceState);
		int displayType = getIntent().getIntExtra(BUNDLE_KEY_DISPLAY_TYPE,
				DISPLAY_NEWS);
		BaseFragment fragment = null;
		int actionBarTitle = 0;
		switch (displayType) {
		case DISPLAY_NEWS:
			actionBarTitle = R.string.actionbar_title_news;
			fragment = new NewsDetailFragment();
			break;
		case DISPLAY_BLOG:
			actionBarTitle = R.string.actionbar_title_blog;
			fragment = new BlogDetailFragment();
			break;
		case DISPLAY_SOFTWARE:
			actionBarTitle = R.string.actionbar_title_software;
			fragment = new SoftwareDetailFragment();
			break;
		case DISPLAY_QUESTION:
			actionBarTitle = R.string.actionbar_title_question;
			fragment = new QuestionDetailFragment();
			break;
		case DISPLAY_TWEET:
			actionBarTitle = R.string.actionbar_title_tweet;
			fragment = new TweetDetailFragment();
			break;
		default:
			break;
		}
		setActionBarTitle(actionBarTitle);
		FragmentTransaction trans = getSupportFragmentManager()
				.beginTransaction();
		mFragment = new WeakReference<BaseFragment>(fragment);
		trans.replace(R.id.container, fragment);

		mViewEmojiContaienr = findViewById(R.id.emoji_container);
		mViewToolBarContaienr = findViewById(R.id.toolbar_container);
		
		if (fragment instanceof EmojiFragmentControl) {
			EmojiFragment f = new EmojiFragment();
			mEmojiFragment = new WeakReference<BaseFragment>(f);
			trans.replace(R.id.emoji_container, f);
			((EmojiFragmentControl) fragment).setEmojiFragment(f);
		}

		if (fragment instanceof ToolbarFragmentControl) {
			ToolbarFragment f = new ToolbarFragment();
			// mEmojiFragment = new WeakReference<BaseFragment>(f);
			trans.replace(R.id.toolbar_container, f);
			((ToolbarFragmentControl) fragment).setToolBarFragment(f);


			mViewEmojiContaienr.setVisibility(View.GONE);
			mViewToolBarContaienr.setVisibility(View.VISIBLE);
		}

		trans.commit();
	}

	@Override
	public void toggleToolbarEmoji() {
		if (mViewEmojiContaienr.getVisibility() == View.VISIBLE) {
			if (mEmojiFragment != null) {
				// mEmojiFragment.get().
			}
			TDevice.hideSoftKeyboard(getCurrentFocus());

			final Animation in = AnimationUtils.loadAnimation(this,
					R.anim.footer_menu_slide_in);
			Animation out = AnimationUtils.loadAnimation(this,
					R.anim.footer_menu_slide_out);
			mViewEmojiContaienr.clearAnimation();
			mViewToolBarContaienr.clearAnimation();
			mViewEmojiContaienr.startAnimation(out);
			out.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {
					//
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
					// TODO Auto-generated method stub
				}

				@Override
				public void onAnimationEnd(Animation animation) {
					mViewEmojiContaienr.setVisibility(View.GONE);
					mViewToolBarContaienr.setVisibility(View.VISIBLE);
					mViewToolBarContaienr.clearAnimation();
					mViewToolBarContaienr.startAnimation(in);
				}
			});
		} else {
			final Animation in = AnimationUtils.loadAnimation(this,
					R.anim.footer_menu_slide_in);
			Animation out = AnimationUtils.loadAnimation(this,
					R.anim.footer_menu_slide_out);
			mViewToolBarContaienr.clearAnimation();
			mViewEmojiContaienr.clearAnimation();
			mViewToolBarContaienr.startAnimation(out);
			out.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {
					// TODO Auto-generated method stub
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
					// TODO Auto-generated method stub
				}

				@Override
				public void onAnimationEnd(Animation animation) {
					mViewToolBarContaienr.setVisibility(View.GONE);
					mViewEmojiContaienr.setVisibility(View.VISIBLE);
					mViewEmojiContaienr.clearAnimation();
					mViewEmojiContaienr.startAnimation(in);
				}
			});
		}
	}

	@Override
	public void onBackPressed() {
		if (mEmojiFragment != null && mEmojiFragment.get() != null
				&& mViewEmojiContaienr.getVisibility() == View.VISIBLE) {
			if (mEmojiFragment.get().onBackPressed()) {
				return;
			}
		}
		if (mFragment != null && mFragment.get() != null) {
			if (mFragment.get().onBackPressed()) {
				return;
			}
		}
		super.onBackPressed();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// MobclickAgent.onPageStart(DETAIL_SCREEN);
		// MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		// MobclickAgent.onPageEnd(DETAIL_SCREEN);
		// MobclickAgent.onPause(this);
	}
}
