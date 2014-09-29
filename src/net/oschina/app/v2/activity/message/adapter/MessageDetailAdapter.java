package net.oschina.app.v2.activity.message.adapter;

import net.oschina.app.v2.AppContext;
import net.oschina.app.v2.base.ListBaseAdapter;
import net.oschina.app.v2.model.Comment;
import net.oschina.app.v2.ui.text.MyLinkMovementMethod;
import net.oschina.app.v2.ui.text.MyURLSpan;
import net.oschina.app.v2.ui.text.TweetTextView;
import net.oschina.app.v2.utils.UIHelper;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.tonlin.osc.happy.R;

public class MessageDetailAdapter extends ListBaseAdapter {

	@Override
	protected boolean loadMoreHasBg() {
		return false;
	}

	@Override
	protected View getRealView(int position, View convertView,
			final ViewGroup parent) {
		final Comment item = (Comment) _data.get(position);
		int itemType = 0;
		if (item.getAuthorId() == AppContext.instance().getLoginUid()) {
			itemType = 1;
		}
		boolean needCreateView = false;
		ViewHolder vh = null;
		if (convertView == null) {
			needCreateView = true;
		} else {
			vh = (ViewHolder) convertView.getTag();
		}
		if (vh != null && (vh.type != itemType)) {
			needCreateView = true;
		}
		if (vh == null) {
			needCreateView = true;
		}

		if (needCreateView) {
			convertView = getLayoutInflater(parent.getContext()).inflate(
					itemType == 0 ? R.layout.v2_list_cell_chat_from
							: R.layout.v2_list_cell_chat_to, null);
			vh = new ViewHolder(convertView);
			vh.type = itemType;
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}

		// vh.name.setText(item.getAuthor());

		vh.content.setMovementMethod(MyLinkMovementMethod.a());
		vh.content.setFocusable(false);
		vh.content.setDispatchToParent(true);
		vh.content.setLongClickable(false);
		Spanned span = Html.fromHtml(item.getContent());
		vh.content.setText(span);
		MyURLSpan.parseLinkText(vh.content, span);

		ImageLoader.getInstance().displayImage(item.getFace(), vh.avatar);
		vh.avatar.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				UIHelper.showUserCenter(parent.getContext(),
						item.getAuthorId(), item.getAuthor());
			}
		});

		return convertView;
	}

	static class ViewHolder {
		int type;
		ImageView avatar;
		TextView name, time;
		TweetTextView content;

		ViewHolder(View view) {
			avatar = (ImageView) view.findViewById(R.id.iv_avatar);
			name = (TextView) view.findViewById(R.id.tv_name);
			time = (TextView) view.findViewById(R.id.tv_time);
			content = (TweetTextView) view.findViewById(R.id.tv_content);
		}
	}
}
