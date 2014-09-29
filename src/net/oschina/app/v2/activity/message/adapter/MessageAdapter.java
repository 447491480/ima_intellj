package net.oschina.app.v2.activity.message.adapter;

import net.oschina.app.v2.AppContext;
import net.oschina.app.v2.base.ListBaseAdapter;
import net.oschina.app.v2.model.Messages;
import net.oschina.app.v2.ui.text.MyLinkMovementMethod;
import net.oschina.app.v2.ui.text.MyURLSpan;
import net.oschina.app.v2.ui.text.TweetTextView;
import net.oschina.app.v2.utils.DateUtil;
import net.oschina.app.v2.utils.UIHelper;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.tonlin.osc.happy.R;

public class MessageAdapter extends ListBaseAdapter {

	@Override
	protected boolean loadMoreHasBg() {
		return false;
	}

	@Override
	protected View getRealView(int position, View convertView,
			final ViewGroup parent) {
		ViewHolder vh = null;
		if (convertView == null || convertView.getTag() == null) {
			convertView = getLayoutInflater(parent.getContext()).inflate(
					R.layout.v2_list_cell_message, null);
			vh = new ViewHolder(convertView);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}

		final Messages item = (Messages) _data.get(position);

		if (AppContext.instance().getLoginUid() == item.getSenderId()) {
			vh.sender.setVisibility(View.VISIBLE);
		} else {
			vh.sender.setVisibility(View.GONE);
		}

		vh.name.setText(item.getFriendName());

		vh.content.setMovementMethod(MyLinkMovementMethod.a());
		vh.content.setFocusable(false);
		vh.content.setDispatchToParent(true);
		vh.content.setLongClickable(false);
		Spanned span = Html.fromHtml(item.getContent());
		vh.content.setText(span);
		MyURLSpan.parseLinkText(vh.content, span);

		vh.time.setText(DateUtil.getFormatTime(item.getPubDate()));
		vh.count.setText(parent.getResources().getString(
				R.string.message_count,item.getMessageCount()));
		
		ImageLoader.getInstance().displayImage(item.getFace(), vh.avatar);
		vh.avatar.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				UIHelper.showUserCenter(parent.getContext(),
						item.getFriendId(), item.getFriendName());
			}
		});
		return convertView;
	}

	static class ViewHolder {
		ImageView avatar;
		TextView name, sender, time,count;
		TweetTextView content;

		ViewHolder(View view) {
			avatar = (ImageView) view.findViewById(R.id.iv_avatar);
			sender = (TextView) view.findViewById(R.id.tv_sender);
			name = (TextView) view.findViewById(R.id.tv_name);
			time = (TextView) view.findViewById(R.id.tv_time);
			count = (TextView) view.findViewById(R.id.tv_count);
			content = (TweetTextView) view.findViewById(R.id.tv_content);
		}
	}
}
