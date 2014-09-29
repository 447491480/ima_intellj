package net.oschina.app.v2.activity.user.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.oschina.app.v2.base.ListBaseAdapter;
import net.oschina.app.v2.model.Active;
import net.oschina.app.v2.model.Blog;
import net.oschina.app.v2.model.Tweet;
import net.oschina.app.v2.model.User;
import net.oschina.app.v2.ui.text.MyLinkMovementMethod;
import net.oschina.app.v2.ui.text.MyURLSpan;
import net.oschina.app.v2.ui.text.TweetTextView;
import net.oschina.app.v2.utils.StringUtils;
import net.oschina.app.v2.utils.UIHelper;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;
import com.tonlin.osc.happy.R;

public class UserCenterAdapter extends ListBaseAdapter implements
		StickyListHeadersAdapter {
	private final static String AT_HOST_PRE = "http://my.oschina.net";
	private final static String MAIN_HOST = "http://www.oschina.net";
	private List<Active> actives = new ArrayList<Active>();
	private List<Blog> blogs = new ArrayList<Blog>();

	private Map<DisplayMode, Integer> mStates = new HashMap<DisplayMode, Integer>();
	private DisplayMode mDisplayMode = DisplayMode.ACTIVE;
	private DislayModeChangeListener mListener;

	private User mUser;

	public interface DislayModeChangeListener {
		void onDisplayModeChanged(DisplayMode mode);
	}

	public enum DisplayMode {
		ACTIVE, BLOG, INFOMATION;
	}

	private DisplayImageOptions options;

	public UserCenterAdapter(DislayModeChangeListener lis) {
		mStates.put(DisplayMode.ACTIVE, STATE_LESS_ONE_PAGE);
		mStates.put(DisplayMode.BLOG, STATE_LESS_ONE_PAGE);
		mStates.put(DisplayMode.INFOMATION, STATE_LESS_ONE_PAGE);
		mListener = lis;
		options = new DisplayImageOptions.Builder().cacheInMemory(true)
				.cacheOnDisk(true).postProcessor(new BitmapProcessor() {

					@Override
					public Bitmap process(Bitmap arg0) {
						return arg0;
					}
				}).build();
	}

	public DisplayMode getDisplayMode() {
		return mDisplayMode;
	}

	private void setDisplayMode(DisplayMode mode) {
		mDisplayMode = mode;
		notifyDataSetChanged();
		if (mListener != null) {
			mListener.onDisplayModeChanged(mDisplayMode);
		}
	}

	@Override
	public int getState() {
		return mStates.get(mDisplayMode);
	}

	@Override
	public void setState(int state) {
		mStates.put(mDisplayMode, state);
		notifyDataSetChanged();
	}

	public void setState(DisplayMode mode, int state) {
		mStates.put(mode, state);
		notifyDataSetChanged();
	}

	@Override
	public int getDataSize() {
		switch (mDisplayMode) {
		case ACTIVE:
			return actives.size();
		case BLOG:
			return blogs.size();
		case INFOMATION:
			return 1;
		default:
			break;
		}
		return 0;
	}

	@Override
	public Object getItem(int arg0) {
		if (arg0 < 0)
			return null;
		switch (mDisplayMode) {
		case ACTIVE:
			if (actives.size() > arg0 && arg0 >= 0) {
				return actives.get(arg0);
			}
			break;
		case BLOG:
			if (blogs.size() > arg0 && arg0 >= 0) {
				return blogs.get(arg0);
			}
			break;
		default:
			break;
		}
		return super.getItem(arg0);
	}

	public void clear(DisplayMode mode) {
		switch (mode) {
		case ACTIVE:
			actives.clear();
			break;
		case BLOG:
			blogs.clear();
			break;
		default:
			break;
		}
		notifyDataSetChanged();
	}

	public void setData(DisplayMode mode, ArrayList data) {
		switch (mode) {
		case ACTIVE:
			actives = data;
			break;
		case BLOG:
			blogs = data;
		default:
			break;
		}
		notifyDataSetChanged();
	}

	public void addData(DisplayMode mode, List data) {
		switch (mode) {
		case ACTIVE:
			actives.addAll(data);
			break;
		case BLOG:
			blogs.addAll(data);
			break;
		default:
			break;
		}
		notifyDataSetChanged();
	}

	public void setUserInformation(User user) {
		mUser = user;
		notifyDataSetChanged();
	}

	@Override
	public View getRealView(int position, View convertView, ViewGroup parent) {
		switch (mDisplayMode) {
		case ACTIVE:
			return getViewForActive(position, convertView, parent);
		case BLOG:
			return getViewForBlog(position, convertView, parent);
		case INFOMATION:
			return getViewForInfomation(position, convertView, parent);
		default:
			break;
		}
		return convertView;
	}

	private View getViewForInfomation(int position, View convertView,
			ViewGroup parent) {
		convertView = LayoutInflater.from(parent.getContext()).inflate(
				R.layout.v2_list_user_center_information, null);

		TextView tvJoinTime = (TextView) convertView
				.findViewById(R.id.tv_join_time);
		TextView location = (TextView) convertView
				.findViewById(R.id.tv_location);
		TextView developmentPlatform = (TextView) convertView
				.findViewById(R.id.tv_development_platform);
		TextView academicFocus = (TextView) convertView
				.findViewById(R.id.tv_academic_focus);

		if (mUser != null) {
			tvJoinTime.setText(mUser.getJointime());
			location.setText(mUser.getLocation());
			developmentPlatform.setText(mUser.getDevplatform());
			academicFocus.setText(mUser.getExpertise());
		}
		return convertView;
	}

	private View getViewForBlog(int position, View convertView, ViewGroup parent) {
		BlogViewHolder vh = null;
		if (convertView == null || convertView.getTag() == null
				|| (!(convertView.getTag() instanceof BlogViewHolder))) {
			convertView = getLayoutInflater(parent.getContext()).inflate(
					R.layout.v2_list_cell_news, null);
			vh = new BlogViewHolder(convertView);
			convertView.setTag(vh);
		} else {
			vh = (BlogViewHolder) convertView.getTag();
		}

		Blog item = blogs.get(position);

		vh.title.setText(item.getTitle());
		vh.source.setText(item.getAuthor());
		vh.time.setText(StringUtils.friendly_time(item.getPubDate()));
		return convertView;
	}

	private View getViewForActive(int position, View convertView,
			ViewGroup parent) {
		ViewHolder vh = null;
		if (convertView == null || convertView.getTag() == null
				|| (!(convertView.getTag() instanceof ViewHolder))) {
			convertView = LayoutInflater.from(parent.getContext()).inflate(
					R.layout.v2_list_cell_active, null);
			vh = new ViewHolder(convertView);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}

		Active item = (Active) actives.get(position);

		vh.name.setText(item.getAuthor());

		vh.action.setText(UIHelper.parseActiveAction2(item.getObjectType(),
				item.getObjectCatalog(), item.getObjectTitle()));

		if (TextUtils.isEmpty(item.getMessage())) {
			vh.body.setVisibility(View.GONE);
		} else {
			vh.body.setMovementMethod(MyLinkMovementMethod.a());
			vh.body.setFocusable(false);
			vh.body.setDispatchToParent(true);
			vh.body.setLongClickable(false);
			Spanned span = Html.fromHtml(modifyPath(item.getMessage()));
			vh.body.setText(span);
			MyURLSpan.parseLinkText(vh.body, span);
		}

		vh.time.setText(StringUtils.friendly_time(item.getPubDate()));

		vh.from.setVisibility(View.VISIBLE);
		switch (item.getAppClient()) {
		default:
			vh.from.setText("");
			break;
		case Tweet.CLIENT_MOBILE:
			vh.from.setText(R.string.from_mobile);
			break;
		case Tweet.CLIENT_ANDROID:
			vh.from.setText(R.string.from_android);
			break;
		case Tweet.CLIENT_IPHONE:
			vh.from.setText(R.string.from_iphone);
			break;
		case Tweet.CLIENT_WINDOWS_PHONE:
			vh.from.setText(R.string.from_windows_phone);
			break;
		case Tweet.CLIENT_WECHAT:
			vh.from.setText(R.string.from_wechat);
			break;
		}

		if (item.getCommentCount() > 0) {
			vh.commentCount.setText(String.valueOf(item.getCommentCount()));
			vh.commentCount.setVisibility(View.VISIBLE);
		} else {
			vh.commentCount.setVisibility(View.GONE);
		}
		if (item.getActiveType() == Active.CATALOG_OTHER) {
			vh.retweetCount.setVisibility(View.VISIBLE);
		} else {
			vh.retweetCount.setVisibility(View.GONE);
		}

		String faceURL = item.getFace();
		if (faceURL.endsWith("portrait.gif") || StringUtils.isEmpty(faceURL)) {
			vh.avatar.setImageBitmap(null);
		} else {
			ImageLoader.getInstance().displayImage(item.getFace(), vh.avatar);
		}

		if (!TextUtils.isEmpty(item.getTweetimage())) {
			vh.pic.setVisibility(View.VISIBLE);
			ImageLoader.getInstance().displayImage(item.getTweetimage(),
					vh.pic, options);
		} else {
			vh.pic.setVisibility(View.GONE);
			vh.pic.setImageBitmap(null);
		}
		return convertView;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getHeaderView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(parent.getContext()).inflate(
					R.layout.v2_list_sticky_header_user_center, null);
		}
		TextView category = (TextView) convertView
				.findViewById(R.id.tv_category);
		category.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				setDisplayMode(DisplayMode.ACTIVE);
			}
		});
		TextView blog = (TextView) convertView.findViewById(R.id.tv_blog);
		blog.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				setDisplayMode(DisplayMode.BLOG);
			}
		});
		TextView infomation = (TextView) convertView
				.findViewById(R.id.tv_information);
		infomation.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				setDisplayMode(DisplayMode.INFOMATION);
			}
		});
		Resources res = parent.getResources();
		switch (mDisplayMode) {
		case ACTIVE:
			category.setTextColor(res.getColor(R.color.main_black));
			blog.setTextColor(res.getColor(R.color.main_gray));
			infomation.setTextColor(res.getColor(R.color.main_gray));
			break;
		case BLOG:
			category.setTextColor(res.getColor(R.color.main_gray));
			blog.setTextColor(res.getColor(R.color.main_black));
			infomation.setTextColor(res.getColor(R.color.main_gray));
			break;
		case INFOMATION:
			category.setTextColor(res.getColor(R.color.main_gray));
			blog.setTextColor(res.getColor(R.color.main_gray));
			infomation.setTextColor(res.getColor(R.color.main_black));
			break;
		default:
			break;
		}
		return convertView;
	}

	@Override
	public long getHeaderId(int position) {
		return 0;// only one header
	}

	public void addActives(List<Active> list) {
		actives.addAll(list);
	}

	private String modifyPath(String message) {
		message = message.replaceAll("(<a[^>]+href=\")/([\\S]+)\"", "$1"
				+ AT_HOST_PRE + "/$2\"");
		message = message.replaceAll(
				"(<a[^>]+href=\")http://m.oschina.net([\\S]+)\"", "$1"
						+ MAIN_HOST + "$2\"");
		return message;
	}

	static class ViewHolder {
		public TextView name, from, time, action, actionName, commentCount,
				retweetCount;
		public TweetTextView body;
		public ImageView avatar, pic;

		public ViewHolder(View view) {
			name = (TextView) view.findViewById(R.id.tv_name);
			from = (TextView) view.findViewById(R.id.tv_from);
			body = (TweetTextView) view.findViewById(R.id.tv_body);
			time = (TextView) view.findViewById(R.id.tv_time);
			action = (TextView) view.findViewById(R.id.tv_action);
			actionName = (TextView) view.findViewById(R.id.tv_action_name);
			commentCount = (TextView) view.findViewById(R.id.tv_comment_count);
			retweetCount = (TextView) view.findViewById(R.id.tv_retweet_count);
			avatar = (ImageView) view.findViewById(R.id.iv_avatar);
			pic = (ImageView) view.findViewById(R.id.iv_pic);
		}
	}

	static class BlogViewHolder {
		public TextView title, source, time;

		public BlogViewHolder(View view) {
			title = (TextView) view.findViewById(R.id.tv_title);
			source = (TextView) view.findViewById(R.id.tv_source);
			time = (TextView) view.findViewById(R.id.tv_time);
		}
	}
}
