package net.oschina.app.v2.activity.question.adapter;

import net.oschina.app.v2.base.ListBaseAdapter;
import net.oschina.app.v2.model.Post;
import net.oschina.app.v2.ui.AvatarView;
import net.oschina.app.v2.utils.DateUtil;
import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tonlin.osc.happy.R;

public class QuestionAdapter extends ListBaseAdapter {

	@SuppressLint("InflateParams")
	@Override
	protected View getRealView(int position, View convertView,final ViewGroup parent) {
		ViewHolder vh = null;
		if (convertView == null || convertView.getTag() == null) {
			convertView = getLayoutInflater(parent.getContext()).inflate(
					R.layout.v2_list_cell_post, null);
			vh = new ViewHolder(convertView);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}

		final Post item = (Post) _data.get(position);

		vh.title.setText(item.getTitle());
		vh.source.setText(item.getAuthor());
		vh.avCount.setText(item.getAnswerCount()+"/"+item.getViewCount());
		vh.time.setText(DateUtil.getFormatTime(item.getPubDate()));
		
		vh.avatar.setUserInfo(item.getAuthorId(), item.getAuthor());
		vh.avatar.setAvatarUrl(item.getFace());
		
		return convertView;
	}

	static class ViewHolder {
		public TextView title, source,avCount, time;
		public AvatarView avatar;
		public ViewHolder(View view) {
			title = (TextView) view.findViewById(R.id.tv_title);
			source = (TextView) view.findViewById(R.id.tv_source);
			avCount = (TextView) view.findViewById(R.id.tv_answer_view_count);
			time = (TextView) view.findViewById(R.id.tv_time);
			avatar = (AvatarView)view.findViewById(R.id.iv_avatar);
		}
	}
}
