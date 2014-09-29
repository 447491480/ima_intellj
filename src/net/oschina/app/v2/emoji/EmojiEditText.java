package net.oschina.app.v2.emoji;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.text.Spannable;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

public class EmojiEditText extends EditText {

	public static final Pattern EMOJI = Pattern
			.compile("\\[(([\u4e00-\u9fa5]+)|([a-zA-z]+))\\]");
	public static final Pattern EMOJI_PATTERN = Pattern
			.compile("\\[[(0-9)]+\\]");

	public EmojiEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public EmojiEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public EmojiEditText(Context context) {
		super(context);
	}

	@Override
	protected void onTextChanged(CharSequence text, int start,
			int lengthBefore, int lengthAfter) {
		super.onTextChanged(text, start, lengthBefore, lengthAfter);
		Spannable sp = getText();
		String str = getText().toString();
		Matcher m = EMOJI_PATTERN.matcher(str);
		while (m.find()) {
			int s = m.start();
			int e = m.end();
			String value = m.group();
			Emoji emoji = EmojiHelper.getEmojiByNumber(value);
			if (emoji != null) {
				sp.setSpan(new EmojiSpan(value, 30, 1), s, e,
						Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}

		Matcher m2 = EMOJI.matcher(str);
		while (m2.find()) {
			int s = m2.start();
			int e = m2.end();
			String value = m2.group();
			Emoji emoji = EmojiHelper.getEmoji(value);
			if (emoji != null) {
				sp.setSpan(new EmojiSpan(value, 30, 0), s, e,
						Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}
	}

	public void insertEmoji(Emoji emoji) {
		if (emoji == null)
			return;
		int start = getSelectionStart();
		int end = getSelectionEnd();
		String value = emoji.getValue2();
		if (start < 0) {
			append(value);
		} else {
			getText().replace(Math.min(start, end), Math.max(start, end),
					value, 0, value.length());
		}
	}

	public void delete() {
		KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0,
				0, KeyEvent.KEYCODE_ENDCALL);
		dispatchKeyEvent(event);
	}
}
