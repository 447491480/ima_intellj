package net.oschina.app.v2.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.joda.time.DateTime;

public class DateUtil {
	private final static String PATTERN = "yyyy-MM-dd HH:mm:ss";
	private final static String DEF_FORMAT = "yyyy-MM-dd HH:mm";
	private final static String YEAR_MONTH_DAY = "yyyy-MM-dd";
	private final static String YESTODAY = "昨天";
	private final static ThreadLocal<SimpleDateFormat> dateFormater = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}
	};

	public static String getNow(String format) {
		if (null == format || "".equals(format)) {
			format = PATTERN;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		String date = sdf.format(new Date());
		return date;
	}

	public static boolean isToday(long time) {
		if (getNow(YEAR_MONTH_DAY).equals(getDateStr(time, YEAR_MONTH_DAY))) {
			return true;
		}
		return false;
	}

	public static Date stringToDate(String _date, String format) {
		if (null == format || "".equals(format)) {
			format = PATTERN;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Date date = null;
		try {
			date = sdf.parse(_date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	public static String dateToString(Date date, String format) {
		if (null == format || "".equals(format)) {
			format = PATTERN;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format);

		return sdf.format(date);
	}

	public static String dateToString(Date date, String format, Locale locale) {
		if (null == format || "".equals(format)) {
			format = PATTERN;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format, locale);

		return sdf.format(date);
	}

	public static Date intToDate(long lDate) {
		Date date = new Date(lDate);
		return date;
	}

	public static Date cstToDate(String dateStr) {
		DateFormat df = new SimpleDateFormat(
				"EEE MMM dd HH:mm:ss '+0800' yyyy", Locale.CHINA);// CST格式
		Date date = null;
		try {
			date = df.parse(dateStr);// parse函数进行转换
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date;
	}

	public static String getDateStr(long times, String format) {
		if (times == 0)
			return "";
		Date date = intToDate(times);
		return dateToString(date, format);
	}

	public static Date getDate(int year, int month, int weekInMonth,
			int dayInWeek) {
		Calendar date = Calendar.getInstance();
		date.clear();
		date.set(Calendar.YEAR, year);
		date.set(Calendar.MONTH, month - 1);
		date.set(Calendar.DAY_OF_WEEK_IN_MONTH, weekInMonth);
		date.set(Calendar.DAY_OF_WEEK, dayInWeek + 1);
		return date.getTime();
	}

	public static Date getDate(int month, int weekInMonth, int dayInWeek) {
		Calendar date = Calendar.getInstance();
		int year = date.get(Calendar.YEAR);
		date.clear();
		date.set(Calendar.YEAR, year);
		date.set(Calendar.MONTH, month - 1);
		date.set(Calendar.DAY_OF_WEEK_IN_MONTH, weekInMonth);
		date.set(Calendar.DAY_OF_WEEK, dayInWeek + 1);
		return date.getTime();
	}

	public static String getDate(int month, int weekInMonth, int dayInWeek,
			String format) {
		Date date = getDate(month, weekInMonth, dayInWeek);
		return getDateStr(date.getTime(), format);
	}

	public final static long ONE_MINUTE = 1000 * 60;
	public final static long ONE_HOUR = ONE_MINUTE * 60;
	public final static long ONE_DAY = ONE_HOUR * 24;

	public static String getFormatTime(String sdate) {
		Date time = null;
		//if (TimeZoneUtil.isInEasternEightZones()) {
			time = toDate(sdate);
		//} else {
		//	time = TimeZoneUtil.transformTime(toDate(sdate),
		//			TimeZone.getTimeZone("GMT+08"), TimeZone.getDefault());
		//}
		return getFormatTime(time.getTime());
	}

	private static Date toDate(String sdate) {
		try {
			return dateFormater.get().parse(sdate);
		} catch (ParseException e) {
			return new Date();
		}
	}

	public static String getFormatTime(long time) {
		long current = System.currentTimeMillis();
		long inteval = current - time;
		if (isToday(time)) {
			if (inteval < ONE_MINUTE) {
				return "刚刚";
			} else if (inteval < ONE_HOUR) {
				return inteval / ONE_MINUTE + "分钟前";
			} else if (inteval < ONE_DAY) {
				return inteval / ONE_HOUR + "小时前";
			}
			return "";
		} else {
			if (isTheSameYear(time)) {
				if (isYestoday(time)) {
					return YESTODAY;
				}
				return getDateStr(time, "MM/dd");
			} else {
				return getDateStr(time, "yyyy/MM/dd");
			}
		}
	}

	private static boolean isYestoday(long time) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(time);
		cal.add(Calendar.DAY_OF_YEAR, 1);
		return isToday(cal.getTimeInMillis());
	}

	private static boolean isTheSameYear(long time) {
		DateTime dt = new DateTime(time);
		DateTime now = new DateTime();
		return dt.getYear() == now.getYear();
	}

	public static String getIntervalHours(long begin, long end) {
		if (end < begin)
			return "";
		long between = (end - begin) / 1000;// 除以1000是为了转换成秒
		long day = between / (24 * 3600);
		long hour = between % (24 * 3600) / 3600;
		long minute = between % 3600 / 60;
		long second = between % 60 / 60;
		return (day == 0 ? "" : day + "天 ") + (hour == 0 ? "" : hour + "小时 ")
				+ (minute == 0 ? "" : minute + "分 ")
				+ (second == 0 ? "" : second + "秒");
	}

	public static long getHours(long finishTime, long endTime) {
		long time = finishTime - endTime;
		if (time > 0) {
			return time / (3600 * 1000);
		}
		return 0;
	}

	public static boolean isSameTimeWithoutSecond(long time, long time2) {
		return getDateStr(time, DEF_FORMAT).equals(
				getDateStr(time2, DEF_FORMAT));
	}

	public static long getDurationMinutes(long begin, long end) {
		if (end < begin)
			return 0;
		long between = (end - begin) / 1000;// 除以1000是为了转换成秒
		long minutes = between / 60;
		return minutes;
	}

	public static String getDurationMinutes(long minutes) {
		if (minutes <= 0)
			return "";
		long day = minutes / (24 * 60);
		long hour = minutes % (24 * 60) / 60;
		long minute = minutes % 60;
		StringBuilder buf = new StringBuilder();
		buf.append((day == 0 ? "" : day + "天 "));
		buf.append((hour == 0 ? "" : hour + "小时 "));
		buf.append((minute == 0 ? "" : minute + "分 "));
		return buf.toString();
	}

	public static String getDailyDateStr(long publishTime) {
		if (isToday(publishTime)) {
			return "今日之最";
		} else {
			return getDateStr(publishTime, "MM月dd日");
		}
	}
}
