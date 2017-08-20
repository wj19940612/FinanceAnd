package com.sbai.finance.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ${wangJie} on 2016/9/28.
 * 字符串合法性的判断
 */

public class ValidityDecideUtil {

    /**
     * 限制昵称只能输入中文、字母和数字
     *
     * @param nickName
     * @return
     */
    public static boolean isLegalNickName(CharSequence nickName) {
        nickName = nickName.toString().trim();
        Pattern letter = Pattern.compile("^[A-Za-z0-9\u4e00-\u9fa5]+$");
        Matcher letterMatcher = letter.matcher(nickName);
        return letterMatcher.matches();
    }

    /**
     * 真实姓名只能是中文
     *
     * @param realName
     * @return
     */
    public static boolean isOnlyAChineseName(CharSequence realName) {
        realName = realName.toString().trim();
        Pattern letter = Pattern.compile("^[\u4e00-\u9fa5]+$");
        Matcher letterMatcher = letter.matcher(realName);
        return letterMatcher.matches();
    }

    public static boolean isIdentityCard(String identityCard) {
        Pattern compile = Pattern.compile("\\d{17}[\\d|x]|\\d{15}");
        return compile.matcher(identityCard).matches();
    }
}
