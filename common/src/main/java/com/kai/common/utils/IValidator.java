package com.kai.common.utils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class IValidator {
    private String str;
    private boolean pass = false;
    private String errMessage;

    public static boolean checkSize(String str, int minSize, int maxSize) {
        return str.length() >= minSize && str.length() <= maxSize;
    }

    public static boolean containDigit(String str) {
        return IValidator.match(str, "\\d");
    }

    public static boolean containLetter(String str) {
        return IValidator.match(str, "[A-Za-z]");
    }

    public static boolean containChineseCharacters(String str) {
        return IValidator.match(str, "[\\u4e00-\\u9fa5]");
    }

    /**
     *
     * @param str
     * @return 是数字就返回true，否则返回false
     */
    public static boolean number(String str) {
        return match(str, "^\\d+$");
    }

    /**
     *
     * @param str
     * @param regex 正则表达式
     * @return 正则匹配
     */
    public static boolean match(String str, String regex) {
        Pattern pattern = Pattern.compile(regex);
        return str.matches(regex);
    }

    /**
     *
     * @param objs 待验证的串
     * @return 只要有一个为空就返回 true
     */
    public static boolean empty(Object ...objs) {
        if (objs == null || objs.length == 0)
            return true;

        for (Object obj : objs) {
            if (obj == null)
                return true;
            if (obj instanceof String && ((String) obj).length() == 0)
                return true;
            if (obj instanceof StringBuffer && ((StringBuffer) obj).length() == 0)
                return true;
            if (obj instanceof StringBuilder && ((StringBuilder) obj).length() == 0)
                return true;
            if (obj instanceof List)
                return ((List<?>)obj).isEmpty();
            if (obj instanceof Map)
                return ((Map<?, ?>)obj).isEmpty();
        }

        return false;
    }

    public static boolean empty(Collection<?>... collections) {
        if (collections == null)
            return true;

        for (Collection<?> coll : collections) {
            if (coll == null || coll.size() == 0)
                return true;
        }

        return false;
    }

    public IValidator(String str) {
        this.str = str;
    }

    public IValidator checkSize(int minSize, int maxSize, String errMessage) {
        if (!pass)
            return this;

        pass = IValidator.checkSize(str, minSize, maxSize);
        if (!pass)
            this.errMessage = errMessage;

        return this;
    }

    /**
     *
     * @param str
     * @return 是数字就返回true，否则返回false
     */
    public IValidator number(String str, String errMessage) {
        if (!pass)
            return this;

        pass = IValidator.number(str);
        if (!pass)
            this.errMessage = errMessage;

        return this;
    }

    /**
     *
     * @param str
     * @param regex 正则表达式
     * @return 正则匹配
     */
    public IValidator match(String str, String regex, String errMessage) {
        if (!pass)
            return this;

        pass = IValidator.match(str, regex);
        if (!pass)
            this.errMessage = errMessage;

        return this;
    }

    /**
     *
     * @param strs 待验证的串
     * @return 只要有一个为空就返回 true
     */
    public IValidator empty(String[] strs, String errMessage) {
        if (!pass)
            return this;

        pass = IValidator.empty(strs);
        if (!pass)
            this.errMessage = errMessage;

        return this;
    }

    public IValidator containDigit(String str, String errMessage) {
        if (!pass)
            return this;

        pass = IValidator.containDigit(str);
        if (!pass)
            this.errMessage = errMessage;

        return this;
    }

    public IValidator containLetter(String str, String errMessage) {
        if (!pass)
            return this;

        pass = IValidator.containLetter(str);
        if (!pass)
            this.errMessage = errMessage;

        return this;
    }

    public IValidator containChineseCharacters(String str, String errMessage) {
        if (!pass)
            return this;

        pass = IValidator.containChineseCharacters(str);
        if (!pass)
            this.errMessage = errMessage;

        return this;
    }

    public boolean validate() {
        return pass;
    }

    public String getErrMessage() {
        return errMessage;
    }
}