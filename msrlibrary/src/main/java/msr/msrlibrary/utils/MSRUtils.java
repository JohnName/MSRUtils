package msr.msrlibrary.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.provider.Settings.Secure;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 通过方法
 */
@SuppressLint("SimpleDateFormat")
public class MSRUtils {

    /**
     * 获取CPU核心数
     *
     * @return
     */
    public static int getCPUcores() {
        // Private Class to display only CPU devices in the directory listing
        class CpuFilter implements FileFilter {
            @Override
            public boolean accept(File pathname) {
                // Check if filename is "cpu", followed by a single digit number
                if (Pattern.matches("cpu[0-9]", pathname.getName())) {
                    return true;
                }
                return false;
            }
        }
        try {
            // Get directory containing CPU info
            File dir = new File("/sys/devices/system/cpu/");
            // Filter to only list the devices we care about
            File[] files = dir.listFiles(new CpuFilter());
            // Return the number of cores (virtual CPU devices)
            return files.length;
        } catch (Exception e) {
            // Default to return 1 core
            return 1;
        }
    }

    /**
     * 将一个inputstream读入，以Byte数组的形式返回
     *
     * @param in
     * @return
     */
    public static byte[] readInputStream(InputStream in) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        byte[] readByte = new byte[1024];
        int readCount = -1;

        try {
            while ((readCount = in.read(readByte, 0, 1024)) != -1) {
                baos.write(readByte, 0, readCount);
            }
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return baos.toByteArray();
    }

    /**
     * 将一个inputstream，以字符串的形式返回
     *
     * @param in
     * @return
     */
    public static String readInputStreamToString(InputStream in) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        byte[] readByte = new byte[1024];
        int readCount = -1;

        try {
            while ((readCount = in.read(readByte, 0, 1024)) != -1) {
                baos.write(readByte, 0, readCount);
            }
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new String(baos.toByteArray());
    }

    /**
     * 将输入的dp转换为对应的px值，并返回px值
     *
     * @param dp
     * @param c
     * @return
     */
    public static int dp2px(int dp, Context c) {

        return (int) (dp * c.getResources().getDisplayMetrics().density + 0.5f);
    }

    /**
     * 获取DP数量
     *
     * @param c
     * @return
     */
    public static int getDensity(Context c) {
        return (int) c.getResources().getDisplayMetrics().density;
    }

