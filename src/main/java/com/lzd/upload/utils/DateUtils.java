package com.lzd.upload.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang3.time.DateFormatUtils;

/**
 * 时间工具类
 * @author lzd
 * @date 2019年4月3日
 * @version
 */
public final class DateUtils extends org.apache.commons.lang3.time.DateUtils {
	/**
	 * 日期格式 yyyy-MM-dd
	 */
	public static final String DATE_FULL = "yyyy-MM-dd";
	/**
	 * 时间格式 yyyy-MM-dd HH:mm:ss
	 */
	public static final String DATETIME_FULL = "yyyy-MM-dd HH:mm:ss";
	/**
	 * 日期格式（无连接符）yyyyMMdd
	 */
	public static final String DATE_SHORT = "yyyyMMdd";
	/**
	 * 时间格式（无连接符） yyyyMMddHHmmss
	 */
	public static final String DATETIME_SHORT = "yyyyMMddHHmmss";
	/**
	 * 时间格式（无连接符） yyyyMMddHH
	 */
	public static final String DATETIME_HOUR = "yyyyMMddHH";
	/**
	 * 时间格式（无连接符）HHmmss
	 */
	public static final String TIME_SHORT = "HHmmss";
	/**
	 * 时间格式 HH:mm:ss
	 */
	public static final String TIME_FULL = "HH:mm:ss";
	/**
	 * 时间格式 yyyy-MM-dd HH:mm:ss.SSS
	 */
	public static final String DATETIME_FULL_S = "yyyy-MM-dd HH:mm:ss.SSS";

	public static final int ONE_DAY = 1;
	
	public static final int ONE_YEAR = 1;

	private static String[] parsePatterns ={"yyyy-MM-dd HH:mm:ss","yyyy/MM/dd HH:mm:ss","yyyyMMddHHmmss","yyyy.MM.dd HH:mm:ss","yyyy-MM-dd HH:mm:ss.S",
											"yyyy-MM-dd HH:mm","yyyy/MM/dd HH:mm","yyyy.MM.dd HH:mm",
											"yyyy-MM-dd","yyyy/MM/dd","yyyy.MM.dd","yyyyMMddHHmm","yyyyMMdd",
											"yyyy-MM","yyyy/MM","yyyy.MM","yyyyMM"};
	
	/**
	 * 获取当前的发稿时间
	 * 
	 * @return
	 */
	public static String getReportData() {
		Calendar now = Calendar.getInstance();
		Integer month = now.get(Calendar.MONTH) + 1;
		Integer today = now.get(Calendar.DATE);
		return month + "月" + today + "日";
	}

	// 获取系统时间
	public static String toString(String pattern) {
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		format.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
		return format.format(new Date());

	}

	// 根据参数格式显示格式化日期
	public static String toString(Date date, String pattern) {
		return new SimpleDateFormat(pattern).format(date);
	}

