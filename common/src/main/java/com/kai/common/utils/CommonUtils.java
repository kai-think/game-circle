package com.kai.common.utils;

import java.text.DecimalFormat;


public class CommonUtils {
	private final static double DBTOLARANCE = 0.00001;
	

	

	
	/**
	 * @MethodName:
	 * @Description: 比较两个double是否相等，是返回true，否则返回false
	 * @Param:
	 * @Return:
	 * @Author: linjiangyi
	 * @Date: 2019-12-11 13:15
	**/
	public static boolean comDouble(double db1,double db2){
		if(Math.abs(db1-db2)<=DBTOLARANCE){
			return true;
		}else{
			return false;
		}
	}
	
	
	/**
	 * 
	 * @描述  将f 修改为只有 count位小数位
	 * @请求参数
	 * float  f 浮点数
	 * int count 小数位 位数
	 * @作者  chrimer(林江毅)
	 * @创建时间 2015年4月6日 下午9:38:43
	 */
	public static double changeDouble(double db,int count){
		if(count<=0){
			return db;
		}
		String format = ".";
		for(int i=1;i<=count;i++){
			format += "0";
		}
		DecimalFormat decimalFormat=new DecimalFormat(format);//构造方法的字符格式这里如果小数不足2位,会以0补足.
		String strDouble=decimalFormat.format(db);//format 返回的是字符串
		
		return Double.parseDouble(strDouble);
	}
	
	/**
	 * 截取字符串 没有后缀
	 * @param s
	 * @param length 截取的字符串字节长度
	 * @return 截取的字符串+后缀
	 * @throws Exception
	 * Unicode编码：1-0x0031，啊-0x554a
	 */
	public static String getSubstring(String s, int length) throws Exception{
		return getSubstring(s, length,"");
	}
	/**
	 * 截取字符串
	 * @param s
	 * @param length 截取的字符串字节长度
	 * @param suffix 后缀
	 * @return 截取的字符串+后缀
	 * @throws Exception
	 * Unicode编码：1-0x0031，啊-0x554a
	 */
	public static String getSubstring(String s, int length,String suffix) throws Exception  {  
		
		byte[] bytes = s.getBytes("Unicode");  
	    int n = 0; // 表示当前的字节数  
	    int i = 2; // 要截取的字节数，从第3个字节开始  
	    if(bytes.length-2 <= length) return s;
	    for (; i < bytes.length && n < length; i++)  
	    {  
	        // 奇数位置，如3、5、7等，为UCS2编码中两个字节的第二个字节  
	        if (i % 2 == 1)  
	        {  
	            n++; // 在UCS2第二个字节时n加1  
	        }else 
	        {  
	            // 当UCS2编码的第一个字节不等于0时，该UCS2字符为汉字，一个汉字算两个字节  
	            if (bytes[i] != 0)  
	            {  
	                n++;  
	            }  
	        }  
	    }  
	    // 如果i为奇数时，处理成偶数  
	    if (i % 2 == 1)  
	    {  
	        // 该UCS2字符是汉字时，去掉这个截一半的汉字  
	        if (bytes[i - 1] != 0)  
	            i = i - 1;  
	        // 该UCS2字符是字母或数字，则保留该字符  
	        else 
	            i = i + 1;  
	    }  
	    String temp = new String(bytes, 0, i, "Unicode")+ suffix;
	    return temp;  
	}

	public static int getPerc(int scope, int integral) {
		return (int)(1.0*integral/scope*100);
	}
}
