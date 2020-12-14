package com.zjw.apps3pluspro.utils;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaUtil {


    /**
     * 验证手机格式
     */
    public static boolean isMobileNO(String mobiles) {
        /*
         * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
         * 联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通）
         * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
         */
        String telRegex = "[1][0123456789]\\d{9}";// "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobiles))
            return false;
        else
            return mobiles.matches(telRegex);
    }

    /**
     * 判断邮箱是否合法
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        if (null == email || "".equals(email)) return false;
        //Pattern p = Pattern.compile("\\w+@(\\w+.)+[a-z]{2,3}"); //简单匹配
        Pattern p = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");//复杂匹配
        Matcher m = p.matcher(email);
        return m.matches();
    }


    /**
     * 是否包含空格
     *
     * @param str
     * @return
     */
    public static boolean isKongGe(String str) {
        if (str.contains(" ")) {
            return true;
        } else {
            return false;
        }

    }


    public static boolean containsEmoji(String source) {
        int len = source.length();
        boolean isEmoji = false;
        for (int i = 0; i < len; i++) {
            char hs = source.charAt(i);
            if (0xd800 <= hs && hs <= 0xdbff) {
                if (source.length() > 1) {
                    char ls = source.charAt(i + 1);
                    int uc = ((hs - 0xd800) * 0x400) + (ls - 0xdc00) + 0x10000;
                    if (0x1d000 <= uc && uc <= 0x1f77f) {
                        return true;
                    }
                }
            } else {
                // non surrogate
                if (0x2100 <= hs && hs <= 0x27ff && hs != 0x263b) {
                    return true;
                } else if (0x2B05 <= hs && hs <= 0x2b07) {
                    return true;
                } else if (0x2934 <= hs && hs <= 0x2935) {
                    return true;
                } else if (0x3297 <= hs && hs <= 0x3299) {
                    return true;
                } else if (hs == 0xa9 || hs == 0xae || hs == 0x303d
                        || hs == 0x3030 || hs == 0x2b55 || hs == 0x2b1c
                        || hs == 0x2b1b || hs == 0x2b50 || hs == 0x231a) {
                    return true;
                }
                if (!isEmoji && source.length() > 1 && i < source.length() - 1) {
                    char ls = source.charAt(i + 1);
                    if (ls == 0x20e3) {
                        return true;
                    }
                }
            }
        }
        return isEmoji;
    }


    /**
     * 密码是否合法
     *
     * @param str
     * @return
     */
    public static boolean isPassword(String str) {
        if (!str.contains(" ") && str.length() >= 6 && str.length() <= 20) {
            return true;
        } else {
            return false;
        }

    }

    public static int getAccountType(String account) {
        if (!TextUtils.isEmpty(account)) {
            if (isMobileNO(account)) {
                return 1;
            } else if (isEmail(account)) {
                return 2;
            } else {
                return 0;
            }

        } else {
            return 0;
        }

    }


    /**
     * 判断数据是否合法
     *
     * @param something
     * @return
     */
    public static boolean checkIsNull(String something) {
        if (TextUtils.isEmpty(something) || something == null
                || something.equals("") || something.equals("null")
                || something.equals("0") || something.equals("0.0") || something.equals("0.00")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断数据是否合法-0除外
     *
     * @param something
     * @return
     */
    public static boolean checkIsNullnoZero(String something) {
        if (TextUtils.isEmpty(something) || something == null
                || something.equals("") || something.equals("null")
                ) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断是否是数字
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }


}
