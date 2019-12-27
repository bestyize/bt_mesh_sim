package com.yize.tool;

import java.util.Calendar;
import java.util.Date;

public class TimerHelper {
    public static String getTimeStamp(){
        Date date = new Date(); // 获取当前的系统时间
        StringBuilder sb=new StringBuilder();

        Calendar calendar = Calendar.getInstance(); //获取当前的系统时间。
        return calendar.get(Calendar.YEAR)+"年"+ (calendar.get(Calendar.MONTH)+1)+"月"+calendar.get(Calendar.DATE)+"日："+calendar.get(Calendar.HOUR_OF_DAY)+"时"+calendar.get(Calendar.MINUTE)+"分"+calendar.get(Calendar.SECOND)+"秒";

    }
    public static String getTimeStampToHour(){
        Date date = new Date(); // 获取当前的系统时间
        StringBuilder sb=new StringBuilder();

        Calendar calendar = Calendar.getInstance(); //获取当前的系统时间。
        return calendar.get(Calendar.YEAR)+"年"+ (calendar.get(Calendar.MONTH)+1)+"月"+calendar.get(Calendar.DATE)+"日："+calendar.get(Calendar.HOUR_OF_DAY)+"时";

    }
}
