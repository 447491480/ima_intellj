package net.oschina.app.v2.activity.friend.adapter;

import net.oschina.app.v2.base.ListBaseAdapter;
import net.oschina.app.v2.model.FriendList.Friend;
import net.oschina.app.v2.utils.UIHelper;
import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.tonlin.osc.happy.R;

public class FriendAdapter extends ListBaseAdapter {

	@SuppressLint("InflateParams")
	@Override
	protected View getRealView(int position, View convertView,
			final ViewGroup parent) {
		ViewHolder vh = null;
		if (convertView == null || convertView.getTag() == null) {
			convertView = getLayoutInflater(parent.getContext()).inflate(
					R.layout.v2_list_cell_friend, null);
			vh = new ViewHolder(convertView);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}

		final Friend item = (Friend) _data.get(position);

		vh.name.setText(item.getName());
		vh.desc.setText(item.getExpertise());
		vh.gender.setImageResource(item.getGender() == 1 ? R.drawable.list_male
				: R.drawable.list_female);

		ImageLoader.getInstance().displayImage(item.getFace(), vh.avatar);
		vh.avatar.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				UIHelper.showUserCenter(parent.getContext(), item.getUserid(),
						item.getName());
			}
		});

		return convertView;
	}

	static class ViewHolder {
		public TextView name, desc;
		public ImageView gender;
		public ImageView avatar;

		public ViewHolder(View view) {
			name = (TextView) view.findViewById(R.id.tv_name);
			desc = (TextView) view.findViewById(R.id.tv_desc);
			gender = (ImageView) view.findViewById(R.id.iv_gender);
			avatar = (ImageView) view.findViewById(R.id.iv_avatar);
		}
	}
}
