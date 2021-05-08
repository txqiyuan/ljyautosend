package com.utils;

import org.apache.commons.codec.digest.DigestUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AlexUtil {

    private static String dateFormatalex = "yyyy-MM-dd HH:mm:ss";
    private static String dateFormatalice = "yyyy-MM-dd HH:mm";

    public static String formatDatealex(Date dt) {

        if (dt == null)
            return null;
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormatalex);
        return sdf.format(dt);
    }

    public static String getonehourfront() {

        Calendar calendar = Calendar.getInstance();
        /* HOUR_OF_DAY 指示一天中的小时 */
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - 1);
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormatalice);
        return sdf.format(calendar.getTime())+ "--" + sdf.format(new Date());
    }

    public static String getTableName(Date date) {
        String tableName = "ST_AllDetails_Factor";
        if (date != null) {
            tableName = tableName + "_" + dateToString(date, "yyyyMM");
        }

        return tableName;
    }

    public static String getdiytm1(){
        Integer nowhour = gethour();
        String recon;
        if (nowhour <= 8){
            recon = getyear()+ "年" + getmonth() + "月"+ (getday() - 1) +"日8时";
        }else {
            recon = getyear()+ "年" + getmonth() + "月"+ getday() +"日8时";
        }
        return recon;
    }

    public static String getdiytm2(){
        Integer nowhour = gethour();
        String recon;
        if (nowhour < 8){
            recon = getyear()+ "年" + getmonth() + "月"+ (getday() - 1) +"日8时";
        }else {
            recon = getyear()+ "年" + getmonth() + "月"+ getday() +"日8时";
        }
        return recon;
    }

    public static String dateToString(Date date, String pattern) {
        String dateStr = null;

        try {
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            dateStr = df.format(date);
        } catch (Exception var4) {
            var4.printStackTrace();
        }

        return dateStr;
    }

    public static Integer getyear(){
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        return year;
    }

    public static Integer getmonth(){
        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH) + 1;
        return month;
    }

    public static Integer getday(){
        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DATE);
        return day;
    }

    public static Integer gethour(){
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        return hour;
    }

    public static String MD5(){
        String md5Str = null;
        Long timeMillis = System.currentTimeMillis();
        md5Str = DigestUtils.md5Hex(timeMillis.toString());
        return md5Str;
    }

    public static String commonMD5(String s){
        String md5Str = null;
        md5Str = DigestUtils.md5Hex(s);
        return md5Str;
    }

    public static String alexMD5(String s){
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

        try {
            byte[] btInput = s.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            char[] str = new char[md.length * 2];
            int k = 0;
            for (byte byte0 : md) {
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String MD5ToUpp32(String sourceStr) {
        try {
            StringBuffer buf = getMD5StringBuffer(sourceStr);
            return buf.toString().toUpperCase();// 32位加密
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static StringBuffer getMD5StringBuffer(String sourceStr) throws NoSuchAlgorithmException {
        // 获得MD5摘要算法的 MessageDigest对象
        MessageDigest mdInst = MessageDigest.getInstance("MD5");
        // 使用指定的字节更新摘要
        mdInst.update(sourceStr.getBytes());
        // 获得密文
        byte[] md = mdInst.digest();
        // 把密文转换成十六进制的字符串形式
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < md.length; i++) {
            int tmp = md[i];
            if (tmp < 0){
                tmp += 256;
            }
            if (tmp < 16){
                buf.append("0");
            }
            buf.append(Integer.toHexString(tmp));
        }
        return buf;
    }

    public static void main1(String[] args) {
        String y = getdiytm1();
        System.out.println(y);
    }

    /**
     * 计算CRC16校验码
     *
     * @param bytes
     *            字节数组
     * @return {@link String} 校验码
     * @since 1.0
     */
    public static String getCRC(byte[] bytes) {
        // CRC寄存器全为1
        int CRC = 0x0000ffff;
        // 多项式校验值
        int POLYNOMIAL = 0x0000a001;
        int i, j;
        for (i = 0; i < bytes.length; i++) {
            CRC ^= ((int) bytes[i] & 0x000000ff);
            for (j = 0; j < 8; j++) {
                if ((CRC & 0x00000001) != 0) {
                    CRC >>= 1;
                    CRC ^= POLYNOMIAL;
                } else {
                    CRC >>= 1;
                }
            }
        }
        // 结果转换为16进制
        String result = Integer.toHexString(CRC).toUpperCase();
        if (result.length() != 4) {
            StringBuffer sb = new StringBuffer("0000");
            result = sb.replace(4 - result.length(), 4, result).toString();
        }
        //高位在前地位在后
        //return result.substring(2, 4) + " " + result.substring(0, 2);
        // 交换高低位，低位在前高位在后
        return result.substring(2, 4) + " " + result.substring(0, 2);
    }

    public static String getCRC(String data) {
        data = data.replace(" ", "");
        int len = data.length();
        if (!(len % 2 == 0)) {
            return "0000";
        }
        int num = len / 2;
        byte[] para = new byte[num];
        for (int i = 0; i < num; i++) {
            int value = Integer.valueOf(data.substring(i * 2, 2 * (i + 1)), 16);
            para[i] = (byte) value;
        }
        return getCRC(para);
    }

    public static void mainx(String[] args) {
        String y = getdiytm1();
        System.out.println(y);
    }


    public static void main(String[] args) {
        String crcon = getCRC("630600140001");
        String crcoff = getCRC("630600140000");
        System.out.println("开: " + "630600140001"+crcon.replaceAll(" ",""));
        System.out.println("关: " + "630600140000"+crcoff.replaceAll(" ",""));
    }
}

