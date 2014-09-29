package net.oschina.app.v2.emoji;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.tonlin.osc.happy.R;

public class EmojiAdatper extends BaseAdapter {

	private List<Emoji> mEmojis = new ArrayList<Emoji>();
	private int mEmojiHeight;

	public EmojiAdatper(List<Emoji> emojis, int emojiHeight) {
		mEmojis = emojis;
		mEmojiHeight = emojiHeight;
	}

	@Override
	public int getCount() {
		return mEmojis.size() + 1;
	}

	@Override
	public Object getItem(int position) {
		return mEmojis.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(parent.getContext()).inflate(
					R.layout.v2_list_cell_emoji_item, null);
			vh = new ViewHolder(convertView);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}

		if (position < getCount() - 1) {
			Emoji item = mEmojis.get(position);
			vh.icon.setBackgroundDrawable(null);
			vh.icon.setImageResource(item.getResId());
		} else {
			vh.icon.setImageBitmap(null);
			vh.icon.setBackgroundResource(R.drawable.btn_del_selector);
		}

		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT, mEmojiHeight);
		vh.icon.setLayoutParams(lp);

		return convertView;
	}

	static class ViewHolder {
		ImageView icon;

		ViewHolder(View view) {
			icon = (ImageView) view.findViewById(R.id.icon);
		}
	}
}