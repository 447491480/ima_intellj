package net.oschina.app.v2.emoji;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.oschina.app.v2.AppContext;
import net.oschina.app.v2.base.BaseFragment;
import net.oschina.app.v2.emoji.EmojiViewPagerAdapter.OnClickEmojiListener;
import net.oschina.app.v2.emoji.SoftKeyboardStateHelper.SoftKeyboardStateListener;
import net.oschina.app.v2.utils.TDevice;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.tonlin.osc.happy.R;
import com.viewpagerindicator.CirclePageIndicator;

public class EmojiFragment extends BaseFragment implements
		SoftKeyboardStateListener, OnClickEmojiListener {

	private ViewPager mViewPager;
	private CirclePageIndicator mIndicator;
	private ImageButton mBtnEmoji, mBtnSend, mBtnMore;
	private EmojiEditText mEtInput;
	private View mLyEmoji;
	private EmojiViewPagerAdapter mPagerAdapter;
	private SoftKeyboardStateHelper mKeyboardHelper;
	private boolean mIsKeyboardVisible;
	private boolean mNeedHideEmoji;
	private int mCurrentKeyboardHeigh;

	private EmojiTextListener mListener;

	private int mMoreVisisable = View.GONE;

	private OnClickListener mMoreClickListener;

	public interface EmojiTextListener {
		public void onSendClick(String text);
	}

	public void setEmojiTextListener(EmojiTextListener lis) {
		mListener = lis;
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.v2_fragment_emoji, container,
				false);

		initViews(view);
		mKeyboardHelper = new SoftKeyboardStateHelper(getActivity().getWindow()
				.getDecorView());// .findViewById(R.id.activity_root)
		mKeyboardHelper.addSoftKeyboardStateListener(this);
		return view;
	}

	@Override
	public void onDestroyView() {
		mKeyboardHelper.removeSoftKeyboardStateListener(this);
		super.onDestroyView();
	}

	private void initViews(View view) {
		mLyEmoji = view.findViewById(R.id.ly_emoji);
		mViewPager = (ViewPager) view.findViewById(R.id.view_pager);
		mIndicator = (CirclePageIndicator) view.findViewById(R.id.indicator);

		mBtnMore = (ImageButton) view.findViewById(R.id.btn_more);
		mBtnMore.setVisibility(mMoreVisisable);
		mBtnMore.setOnClickListener(mMoreClickListener);

		mBtnEmoji = (ImageButton) view.findViewById(R.id.btn_emoji);
		mBtnSend = (ImageButton) view.findViewById(R.id.btn_send);
		mBtnSend.setOnClickListener(this);
		mEtInput = (EmojiEditText) view.findViewById(R.id.et_input);

		mBtnEmoji.setOnClickListener(this);

		Map<String, Emoji> emojis = EmojiHelper.qq_emojis_nos;
		// int pagerSize = emojis.size() / 20;

		List<Emoji> allEmojis = new ArrayList<Emoji>();
		Iterator<String> itr1 = emojis.keySet().iterator();
		while (itr1.hasNext()) {
			Emoji ej = emojis.get(itr1.next());
			allEmojis.add(new Emoji(ej.getResId(), ej.getValue(),
					ej.getValueNo(), ej.getIndex()));
		}
		Collections.sort(allEmojis);

		List<List<Emoji>> pagers = new ArrayList<List<Emoji>>();
		List<Emoji> es = null;
		int size = 0;
		boolean justAdd = false;
		for (Emoji ej : allEmojis) {
			if (size == 0) {
				es = new ArrayList<Emoji>();
			}
			es.add(new Emoji(ej.getResId(), ej.getValue(), ej.getValueNo()));
			size++;
			if (size == 20) {
				pagers.add(es);
				size = 0;
				justAdd = true;
			} else {
				justAdd = false;
			}
		}
		if (!justAdd && es != null) {
			pagers.add(es);
		}

		int emojiHeight = caculateEmojiPanelHeight();

		mPagerAdapter = new EmojiViewPagerAdapter(getActivity(), pagers,
				emojiHeight, this);
		mViewPager.setAdapter(mPagerAdapter);
		mIndicator.setViewPager(mViewPager);
	}

	private int caculateEmojiPanelHeight() {
		mCurrentKeyboardHeigh = AppContext.getSoftKeyboardHeight();
		if (mCurrentKeyboardHeigh == 0) {
			mCurrentKeyboardHeigh = (int) TDevice.dpToPixel(180);
		}
		int emojiPanelHeight = (int) (mCurrentKeyboardHeigh - TDevice
				.dpToPixel(20));
		int emojiHeight = (int) (emojiPanelHeight / 3);

		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, emojiPanelHeight);
		mViewPager.setLayoutParams(lp);
		if (mPagerAdapter != null) {
			mPagerAdapter.setEmojiHeight(emojiHeight);
		}
		return emojiHeight;
	}

	@Override
	public boolean onBackPressed() {
		if (mLyEmoji.getVisibility() == View.VISIBLE) {
			hideEmojiPanel();
			return true;
		}
		return super.onBackPressed();
	}

	@Override
	public void onClick(View v) {
		final int id = v.getId();
		if (id == R.id.btn_emoji) {
			if (mLyEmoji.getVisibility() == View.GONE) {
				mNeedHideEmoji = true;
				tryShowEmojiPanel();
			} else {
				tryHideEmojiPanel();
			}
		} else if (id == R.id.btn_send) {
			if (mListener != null) {
				mListener.onSendClick(mEtInput.getText().toString());
			}
		}
	}

	private void tryShowEmojiPanel() {
		if (mIsKeyboardVisible) {
			TDevice.hideSoftKeyboard(getActivity().getCurrentFocus());
		} else {
			showEmojiPanel();
		}
	}

	private void showEmojiPanel() {
		mNeedHideEmoji = false;
		mLyEmoji.setVisibility(View.VISIBLE);
		mBtnEmoji.setBackgroundResource(R.drawable.btn_emoji_pressed);
	}

	private void tryHideEmojiPanel() {
		if (!mIsKeyboardVisible) {
			mEtInput.requestFocus();
			TDevice.showSoftKeyboard(mEtInput);
		} else {
			hideEmojiPanel();
		}
	}

	private void hideEmojiPanel() {
		if (mLyEmoji.getVisibility() == View.VISIBLE) {
			mLyEmoji.setVisibility(View.GONE);
			mBtnEmoji.setBackgroundResource(R.drawable.btn_emoji_selector);
		}
	}

	@Override
	public void onSoftKeyboardOpened(int keyboardHeightInPx) {
		int realKeyboardHeight = keyboardHeightInPx
				- TDevice.getStatusBarHeight();

		AppContext.setSoftKeyboardHeight(realKeyboardHeight);
		if (mCurrentKeyboardHeigh != realKeyboardHeight) {
			caculateEmojiPanelHeight();
		}

		mIsKeyboardVisible = true;
		hideEmojiPanel();
	}

	@Override
	public void onSoftKeyboardClosed() {
		mIsKeyboardVisible = false;
		if (mNeedHideEmoji) {
			showEmojiPanel();
		}
	}

	@Override
	public void onEmojiClick(Emoji emoji) {
		mEtInput.insertEmoji(emoji);
	}

	@Override
	public void onDelete() {
		mEtInput.delete();
	}

	public void requestFocusInput() {
		if (mEtInput != null) {
			mEtInput.requestFocus();
			if (!mIsKeyboardVisible) {
				TDevice.toogleSoftKeyboard(getActivity().getCurrentFocus());
				// TDevice.showSoftKeyboard(getActivity().getCurrentFocus());
			}
		}
	}

	public void hideKeyboard() {
		if (mIsKeyboardVisible) {
			TDevice.toogleSoftKeyboard(getActivity().getCurrentFocus());
		}
	}

	public void setInputHint(String hint) {
		if (mEtInput != null) {
			mEtInput.setHint(hint);
		}
	}

	public void setButtonMoreVisibility(int visibility) {
		if (mBtnMore != null)
			mBtnMore.setVisibility(visibility);
		else
			mMoreVisisable = visibility;
	}

	public void setButtonMoreClickListener(View.OnClickListener lis) {
		if (mBtnMore != null)
			mBtnMore.setOnClickListener(lis);
		else
			mMoreClickListener = lis;
	}

	public void reset() {
		if (mIsKeyboardVisible) {
			// TDevice.toogleSoftKeyboard(getActivity().getCurrentFocus());
			TDevice.hideSoftKeyboard(mEtInput);
		}
		if (mLyEmoji.getVisibility() == View.VISIBLE) {
			hideEmojiPanel();
		}
		if (mEtInput != null) {
			mEtInput.getText().clear();
			mEtInput.clearFocus();
			mEtInput.setHint(R.string.publish_comment);
		}
	}

	public void showKeyboardIfNoEmojiGrid() {
		if (!mIsKeyboardVisible && mLyEmoji.getVisibility() != View.VISIBLE) {
			mEtInput.requestFocus();
			TDevice.showSoftKeyboard(mEtInput);
		}
	}
}
