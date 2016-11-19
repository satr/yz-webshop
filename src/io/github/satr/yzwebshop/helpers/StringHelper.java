package io.github.satr.yzwebshop.helpers;

import java.util.regex.Pattern;

public class StringHelper {
    private static Pattern intPattern = Pattern.compile("^-?\\d{1,10}$");
    private static Pattern doublePattern = Pattern.compile("^-?\\d?\\d{0,30}([\\.\\,]\\d{1,30})?$");

    public static boolean isInteger(String value) {
        return intPattern.matcher(value).matches();
    }

    /* Simplified version:
    * - no support of E-notation
    * - max 30 digits in integer and fractional parts
    * - separator sign corresponded to locale
    * - no group separator
    */
    public static boolean isDouble(String value) {
        return doublePattern.matcher(value).matches();
    }
}
