package com.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wechat.AzureToken;
import com.wechat.MyX509TrustManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import sun.net.www.protocol.https.HttpsURLConnectionImpl;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class WeixinUtil {


    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static Logger logger = LoggerFactory.getLogger(WeixinUtil.class);

    /**
     * 外部获取签名入口类
     * @param appUrl    应用的url
     * @return
     */
    public Map<String, Object> getSignature(String appUrl, String url, String urlpath) {
        // 生成签名的随机串
        String noncestr = UUID.randomUUID().toString();
        if (appUrl == null || "".equals(appUrl)) {
            return null;
        }
        String signature = null;
        AzureToken wxAccessToken = getWxAccessToken();
        signature = signature(wxAccessToken.getTicket(), wxAccessToken.getAddTime().toString(), noncestr, appUrl);
        Map<String, Object> map = new HashMap<>();
        map.put("appId", AccessTokenTimer.appid);
        map.put("timestamp", wxAccessToken.getAddTime().toString());
        map.put("nonceStr", noncestr);
        map.put("appUrl", appUrl);
        map.put("signature", signature);
        map.put("url", url);
        map.put("urlpath", urlpath);
        return map;
    }

    public AzureToken getWxAccessToken(){
        AzureToken accesstoken;
        String key = "wxtoken";
        try {
            if (redisTemplate.hasKey(key)) {
                String value = redisTemplate.opsForValue().get(key);
                accesstoken = JSON.parseObject(value, AzureToken.class);
                return accesstoken;
            }
        } catch (Exception e) {
            logger.error("redis connection failed!");
        }
        try {
            String tokenurl = AccessTokenTimer.AccessTokenUri.replace("APPID", AccessTokenTimer.appid).replace("APPSECRET", AccessTokenTimer.appsecret);
            JSONObject jsonObject = httpRequest(tokenurl, "GET", null);
            if (null != jsonObject) {
                accesstoken = new AzureToken();
                accesstoken.setToken(jsonObject.getString("access_token"));
                String ticketurl = AccessTokenTimer.jsapi_ticket_url.replace("ACCESS_TOKEN", accesstoken.getToken());
                JSONObject jsonticket = httpRequest(ticketurl, "GET", null);
                if (null != jsonticket){
                    accesstoken.setTicket(jsonticket.getString("ticket"));
                }
                accesstoken.setAddTime(System.currentTimeMillis());
                //正常过期时间是7200秒，此处设置3600秒读取一次
                accesstoken.setExpiresIn(jsonObject.getIntValue("expires_in"));
                try {
                    //加入缓存
                    redisTemplate.opsForValue().set(key, JSON.toJSONString(accesstoken));
                    redisTemplate.expire(key, 3600, TimeUnit.SECONDS);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return accesstoken;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 签名
     *
     * @param timestamp
     * @return
     */
    private static String signature(String jsapi_ticket, String timestamp, String noncestr, String url) {
        jsapi_ticket = "jsapi_ticket=" + jsapi_ticket;
        timestamp = "timestamp=" + timestamp;
        noncestr = "noncestr=" + noncestr;
        url = "url=" + url;
        String[] arr = new String[]{jsapi_ticket, noncestr, timestamp, url};
        // 将token、timestamp、nonce,url参数进行字典序排序
        Arrays.sort(arr);
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            content.append(arr[i]);
            if (i != arr.length - 1) {
                content.append("&");
            }
        }
        MessageDigest md = null;
        String tmpStr = null;

        try {
            md = MessageDigest.getInstance("SHA-1");
            // 将三个参数字符串拼接成一个字符串进行sha1加密
            byte[] digest = md.digest(content.toString().getBytes());
            tmpStr = byteToStr(digest);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        content = null;
        return tmpStr;
    }

    /**
     * 发起https请求并获取结果
     *
     * @param requestUrl    请求地址
     * @param requestMethod 请求方式（GET、POST）
     * @param outputStr     提交的数据
     * @return JSONObject(通过JSONObject.get(key)的方式获取json对象的属性值)
     */
    private static JSONObject httpRequest(String requestUrl, String requestMethod, String outputStr) {
        JSONObject jsonObject = null;
        StringBuffer buffer = new StringBuffer();
        try {
            // 创建SSLContext对象，并使用我们指定的信任管理器初始化
            TrustManager[] tm = {new MyX509TrustManager()};
            SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
            sslContext.init(null, tm, new java.security.SecureRandom());
            // 从上述SSLContext对象中得到SSLSocketFactory对象
            SSLSocketFactory ssf = sslContext.getSocketFactory();

            URL url = new URL(requestUrl);
            HttpsURLConnectionImpl httpUrlConn = (HttpsURLConnectionImpl) url.openConnection();
            httpUrlConn.setSSLSocketFactory(ssf);

            httpUrlConn.setDoOutput(true);
            httpUrlConn.setDoInput(true);
            httpUrlConn.setUseCaches(false);
            // 设置请求方式（GET/POST）
            httpUrlConn.setRequestMethod(requestMethod);

            if ("GET".equalsIgnoreCase(requestMethod))
                httpUrlConn.connect();

            // 当有数据需要提交时
            if (null != outputStr) {
                OutputStream outputStream = httpUrlConn.getOutputStream();
                // 注意编码格式，防止中文乱码
                outputStream.write(outputStr.getBytes("UTF-8"));
                outputStream.close();
            }

            // 将返回的输入流转换成字符串
            InputStream inputStream = httpUrlConn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            bufferedReader.close();
            inputStreamReader.close();
            // 释放资源
            inputStream.close();
            inputStream = null;
            httpUrlConn.disconnect();
            jsonObject = JSONObject.parseObject(buffer.toString());
            // jsonObject = JSONObject.fromObject(buffer.toString());
        } catch (ConnectException ce) {
            System.out.println("Weixin server connection timed out.");
        } catch (Exception e) {
            System.out.println("https request error:{}" + e.getMessage());
        }
        return jsonObject;
    }

    /**
     * 将字节数组转换为十六进制字符串
     *
     * @param byteArray
     * @return
     */
    private static String byteToStr(byte[] byteArray) {
        String strDigest = "";
        for (int i = 0; i < byteArray.length; i++) {
            strDigest += byteToHexStr(byteArray[i]);
        }
        return strDigest;
    }

    /**
     * 将字节转换为十六进制字符串
     *
     * @param mByte
     * @return
     */
    private static String byteToHexStr(byte mByte) {

        char[] Digit = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        char[] tempArr = new char[2];
        tempArr[0] = Digit[(mByte >>> 4) & 0X0F];
        tempArr[1] = Digit[mByte & 0X0F];

        String s = new String(tempArr);
        return s;
    }

}