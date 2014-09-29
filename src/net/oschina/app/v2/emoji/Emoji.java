package net.oschina.app.v2.emoji;

public class Emoji implements Comparable<Emoji> {
	public final static String EMOJI_PREFIX = "[";
	public final static String EMOJI_SUFFIX = "]";

	private int resId;
	private String value;
	private int index;
	private String value2;
	private String valueNo;

	public Emoji() {
	}

	public Emoji(int resId, String value, String valueNo, int index) {
		this.resId = resId;
		this.value = value;
		this.valueNo = valueNo;
		this.setIndex(index);
	}

	public Emoji(int resId, String value,String valueNo) {
		this.resId = resId;
		this.value = value;
		this.valueNo = valueNo;
	}

	public int getResId() {
		return resId;
	}

	public void setResId(int resId) {
		this.resId = resId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	@Override
	public int compareTo(Emoji another) {
		if (index > another.getIndex()) {
			return 1;
		} else if (index == another.getIndex())
			return 0;
		else
			return -1;
	}

	public String getValue2() {
		if (value2 == null) {
			value2 = new StringBuffer(EMOJI_PREFIX).append(valueNo)
					.append(EMOJI_SUFFIX).toString();
		}
		return value2;
	}

	public String getValueNo() {
		return valueNo;
	}

	public void setValueNo(String valueNo) {
		this.valueNo = valueNo;
	}
}
