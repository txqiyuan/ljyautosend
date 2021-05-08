package com.controller;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.httpclientpool.HttpConnectionManager;
import com.model.StAlarmPerson;
import com.utils.AlexUtil;
import com.utils.TrustAllTrustManager;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PhoneMessage {
    //对应你阿里云账户的 accessKeyId
    private static final String accessKeyId = "LTAI4G9dc5jiSwjKHZUBJzq4";
    //对应你阿里云账户的 accessKeySecret
    private static final String accessKeySecret = "NWhHHxIXdTclRmKy4pzAky64xruVTV";
    //对应签名名称
    private static final String signName="Alex商城";
    //对应模板代码
    private static final String templateCode="SMS_190273089";

    private static int mobile_code = (int)((Math.random()*9+1)*100000);

    private static Logger logger = LoggerFactory.getLogger(PhoneMessage.class);

    private static String jstname = "scrxkj";

    private static String jstpwd = "XuqBJIoC";

    private static String opKind = "51";

    private static String USER_AGENT = "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.7.6)";

    private static String jsturl = "http://userinterface.vcomcn.com/Opration.aspx";
    /**
     * 短信发送--阿里短信
     * @param telphone 发送的手机号
     */
    public static void SendMessage(String telphone, String pj, String z) {

        DefaultProfile profile = DefaultProfile.getProfile("default",
                accessKeyId, accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        //request.setProtocol(ProtocolType.HTTPS);
        request.setMethod(MethodType.POST);
        //阿里云对应发送短信的服务器地址
        request.setDomain("dysmsapi.aliyuncs.com");
        //对应的版本号
        request.setVersion("2017-05-25");

        request.setAction("SendSms");
        request.putQueryParameter("PhoneNumbers", telphone);
        request.putQueryParameter("SignName", signName);
        request.putQueryParameter("TemplateCode", templateCode);
        request.putQueryParameter("TemplateParam", "{\"code\":"+mobile_code+"}");
        try {
            CommonResponse response = client.getCommonResponse(request);
            System.out.println(response.getData());
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }

    public static void messageJST(List<String> phonemuns, String content) throws Exception {

        CloseableHttpClient client = null;
        try {
            client = HttpConnectionManager.getHttpClient(5000);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        HttpPost post = new HttpPost(buildRequestURI());
        post.addHeader("Accept","*/*");
        String phones = "";
        for (int i = 0;i < phonemuns.size();i ++){
            if (i == (phonemuns.size() - 1)){
                phones += phonemuns.get(i);
            }else {
                phones += phonemuns.get(i) + ",";
            }
        }
        String body;
        //body = XmlDom.createDom(jstname, AlexUtil.alexMD5(jstpwd), opKind, phonemuns, content);
        //pwd:47036F2F92B941980B5E24043904C46A
        body = "<Group Login_Name=\"" + jstname + "\" Login_Pwd=\"" + AlexUtil.alexMD5(jstpwd) + "\" OpKind=\"51\" InterFaceID=\"\" SerType=\"预警\">\n" +
                    "<E_Time>"+ AlexUtil.formatDatealex(new Date()) + "</E_Time>\n" +
                    "<Mobile>"+ phones +"</Mobile>\n" +
                    "<Content><![CDATA[" + content + "]]></Content>\n" +
                    "<ClientID>"+ System.currentTimeMillis() + "" + "</ClientID>\n" +
                "</Group>";
        post.setEntity(new StringEntity(body,"GBK"));
        CloseableHttpResponse resp = null;
        try {
            // 执行请求
            resp = client.execute(post);
            // 判断返回状态是否为200
            if (resp.getStatusLine().getStatusCode() == 200) {
                //内容写入文件
                HttpEntity entity = resp.getEntity();
                if (entity.getContent() != null) {
                    String res = EntityUtils.toString(resp.getEntity(), "utf-8");
                    System.out.println("短信接口调用结果状态码：" + res);
                }
            }
        }catch (Exception e) {
            logger.error(e.getMessage(), e);
        }finally {
            if (resp.getEntity() != null){
                resp.getEntity().getContent().close();
            }
            if (resp != null) {
                resp.close();
            }
        }
    }

    public static void messageJSTalex(List<StAlarmPerson> phonemuns, String content) throws Exception {

        CloseableHttpClient client = null;
        try {
            client = HttpConnectionManager.getHttpClient(5000);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        HttpPost post = new HttpPost(buildRequestURI());
        post.addHeader("Accept","*/*");
        String phones = "";
        for (int i = 0;i < phonemuns.size();i ++){
            if (i == (phonemuns.size() - 1)){
                phones += phonemuns.get(i).getphone();
            }else {
                phones += phonemuns.get(i).getphone() + ",";
            }
        }
        String body;
        //body = XmlDom.createDom(jstname, AlexUtil.alexMD5(jstpwd), opKind, phonemuns, content);
        //pwd:47036F2F92B941980B5E24043904C46A
        body = "<Group Login_Name=\"" + jstname + "\" Login_Pwd=\"" + AlexUtil.alexMD5(jstpwd) + "\" OpKind=\"51\" InterFaceID=\"\" SerType=\"预警\">\n" +
                "<E_Time>"+ AlexUtil.formatDatealex(new Date()) + "</E_Time>\n" +
                "<Mobile>"+ phones +"</Mobile>\n" +
                "<Content><![CDATA[" + content + "]]></Content>\n" +
                "<ClientID>"+ System.currentTimeMillis() + "" + "</ClientID>\n" +
                "</Group>";
        post.setEntity(new StringEntity(body,"GBK"));
        CloseableHttpResponse resp = null;
        try {
            // 执行请求
            resp = client.execute(post);
            // 判断返回状态是否为200
            if (resp.getStatusLine().getStatusCode() == 200) {
                //内容写入文件
                HttpEntity entity = resp.getEntity();
                if (entity.getContent() != null) {
                    String res = EntityUtils.toString(resp.getEntity(), "utf-8");
                    System.out.println("短信接口调用结果状态码：" + res);
                }
            }
        }catch (Exception e) {
            logger.error(e.getMessage(), e);
        }finally {
            if (resp.getEntity() != null){
                resp.getEntity().getContent().close();
            }
            if (resp != null) {
                resp.close();
            }
        }
    }

    public static void messageJSTx(List<String> phonemuns, String content) throws Exception {

        //  直接通过主机认证
        HostnameVerifier hv = new HostnameVerifier() {
            @Override
            public boolean verify(String urlHostName, SSLSession session) {
                return true;
            }
        };
        //  配置认证管理器
        TrustManager[] trustAllCerts = {new TrustAllTrustManager()};
        SSLContext sc = SSLContext.getInstance("SSL");
        SSLSessionContext sslsc = sc.getServerSessionContext();
        sslsc.setSessionTimeout(0);
        sc.init(null, trustAllCerts, null);
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        //  激活主机认证
        HttpsURLConnection.setDefaultHostnameVerifier(hv);
        HttpsURLConnection post = getHttpsConnection(jsturl);
        post.setDoInput(true);
        post.setDoOutput(true);
        post.setConnectTimeout(5000);
        post.setReadTimeout(15000);
        post.setRequestMethod("POST");
        post.setRequestProperty("User-Agent", USER_AGENT);
        post.setRequestProperty("Accept", "*/*");
        String phones = "";
        for (int i = 0;i < phonemuns.size();i ++){
            if (i == (phonemuns.size() - 1)){
                phones += phonemuns.get(i);
            }else {
                phones += phonemuns.get(i) + ",";
            }
        }
        String body;
        //body = XmlDom.createDom(jstname, AlexUtil.alexMD5(jstpwd), opKind, phonemuns, content);
        body = "<Group Login_Name=\"" + jstname + "\" Login_Pwd=\""+ AlexUtil.alexMD5(jstpwd) +"\" OpKind=\"51\" InterFaceID=\"\" SerType=”\n" +
                "短信类型”>\n" +
                "<E_Time>"+ AlexUtil.formatDatealex(new Date()) +"</E_Time>\n" +
                "<Mobile>"+ phones +"</Mobile>\n" +
                "<Content><![CDATA["+ content +"]]></Content>\n" +
                "<ClientID>"+ System.currentTimeMillis() + "" +"</ClientID>\n" +
                "</Group>";
        byte[] bytes = body.getBytes("utf-8");
        post.setRequestProperty("content-length", String.valueOf(bytes.length));
        post.connect();
        DataOutputStream dop = new DataOutputStream(post.getOutputStream());
        dop.write(bytes);
        dop.flush();
        dop.close();
        try {
            if (post.getResponseCode() == 200){
                InputStream is = post.getInputStream(); // 获取输入流，此时才真正建立链接
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader bufferReader = new BufferedReader(isr);
                String inputLine = "";
                String resultData = "";
                while ((inputLine = bufferReader.readLine()) != null) {
                    resultData += inputLine;
                }
                System.out.println(resultData);
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private static URI buildRequestURI() throws MalformedURLException {
        return URI.create(jsturl);
    }

    public static HttpsURLConnection getHttpsConnection(String connectingUrl) throws Exception {

        //URL url = new URL(connectingUrl);
        URL url= new URL(null, connectingUrl, new sun.net.www.protocol.https.Handler());
        HttpsURLConnection webRequest = (HttpsURLConnection) url.openConnection();
        return webRequest;
    }

    public static void main(String[] args) {
        SendMessage("13980024835",null,null);
        SendMessage("18328559890",null,null);
    }

    public static void main1(String[] args) throws Exception {
        List<String> ph = new ArrayList<>();
        ph.add("13980024835");
        //ph.add("18328559890");
        ph.add("18683788690");
        String content = "（测试）李家岩水情-铁索站站点测量降雨量(雨量157.5mm，水位768.1)超过预警值30mm(水位768m)，请注意关注和防范山洪和其次生灾害！";
        PhoneMessage.messageJST(ph,content);
    }
}
