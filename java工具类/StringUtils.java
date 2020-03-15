package la.xiong.androidquick.tool;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.AssetManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ddnosh
 * @website http://blog.csdn.net/ddnosh
 */
public class StringUtil {
    /**
     * return if str is empty
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        if (str == null || str.length() == 0 || str.equalsIgnoreCase("null") || str.isEmpty() || str.equals("")) {
            return true;
        } else {
            return false;
        }
    }

    public static String getTrimedString(String s) {
        return trim(s);
    }

    public static String trim(String s) {
        return s == null ? "" : s.trim();
    }

    public static boolean equals(String str1, String str2) {
        return str1 == str2 || equalsNotNull(str1, str2);
    }

    public static boolean equalsNotNull(String str1, String str2) {
        return str1 != null && str1.equals(str2);
    }
    public static String getJson(Context context,String fileName) {

        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public static boolean isBlank(String str) {
        return (str == null || str.trim().length() == 0);
    }

    public static String getHostName(String urlString) {
        String head = "";
        int index = urlString.indexOf("://");
        if (index != -1) {
            head = urlString.substring(0, index + 3);
            urlString = urlString.substring(index + 3);
        }
        index = urlString.indexOf("/");
        if (index != -1) {
            urlString = urlString.substring(0, index + 1);
        }
        return head + urlString;
    }

    public static String getDataSize(long var0) {
        DecimalFormat var2 = new DecimalFormat("###.00");
        return var0 < 1024L ? var0 + "bytes" : (var0 < 1048576L ? var2.format((double) ((float) var0 / 1024.0F))
                + "KB" : (var0 < 1073741824L ? var2.format((double) ((float) var0 / 1024.0F / 1024.0F))
                + "MB" : (var0 < 0L ? var2.format((double) ((float) var0 / 1024.0F / 1024.0F / 1024.0F))
                + "GB" : "error")));
    }

    /**
     * 将对象数据转换成String
     *
     * @param obj
     * @return
     */
    public static String toString(Object obj) {
        if (obj == null || "".equals(obj) || "null".equals(obj)) {
            return "";
        }
        return String.valueOf(obj);
    }

    /**
     * 毫秒数转换成时分秒
     */
    public static String formateTime2(long elapsed) {
        int hour, minute, second, milli;
        second = (int) (elapsed % 60);
        elapsed = elapsed / 60;
        minute = (int) (elapsed % 60);
        elapsed = elapsed / 60;
        hour = (int) (elapsed % 60);
        return String.format("%02d:%02d:%02d ", hour, minute, second);
    }

    /**
     * 取代空格
     */
    public static String replaceBlank(String src) {
        String dest = "";
        if (src != null) {
            Pattern pattern = Pattern.compile("\t|\r|\n|\\s*");
            Matcher matcher = pattern.matcher(src);
            dest = matcher.replaceAll("");
        }
        return dest;
    }

