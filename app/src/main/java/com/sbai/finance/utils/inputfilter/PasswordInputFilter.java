package com.sbai.finance.utils.inputfilter;

import android.text.InputFilter;
import android.text.Spanned;

public class PasswordInputFilter implements InputFilter {
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < end; i++) {
            char c = source.charAt(i);
            if (!Character.isWhitespace(c) && c != '-') {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
