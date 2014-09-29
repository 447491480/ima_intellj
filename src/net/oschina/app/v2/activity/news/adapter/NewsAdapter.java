package net.oschina.app.v2.activity.news.adapter;

import net.oschina.app.v2.base.ListBaseAdapter;
import net.oschina.app.v2.model.News;
import net.oschina.app.v2.utils.DateUtil;
import net.oschina.app.v2.utils.StringUtils;
import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tonlin.osc.happy.R;

public class NewsAdapter extends ListBaseAdapter {

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

		News news = (News) _data.get(position);

		vh.title.setText(news.getTitle());
		vh.source.setText(news.getAuthor());
		vh.time.setText(DateUtil.getFormatTime(news.getPubDate()));
		//StringUtils.friendly_time(news.getPubDate())
		if(StringUtils.isToday(news.getPubDate())){
			vh.tip.setVisibility(View.VISIBLE);
			vh.tip.setImageResource(R.drawable.ic_today);
		} else {
			vh.tip.setVisibility(View.GONE);
		}
		vh.commentCount.setText(parent.getResources().getString(R.string.comment_count, news.getCommentCount()));
		//Drawable d = parent.getContext().getResources().getDrawable(R.drawable.ic_comment_count);
		//vh.commentCount.setCompoundDrawablesWithIntrinsicBounds(d, null, null, null);
		return convertView;
	}

	static class ViewHolder {
		public TextView title, source, time,commentCount;
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
