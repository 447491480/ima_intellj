package net.oschina.app.v2.activity.favorite.adapter;

import net.oschina.app.v2.base.ListBaseAdapter;
import net.oschina.app.v2.model.FavoriteList.Favorite;
import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tonlin.osc.happy.R;

public class FavoriteAdapter extends ListBaseAdapter {

	@SuppressLint("InflateParams")
	@Override
	protected View getRealView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh = null;
		if (convertView == null || convertView.getTag() == null) {
			convertView = getLayoutInflater(parent.getContext()).inflate(
					R.layout.v2_list_cell_simple_text, null);
			vh = new ViewHolder(convertView);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}
		
		Favorite item = (Favorite) _data.get(position);
		
		vh.content.setText(item.title);
		
		return convertView;
	}

	static class ViewHolder {
		public TextView content;

		public ViewHolder(View view) {
			content = (TextView) view.findViewById(R.id.tv_content);
		}
	}
}
