package net.oschina.app.v2.activity.software.adapter;

import net.oschina.app.v2.base.ListBaseAdapter;
import net.oschina.app.v2.model.SoftwareList.Software;
import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tonlin.osc.happy.R;

public class SoftwareAdapter extends ListBaseAdapter {

	@SuppressLint("InflateParams")
	@Override
	protected View getRealView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh = null;
		if (convertView == null || convertView.getTag() == null) {
			convertView = getLayoutInflater(parent.getContext()).inflate(
					R.layout.v2_list_cell_software, null);
			vh = new ViewHolder(convertView);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}

		Software item = (Software) _data.get(position);
		vh.name.setText(item.name);
		vh.desc.setText(item.description);
		return convertView;
	}

	static class ViewHolder {
		public TextView name, desc;

		public ViewHolder(View view) {
			name = (TextView) view.findViewById(R.id.tv_name);
			desc = (TextView) view.findViewById(R.id.tv_desc);
		}
	}
}