	public static Date toDate(String source, String pattern){
		try {
			return new SimpleDateFormat(pattern).parse(source);
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}

	/**
	 * 获取最近的一个季度
	 * 
	 * @return
	 */
	@SuppressWarnings("unused")
	public static String getLastQuarter2() {
		Calendar now = Calendar.getInstance();
		Integer year = now.get(Calendar.YEAR);
		Integer month = now.get(Calendar.MONTH) + 1;
		Integer today = now.get(Calendar.DATE);
		StringBuilder lastQuarter = new StringBuilder();
		switch (month) {
		case 1:
		case 2:
		case 3:
			lastQuarter.append(year - 1);
			lastQuarter.append("-12-31");
			break;
		case 4:
		case 5:
		case 6:
			lastQuarter.append(year);
			lastQuarter.append("-03-31");
			break;
		case 7:
		case 8:
		case 9:
			lastQuarter.append(year);
			lastQuarter.append("-06-30");
			break;
		case 10:
		case 11:
		case 12:
			lastQuarter.append(year);
			lastQuarter.append("-09-30");
			break;
		default:
			break;
		}
		return lastQuarter.toString();
	}

	/**
	 * 获取当前时间的上一个季度日期
	 * @author lzd
	 * @date 2019年5月29日:上午10:12:59
	 * @return
	 * @description
	 */
	public static String getLastQuarter(){
        return getLastQuarter(null);
	}
	
	/**
	 * 获取目标时间的上一个季度日期
	 * @author lzd
	 * @date 2019年12月25日:上午11:53:27
	 * @param targetTime
	 * @return
	 * @description
	 */
	public static String getLastQuarter(String targetTime){
		try {
			Calendar c = Calendar.getInstance();
			if(StringUtils.isNotNull(targetTime)){
				Date target = parseDate(targetTime, "yyyy-MM-dd");
				c.setTime(target);
			}
			int m = c.get(Calendar.MONTH) + 1;            //月份
		    int s = (int)Math.ceil( (m - 1)  / 3) + 1;    //季度
		    int em = (s - 1) * 3;                         //上一个季度最后一个月
		    Calendar sc = Calendar.getInstance();
	        sc.setTimeInMillis(c.getTimeInMillis());
	        sc.set(Calendar.MONTH, em);
	        sc.set(Calendar.DAY_OF_MONTH, 1);
	        sc.set(Calendar.HOUR_OF_DAY, 0);
	        sc.set(Calendar.MINUTE, 0);
	        sc.set(Calendar.SECOND, 0);
	        sc.set(Calendar.MILLISECOND, 0);
	        sc.add(Calendar.MILLISECOND, -1);
	        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	        String lastQuarter = sdf.format(sc.getTime());
	        return lastQuarter;
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}

	
	/**
	 * 根据周岁和系统当前日期，推算出出生年份 Function : getYear<br/>
	 *
	 * @param age
	 * @return
	 * @throws @since
	 *             JDK 1.7
	 */
	public static int getYear(int age) throws Exception {
		Calendar c3 = Calendar.getInstance();
		c3.set(Calendar.YEAR, 1970 + age);
		Calendar c1 = Calendar.getInstance();
		long nowmillSeconds = c1.getTimeInMillis();
		long millis = nowmillSeconds - c3.getTimeInMillis();
		Calendar c2 = Calendar.getInstance();
		c2.setTimeInMillis(millis);
		Date date = c2.getTime();

		Calendar c4 = Calendar.getInstance();
		c4.setTime(date);
		return c4.get(Calendar.YEAR);
	}

	/**
	 * Function : 获取下一天<br/>
	 *
	 * @param date
	 * @return
	 * @since JDK 1.7
	 */
	public static Date getNextDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, ONE_DAY);
		return calendar.getTime();
	}

