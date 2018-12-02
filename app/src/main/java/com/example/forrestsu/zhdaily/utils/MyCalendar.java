package com.example.forrestsu.zhdaily.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MyCalendar {

    private static Date date;

    //将Date转换为String
    public static String dateToStr(Date date, String dateFormat) {
        SimpleDateFormat sf;
        switch(dateFormat) {
            case "yyyy-MM-dd":
                sf = new SimpleDateFormat(dateFormat);
                return sf.format(date);
            case "yyyyMMdd":
                sf = new SimpleDateFormat(dateFormat);
                return sf.format(date);
            default:
                break;
        }
        return "";
    }

    /**
     * 将String转换为Date
     * @param str  日期，String类型
     * @param dateFormat  日期格式
     * @return Date对象
     */
    public static Date strToDate(String str, String dateFormat) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        switch (dateFormat) {
            case "yyyy-MM-dd":
                break;
            case "yyyy/MM/dd":
                sf = new SimpleDateFormat("yyyy/MM/dd");
                break;
            case "yyyyMMdd":
                sf = new SimpleDateFormat("yyyyMMdd");
                break;
            default:
                break;
        }

        try {
            //使用SimpleDateFormat的parse()方法生成Date
            date = sf.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    //获取当前日期
    public static Date getCurrentDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        return new Date(System.currentTimeMillis());
    }

    //获取当前年份(String)
    public static String getSysYearStr() {
        Calendar calendar = Calendar.getInstance();
        String year = String.valueOf(calendar.get(Calendar.YEAR));
        return year;
    }

    //获取当前年份(int)
    public static int getSysYearInt() {
        Calendar calendar = Calendar.getInstance();
        String year = String.valueOf(calendar.get(Calendar.YEAR));
        return Integer.parseInt(year);
    }

    //获取当前月份
    public static int getSysMonthInt() {
        Calendar calendar = Calendar.getInstance();
        String month = String.valueOf(calendar.get(Calendar.MONTH));
        return Integer.parseInt(month);
    }

    //获取当前日
    public static int getSysDayInt() {
        Calendar calendar = Calendar.getInstance();
        String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        return Integer.parseInt(day);
    }

    /**
     * 根据日期返回星期
     * @param date  日期
     * @return  星期几
     */
    public static String getDayOfWeek(Date date) {
        String[] weekDays = { "星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日" };

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int i = calendar.get(Calendar.DAY_OF_WEEK);
        return weekDays[i - 1];
    }

    //计算当前年龄
    public static int getCurrentAge(Date birthDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(birthDate);
        String year = String.valueOf(calendar.get(Calendar.YEAR));
        String month = String.valueOf(calendar.get(Calendar.MONTH));
        String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));

        int birthYear = Integer.parseInt(year);
        int birthMonth = Integer.parseInt(month);
        int birthDay = Integer.parseInt(day);

        int age = 0;
        if ((getSysMonthInt() > birthMonth)
                || ((getSysMonthInt() == birthMonth) && (getSysDayInt() >= birthDay))) {
            age = getSysYearInt() - birthYear;
        }else {
            age = getSysYearInt() - birthYear - 1;
        }
        return age;
    }

    /**
     * 计算date1和date2之间相差的天数
     * date2 - date1
     * 注意：返回值类型为int，不能计算相差过大的天数
     */
    public static int differenceOfDays(Date date1, Date date2)
    {
        int differenceOfDays = (int) ((date2.getTime() - date1.getTime()) / (1000*3600*24));
        return differenceOfDays;
    }

}
