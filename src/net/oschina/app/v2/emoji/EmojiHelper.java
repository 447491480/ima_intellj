package net.oschina.app.v2.emoji;

import java.util.HashMap;
import java.util.Map;

import net.oschina.app.v2.AppContext;
import android.content.res.Resources;

import com.tonlin.osc.happy.R;

public class EmojiHelper {

	public static Map<String, Emoji> qq_emojis = new HashMap<String, Emoji>();
	public static Map<String, Emoji> qq_emojis_nos = new HashMap<String, Emoji>();

	public static Emoji getEmoji(String val) {
		return qq_emojis.get(val.substring(1, val.length() - 1));
	}

	public static Emoji getEmojiByNumber(String val) {
		return qq_emojis_nos.get(val.substring(1, val.length() - 1));
	}

	public static void initEmojis() {
		AppContext contenxt = AppContext.instance();
		Resources res = contenxt.getResources();
		String[] vals = res.getStringArray(R.array.qq_emoji_vals);
		for (int i = 0; i < 104; i++) {
			int id = res.getIdentifier("smiley_" + i, "drawable",
					contenxt.getPackageName());
			Emoji emoji = new Emoji(id, vals[i], i + "", i);
			qq_emojis.put(vals[i], emoji);
			qq_emojis_nos.put(i + "", emoji);
		}
	}
}
