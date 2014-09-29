package net.oschina.app.v2.activity.blog.adapter;

import net.oschina.app.v2.base.ListBaseAdapter;
import net.oschina.app.v2.model.Blog;
import net.oschina.app.v2.utils.DateUtil;
import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tonlin.osc.happy.R;

public class BlogAdapter extends ListBaseAdapter {

	@SuppressLint("InflateParams")
	@Override
	protected View getRealView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh = null;
		if (convertView == null || convertView.getTag() == null) {
			convertView = getLayoutInflater(parent.getContext()).inflate(
					R.layout.v2_list_cell_news, null);
			vh = new ViewHolder(convertView);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}

		Blog item = (Blog) _data.get(position);
		vh.title.setText(item.getTitle());
		vh.source.setText(item.getAuthor());
		vh.time.setText(DateUtil.getFormatTime(item.getPubDate()));

		vh.tip.setVisibility(View.VISIBLE);
		if (item.getDocumentType() == Blog.DOC_TYPE_ORIGINAL) {
			vh.tip.setImageResource(R.drawable.ic_source);
		} else {
			vh.tip.setImageResource(R.drawable.ic_forward);
		}
		vh.commentCount.setText(parent.getResources().getString(
				R.string.comment_count, item.getCommentCount()));
		return convertView;
	}

	static class ViewHolder {
		public TextView title, source, time, commentCount;
		public ImageView tip;

		public ViewHolder(View view) {
			title = (TextView) view.findViewById(R.id.tv_title);
			source = (TextView) view.findViewById(R.id.tv_source);
			time = (TextView) view.findViewById(R.id.tv_time);
			commentCount = (TextView) view.findViewById(R.id.tv_comment_count);
			tip = (ImageView) view.findViewById(R.id.iv_tip);
		}
	}
}
