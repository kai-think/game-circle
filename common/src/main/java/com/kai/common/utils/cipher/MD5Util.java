package com.kai.common.utils.cipher;
import java.security.MessageDigest;

/** 
 * 采用MD5加密
 * @author Xingxing,Xie
 * @datetime 2014-5-31 
 */
public class MD5Util {
    /*** 
     * MD5加密 生成32位md5码
     * @param 待加密字符串
     * @return 返回32位md5码
     */
    public static String md5Encode(String inStr) throws Exception {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
            return "";
        }

        byte[] byteArray = inStr.getBytes("UTF-8");
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }

    /**
     * 测试主函数
     * @param args
     * @throws Exception
     */
    public static void main(String args[]) throws Exception {
        String str = new String("xqg123456");
        String salt = "301e4851add800295b6d599f60c527dc";
        System.out.println("原始：" + str);
        System.out.println("MD5后：" + md5Encode(md5Encode(md5Encode(str))+salt));
        //System.out.println("MD5后：" + md5Encode(str).toUpperCase());
    }

	public static String getEncode(String pwd, String salt) throws Exception {
		return md5Encode(md5Encode(md5Encode(pwd))+salt);
	}
}