    /**
     * 字符ids拼接
     */
    public static String getids(List<Integer> ids) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < ids.size(); i++) {
            stringBuffer.append(ids.get(i) + ",");
        }
        String idstr = stringBuffer.toString();
        return idstr.substring(0, idstr.length() - 1);
    }

    /**
     * 获取随机数
     */
    private static String string = "abcdefghijklmnopqrstuvwxyz";

    public static String getRandomString(int length) {
        StringBuffer sb = new StringBuffer();
        int len = string.length();
        for (int i = 0; i < length; i++) {
            sb.append(string.charAt(getRandom(len - 1)));
        }
        return sb.toString();
    }

    private static int getRandom(int count) {
        return (int) Math.round(Math.random() * (count));
    }

    /**
     * 2019:12:22 ->转换成data
     */
    public static Date string2Date(final String time, @NonNull final DateFormat format) {
        try {
            return format.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * data 转换成 fromat格式
     */
    public static String getDate(Date date, String fromat) {
        SimpleDateFormat format = new SimpleDateFormat(fromat);
        return format.format(date);
    }

    public static String changeDatastr(final String time, @NonNull final String parseFormat, String toFormat) {
        try {
            SimpleDateFormat ps = new SimpleDateFormat(parseFormat);
            return getDate(string2Date(time, ps), toFormat);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static final int PRICE_FORMAT_DEFAULT = 0;
    public static final int PRICE_FORMAT_PREFIX = 1;
    public static final int PRICE_FORMAT_SUFFIX = 2;
    public static final int PRICE_FORMAT_PREFIX_WITH_BLANK = 3;
    public static final int PRICE_FORMAT_SUFFIX_WITH_BLANK = 4;
    public static final String[] PRICE_FORMATS = {
            "", "￥", "元", "￥ ", " 元"
    };

    /**
     * 获取价格，保留两位小数
     *
     * @param price
     * @return
     */
    public static String getPrice(String price) {
        return getPrice(price, PRICE_FORMAT_DEFAULT);
    }

    /**
     * 判断字符是否为空
     *
     * @param s
     * @param trim
     * @return
     */
    public static boolean isEmpty(String s, boolean trim) {
        //		Log.i(TAG, "isEmpty   s = " + s);
        if (s == null) {
            return true;
        }
        if (trim) {
            s = s.trim();
        }
        if (s.length() <= 0) {
            return true;
        }
        return false;
    }

    public static String getJsonKeyStr(String jsonstr, String key) {
        if (isEmpty(jsonstr)) {
            return "";
        }
        Map map = JSON.parseObject(jsonstr, Map.class);
        return toString(map.get(key));
    }

    public static int getJsonKeyint(String jsonstr, String key) {
        try {
            if (isEmpty(jsonstr)) {
                return 0;
            }
            Map map = JSON.parseObject(jsonstr, Map.class);
            return toInt(map.get(key));
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static float toFloat(Object obj) {
        if (obj == null || "".equals(obj)) {
            return 0;
        }
        String str = String.valueOf(obj);
        return Float.parseFloat(str);
    }

    public static int toInt(Object obj) {
        if (obj == null || "".equals(obj)) {
            return 0;
        }
        String str = String.valueOf(obj);
        return Integer.valueOf(str);
    }
    public static String addOne(String num){
        int number=toInt(num)+1;
        return toString(number);


    }

    /**
     * 获取价格，保留两位小数
     *
     * @param price
     * @param formatType 添加单位（元）
     * @return
     */
    public static String getPrice(String price, int formatType) {
        if (isEmpty(price, true)) {
            return getPrice(0, formatType);
        }

        //单独写到getCorrectPrice? <<<<<<<<<<<<<<<<<<<<<<
        String correctPrice = "";
        String s;
        for (int i = 0; i < price.length(); i++) {
            s = price.substring(i, i + 1);
            if (".".equals(s) || isNumber(s)) {
                correctPrice += s;
            }
        }
        if (correctPrice.contains(".")) {
            //			if (correctPrice.startsWith(".")) {
            //				correctPrice = 0 + correctPrice;
            //			}
            if (correctPrice.endsWith(".")) {
                correctPrice = correctPrice.replaceAll(".", "");
            }
        }
        return isEmpty(correctPrice, true) ? getPrice(0, formatType) : getPrice(new BigDecimal(0 + correctPrice), formatType);
    }

    /**
     * 判断是否是数据
     */
    public static boolean isNumber(String number) {
        if (isEmpty(number, true)) {
            return false;
        }

        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(number);
        if (isNum.matches() == false) {
            return false;
        }
        return true;
    }

    /**
     * 获取价格，保留两位小数
     *
     * @param price
     * @return
     */
    public static String getPrice(BigDecimal price) {
        return getPrice(price, PRICE_FORMAT_DEFAULT);
    }

    /**
     * 获取价格，保留两位小数
     *
     * @param price
     * @return
     */
    public static String getPrice(double price) {
        return getPrice(price, PRICE_FORMAT_DEFAULT);
    }

    /**
     * 获取价格，保留两位小数
     *
     * @param price
     * @param formatType 添加单位（元）
     * @return
     */
    public static String getPrice(BigDecimal price, int formatType) {
        return getPrice(price == null ? 0 : price.doubleValue(), formatType);
    }

    /**
     * 获取价格，保留两位小数
     *
     * @param price
     * @param formatType 添加单位（元）
     * @return
     */
    public static String getPrice(double price, int formatType) {
        String s = new DecimalFormat("#########0.00").format(price);
        switch (formatType) {
            case PRICE_FORMAT_PREFIX:
                return PRICE_FORMATS[PRICE_FORMAT_PREFIX] + s;
            case PRICE_FORMAT_SUFFIX:
                return s + PRICE_FORMATS[PRICE_FORMAT_SUFFIX];
            case PRICE_FORMAT_PREFIX_WITH_BLANK:
                return PRICE_FORMATS[PRICE_FORMAT_PREFIX_WITH_BLANK] + s;
            case PRICE_FORMAT_SUFFIX_WITH_BLANK:
                return s + PRICE_FORMATS[PRICE_FORMAT_SUFFIX_WITH_BLANK];
            default:
                return s;
        }
    }


    private static String BASE_PHOTO_URL="";
    /**
     * @param url
     * @return
     */
    public static String getImageUrl(String url) {
        if (!TextUtils.isEmpty(url)) {
            if (url.contains("http")||new File(url).isFile()) {
                return url;
            } else {
                return BASE_PHOTO_URL+url;
            }
        } else {
            return "";
        }
    }

    public static void copyBord(String copyStr,Context context) {
        ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(copyStr);
    }
    public static List<String> ceshiList(int tage) {
        List<String> strings=new ArrayList<>();
        for (int i = 0; i < tage; i++) {
            strings.add("dd"+i);
        }
        return strings;

    }

    public static List<String> getImageUrls(String urls) {
        List<String> list = new ArrayList<>();
        if(StringUtil.isEmpty(urls)){
            return list;
        }else{
            if (urls.contains(",")){
                String[] strings = urls.split(",");
                for (int i =0; i<strings.length; i++){
                    list.add(strings[i]);
                }
            }else {
                list.add(urls);
            }

        }
        return list;
    }
}
