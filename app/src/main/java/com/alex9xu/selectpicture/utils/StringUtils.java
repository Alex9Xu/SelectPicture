package com.alex9xu.selectpicture.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 */
public class StringUtils {

    private static final Pattern p1 = Pattern.compile("^\\[|\\]$");
    private static final char c1 = '\u3000';
    private static final char c2 = '\177';
    private static final char c3 = ' ';
    //
    private static final char c4 = '\uFF00';
    private static final char c5 = '\uFF5F';

    private static Locale locale;

    /**
     * 替换掉字符串首尾的方括号（^\\[|\\]$）
     *
     * @param listString
     * @return
     */
    public static String clearList4SQL(String listString) {
        return p1.matcher(listString).replaceAll("");
    }

    /**
     * 半角转全角
     *
     * @param input String.
     * @return 全角字符串.
     */
    public static String toSBC(String input) {
        if (input == null) {
            return "";
        }
        char c[] = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == c3) {
                c[i] = c1; // 采用十六进制,相当于十进制的12288

            } else if (c[i] < c2) { // 采用八进制,相当于十进制的127
                c[i] = (char) (c[i] + 65248);

            }
        }
        return new String(c);
    }

    /**
     * 全角转半角
     *
     * @param input String.
     * @return 半角字符串
     */
    public static String toDBC(String input) {

        char c[] = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == c1) {
                c[i] = c3;
            } else if (c[i] > c4 && c[i] < c5) {
                c[i] = (char) (c[i] - 65248);
            }
        }
        return new String(c);
    }

    /**
     * 从输出流转成字符
     *
     * @param is
     * @return
     */
    public static String readStream(InputStream is) {
        ByteArrayOutputStream bo = null;
        try {
            bo = new ByteArrayOutputStream();
            int i = is.read();
            while (i != -1) {
                bo.write(i);
                i = is.read();
            }
            return bo.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bo != null) {
                    bo.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    /**
     * 字符串反序 // modify by huangyuan at 2012-10-09
     *
     * @param str
     * @return
     */
    public static String reverse(String str) {
        StringBuilder resultString = new StringBuilder();
        char[] charArray = str.toCharArray();

        for (int i = charArray.length - 1; i >= 0; i--) {
            resultString.append(charArray[i]);
        }
        return resultString.toString();
    }

    /**
     * 根据 %1$s 规则替换字符串的 方法
     *
     * @param temp
     * @param formatArgs
     * @return
     */
    public static String getString(String temp, Object... formatArgs) {
        if (locale == null) {
            locale = Locale.getDefault();
        }
        return String.format(locale, temp, formatArgs);
    }

    public static boolean isStringEmpty(String str) {
        if( null == str || str.equals("") || str.equals("null") ) {
            return true;
        }

        return false;
    }

    /**
     * Check if the string is Email
     */
    public static boolean isEmail(String strToCheck) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(strToCheck);
        return m.matches();
    }

}
