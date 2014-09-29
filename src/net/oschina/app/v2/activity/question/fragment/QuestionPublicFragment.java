package net.oschina.app.v2.activity.question.fragment;

import net.oschina.app.v2.AppContext;
import net.oschina.app.v2.base.BaseFragment;
import net.oschina.app.v2.model.Post;
import net.oschina.app.v2.service.ServerTaskUtils;
import net.oschina.app.v2.ui.dialog.CommonDialog;
import net.oschina.app.v2.ui.dialog.DialogHelper;
import net.oschina.app.v2.utils.TDevice;
import net.oschina.app.v2.utils.UIHelper;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.tonlin.osc.happy.R;
import com.umeng.analytics.MobclickAgent;

public class QuestionPublicFragment extends BaseFragment {

	private static final String QUESTION_PUBLIC_SCREEN = "question_public_screen";
	private EditText mEtTitle, mEtContent;
	private TextView mTvCategory;
	private CheckBox mCbLetMeKnow;

	private String[] mCategoryOptions;
	private int mCategory;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		int mode = WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
				| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;
		getActivity().getWindow().setSoftInputMode(mode);
		mCategoryOptions = getResources().getStringArray(
				R.array.post_pub_options);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.public_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.public_menu_send:
			handleSubmit();
			break;
		default:
			break;
		}
		return true;
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.v2_fragment_question_public,
				container, false);

		initView(view);
		return view;
	}

	private void initView(View view) {
		mEtTitle = (EditText) view.findViewById(R.id.et_title);
		mTvCategory = (TextView) view.findViewById(R.id.tv_category);
		mCategory = AppContext.getLastQuestionCategoryIdx();
		view.findViewById(R.id.ly_category).setOnClickListener(this);
		mEtContent = (EditText) view.findViewById(R.id.et_content);
		mCbLetMeKnow = (CheckBox) view.findViewById(R.id.cb_let_me_know);

		String draft = AppContext.getQuestionTitleDraft();
		mEtTitle.setText(draft);
		if (!TextUtils.isEmpty(draft)) {
			mEtTitle.setSelection(mEtTitle.getText().toString().length());
		}
		
		mCategory = AppContext.getQuestionTypeDraft();
		mTvCategory.setText(getString(R.string.question_public_category,
				mCategoryOptions[mCategory]));
		
		draft = AppContext.getQuestionContentDraft();
		mEtContent.setText(draft);
		if (!TextUtils.isEmpty(draft)) {
			mEtContent.setSelection(mEtContent.getText().toString().length());
		}
		
		mCbLetMeKnow.setChecked(AppContext.getQuestionLetMeKnowDraft());
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.ly_category) {
			handleShowCategory();
		}
	}

	private void handleShowCategory() {
		final CommonDialog dialog = DialogHelper
				.getPinterestDialogCancelable(getActivity());
		dialog.setTitle(R.string.category);
		dialog.setItems(mCategoryOptions, mCategory, new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				dialog.dismiss();
				mCategory = position;
				mTvCategory.setText(getString(
						R.string.question_public_category,
						mCategoryOptions[mCategory]));
			}
		});
		dialog.setNegativeButton(R.string.cancle, null);
		dialog.show();
	}

	private boolean prepareForSubmit() {
		if (!TDevice.hasInternet()) {
			AppContext.showToastShort(R.string.tip_network_error);
			return false;
		}
		if (!AppContext.instance().isLogin()) {
			UIHelper.showLogin(getActivity());
			return false;
		}
		String title = mEtTitle.getText().toString().trim();
		if (TextUtils.isEmpty(title.trim())) {
			AppContext.showToastShort(R.string.tip_title_empty);
			mEtTitle.requestFocus();
			return false;
		}
		String content = mEtContent.getText().toString().trim();
		if (TextUtils.isEmpty(content.trim())) {
			AppContext.showToastShort(R.string.tip_content_empty);
			mEtContent.requestFocus();
			return false;
		}
		return true;
	}

	private void handleSubmit() {
		if (!prepareForSubmit()) {
			return;
		}
		String title = mEtTitle.getText().toString().trim();
		String content = mEtContent.getText().toString().trim();
		Post post = new Post();
		post.setAuthorId(AppContext.instance().getLoginUid());
		post.setTitle(title);
		post.setBody(content);
		post.setCatalog(mCategory + 1);
		if (mCbLetMeKnow.isChecked())
			post.setIsNoticeMe(1);
		ServerTaskUtils.publicPost(getActivity(), post);
		getActivity().finish();
	}

	@Override
	public boolean onBackPressed() {
		final String title = mEtTitle.getText().toString();
		final String content = mEtContent.getText().toString();
		if(!TextUtils.isEmpty(title)
				|| !TextUtils.isEmpty(content)){
			CommonDialog dialog = DialogHelper
					.getPinterestDialogCancelable(getActivity());
			dialog.setMessage(R.string.draft_tweet_message);
			dialog.setNegativeButton(R.string.cancle, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					AppContext.setQuestionTitleDraft("");
					AppContext.setQuestionTypeDraft(0);
					AppContext.setQuestionContentDraft("");
					AppContext.setQuestionLetMeKnowDraft(false);
					getActivity().finish();
				}
			});
			dialog.setPositiveButton(R.string.ok, new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					AppContext.setQuestionTitleDraft(title);
					AppContext.setQuestionTypeDraft(mCategory);
					AppContext.setQuestionContentDraft(content);
					AppContext.setQuestionLetMeKnowDraft(mCbLetMeKnow.isChecked());
					getActivity().finish();
				}
			});
			dialog.show();
			return true;
		}
		return super.onBackPressed();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(QUESTION_PUBLIC_SCREEN);
		MobclickAgent.onResume(getActivity());
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(QUESTION_PUBLIC_SCREEN);
		MobclickAgent.onPause(getActivity());
	}
}
