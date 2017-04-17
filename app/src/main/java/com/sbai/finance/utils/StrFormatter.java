package com.sbai.finance.utils;

/**
 * Created by ${wangJie} on 2017/4/14.
 */

public class StrFormatter {

    private static final String TAG = "StrFormatter";

    /**
     * 格式化手机号 为 *** **** ****
     *
     * @param phoneNoSpace
     * @return
     */
    public static String getFormatPhoneNumber(String phoneNoSpace) {
        if (phoneNoSpace.length() <= 3) {
            return phoneNoSpace;
        } else if (phoneNoSpace.length() <= 7) {
            return phoneNoSpace.substring(0, 3)
                    + " " + phoneNoSpace.substring(3, phoneNoSpace.length());
        } else if (phoneNoSpace.length() <= 11) {
            return phoneNoSpace.substring(0, 3)
                    + " " + phoneNoSpace.substring(3, 7)
                    + " " + phoneNoSpace.substring(7, phoneNoSpace.length());
        }
        return phoneNoSpace;
    }
}
