package net.oschina.app.v2.activity.tweet.adapter;

import net.oschina.app.v2.base.ListBaseAdapter;
import net.oschina.app.v2.model.Tweet;
import net.oschina.app.v2.ui.AvatarView;
import net.oschina.app.v2.ui.text.MyLinkMovementMethod;
import net.oschina.app.v2.ui.text.MyURLSpan;
import net.oschina.app.v2.ui.text.TweetTextView;
import net.oschina.app.v2.utils.DateUtil;
import net.oschina.app.v2.utils.UIHelper;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;
import com.tonlin.osc.happy.R;

public class TweetAdapter extends ListBaseAdapter {

	private DisplayImageOptions options;

	public TweetAdapter() {
		options = new DisplayImageOptions.Builder().cacheInMemory(true)
				.cacheOnDisk(true).postProcessor(new BitmapProcessor() {

					@Override
					public Bitmap process(Bitmap arg0) {
						return arg0;
					}
				}).build();
	}

	@SuppressLint("InflateParams")
	@Override
	protected View getRealView(int position, View convertView,
			final ViewGroup parent) {
		ViewHolder vh = null;
		if (convertView == null || convertView.getTag() == null) {
			convertView = getLayoutInflater(parent.getContext()).inflate(
					R.layout.v2_list_cell_tweet, null);
			vh = new ViewHolder(convertView);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}

		final Tweet item = (Tweet) _data.get(position);
		vh.name.setText(item.getAuthor());

		vh.title.setMovementMethod(MyLinkMovementMethod.a());
		vh.title.setFocusable(false);
		vh.title.setDispatchToParent(true);
		vh.title.setLongClickable(false);
		Spanned span = Html.fromHtml(item.getBody());
		vh.title.setText(span);
		MyURLSpan.parseLinkText(vh.title, span);

		vh.time.setText(DateUtil.getFormatTime(item.getPubDate()));

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

		vh.commentCount.setText(String.valueOf(item.getCommentCount()));

		vh.avatar.setUserInfo(item.getAuthorId(), item.getAuthor());
		vh.avatar.setAvatarUrl(item.getFace());

		if (!TextUtils.isEmpty(item.getImgSmall())) {
			vh.pic.setVisibility(View.VISIBLE);
			ImageLoader.getInstance().displayImage(item.getImgSmall(), vh.pic,
					options);
			vh.pic.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// UIHelper.showImageZoomDialog(parent.getContext(),
					// item.getImgBig());
					UIHelper.showImagePreview(parent.getContext(),
							new String[] { item.getImgBig() });
				}
			});
		} else {
			vh.pic.setVisibility(View.GONE);
			vh.pic.setImageBitmap(null);
		}

		return convertView;
	}

	static class ViewHolder {
		public TextView name, from, time, commentCount;
		public TweetTextView title;
		public ImageView pic;
		public AvatarView avatar;
		
		public ViewHolder(View view) {
			name = (TextView) view.findViewById(R.id.tv_name);
			title = (TweetTextView) view.findViewById(R.id.tv_title);
			from = (TextView) view.findViewById(R.id.tv_from);
			time = (TextView) view.findViewById(R.id.tv_time);
			commentCount = (TextView) view.findViewById(R.id.tv_comment_count);
			avatar = (AvatarView) view.findViewById(R.id.iv_avatar);
			pic = (ImageView) view.findViewById(R.id.iv_pic);
		}
	}
}
