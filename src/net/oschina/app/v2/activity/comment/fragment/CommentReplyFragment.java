package net.oschina.app.v2.activity.comment.fragment;

import java.io.ByteArrayInputStream;

import net.oschina.app.v2.AppContext;
import net.oschina.app.v2.api.remote.NewsApi;
import net.oschina.app.v2.base.BaseActivity;
import net.oschina.app.v2.base.BaseFragment;
import net.oschina.app.v2.emoji.EmojiFragment;
import net.oschina.app.v2.emoji.EmojiFragment.EmojiTextListener;
import net.oschina.app.v2.model.Comment;
import net.oschina.app.v2.model.Result;
import net.oschina.app.v2.ui.text.MyLinkMovementMethod;
import net.oschina.app.v2.ui.text.MyURLSpan;
import net.oschina.app.v2.ui.text.TweetTextView;
import net.oschina.app.v2.utils.TDevice;
import net.oschina.app.v2.utils.UIHelper;

import org.apache.http.Header;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tonlin.osc.happy.R;
import com.umeng.analytics.MobclickAgent;

public class CommentReplyFragment extends BaseFragment implements
		EmojiTextListener {

	public static final String BUNDLE_KEY_ID = "bundle_key_id";
	public static final String BUNDLE_KEY_IS_BLOG = "bundle_key_is_blog";
	public static final String BUNDLE_KEY_CATALOG = "bundle_key_catalog";
	public static final String BUNDLE_KEY_COMMENT = "bundle_key_comment";
	private static final String COMMENT_REPLY_SCREEN = "comment_reply_screen";

	private EmojiFragment mEmojiFragment;
	private TweetTextView mTvContent;
	private ImageView mIvAvatar;

	private int mId, mCatalog;
	private Comment mComment;
	private boolean mIsBlogComment;

	private AsyncHttpResponseHandler mReplyCommentHandler = new AsyncHttpResponseHandler() {

		@Override
		public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
			try {
				Result res = Result.parse(new ByteArrayInputStream(arg2));
				if (res.OK()) {
					hideWaitDialog();
					AppContext.showToastShort(R.string.comment_success);

					Intent intent = new Intent();
					Bundle data = new Bundle();
					data.putParcelable(Comment.BUNDLE_KEY_COMMENT, res.getComment());
					intent.putExtras(data);
					getActivity().setResult(Activity.RESULT_OK,intent);
					getActivity().finish();

					UIHelper.sendBroadCastCommentChanged(getActivity(),
							mIsBlogComment, mId, mCatalog, Comment.OPT_ADD,
							res.getComment());
				} else {
					hideWaitDialog();
					AppContext.showToastShort(res.getErrorMessage());
				}
			} catch (Exception e) {
				e.printStackTrace();
				onFailure(arg0, arg1, arg2, e);
			}
		}

		@Override
		public void onFailure(int arg0, Header[] arg1, byte[] arg2,
				Throwable arg3) {
			hideWaitDialog();
			AppContext.showToastShort(R.string.comment_faile);
		}
	};

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		BaseActivity act = ((BaseActivity) activity);
		FragmentTransaction trans = act.getSupportFragmentManager()
				.beginTransaction();
		mEmojiFragment = new EmojiFragment();
		mEmojiFragment.setEmojiTextListener(this);
		trans.replace(R.id.emoji_container, mEmojiFragment);
		trans.commit();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		int mode = WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
				| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;
		getActivity().getWindow().setSoftInputMode(mode);
	}

	@Override
	public void onDestroy() {
		if (mEmojiFragment != null) {
			mEmojiFragment.hideKeyboard();
		}
		super.onDestroy();
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.v2_fragment_reply_comment,
				container, false);
		initViews(view);
		initData();
		return view;
	}

	private void initViews(View view) {
		mIvAvatar = (ImageView) view.findViewById(R.id.iv_avatar);
		mTvContent = (TweetTextView) view.findViewById(R.id.tv_content);
	}

	private void initData() {
		Bundle args = getArguments();
		mIsBlogComment = args.getBoolean(BUNDLE_KEY_IS_BLOG);
		mId = args.getInt(BUNDLE_KEY_ID);
		mCatalog = args.getInt(BUNDLE_KEY_CATALOG);
		mComment = args.getParcelable(BUNDLE_KEY_COMMENT);

		ImageLoader.getInstance().displayImage(mComment.getFace(), mIvAvatar);

		mTvContent.setMovementMethod(MyLinkMovementMethod.a());
		mTvContent.setFocusable(false);
		mTvContent.setDispatchToParent(true);
		mTvContent.setLongClickable(false);
		Spanned span = Html.fromHtml(mComment.getContent());
		mTvContent.setText(span);
		MyURLSpan.parseLinkText(mTvContent, span);
	}

	@Override
	public void onSendClick(String text) {
		if (!TDevice.hasInternet()) {
			AppContext.showToastShort(R.string.tip_network_error);
			return;
		}
		if (!AppContext.instance().isLogin()) {
			UIHelper.showLogin(getActivity());
			return;
		}
		if (TextUtils.isEmpty(text.trim())) {
			AppContext.showToastShort(R.string.tip_comment_content_empty);
			return;
		}
		showWaitDialog(R.string.progress_submit);
		if (mIsBlogComment) {
			NewsApi.replyBlogComment(mId, AppContext.instance().getLoginUid(),
					text, mComment.getId(), mComment.getAuthorId(),
					mReplyCommentHandler);
		} else {
			NewsApi.replyComment(mId, mCatalog, mComment.getId(),
					mComment.getAuthorId(),
					AppContext.instance().getLoginUid(), text,
					mReplyCommentHandler);
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(COMMENT_REPLY_SCREEN);
		MobclickAgent.onResume(getActivity());
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(COMMENT_REPLY_SCREEN);
		MobclickAgent.onPause(getActivity());
	}
}