    /**
     * 半角转全角
     *
     * @param input
     * @return
     */
    public static String ToSBC(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 32) {
                c[i] = (char) 12288;
                continue;
            }
            if (c[i] < 127)
                c[i] = (char) (c[i] + 65248);
        }
        return new String(c);
    }

    /**
     * 全角转半角的函数(DBC case)
     *
     * @param input
     * @return
     */
    public static String ToDBC(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }
            if (c[i] > 65280 && c[i] < 65375)
                c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }

    /**
     * 去除特殊字符或将所有中文标号替换为英文标号
     *
     * @param str
     * @return
     */
    public static String stringFilter(String str) {
        str = str.replaceAll("【", "[").replaceAll("】", "]")
                .replaceAll("！", "!").replaceAll("：", ":");// 替换中文标号
        String regEx = "[『』]"; // 清除掉特殊字符
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    /**
     * 特殊符号验证
     *
     * @param str
     * @return
     */
    public static boolean chartFilter(String str) {
        String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.find();
    }

    /**
     * 将一个时间戳转换成提示性时间字符串，如刚刚，1秒前
     *
     * @param tms
     * @return
     */
    public static String convertTimeToFormat(long tms) {

        long curTime = System.currentTimeMillis();

        long time = (curTime - tms) / (long) 1000;

        if (time < 60 && time >= 0) {
            return "刚刚";
        } else if (time >= 60 && time < 3600) {
            return time / 60 + "分钟前";
        } else if (time >= 3600 && time < 3600 * 24) {
            return time / 3600 + "小时前";
        } else if (time >= 3600 * 24 && time < 3600 * 24 * 30) {
            return time / 3600 / 24 + "天前";
        } else if (time >= 3600 * 24 * 30 && time < 3600 * 24 * 30 * 12) {
            return time / 3600 / 24 / 30 + "个月前";
        } else if (time >= 3600 * 24 * 30 * 12) {
            return time / 3600 / 24 / 30 / 12 + "年前";
        } else {
            return "刚刚";
        }

    }

    /**
     * 判断一个时间戳是否在当天
     *
     * @param time
     * @return
     */
    public static boolean isToday(long time) {
        Date startTime = new Date();
        Date endTime = new Date();

        startTime.setHours(0);
        startTime.setMinutes(0);
        startTime.setSeconds(0);

        endTime.setDate(endTime.getDate() + 1);
        endTime.setHours(0);
        endTime.setMinutes(0);
        endTime.setSeconds(0);
        return time >= startTime.getTime() && time < endTime.getTime();
    }

    /**
     * 如果在一天之内显示12:00,不在一天显示2月3日
     *
     * @param tms
     * @return
     */
    public static String convertNewTimeToFormat(long tms) {

        Date date = new Date(tms);
        String val = "";
        long curTime = System.currentTimeMillis();
        long time = (curTime - tms) / (long) 1000;
        // long time = (curTime - tms) / (long) 1000;
        try {
            if (isYeaterday(date, null) == -1) {// -1为当天 ，则显示时间
                val = convertLongToHHmm(tms);
            } else if (time <= 3600 * 24 * 30 * 12) {
                val = convertTime(tms, "MM-dd");
            } else {
                val = convertTime(tms, "yyyy-MM-dd ");
            }
            // if (time <= 3600 * 24) {
            // return convertTime(tms, "HH:mm:ss");
            // } else {
            // return convertTime(tms, "yyyy-MM-dd");
            // }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return val;
    }

    /**
     * 将一个时间戳转换成HH:mm格式
     *
     * @param tms
     * @return
     */
    public static String convertLongToHHmm(long tms) {
        Date date = new Date(tms);

        return date.getHours()
                + ":"
                + (date.getMinutes() < 10 ? "0" + date.getMinutes() : date
                .getMinutes());
    }

    /**
     * @param oldTime 较小的时间
     * @param newTime 较大的时间 (如果为空 默认当前时间 ,表示和当前时间相比)
     * @return -1 ：同一天. 0：昨天 . 1 ：至少是前天.
     * @throws ParseException 转换异常
     * @author LuoB.
     */
    public static int isYeaterday(Date oldTime, Date newTime)
            throws ParseException {
        if (newTime == null) {
            newTime = new Date();
        }
        // 将下面的 理解成 yyyy-MM-dd 00：00：00 更好理解点
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String todayStr = format.format(newTime);
        Date today = format.parse(todayStr);
        // 昨天 86400000=24*60*60*1000 一天
        if ((today.getTime() - oldTime.getTime()) > 0
                && (today.getTime() - oldTime.getTime()) <= 86400000) {
            return 0;
        } else if ((today.getTime() - oldTime.getTime()) <= 0) { // 至少是今天
            return -1;
        } else { // 至少是前天
            return 1;
        }

    }

    /**
     * 计算两个日期的差值
     *
     * @param startTime
     * @param endTime
     * @param format
     */
    public static long convertDateDiff(String startTime, String endTime,
                                       String format) {
        // 按照传入的格式生成一个simpledateformate对象
        SimpleDateFormat sd = new SimpleDateFormat(format);
        long nd = 1000 * 24 * 60 * 60;// 一天的毫秒数
        long nh = 1000 * 60 * 60;// 一小时的毫秒数
        long nm = 1000 * 60;// 一分钟的毫秒数
        long ns = 1000;// 一秒钟的毫秒数
        long diff;
        try {
            // 获得两个时间的毫秒时间差异
            diff = sd.parse(endTime).getTime() - sd.parse(startTime).getTime();
            long day = diff / nd;// 计算差多少天
            long hour = diff % nd / nh;// 计算差多少小时
            long min = diff % nd % nh / nm;// 计算差多少分钟
            long sec = diff % nd % nh % nm / ns;// 计算差多少秒
            return day;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 计算两个日期是否在minDiff分钟之内
     *
     * @param startTime
     * @param endTime
     * @param format
     * @param minDiff
     * @return
     */
    public static boolean convertDateDiff(String startTime, String endTime,
                                          String format, int minDiff) {
        // 按照传入的格式生成一个simpledateformate对象
        SimpleDateFormat sd = new SimpleDateFormat(format);
        long nd = 1000 * 24 * 60 * 60;// 一天的毫秒数
        long nh = 1000 * 60 * 60;// 一小时的毫秒数
        long nm = 1000 * 60;// 一分钟的毫秒数
        long diff;
        try {
            // 获得两个时间的毫秒时间差异
            diff = sd.parse(endTime).getTime() - sd.parse(startTime).getTime();
            long min = diff % nd % nh / nm;// 计算差多少分钟
            if (min < minDiff) {
                return true;
            } else {
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断传入的字符串是否是一个邮箱地址
     *
     * @param strEmail
     * @return
     */
    public static boolean isEmail(String strEmail) {
        Pattern p = Pattern
                .compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
        Matcher m = p.matcher(strEmail);
        return m.find();
    }

    /**
     * 判断传入的字符串是否是一个手机号码
     *
     * @param strPhone
     * @return
     */
    public static boolean isPhoneNumber(String strPhone) {
        String str = "^(13[0-9]|15[0-9]|18[0-9]|14[0-9]|17[0-9])\\d{8}$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(strPhone);
        return m.find();
    }

    /**
     * 将一个时间戳转化成时间字符串，如2010-12-12 23:24:33
     *
     * @param time
     * @return
     */
    public static String convertTime(long time) {
        if (time == 0) {
            return "";
        }

        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);

    }

    /**
     * 将一个时间字符串转换成时间戳
     *
     * @param time   1991-11-11 45:45:44
     * @param format yyyy-MM-dd HH:mm:ss
     * @return
     * @throws ParseException
     */
    public static long convertTime(String time, String format)
            throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        Date date = null;
        date = simpleDateFormat.parse(time);
        return date.getTime();
    }

    /**
     * 将一个时间戳转化成时间字符串，自定义格式
     *
     * @param time
     * @param format 如 yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String convertTime(long time, String format) {
        if (time == 0) {
            return "";
        }

        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    /**
     * 将一个时间戳转换格式,如2010-12-12 23:24:33
     *
     * @param time
     * @param timezoneOffset 时间偏移
     * @return
     */
    public static String convertTime(String time, int timezoneOffset) {
        double t = Double.parseDouble(time);
        long tt = (long) (t - timezoneOffset * 60 * 1000 + 480 * 60 * 1000);

        return convertTime(tt);
    }

    /**
     * 将一个时间戳转换为yyyy-MM-dd格式
     *
     * @param time
     * @param timezoneOffset 时间
     * @return
     */
    public static String convertTime_yyyymmdd(String time, int timezoneOffset) {
        double t = Double.parseDouble(time);
        long tt = (long) (t - timezoneOffset * 60 * 1000 + 480 * 60 * 1000);

        return convertTime(tt, "yyyy-MM-dd");
    }

    /**
     * 获取本地版本号 versionCode
     *
     * @param context
     * @return
     */
    public static int getLocalVersionCode(Context context) {
        PackageInfo info = null;
        try {
            info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return info.versionCode;
    }

    /**
     * 获取本地版本号名称 versionName
     *
     * @param context
     * @return
     */
    public static String getLocalVersionName(Context context) {
        PackageInfo info = null;
        try {
            info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return info.versionName;
    }

    /**
     * 执行Linux命令，并返回执行结果。
     */
    public static String exec(String[] args) {
        String result = "";
        ProcessBuilder processBuilder = new ProcessBuilder(args);
        Process process = null;
        InputStream errIs = null;
        InputStream inIs = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int read = -1;
            process = processBuilder.start();
            errIs = process.getErrorStream();
            while ((read = errIs.read()) != -1) {
                baos.write(read);
            }
            baos.write('\n');
            inIs = process.getInputStream();
            while ((read = inIs.read()) != -1) {
                baos.write(read);
            }
            byte[] data = baos.toByteArray();
            result = new String(data);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (errIs != null) {
                    errIs.close();
                }
                if (inIs != null) {
                    inIs.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (process != null) {
                process.destroy();
            }
        }
        return result;
    }

    /**
     * 获取当前的网络状态 -1：没有网络 1：WIFI网络 2：wap网络 3：net网络
     *
     * @param context
     * @return
     * @author panshihao
     */
    public static int getNetworkState(Context context) {
        int netType = -1;
        ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo == null) {
            return netType;
        }
        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_MOBILE) {
            if (networkInfo.getExtraInfo().toLowerCase().equals("cmnet")) {
                netType = 3;
            } else {
                netType = 2;
            }
        } else if (nType == ConnectivityManager.TYPE_WIFI) {
            netType = 1;
        }
        return netType;
    }

    /**
     * 将View转换为bitmap
     *
     * @param view
     * @return
     */
    public static Bitmap convertViewToBitmap(View view) {
        view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();

        return bitmap;
    }


    /**
     * 获取屏幕的分辨率key width,height
     */
    public static HashMap<String, Integer> getResolution(Context c) {
        HashMap<String, Integer> hashmap = new HashMap<String, Integer>();
        DisplayMetrics dm = c.getResources().getDisplayMetrics();
        hashmap.put("width", dm.widthPixels);
        hashmap.put("height", dm.heightPixels);
        return hashmap;
    }

    /**
     * 删除input字符串中的html格式
     *
     * @param input
     * @param length
     * @return
     */
    public static String splitAndFilterString(String input, int length) {
        if (input == null || input.trim().equals("")) {
            return "";
        }
        // 去掉所有html元素,
        String str = input.replaceAll("\\&[a-zA-Z]{1,10};", "").replaceAll(
                "<[^>]*>", "");
        str = str.replaceAll("[(/>)<]", "").replaceAll("[\\t\\n\\r]", "");
        int len = str.length();
        if (len <= length) {
            return str;
        } else {
            str = str.substring(0, length);
            str += "......";
        }
        return str;
    }

    /**
     * 获取设备屏幕的分辨率，返回px值
     *
     * @param context
     * @param flag    ,flag为0时返回宽度，为1时返回高度,为其他值时返回0值
     * @return
     */
    public static int getDisplayWidth_Height(Context context, int flag) {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay()
                .getMetrics(dm);
        if (flag == 0) {
            return dm.widthPixels;
        } else if (flag == 1) {
            return dm.heightPixels;
        }
        return 0;
    }

    /**
     * 将电话号码中间替换成“*”号
     *
     * @param phoneNumber
     * @return
     */
    public static String convertPhoneNumberStyle(String phoneNumber) {
        String mobile = "";
        if (phoneNumber != null && !phoneNumber.equals("")) {

            int between = phoneNumber.length() / 2;
            String newChar = "****";
            // 拼接最终值
            mobile = phoneNumber.substring(0, between - 2) + newChar
                    + phoneNumber.substring(between + 2, phoneNumber.length());
        }
        return mobile;
    }


    /**
     * 将银行卡信息中间变为星号
     * 前面显示四位数,后面显示三位数
     *
     * @return
     */
    public static String converBankNumber2Star(String cardno) {

        if (cardno == null || cardno.isEmpty()) {
            return "";
        }
        if (cardno.length() <= 7) {
            return cardno;
        } else {
            String first = cardno.substring(0, 4);
            String end = cardno.substring(cardno.length() - 3, cardno.length());
            StringBuffer sb = new StringBuffer();
            for (int i = 1; i < cardno.length() - 8; i++) {
                if (i % 4 == 0 && i != 0) {
                    sb.append("* ");
                } else {
                    sb.append("*");
                }
            }
            return first+" "+sb.toString()+" "+end;
        }
    }

    /**
     * 获取android手机设备的唯一标识
     */
    public static String getAndroidId(Context context) {
        String android_id = Secure.getString(context.getContentResolver(),
                Secure.ANDROID_ID);
        return android_id;
    }

    /**
     * @param context
     * @param type    0:返回IMEI，1：返回IMSI
     * @return
     */
    public static String getIMEI_IMSI(Context context, int type) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return type == 0 ? telephonyManager.getDeviceId() : telephonyManager.getSubscriberId();
    }

    /**
     * 设置一个TextView最大为多少行的时候打省略号
     *
     * @param textAbstract
     * @param maxLines
     */
    public static void maxLinesDot(final TextView textAbstract,
                                   final int maxLines) {
        ViewTreeObserver observer = textAbstract.getViewTreeObserver(); // textAbstract为TextView控件
        observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                // ViewTreeObserver obs = textAbstract.getViewTreeObserver();
                // obs.removeGlobalOnLayoutListener(this);
                if (textAbstract.getLineCount() > maxLines) // 判断行数大于多少时改变
                {
                    int lineEndIndex = textAbstract.getLayout().getLineEnd(
                            maxLines - 1); // 设置第六行打省略号
                    String text = textAbstract.getText().subSequence(0,
                            lineEndIndex - 3)
                            + "...";
                    textAbstract.setText(text);
                }
            }
        });
    }

    /**
     * 打开某个应用程序
     *
     * @param context
     * @param packageName
     */
    public static void openApp(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        PackageInfo pi = null;
        try {
            pi = pm.getPackageInfo(packageName, 0);
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(pi.packageName);

        List<ResolveInfo> apps = pm.queryIntentActivities(resolveIntent, 0);

        ResolveInfo ri = apps.iterator().next();
        if (ri != null) {
            String pn = ri.activityInfo.packageName;
            String className = ri.activityInfo.name;

            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);

            ComponentName cn = new ComponentName(pn, className);

            intent.setComponent(cn);
            context.startActivity(intent);
        }
    }

    /**
     * 发送短信
     *
     * @param context
     * @param phone   手机号码
     * @param sms     短信内容
     * @param hint    发送成功的提示信息
     */
    public static void sendSMS(Context context, String phone, String sms,
                               String hint) {
        SmsManager smsManager = SmsManager.getDefault();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
                new Intent(), 0);
        if (sms.length() > 70) {
            List<String> texts = smsManager.divideMessage(sms);
            for (String str : texts) {
                smsManager.sendTextMessage(phone, null, str, pendingIntent,
                        null);
            }
        } else {
            smsManager.sendTextMessage(phone, null, sms, pendingIntent, null);
        }
        Toast.makeText(context, hint, Toast.LENGTH_LONG).show();
    }

    /**
     * 判断给定字符串是否空白串。 空白串是指由空格、制表符、回车符、换行符组成的字符串 若输入字符串为null或空字符串，返回true
     *
     * @param input
     * @return boolean
     */
    public static boolean isEmpty(String input) {
        if (input == null || "".equals(input))
            return true;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断是否为移动号码
     *
     * @param phoneNumber
     * @return
     */
    public static boolean isChinaMobile(String phoneNumber) {
        boolean msg = false;// 134,135，136,137,138，139,147,150，151，152,157，158，159,182,183,187,188 
        if (isPhoneNumber(phoneNumber)) {
            String temp = phoneNumber.substring(0, 3);
            if (temp.equals("134") || temp.equals("135") || temp.equals("136")
                    || temp.equals("137") || temp.equals("138")
                    || temp.equals("139") || temp.equals("147")
                    || temp.equals("150") || temp.equals("151")
                    || temp.equals("152") || temp.equals("158")
                    || temp.equals("159") || temp.equals("182")
                    || temp.equals("183") || temp.equals("187")
                    || temp.equals("188")) {
                msg = true;
            }
        }
        return msg;
    }

    /**
     * 检测SDcard是否存在
     *
     * @return
     */
    public static boolean hasSDCard() {
        return Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState());
    }

    /**
     * 判断当前应用程序处于前台还是后台
     *
     * @param context
     *
     * @return
     */
    /**
     * @param context
     * @return
     */
    public static boolean isApplicationBroughtToBackground(Context context) {
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 保存裁剪的bitmap到本地
     *
     * @param file
     * @param bitmap
     */
    public static void saveBitmapToPhoto(String file, Bitmap bitmap) {
        File myCaptureFile = new File(file);
        if (myCaptureFile.exists()) {
            myCaptureFile.delete();
        }
        try {
            BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(myCaptureFile));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * ScrollView嵌套ListView修改ListView高度
     *
     * @param listView
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    /**
     * get statusbar height
     *
     * @param context
     * @return
     */
    public static int getStatusBarheight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * return c activity is alive in application
     *
     * @param context
     * @param c
     * @return
     */
    public static boolean isActivityAlive(Context context, Class c) {
        Intent intent = new Intent(context, c);
        ComponentName cmpName = intent.resolveActivity(context.getPackageManager());
        if (cmpName != null) { // 说明系统中存在这个activity
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<RunningTaskInfo> taskInfoList = am.getRunningTasks(10);
            for (RunningTaskInfo taskInfo : taskInfoList) {
                if (taskInfo.baseActivity.equals(cmpName)) { // 说明它已经启动了
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 通过文件名获取资源id 例子：getResId("icon", R.drawable.class);
     *
     * @param variableName
     * @param c
     * @return
     */
    public static int getResId(String variableName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(variableName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static String convertUnicode(String ori) {
        char aChar;
        int len = ori.length();
        StringBuffer outBuffer = new StringBuffer(len);
        for (int x = 0; x < len; ) {
            aChar = ori.charAt(x++);
            if (aChar == '\\') {
                aChar = ori.charAt(x++);
                if (aChar == 'u') {
                    int value = 0;
                    for (int i = 0; i < 4; i++) {
                        aChar = ori.charAt(x++);
                        switch (aChar) {
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                value = (value << 4) + aChar - '0';
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (value << 4) + 10 + aChar - 'a';
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (value << 4) + 10 + aChar - 'A';
                                break;
                            default:
                                throw new IllegalArgumentException(
                                        "Malformed   \\uxxxx   encoding.");
                        }
                    }
                    outBuffer.append((char) value);
                } else {
                    if (aChar == 't')
                        aChar = '\t';
                    else if (aChar == 'r')
                        aChar = '\r';
                    else if (aChar == 'n')
                        aChar = '\n';
                    else if (aChar == 'f')
                        aChar = '\f';
                    outBuffer.append(aChar);
                }
            } else
                outBuffer.append(aChar);

        }
        return outBuffer.toString();
    }

    /**
     * 获取手机运营商
     *
     * @param phone_number
     * @return
     */
    public static int matchesPhoneNumber(String phone_number) {
        String cm = "^1(34[0-8]|705|(3[5-9]|5[0127-9]|8[23478]|78|47)\\d)\\d{7}$";
        String cu = "^1((3[0-2]|45|5[56]|8[56])\\d{8}|709\\d{7})$";
        String ct = "^1((33|53|8[019])[0-9]|349|700)\\d{7}$";
        if (phone_number.matches(cm)) {//移动
            return 1;
        } else if (phone_number.matches(cu)) {//联通
            return 2;
        } else if (phone_number.matches(ct)) {//电信
            return 3;
        } else {//未知
            return 4;
        }
    }

    /**
     * Drawable转化为Bitmap
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;

    }

    /**
     * Bitmap to Drawable
     *
     * @param bitmap
     * @param mcontext
     * @return
     */
    public static Drawable bitmapToDrawble(Bitmap bitmap, Context mcontext) {
        Drawable drawable = new BitmapDrawable(mcontext.getResources(), bitmap);
        return drawable;
    }

    /**
     * 将json转换为map
     *
     * @param json
     * @return
     */
    public static MSRHashMap convertJSONToMap(JSONObject json) {
        if (json == null) {
            return null;
        }
        MSRHashMap map = new MSRHashMap();

        Iterator<String> i = json.keys();
        while (i.hasNext()) {
            String name = i.next();
            Object o = null;
            try {
                o = json.get(name);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (o == null) {
                continue;
            }
            if (o.equals(null)) {
                map.put(name, "");
                continue;
            }
            if (o instanceof JSONObject) {
                map.put(name, convertJSONToMap((JSONObject) o));
            } else if (o instanceof JSONArray) {
                map.put(name, convertJSONArrayToList((JSONArray) o));
            } else {
                map.put(name, o);
            }

        }
        return map;
    }

    /**
     * 将jsonarray 转换为List
     *
     * @param array
     * @return
     */
    public static MSRArrayList convertJSONArrayToList(JSONArray array) {
        if (array == null) {
            return null;
        }
        MSRArrayList list = new MSRArrayList();

        for (int i = 0; i < array.length(); i++) {
            Object o = null;
            try {
                o = array.get(i);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if (o == null) {
                continue;
            }
            if (o.equals(null)) {
                list.add("");
                continue;
            }
            if (o instanceof JSONObject) {
                list.add(convertJSONToMap((JSONObject) o));
            } else if (o instanceof JSONArray) {
                list.add(convertJSONArrayToList((JSONArray) o));
            } else {
                list.add(o);
            }

        }
        return list;
    }

    /**
     * Map转为Json
     *
     * @param map
     * @return
     */
    public static JSONObject convertMapToJSON(MSRHashMap map) {
        if (map == null) {
            return null;
        }
        JSONObject json = new JSONObject();

        Set<String> set = map.keySet();
        Iterator<String> i = set.iterator();
        while (i.hasNext()) {
            String name = i.next();
            Object o = map.get(name);

            if (o == null) {
                continue;
            }
            try {
                if (o instanceof MSRHashMap) {
                    json.put(name, convertMapToJSON((MSRHashMap) o));
                } else if (o instanceof MSRArrayList) {
                    json.put(name, convertListToJSONArray((MSRArrayList) o));
                } else {
                    json.put(name, o);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return json;
    }

    /**
     * List转为JsonArray
     *
     * @param list
     * @return
     */
    public static JSONArray convertListToJSONArray(MSRArrayList list) {
        if (list == null) {
            return null;
        }
        JSONArray array = new JSONArray();

        for (int i = 0; i < list.size(); i++) {
            Object o = list.get(i);
            if (o == null) {
                continue;
            }
            if (o instanceof MSRHashMap) {
                array.put(convertMapToJSON((MSRHashMap) o));
            } else if (o instanceof MSRArrayList) {
                array.put(convertListToJSONArray((MSRArrayList) o));
            } else {
                array.put(o);
            }

        }
        return array;
    }
}