	/**
	 * Function : 获取前一天<br/>
	 *
	 * @param date
	 * @return
	 * @since JDK 1.7
	 */
	public static Date getBeforeDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, -ONE_DAY);
		return calendar.getTime();
	}
	
	/**
	 * 获取目标时间的（前or后）day天format格式的日期
	 * day是负数为前，day是正数为后
	 * @author lzd
	 * @date 2019年12月10日:下午2:23:38
	 * @param targetTime
	 * @param day
	 * @param format
	 * @return
	 * @description
	 */
	public static String getTargetDay(String targetTime,int day,String format){
		try {
			if(StringUtils.isNull(targetTime)){
				return targetTime;
			}
			Calendar calendar = Calendar.getInstance();
			Date target = parseDate(targetTime);
			calendar.setTime(target);
			calendar.add(Calendar.DATE, day);
			return parseDateToStr(format,calendar.getTime());
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}
	
	/**
	 * 获取下一年日期
	 * @author lzd
	 * @date 2019年4月9日:下午4:48:54
	 * @param date
	 * @return
	 */
	public static Date getNextYear(Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.YEAR, ONE_YEAR);
		return calendar.getTime();
	}
	
	/**
	 * 获取上一年日期
	 * @author lzd
	 * @date 2019年4月9日:下午4:49:29
	 * @param date
	 * @return
	 */
	public static Date getBeforeYear(Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.YEAR, -ONE_YEAR);
		return calendar.getTime();
	}
	
	
	/**
	 * 获取上一年日期
	 * @author lzd
	 * @date 2019年4月9日:下午4:49:29
	 * @param date
	 * @return
	 */
	public static Date getBeforeYear(String date){
		if (date == null || "".equals(date)) {
			return null;
		}
		try {
			Date time =parseDate(date, parsePatterns);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(time);
			calendar.add(Calendar.YEAR, -ONE_YEAR);
			return calendar.getTime();
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
		
	}
	
	/**
	 * 获取上几年日期
	 * @author lzd
	 * @date 2019年4月9日:下午4:49:29
	 * @param date
	 * @return
	 */
	public static Date getBeforeNumYear(String date,int year){
		if (date == null || "".equals(date)|| "null".equals(date)) {
			return null;
		}
		try {
			Date time =parseDate(date, parsePatterns);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(time);
			calendar.add(Calendar.YEAR, -year);
			return calendar.getTime();
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}
	
	/**
	 * 获取上几年日期转格式字符
	 * @author lzd
	 * @date 2019年4月9日:下午4:49:29
	 * @param date
	 * @return
	 */
	public static String getBeforeNumYear(String date,int year,String pattern){
		if (date == null || "".equals(date)|| "null".equals(date)) {
			return null;
		}
		try {
			Date beforeNumYear = getBeforeNumYear(date, year);
			String beforeYearStr = parseDateToStr(pattern, beforeNumYear);
			return beforeYearStr;
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}
	
	/**
	 * 获取上一年日期转格式字符
	 * @author lzd
	 * @date 2019年4月9日:下午4:49:29
	 * @param date
	 * @return
	 */
	public static String getBeforeYearToStr(String date,String pattern){
		if (date == null || "".equals(date)) {
			return null;
		}
		try {
			Date time = toDate(date, pattern);
			Date beforeYear = getBeforeYear(time);
			String beforeYearStr = parseDateToStr(pattern, beforeYear);
			return beforeYearStr;
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
		
	}
	
	
	/**
	 * 比较两个日期
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static int compareDate(Date date1, Date date2) {
		try {
			if (date1.getTime() > date2.getTime()) {
				System.out.println("dt1 在dt2前");
				return 1;
			} else if (date1.getTime() < date2.getTime()) {
				System.out.println("dt1在dt2后");
				return -1;
			} else {
				return 0;
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return 0;
	}

	/**
	 * 检查今天是不是周六日一
	 * 
	 * @return
	 */
	public static Date checkSSMDay(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
			cal.add(Calendar.DATE, -1);
		} else if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
			cal.add(Calendar.DATE, -2);
		} else if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
			cal.add(Calendar.DATE, -3);
		} else {
			return DateUtils.getBeforeDay(date);
		}
		return cal.getTime();
	}

	public static int differentDaysByMillisecond(Date date1, Date date2) {
		int days = (int) ((date2.getTime() - date1.getTime()) / (1000 * 3600 * 24));
		return days;
	}

	/**
	 * 获得累计开市时间
	 * 
	 * @return 累计开市时间
	 */
	@SuppressWarnings("deprecation")
	public static String getTotalOpenTime() {
		String totalTime = "";
		Date date = new Date();
		date.setHours(14);
		SimpleDateFormat hhmm = new SimpleDateFormat("HHmm");
		Integer nowHM = Integer.parseInt(hhmm.format(date));
		if (nowHM < 930) {
			totalTime = "240";
		} else if (nowHM > 930 && nowHM < 1130) {
			Calendar c = Calendar.getInstance();
			c.set(Calendar.HOUR_OF_DAY, 9);
			c.set(Calendar.MINUTE, 30);
			c.set(Calendar.SECOND, 0);
			long firstMinusSecond = date.getTime() - c.getTime().getTime();
			totalTime = (firstMinusSecond / 1000 / 60) + "";
		} else if (nowHM > 1130 && nowHM < 1530) {
			Calendar c = Calendar.getInstance();
			c.set(Calendar.HOUR_OF_DAY, 11);
			c.set(Calendar.MINUTE, 30);
			c.set(Calendar.SECOND, 0);
			long firstMinusSecond = date.getTime() - c.getTime().getTime();
			totalTime = (firstMinusSecond / 1000 / 60) + "";
		} else if (nowHM > 1530) {
			totalTime = "240";
		}
		return totalTime;
	}

	/**
	 * 获取当前Date型日期
	 * 
	 * @return Date() 当前日期
	 */
	public static Date getNowDate() {
		return new Date();
	}

	/**
	 * 获取当前日期, 默认格式为yyyy-MM-dd
	 * 
	 * @return String
	 */
	public static String getDate() {
		return dateTimeNow(DATE_FULL);
	}

	public static final String getTime() {
		return dateTimeNow(DATETIME_FULL_S);
	}

	public static final String dateTimeNow() {
		return dateTimeNow(DATETIME_SHORT);
	}

	public static final String dateTimeNow(final String format) {
		return parseDateToStr(format, new Date());
	}

	public static final String dateTime(final Date date) {
		return parseDateToStr(DATE_FULL, date);
	}

	public static final String parseDateToStr(final String format, final Date date) {
		return new SimpleDateFormat(format).format(date);
	}

	public static final Date dateTime(final String format, final String ts) {
		try {
			return new SimpleDateFormat(format).parse(ts);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 日期路径 即年/月/日 如2018/08/08
	 */
	public static final String datePath() {
		Date now = new Date();
		return DateFormatUtils.format(now, "yyyy/MM/dd");
	}

	/**
	 * 日期路径 即年/月/日 如20180808
	 */
	public static final String dateTime() {
		Date now = new Date();
		return DateFormatUtils.format(now, "yyyyMMdd");
	}
	
	/**
	 * 输入格式获取当前系统时间
	 * @author lzd
	 * @date 2019年12月24日:下午5:55:06
	 * @param pattern
	 * @return
	 * @description
	 */
	public static final String getNowTime(String pattern){
		try {
			Date now = new Date();
			return DateFormatUtils.format(now, pattern);
		} catch (Exception e) {
			// TODO: handle exception
			return "";
		}
	}
	
	/**
	 * 日期型字符串转化为日期 格式
	 */
	public static Date parseDate(String time) {
		if (time == null) {
			return null;
		}
		try {
			return parseDate(time, parsePatterns);
		} catch (Exception e) {
			return null;
		}
	}
	
	public static Date parsesStrToDate(String time) {
		if (time == null) {
			return null;
		}
		try {
			return parseDate(time, parsePatterns);
		} catch (Exception e) {
			return null;
		}
	}
	
	
	/**
	 * 日期 格式转化为日期型字符串
	 */
	public static String parseDateToStrByPattern(String pattern,Date time) {
		if (time == null) {
			return null;
		}
		try {
			return parseDateToStr(pattern,time);
		} catch (Exception e) {
			return null;
		}
		
	}
	
	/**
	 * 日期字符串规范化
	 * 例如2018-9-3===》2018-09-03
	 */
	public static String formatDateStr(String pattern,String timeStr) {
		if (timeStr == null || "".equals(timeStr)) {
			return null;
		}
		try {
			Date time=DateUtils.parseDate(timeStr);
			return parseDateToStr(pattern,time);
		} catch (Exception e) {
			return null;
		}
		
	}
	
	/**
	 * 获取目标时间字符的上一个月的时间字符
	 * @author lzd
	 * @date 2019年7月8日:下午3:44:31
	 * @param timeStr
	 * @param pattern
	 * @return
	 * @description
	 */
	public static String getLastMonthDateStr(String timeStr,String pattern){
		if (timeStr == null || "".equals(timeStr)) {
			return null;
		}
		try {
			Date time = toDate(timeStr, pattern);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(time);
			calendar.add(Calendar.MONTH, -1);
			return parseDateToStr(pattern, calendar.getTime());
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}
	
	/**
	 * 获取目标时间与当前系统时间的上一个季度日期相差的季度数
	 * @author lzd
	 * @date 2019年12月25日:下午12:44:25
	 * @param targetTime
	 * @return
	 * @description
	 */
	public static Integer getQuarterRange(String targetTime){
		try {
			if(StringUtils.isNull(targetTime)) return null;
			//获取当前系统时间的上一个季度日期
			String nowLastQuarterTime = getLastQuarter();
			if(StringUtils.isNull(nowLastQuarterTime)) return null;
			
			Calendar c1 = Calendar.getInstance();
			Date nowLastQuarterDate = parseDate(nowLastQuarterTime, "yyyy-MM-dd");
			c1.setTime(nowLastQuarterDate);
			Calendar c2 = Calendar.getInstance();
			Date targetDate = parseDate(targetTime, "yyyy-MM-dd");
			c2.setTime(targetDate);
			int year1 = c1.get(Calendar.YEAR);
			int year2 = c2.get(Calendar.YEAR);
			int month1 = c1.get(Calendar.MONTH);
			int month2 = c2.get(Calendar.MONTH);
			int day1 = c1.get(Calendar.DAY_OF_MONTH);
			int day2 = c2.get(Calendar.DAY_OF_MONTH);
			// 获取年的差值 
			int yearInterval = year1 - year2;
			// 如果 d1的 月-日 小于 d2的 月-日 那么 yearInterval-- 这样就得到了相差的年数
			if (month1 < month2 || month1 == month2 && day1 < day2) {
			   yearInterval--;
			}
			// 获取月数差值
			int monthInterval = (month1 + 12) - month2;
			if (day1 < day2) {
			   monthInterval--;
			}
			monthInterval %= 12;
			int monthsDiff = Math.abs(yearInterval * 12 + monthInterval); 
			return monthsDiff/3;
		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}
	}
}
