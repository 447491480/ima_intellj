package net.oschina.app.v2.activity.news.fragment;

import net.oschina.app.v2.base.BaseFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tonlin.osc.happy.R;

public class ToolbarFragment extends BaseFragment {

	public interface OnActionClickListener {
		public void onActionClick(ToolAction action);
	}

	public enum ToolAction {
		ACTION_CHANGE, ACTION_WRITE_COMMENT, ACTION_VIEW_COMMENT, ACTION_FAVORITE, ACTION_SHARE, ACTION_REPORT
	}

	private OnActionClickListener mActionListener;

	private boolean mActionVisiableChange = true,
			mActionVisiableWriteComment = false,
			mActionVisiableViewComment = false,
			mActionVisiableFavorite = false, mActionVisiableShare = false,
			mActionVisiableReport = false;

	private View mActionChange, mActionWriteComment, mActionViewComment,
			mActionFavorite, mActionReport, mActionShare;

	private View mIvFavorite;
	private boolean mFavorite;

	private int mCommentCount;

	private TextView mTvCommentCount;

	private String mCommentCountText;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.v2_fragment_detail_tool_bar,
				container, false);

		initView(view);
		return view;
	}

	private void initView(View view) {
		mActionChange = view.findViewById(R.id.btn_change);
		mActionChange.setOnClickListener(this);
		mActionChange.setVisibility(mActionVisiableChange ? View.VISIBLE
				: View.GONE);

		mActionWriteComment = view.findViewById(R.id.write_comment_layout);
		mActionWriteComment.setOnClickListener(this);
		mActionWriteComment
				.setVisibility(mActionVisiableWriteComment ? View.VISIBLE
						: View.GONE);

		mActionFavorite = view.findViewById(R.id.favor_layout);
		mActionFavorite.setOnClickListener(this);
		mActionFavorite.setVisibility(mActionVisiableFavorite ? View.VISIBLE
				: View.GONE);

		mActionViewComment = view.findViewById(R.id.view_comment_layout);
		mActionViewComment.setOnClickListener(this);
		mActionViewComment
				.setVisibility(mActionVisiableViewComment ? View.VISIBLE
						: View.GONE);

		mActionShare = view.findViewById(R.id.repost_layout);
		mActionShare.setOnClickListener(this);
		mActionShare.setVisibility(mActionVisiableShare ? View.VISIBLE
				: View.GONE);

		mActionReport = view.findViewById(R.id.report_layout);
		mActionReport.setOnClickListener(this);
		mActionReport.setVisibility(mActionVisiableReport ? View.VISIBLE
				: View.GONE);

		mIvFavorite = view.findViewById(R.id.action_favor);
		mIvFavorite.setSelected(mFavorite);

		mTvCommentCount = (TextView) view
				.findViewById(R.id.action_comment_count);
		mTvCommentCount.setText(String.valueOf(mCommentCount));
		mTvCommentCount.setVisibility(mCommentCount > 0 ? View.VISIBLE
				: View.GONE);

		if (mCommentCountText != null) {
			mTvCommentCount.setText(mCommentCountText);
			mTvCommentCount.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onClick(View v) {
		final int id = v.getId();
		ToolAction action = null;
		if (id == R.id.btn_change) {
			action = ToolAction.ACTION_CHANGE;
		} else if (id == R.id.write_comment_layout) {
			action = ToolAction.ACTION_WRITE_COMMENT;
		} else if (id == R.id.view_comment_layout) {
			action = ToolAction.ACTION_VIEW_COMMENT;
		} else if (id == R.id.repost_layout) {
			action = ToolAction.ACTION_SHARE;
		} else if (id == R.id.report_layout) {
			action = ToolAction.ACTION_REPORT;
		} else if (id == R.id.favor_layout) {
			action = ToolAction.ACTION_FAVORITE;
		}
		if (action != null && mActionListener != null) {
			mActionListener.onActionClick(action);
		}
	}

	public void setOnActionClickListener(OnActionClickListener lis) {
		mActionListener = lis;
	}

	public void setCommentCount(int count) {
		mCommentCount = count;
		if (mTvCommentCount != null) {
			mTvCommentCount.setText(String.valueOf(mCommentCount));
			mTvCommentCount.setVisibility(mCommentCount > 0 ? View.VISIBLE
					: View.GONE);
		}
	}

	public void setFavorite(boolean favorite) {
		mFavorite = favorite;
		if (mIvFavorite != null) {
			mIvFavorite.setSelected(favorite);
		}
	}

	public void setActionVisiable(ToolAction action, boolean visiable) {
		switch (action) {
		case ACTION_CHANGE:
			mActionVisiableChange = visiable;
			break;
		case ACTION_FAVORITE:
			mActionVisiableFavorite = visiable;
			break;
		case ACTION_REPORT:
			mActionVisiableReport = visiable;
			break;
		case ACTION_SHARE:
			mActionVisiableShare = visiable;
			break;
		case ACTION_VIEW_COMMENT:
			mActionVisiableViewComment = visiable;
			break;
		case ACTION_WRITE_COMMENT:
			mActionVisiableWriteComment = visiable;
			break;
		default:
			break;
		}
	}

	public void setCommentCount(String text) {
		mCommentCountText = text;
		if (mTvCommentCount != null && mCommentCountText != null) {
			mTvCommentCount.setText(mCommentCountText);
			mTvCommentCount.setVisibility(View.VISIBLE);
		}
	}
}
