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

    /**
     * 安全密码手机号 格式话 181 **** 1111
     *
     * @param phoneNoSpace
     * @return
     */
    public static String getFormatSafetyPhoneNumber(String phoneNoSpace) {
        return phoneNoSpace.substring(0, 3) + " **** " + phoneNoSpace.substring(phoneNoSpace.length() - 4);
    }


    /**
     * 格式化银行卡 4444 4444 4444 4444 444
     *
     * @param bankCardNoSpace
     * @return
     */
    public static String getFormatBankCardNumber(String bankCardNoSpace) {
        if (bankCardNoSpace.length() <= 4) {
            return bankCardNoSpace;
        } else if (bankCardNoSpace.length() <= 8) {
            return bankCardNoSpace.substring(0, 4)
                    + " " + bankCardNoSpace.substring(4, bankCardNoSpace.length());
        } else if (bankCardNoSpace.length() <= 12) {
            return bankCardNoSpace.substring(0, 4)
                    + " " + bankCardNoSpace.substring(4, 8)
                    + " " + bankCardNoSpace.substring(8, bankCardNoSpace.length());
        } else if (bankCardNoSpace.length() <= 16) {
            return bankCardNoSpace.substring(0, 4)
                    + " " + bankCardNoSpace.substring(4, 8)
                    + " " + bankCardNoSpace.substring(8, 12)
                    + " " + bankCardNoSpace.substring(12, bankCardNoSpace.length());
        } else if (bankCardNoSpace.length() <= 20) {
            return bankCardNoSpace.substring(0, 4)
                    + " " + bankCardNoSpace.substring(4, 8)
                    + " " + bankCardNoSpace.substring(8, 12)
                    + " " + bankCardNoSpace.substring(12, 16)
                    + " " + bankCardNoSpace.substring(16, bankCardNoSpace.length());
        } else if (bankCardNoSpace.length() <= 25) {
            return bankCardNoSpace.substring(0, 4)
                    + " " + bankCardNoSpace.substring(4, 8)
                    + " " + bankCardNoSpace.substring(8, 12)
                    + " " + bankCardNoSpace.substring(12, 16)
                    + " " + bankCardNoSpace.substring(16, 20)
                    + " " + bankCardNoSpace.substring(20, bankCardNoSpace.length());
        }
        return bankCardNoSpace;
    }

    /**
     * 替换//为换行
     *
     * @param text
     * @return
     */
    public static String getFormatText(String text) {
        return text.replace("//", "\n");
    }

    /**
     * 5.15454
     *
     * @return 5.12  小数点后两位
     */
    public static String getFormatMoney(String money) {
        if (money.contains(".") && money.indexOf(".") + 4 == money.length()) {
            return money.substring(0, money.indexOf(".") + 3);
        }
        return money;
    }

    /**
     * 积分/元宝超过10000显示为1.0万、1.1万、10.0万、10.1万、以千为单位递进
     *
     * @param money
     * @return
     */
    public static String getFormIngot(double money) {
        String number = String.valueOf((long)money);
        if (money >= 10000.00) {
            int length = number.length();
            return number.substring(0, length - 4) + "." + number.substring(length - 4, length - 3) + FinanceUtil.UNIT_WANG;
        }
        return FinanceUtil.formatWithScaleNoZero(money);
    }

    /**
     * 积分/元宝超过10000显示为1.0万、1.1万、10.0万、10.1万、以千为单位递进
     *
     * @param money
     * @return
     */
    public static String getFormIntegrate(double money) {
        String number = String.valueOf((long) money);
        if (money >= 10000.00) {
            int length = number.length();
            return number.substring(0, length - 4) + "." + number.substring(length - 4, length - 3) + FinanceUtil.UNIT_WANG;
        }
        return FinanceUtil.formatWithScale(money);
    }

    public static String getFormatCount(int count) {
        String number = String.valueOf(count);
        int length = number.length();
        if (count >= 10000 && count < 100000000) {
            return number.substring(0, length -4) + ".0"  + number.substring(length - 4, length - 3) + FinanceUtil.UNIT_WANG;
        } else if (count >= 100000000){
            return number.substring(0, length - 8) + ".0"  + number.substring(length - 8, length - 7) + FinanceUtil.UNIT_YI;
        }
        return number + "";
    }
}
