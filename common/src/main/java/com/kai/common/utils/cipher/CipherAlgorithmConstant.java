package com.kai.common.utils.cipher;


public class CipherAlgorithmConstant {
    public static String AES_CBC_NoPadding = "AES/CBC/NoPadding";   //（128）
    public static String AES_CBC_PKCS5Padding = "AES/CBC/PKCS5Padding";//（128）
    public static String AES_ECB_NoPadding = "AES/ECB/NoPadding";//（128）
    public static String AES_ECB_PKCS5Padding = "AES/ECB/PKCS5Padding";//（128）
    public static String DES_CBC_NoPadding = "DES/CBC/NoPadding";   //（56）
    public static String DES_CBC_PKCS5Padding = "DES/CBC/PKCS5Padding";//（56）
    public static String DES_ECB_NoPadding = "DES/ECB/NoPadding";   //（56）
    public static String DES_ECB_PKCS5Padding = "DES/ECB/PKCS5Padding"; //（56）
    public static String DESede_CBC_NoPadding = "DESede/CBC/NoPadding";
    public static String DESede_CBC_PKCS5Padding = "DESede/CBC/PKCS5Padding";   // （168）
    public static String DESede_ECB_NoPadding = "DESede/ECB/NoPadding"; //（168）
    public static String DESede_ECB_PKCS5Padding = "DESede/ECB/PKCS5Padding";   //（168）
    public static String RSA_ECB_PKCS1Padding = "RSA/ECB/PKCS1Padding"; //（ 1024，2048 ）
    public static String RSA_ECB_OAEPWithSHA1AndMGF1Padding = "RSA/ECB/OAEPWithSHA-1AndMGF1Padding";    //（ 1024，2048 ）
    public static String RSA_ECB_OAEPWithSHA256AndMGF1Padding = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";    //（ 1024，2048 ）
}
