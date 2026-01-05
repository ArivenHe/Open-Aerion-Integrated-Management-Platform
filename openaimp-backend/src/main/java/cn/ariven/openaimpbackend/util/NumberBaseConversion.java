package cn.ariven.openaimpbackend.util;

import java.util.*;

/**
 * 进制转换器
 * 支持二进制、八进制、十进制、十六进制之间的相互转换
 *
 * @author ariven
 * @version 0.1-SNAPSHOT
 */
public class NumberBaseConversion {

    /**
     * 十进制转二进制
     * @param number 十进制数字
     * @return 二进制字符串
     */
    public static String DtB(int number) {
        return Integer.toBinaryString(number);
    }

    /**
     * 二进制转十进制
     * @param binary 二进制字符串
     * @return 十进制数字
     */
    public static int BtD(String binary) {
        return Integer.parseInt(binary, 2);
    }

    /**
     * 十进制转八进制
     * @param number 十进制数字
     * @return 八进制字符串
     */
    public static String DtO(int number) {
        return Integer.toOctalString(number);
    }

    /**
     * 八进制转十进制
     * @param octal 八进制字符串
     * @return 十进制数字
     */
    public static int OtD(String octal) {
        return Integer.parseInt(octal, 8);
    }

    /**
     * 十进制转十六进制
     * @param number 十进制数字
     * @return 十六进制字符串
     */
    public static String DtH(int number) {
        return Integer.toHexString(number).toUpperCase();
    }

    /**
     * 十六进制转十进制
     * @param hex 十六进制字符串
     * @return 十进制数字
     */
    public static int HtD(String hex) {
        return Integer.parseInt(hex, 16);
    }

    /**
     * 二进制转八进制
     * @param binary 二进制字符串
     * @return 八进制字符串
     */
    public static String BtO(String binary) {
        int decimal = BtD(binary);
        return DtO(decimal);
    }

    /**
     * 二进制转十六进制
     * @param binary 二进制字符串
     * @return 十六进制字符串
     */
    public static String BtH(String binary) {
        int decimal = BtD(binary);
        return DtH(decimal);
    }

    /**
     * 八进制转二进制
     * @param octal 八进制字符串
     * @return 二进制字符串
     */
    public static String OtB(String octal) {
        int decimal = OtD(octal);
        return DtB(decimal);
    }

    /**
     * 八进制转十六进制
     * @param octal 八进制字符串
     * @return 十六进制字符串
     */
    public static String OtH(String octal) {
        int decimal = OtD(octal);
        return DtH(decimal);
    }

    /**
     * 十六进制转二进制
     * @param hex 十六进制字符串
     * @return 二进制字符串
     */
    public static String HtB(String hex) {
        int decimal = HtD(hex);
        return DtB(decimal);
    }

    /**
     * 十六进制转八进制
     * @param hex 十六进制字符串
     * @return 八进制字符串
     */
    public static String HtO(String hex) {
        int decimal = HtD(hex);
        return DtO(decimal);
    }

    /**
     * 任意进制转换
     * @param number 要转换的数字字符串
     * @param fromBase 原进制基数
     * @param toBase 目标进制基数
     * @return 转换后的字符串
     */
    public static String baseConvert(String number, int fromBase, int toBase) {
        // 先转换为十进制
        int decimalValue = Integer.parseInt(number, fromBase);

        // 再从十进制转换为目标进制
        switch (toBase) {
            case 2:
                return Integer.toBinaryString(decimalValue);
            case 8:
                return Integer.toOctalString(decimalValue);
            case 10:
                return String.valueOf(decimalValue);
            case 16:
                return Integer.toHexString(decimalValue).toUpperCase();
            default:
                throw new IllegalArgumentException("不支持的进制基数: " + toBase);
        }
    }

    /**
     * 显示数字的各种进制表示
     * @param number 十进制数字
     */
    public static void displayAllBases(int number) {
        System.out.println("十进制: " + number);
        System.out.println("二进制: " + DtB(number));
        System.out.println("八进制: " + DtO(number));
        System.out.println("十六进制: " + DtH(number));
    }
}
