package com.xunchijn.zblocation.util;

import android.support.annotation.NonNull;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtils {

    //获取当前时间的时间戳
    public static String getTimeStamp(){
        Date date = new Date(System.currentTimeMillis());
        return String.valueOf(date.getTime()/1000);
    }

    //字符串转时间戳(到分)
    public static String getTime(String timeString) {
        String timeStamp = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        Date d;
        try {
            d = sdf.parse(timeString);
            long l = d.getTime();
            timeStamp = String.valueOf(l);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeStamp;
    }

    //字符串转时间戳(到秒)
    public static String getSecondTime(String timeString) {
        String timeStamp = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        Date d;
        try {
            d = sdf.parse(timeString);
            long l = d.getTime();
            timeStamp = String.valueOf(l / 1000);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeStamp;
    }

    //时间戳转时间 到秒
    public static String getStrSecond(String timeStamp) {
        String timeString = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        long l = Long.valueOf(timeStamp) * 1000;
        timeString = sdf.format(new Date(l));//单位秒
        return timeString;
    }

    public static String getStrTime(String timeStamp) {
        String timeString = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        long l = Long.valueOf(timeStamp) * 1000;
        timeString = sdf.format(new Date(l));//单位秒
        return timeString;
    }

    //时间戳转字符串 到年月
    public static String getStrYear(String timeStamp) {
        String timeString = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月");
        long l = Long.valueOf(timeStamp) * 1000;
        timeString = sdf.format(new Date(l));//单位秒
        return timeString;
    }

    //时间戳转字符串截止到日期
    public static String getStrDate(String timeStamp) {
        String timeString = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        long l = Long.valueOf(timeStamp) * 1000;
        timeString = sdf.format(new Date(l));//单位秒
        return timeString;
    }

    //获得当天0点时间戳
    public static int getTimesmorning() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return (int) (cal.getTimeInMillis() / 1000);
    }

    //获得当天24点时间戳
    public static int getTimesnight() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 24);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return (int) (cal.getTimeInMillis() / 1000);
    }

    //MD5加密
    @NonNull
    public static String getCustomMD5(String info) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(info.getBytes("UTF-8"));
            byte[] encryption = md5.digest();

            StringBuffer strBuf = new StringBuffer();
            for (int i = 0; i < encryption.length; i++) {
                if (Integer.toHexString(0xff & encryption[i]).length() == 1) {
                    strBuf.append("0").append(Integer.toHexString(0xff & encryption[i]));
                } else {
                    strBuf.append(Integer.toHexString(0xff & encryption[i]));
                }
            }
            strBuf.replace(25, 26, "y");
            strBuf.replace(21, 22, "u");
            strBuf.replace(1, 2, "a");
            strBuf.replace(14, 15, "n");
            strBuf.replace(19, 20, "s");
            strBuf.replace(8, 9, "h");
            strBuf.replace(21, 22, "u");
            strBuf.replace(15, 16, "o");
            strBuf.replace(9, 10, "i");
            strBuf.replace(20, 21, "t");
            return strBuf.toString();
        } catch (NoSuchAlgorithmException e) {
            return "";
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }
}
