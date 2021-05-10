package com.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.model.Alexhqline;
import com.model.MessageVo;
import com.model.Openid;
import com.model.StAlarmPerson;
import com.service.MessagesService;
import com.service.NoticeService;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.utils.AlexUtil;
import com.wechat.AzureToken;
import com.wechat.MyX509TrustManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import sun.net.www.protocol.https.HttpsURLConnectionImpl;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.KeyEvent;
import java.io.*;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 李家岩水情播报及预警监控
 * by alex 王文超
 * 2020.05.12 09:23
 */
@Component
public class AccessTokenTimer {

    private static Logger logger = LoggerFactory.getLogger(AccessTokenTimer.class);

    public final static String AccessTokenUri = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";

    // 获取jsapi_ticket的接口地址（GET） 限2000（次/天）
    public final static String jsapi_ticket_url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=ACCESS_TOKEN&type=jsapi";

    public static final String  appid = "wx4f0039137c82d95e";

    public static final String appsecret = "adfe6140f1fff457b7336233e3010893";

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private MessagesService messagesService;

    //public static Map<String,Object> maptoken = new ConcurrentHashMap<>();

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    //public static ConcurrentHashMap<String, Object> copydata = new ConcurrentHashMap();

    //public static ConcurrentHashMap<String, Object> alarmdata = new ConcurrentHashMap();

    public static String meskey = "daymessage";

    //public static AlexWechatBot wxchatboot = AlexWechatBot.getInstance();

    //public static String wxuser1 = "刘苏";

    //public static String wxuser2 = "听你们八卦";

    //public static String wxuser3 = "李家岩公司防洪度汛工作群";

    public static Robot robot;

    public static final Runtime runtime = Runtime.getRuntime();

    //李家岩公司防洪度汛工作群
    public static String ljygroup = "李家岩公司防洪度汛工作群";

    public static String wchatexeaddr1 = "C:\\Program Files (x86)\\Tencent\\WeChat\\WeChat.exe";

    //公司ftp
    //public static String wchatexeaddr2 = "F:\\app\\Tencent\\WeChat\\WeChat.exe";
    //服务器
    public static String wchatexeaddr2 = "D:\\Program Files (x86)\\Tencent\\WeChat\\WeChat.exe";
    //public static String wchatexeaddr2 = "D:\\360软件\\WeChat\\WeChat.exe";

    /**
     * 使用Robot类 模拟安键输入Ctrl+F和Enter查找指定用户
     */
    static {
        try {
            if (robot == null){
                robot = new Robot();
            }
            //openWehat();
            //getwxscreen();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    /*static {
        copydata.put("pagedata","暂时还没有数据");
    }*/



    //统一使用该方法获取token
    /*public static Map<String,Object> getMaptoken(){
        if (maptoken != null){
            return maptoken;
        }else {
            AzureToken wxAccessToken = getWxAccessToken();
            maptoken.put("token",wxAccessToken);
            return maptoken;
        }
    }*/
    /**
     * 每一小时更新token,后期可用redis缓存，目前用个简易的
     * 0 0 0/1 * * ?
     */
    //@Scheduled(cron = "0 0 0/1 * * ? ")
    /*public void timeforwxtoken(){
        try {
            AzureToken wxAccessToken = getWxAccessToken();
            maptoken.put("token",wxAccessToken);
        } catch (Exception e) {
            logger.error("scheduled for wxtoken failed");
        }
    }*/

    /**
     * 定时器：需要在spring配置中开启定时任务扫描
     * 早8点
     */
    @Scheduled(cron = "20 1 8 * * ? ")
    public void timerfortask1() throws IOException {
        List<Openid> openids = new ArrayList<>();
        List<MessageVo> messageVo = new ArrayList<>();
        messageVo = noticeService.getlist(AlexUtil.getTableName(new Date()));
        String con = "【四川恒宣】水雨情实况通报("+ AlexUtil.getdiytm1() +"至未来24小时): "+ AlexUtil.getmonth() +"月"+ (AlexUtil.getday() - 1) +"日20时-"+ AlexUtil.getmonth() +"月"+ AlexUtil.getday() +"日8时水雨情：";
        String c1 = "";
        if (messageVo != null && messageVo.size() > 0){
            AzureToken token = getWxAccessToken();
            openids = noticeService.getopenids();
            for(int n= 0;n<messageVo.size();n++){
                if (n==messageVo.size()-1){
                    if ("0066668806".equals(messageVo.get(n).getStcd())){
                        if (messageVo.get(n).getZ() >= 770){
                            //有雨量
                            c1 = messageVo.get(n).getStname() + messageVo.get(n).getPj() + "mm" + " / 水位" + messageVo.get(n).getZ() + "m" + " / 流量" + getq(messageVo.get(n).getZ()) + "m³/s(770m流量)," + c1;
                            //无雨量
                            //c1 = messageVo.get(n).getStname() + "水位" + messageVo.get(n).getZ() + "m" + " / 流量" + getq(messageVo.get(n).getZ()) + "m³/s(770m流量)," + c1;
                        }else {
                            //有雨量
                            c1 = messageVo.get(n).getStname() + messageVo.get(n).getPj() + "mm" + " / 水位" + messageVo.get(n).getZ() + "m" + " / 流量" + getq(messageVo.get(n).getZ()) + "m³/s," + c1;
                            //无雨量
                            //c1 = messageVo.get(n).getStname() + "水位" + messageVo.get(n).getZ() + "m" + " / 流量" + getq(messageVo.get(n).getZ()) + "m³/s," + c1;
                        }
                    }else if ("0066668805".equals(messageVo.get(n).getStcd())){
                        if (messageVo.get(n).getZ() != null){
                            //有水位
                            c1 = messageVo.get(n).getStname() + messageVo.get(n).getPj() + "mm" + " / 水位" + gettwoq(messageVo.get(n).getZ()) + "m," + c1;
                        }else {
                            //无水位
                            c1 = messageVo.get(n).getStname() + messageVo.get(n).getPj() + "mm," + c1;
                        }
                    }else {
                        c1 += messageVo.get(n).getStname() + messageVo.get(n).getPj() + "mm.";
                    }
                }else {
                    if ("0066668806".equals(messageVo.get(n).getStcd())){
                        if (messageVo.get(n).getZ() >= 770){
                            //有雨量
                            c1 = messageVo.get(n).getStname() + messageVo.get(n).getPj() + "mm" + " / 水位" + messageVo.get(n).getZ() + "m" + " / 流量" + getq(messageVo.get(n).getZ()) + "m³/s(770m流量)," + c1;
                            //无雨量
                            //c1 = messageVo.get(n).getStname() + "水位" + messageVo.get(n).getZ() + "m" + " / 流量" + getq(messageVo.get(n).getZ()) + "m³/s(770m流量)," + c1;
                        }else {
                            //有雨量
                            c1 = messageVo.get(n).getStname() + messageVo.get(n).getPj() + "mm" + " / 水位" + messageVo.get(n).getZ() + "m" + " / 流量" + getq(messageVo.get(n).getZ()) + "m³/s," + c1;
                            //无雨量
                            //c1 = messageVo.get(n).getStname() + "水位" + messageVo.get(n).getZ() + "m" + " / 流量" + getq(messageVo.get(n).getZ()) + "m³/s," + c1;
                        }
                    }else if ("0066668805".equals(messageVo.get(n).getStcd())){
                        if (messageVo.get(n).getZ() != null){
                            //有水位
                            c1 = messageVo.get(n).getStname() + messageVo.get(n).getPj() + "mm" + " / 水位" + gettwoq(messageVo.get(n).getZ()) + "m," + c1;
                        }else {
                            //无水位
                            c1 = messageVo.get(n).getStname() + messageVo.get(n).getPj() + "mm," + c1;
                        }
                    }else {
                        c1 += messageVo.get(n).getStname() + messageVo.get(n).getPj() + "mm,";
                    }
                }
            }
            con = con + c1.replaceAll("李家岩水情-","");
            savemes(con);
            for (Openid op : openids){
                WxMessagesPush.pushmessage(con, token, op);
            }
            //微信自动发送
            alexautosendx(con);
        }
    }

    /**
     * 中午两点
     * @throws IOException
     */
    @Scheduled(cron = "20 1 14 * * ? ")
    public void timerfortask2() throws IOException {
        List<Openid> openids = new ArrayList<>();
        List<MessageVo> messageVo = new ArrayList<>();
        messageVo = noticeService.getlist(AlexUtil.getTableName(new Date()));
        String con = "【四川恒宣】水雨情实况通报("+ AlexUtil.getdiytm1() +"至未来24小时): " + AlexUtil.getmonth() +"月"+ AlexUtil.getday() +"日8时-"+ AlexUtil.getmonth() +"月"+ AlexUtil.getday() +"日14时水雨情：";
        String c1 = "";
        if (messageVo != null && messageVo.size() > 0){
            AzureToken token = getWxAccessToken();
            openids = noticeService.getopenids();
            for(int n= 0;n<messageVo.size();n++){
                if (n==messageVo.size()-1){
                    if ("0066668806".equals(messageVo.get(n).getStcd())){
                        if (messageVo.get(n).getZ() >= 770){
                            //有雨量
                            c1 = messageVo.get(n).getStname() + messageVo.get(n).getPj() + "mm" + " / 水位" + messageVo.get(n).getZ() + "m" + " / 流量" + getq(messageVo.get(n).getZ()) + "m³/s(770m流量)," + c1;
                            //无雨量
                            //c1 = messageVo.get(n).getStname() + "水位" + messageVo.get(n).getZ() + "m" + " / 流量" + getq(messageVo.get(n).getZ()) + "m³/s(770m流量)," + c1;
                        }else {
                            //有雨量
                            c1 = messageVo.get(n).getStname() + messageVo.get(n).getPj() + "mm" + " / 水位" + messageVo.get(n).getZ() + "m" + " / 流量" + getq(messageVo.get(n).getZ()) + "m³/s," + c1;
                            //无雨量
                            //c1 = messageVo.get(n).getStname() + "水位" + messageVo.get(n).getZ() + "m" + " / 流量" + getq(messageVo.get(n).getZ()) + "m³/s," + c1;
                        }
                    }else if ("0066668805".equals(messageVo.get(n).getStcd())){
                        if (messageVo.get(n).getZ() != null){
                            //有水位
                            c1 = messageVo.get(n).getStname() + messageVo.get(n).getPj() + "mm" + " / 水位" + gettwoq(messageVo.get(n).getZ()) + "m," + c1;
                        }else {
                            //无水位
                            c1 = messageVo.get(n).getStname() + messageVo.get(n).getPj() + "mm," + c1;
                        }
                    }else {
                        c1 += messageVo.get(n).getStname() + messageVo.get(n).getPj() + "mm.";
                    }
                }else {
                    if ("0066668806".equals(messageVo.get(n).getStcd())){
                        if (messageVo.get(n).getZ() >= 770){
                            //有雨量
                            c1 = messageVo.get(n).getStname() + messageVo.get(n).getPj() + "mm" + " / 水位" + messageVo.get(n).getZ() + "m" + " / 流量" + getq(messageVo.get(n).getZ()) + "m³/s(770m流量)," + c1;
                            //无雨量
                            //c1 = messageVo.get(n).getStname() + "水位" + messageVo.get(n).getZ() + "m" + " / 流量" + getq(messageVo.get(n).getZ()) + "m³/s(770m流量)," + c1;
                        }else {
                            //有雨量
                            c1 = messageVo.get(n).getStname() + messageVo.get(n).getPj() + "mm" + " / 水位" + messageVo.get(n).getZ() + "m" + " / 流量" + getq(messageVo.get(n).getZ()) + "m³/s," + c1;
                            //无雨量
                            //c1 = messageVo.get(n).getStname() + "水位" + messageVo.get(n).getZ() + "m" + " / 流量" + getq(messageVo.get(n).getZ()) + "m³/s," + c1;
                        }
                    }else if ("0066668805".equals(messageVo.get(n).getStcd())){
                        if (messageVo.get(n).getZ() != null){
                            //有水位
                            c1 = messageVo.get(n).getStname() + messageVo.get(n).getPj() + "mm" + " / 水位" + gettwoq(messageVo.get(n).getZ()) + "m," + c1;
                        }else {
                            //无水位
                            c1 = messageVo.get(n).getStname() + messageVo.get(n).getPj() + "mm," + c1;
                        }
                    }else {
                        c1 += messageVo.get(n).getStname() + messageVo.get(n).getPj() + "mm,";
                    }
                }
            }
            con = con + c1.replaceAll("李家岩水情-","");
            savemes(con);
            for (Openid op : openids){
                WxMessagesPush.pushmessage(con, token, op);
            }
            //微信自动发送
            alexautosendx(con);
        }
    }

    /**
     * 晚8点
     * @throws IOException
     */
    @Scheduled(cron = "20 1 20 * * ? ")
    public void timerfortask3() throws IOException {
        List<Openid> openids = new ArrayList<>();
        List<MessageVo> messageVo = new ArrayList<>();
        messageVo = noticeService.getlist(AlexUtil.getTableName(new Date()));
        String con = "【四川恒宣】水雨情实况通报("+ AlexUtil.getdiytm1() +"至未来24小时): " + AlexUtil.getmonth() +"月"+ AlexUtil.getday() +"日14时-"+ AlexUtil.getmonth() +"月"+ AlexUtil.getday() +"日20时水雨情：";
        String c1 = "";
        if (messageVo != null && messageVo.size() > 0){
            AzureToken token = getWxAccessToken();
            openids = noticeService.getopenids();
            for(int n= 0;n<messageVo.size();n++){
                if (n==messageVo.size()-1){
                    if ("0066668806".equals(messageVo.get(n).getStcd())){
                        if (messageVo.get(n).getZ() >= 770){
                            //有雨量
                            c1 = messageVo.get(n).getStname() + messageVo.get(n).getPj() + "mm" + " / 水位" + messageVo.get(n).getZ() + "m" + " / 流量" + getq(messageVo.get(n).getZ()) + "m³/s(770m流量)," + c1;
                            //无雨量
                            //c1 = messageVo.get(n).getStname() + "水位" + messageVo.get(n).getZ() + "m" + " / 流量" + getq(messageVo.get(n).getZ()) + "m³/s(770m流量)," + c1;
                        }else {
                            //有雨量
                            c1 = messageVo.get(n).getStname() + messageVo.get(n).getPj() + "mm" + " / 水位" + messageVo.get(n).getZ() + "m" + " / 流量" + getq(messageVo.get(n).getZ()) + "m³/s," + c1;
                            //无雨量
                            //c1 = messageVo.get(n).getStname() + "水位" + messageVo.get(n).getZ() + "m" + " / 流量" + getq(messageVo.get(n).getZ()) + "m³/s," + c1;
                        }
                    }else if ("0066668805".equals(messageVo.get(n).getStcd())){
                        if (messageVo.get(n).getZ() != null){
                            //有水位
                            c1 = messageVo.get(n).getStname() + messageVo.get(n).getPj() + "mm" + " / 水位" + gettwoq(messageVo.get(n).getZ()) + "m," + c1;
                        }else {
                            //无水位
                            c1 = messageVo.get(n).getStname() + messageVo.get(n).getPj() + "mm," + c1;
                        }
                    }else {
                        c1 += messageVo.get(n).getStname() + messageVo.get(n).getPj() + "mm.";
                    }
                }else {
                    if ("0066668806".equals(messageVo.get(n).getStcd())){
                        if (messageVo.get(n).getZ() >= 770){
                            //有雨量
                            c1 = messageVo.get(n).getStname() + messageVo.get(n).getPj() + "mm" + " / 水位" + messageVo.get(n).getZ() + "m" + " / 流量" + getq(messageVo.get(n).getZ()) + "m³/s(770m流量)," + c1;
                            //无雨量
                            //c1 = messageVo.get(n).getStname() + "水位" + messageVo.get(n).getZ() + "m" + " / 流量" + getq(messageVo.get(n).getZ()) + "m³/s(770m流量)," + c1;
                        }else {
                            //有雨量
                            c1 = messageVo.get(n).getStname() + messageVo.get(n).getPj() + "mm" + " / 水位" + messageVo.get(n).getZ() + "m" + " / 流量" + getq(messageVo.get(n).getZ()) + "m³/s," + c1;
                            //无雨量
                            //c1 = messageVo.get(n).getStname() + "水位" + messageVo.get(n).getZ() + "m" + " / 流量" + getq(messageVo.get(n).getZ()) + "m³/s," + c1;
                        }
                    }else if ("0066668805".equals(messageVo.get(n).getStcd())){
                        if (messageVo.get(n).getZ() != null){
                            //有水位
                            c1 = messageVo.get(n).getStname() + messageVo.get(n).getPj() + "mm" + " / 水位" + gettwoq(messageVo.get(n).getZ()) + "m," + c1;
                        }else {
                            //无水位
                            c1 = messageVo.get(n).getStname() + messageVo.get(n).getPj() + "mm," + c1;
                        }
                    }else {
                        c1 += messageVo.get(n).getStname() + messageVo.get(n).getPj() + "mm,";
                    }
                }
            }
            con = con + c1.replaceAll("李家岩水情-","");
            savemes(con);
            for (Openid op : openids){
                WxMessagesPush.pushmessage(con, token, op);
            }
            //微信自动发送
            alexautosendx(con);
        }
    }

    private void savemes(String con) {
        //加入缓存
        redisTemplate.opsForValue().set(meskey, JSON.toJSONString(con));
        redisTemplate.expire(meskey, 1, TimeUnit.DAYS);
    }

    /**
     * 预警扫描
     * 每30秒扫描数据库，监控数据是否正常
     * @throws IOException
     */
    @Scheduled(cron = "0/30 * * * * ?")
    public void timerforalarm() throws Exception {
        List<Openid> openids = new ArrayList<>();
        List<MessageVo> messageVo = new ArrayList<>();
        List<StAlarmPerson> person = new ArrayList<>();
        //预警
        messageVo = messagesService.getvopjz1(AlexUtil.getTableName(new Date()));
        String wxmescon = "【四川恒宣】李家岩水库水雨情测报平台预警(3小时)提示：当前时间 " + AlexUtil.formatDatealex(new Date()) + " : ";
        String wxx = wxmescon;
        if (messageVo != null && messageVo.size() > 0){
            AzureToken token = getWxAccessToken();
            openids = messagesService.getopenids();
            person = noticeService.getperson();
            for(MessageVo v : messageVo){
                //微信群推送  content
                if ("0066668806".equals(v.getStcd())){
                    wxmescon += v.getStname() + "水位" + v.getZ() + "m, " + "雨量" + v.getPj() + "mm," + "流量" + getq(v.getZ()) + "m³/s " + "(预警值: 水位" + v.getWaterRanges() + "m,雨量" + v.getRainRanges() + "mm)," + "请注意可能发生的山洪和山体塌方等自然灾害，请做好相关防洪度汛准备！";
                }else if ("0066668805".equals(v.getStcd())){
                    wxmescon += v.getStname() + "水位" + v.getZ() + "m," + "雨量" + v.getPj() + "mm "+ "(预警值: 水位" + v.getWaterRanges() + "m,雨量" + v.getRainRanges() + "mm)," + "请注意可能发生的山洪和山体塌方等自然灾害，请做好相关防洪度汛准备！";
                }else {
                    wxmescon += v.getStname() + "雨量" + v.getPj() + "mm " + "(预警值:雨量" + v.getRainRanges() + "mm)," + "请注意可能发生的山洪和山体塌方等自然灾害，请做好相关防洪度汛准备！";
                }

                Integer issended = messagesService.issended(v);
                if (issended == 0){
                    //微信推送
                    for (Openid x : openids){
                        try {
                            v.setContent(wxmescon);
                            WxMessagesPush.pushmessagex(v, token,x);
                            v.setUsername(x.getWxuser());
                            v.setPhone(x.getPhone());
                            v.setTm(new Date());
                            messagesService.addwxpushlog(v);
                            //wxmescon = wxx;
                        } catch (IOException e) {
                            logger.error("wechat push failed!");
                        }
                    }
                    //微信群推送
                    try {
                        //将wx置于前面，防止失去焦点
                        openWehat();
                        copy(wxmescon);
                        alexCtrlWithV('V');
                        alexEnter();
                        wxmescon = wxx;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //短信
                    /*for (StAlarmPerson sap : person){
                        try {
                            String xpj = "0";
                            String xz = "0";
                            if (v.getPj() != null){
                                xpj = v.getPj().toString();
                            }
                            if (v.getZ() != null){
                                xz = v.getZ().toString();
                            }
                            PhoneMessage.SendMessage(sap.getphone(), xpj, xz);
                            v.setId(AlexUtil.MD5());
                            v.setPhone(sap.getphone());
                            v.setUsername(sap.getName());
                            v.setTm(new Date());
                            noticeService.addmeslog(v);
                        } catch (Exception e) {
                            //e.printStackTrace();
                            logger.error("phone messages push failed!");
                        }
                    }*/
                    //集时通
                    /*try {
                        String con = v.getStname() + "站点测量降雨量" + v.getPj() + "mm，水位" + v.getZ() + "超过预警值30mm(水位768m)，请注意关注和防范山洪和其次生灾害！"
                        PhoneMessage.messageJSTalex(person,con);
                        v.setId(AlexUtil.MD5());
                        for (StAlarmPerson sap : person){
                            v.setPhone(sap.getphone());
                            v.setUsername(sap.getName());
                            v.setTm(new Date());
                            noticeService.addmeslog(v);
                        }
                    } catch (Exception e) {
                        //e.printStackTrace();
                        logger.error("phone messages push failed!");
                    }*/
                }
            }
        }
    }

    //@Scheduled(cron = "0/30 * * * * ?")
    public void fuckljyalarm() throws Exception {
        List<Openid> openids = new ArrayList<>();
        MessageVo messageVo = new MessageVo();
        //微信
        openids = messagesService.getopenids();
        //短信
        List<StAlarmPerson> person = noticeService.getperson();
        messageVo = messagesService.getvozx(AlexUtil.getTableName(new Date()));
        AzureToken token = getWxAccessToken();
        String phonemescon = "【四川恒宣】李家岩水库水雨情测报平台预警：";
        if (messageVo != null ){
            //短信预警信息
            phonemescon += messageVo.getStname();
            phonemescon += "水位" + messageVo.getZ() + "m, " + "流量" + getq(messageVo.getZ()) + "m³/s, " + "请注意可能发生的山洪和山体塌方等自然灾害，请做好相关防洪度汛准备！";
            Integer issended = messagesService.issendedzx(messageVo);
            if (issended == 0){
                //自动发送
                try {
                    //将wx置于前面，防止失去焦点
                    openWehat();
                    copy(phonemescon);
                    alexCtrlWithV('V');
                    alexEnter();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //微信推送

                for (Openid x : openids){
                    try {
                        WxMessagesPush.pushmessagex(messageVo, token, x);
                        messageVo.setUsername(x.getWxuser());
                        messageVo.setPhone(x.getPhone());
                        messageVo.setTm(new Date());
                        messageVo.setContent(phonemescon);
                        messagesService.addwxpushlog(messageVo);
                    } catch (IOException e) {
                        logger.error("wechat push failed!");
                    }
                }
                //微信群发送

                //短信
                /*for (StAlarmPerson sap : person){
                    try {
                        String xpj = "0";
                        String xz = "0";
                        if (v.getPj() != null){
                            xpj = v.getPj().toString();
                        }
                        if (v.getZ() != null){
                            xz = v.getZ().toString();
                        }
                        PhoneMessage.SendMessage(sap.getphone(), xpj, xz);
                        v.setId(AlexUtil.MD5());
                        v.setPhone(sap.getphone());
                        v.setUsername(sap.getName());
                        v.setTm(new Date());
                        noticeService.addmeslog(v);
                    } catch (Exception e) {
                        //e.printStackTrace();
                        logger.error("phone messages push failed!");
                    }
                }*/
                //集时通
                try {
                    PhoneMessage.messageJSTalex(person,phonemescon.replaceAll("【四川恒宣】", ""));
                    messageVo.setId(AlexUtil.MD5());
                    for (StAlarmPerson sap : person){
                        messageVo.setPhone(sap.getphone());
                        messageVo.setUsername(sap.getName());
                        messageVo.setContent(phonemescon);
                        messageVo.setTm(new Date());
                        noticeService.addmeslog(messageVo);
                    }
                } catch (Exception e) {
                    //e.printStackTrace();
                    logger.error("phone messages push failed!");
                }
            }
        }
    }

    //--------------------------------微信自动发送--------------------------------------

    //每隔一小时, 网页版微信不能登陆，该方案gg
    //@Scheduled(cron = "0 0 0/1 * * ?")
    //@Scheduled(cron = "0/50 * * * * ?")
    /*public void autosendforwechat1(){
        List<MessageVo> messageVo = new ArrayList<>();
        messageVo = messagesService.alexnowdata(AlexUtil.getTableName(new Date()));
        String con = "<微信自动发送测试>【四川恒宣】四川恒宣水雨情实况通报("+ AlexUtil.getdiytm2() +"至未来24小时): 截至目前["+ AlexUtil.formatDatealex(new Date()) + "]水雨情数据：";
        String c1 = "";
        if (messageVo != null && messageVo.size() > 0){
            for(int n= 0;n<messageVo.size();n++){
                if (n==messageVo.size()-1){
                    if ("0066668806".equals(messageVo.get(n).getStcd())){
                        if (messageVo.get(n).getZ() >= 770){
                            c1 = messageVo.get(n).getStname() + messageVo.get(n).getPj() + "mm" + " / 水位" + messageVo.get(n).getZ() + "m" + " / 流量" + getq(messageVo.get(n).getZ()) + "m³/s(770m流量)," + c1;
                        }else {
                            c1 = messageVo.get(n).getStname() + messageVo.get(n).getPj() + "mm" + " / 水位" + messageVo.get(n).getZ() + "m" + " / 流量" + getq(messageVo.get(n).getZ()) + "m³/s," + c1;
                        }
                    }else if ("0066668805".equals(messageVo.get(n).getStcd())){
                        c1 = messageVo.get(n).getStname() + messageVo.get(n).getPj() + "mm" + " / 水位" + messageVo.get(n).getZ() + "m," + c1;
                    }else {
                        c1 += messageVo.get(n).getStname() + messageVo.get(n).getPj() + "mm.";
                    }
                }else {
                    if ("0066668806".equals(messageVo.get(n).getStcd())){
                        if (messageVo.get(n).getZ() >= 770){
                            c1 = messageVo.get(n).getStname() + messageVo.get(n).getPj() + "mm" + " / 水位" + messageVo.get(n).getZ() + "m" + " / 流量" + getq(messageVo.get(n).getZ()) + "m³/s(770m流量)," + c1;
                        }else {
                            c1 = messageVo.get(n).getStname() + messageVo.get(n).getPj() + "mm" + " / 水位" + messageVo.get(n).getZ() + "m" + " / 流量" + getq(messageVo.get(n).getZ()) + "m³/s," + c1;
                        }
                    }else if ("0066668805".equals(messageVo.get(n).getStcd())){
                        c1 = messageVo.get(n).getStname() + messageVo.get(n).getPj() + "mm" + " / 水位" + messageVo.get(n).getZ() + "m," + c1;
                    }else {
                        c1 += messageVo.get(n).getStname() + messageVo.get(n).getPj() + "mm,";
                    }
                }
            }
            con = con + c1.replaceAll("李家岩水情-","");
            wxchatboot.sendMsgByName(wxuser1,con);
            wxchatboot.sendMsgByName(wxuser2,con);

        }
    }*/

    //------------------jna方式调用系统api----------------------------------------------

    //此方法废弃
    public static void getwxscreen(){
        WinDef.HWND hwnd = User32.INSTANCE.FindWindow(null, "微信");
        if (hwnd == null) {
            System.out.println("not running");
        } else {
            User32.INSTANCE.ShowWindow(hwnd, 9);
            User32.INSTANCE.SetForegroundWindow(hwnd);   // bring to front
        }
    }

    public static void alexautosendx(String con){
        //发送时时数据
        if (con != null){
            try {
                //将wx置于前面，防止失去焦点
                openWehat();
                copy(con);
                alexCtrlWithV('V');
                alexEnter();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * jna系统api模拟cv操作
     */
    //每50秒
    //@Scheduled(cron = "0/50 * * * * ?")
    //每3分钟
    //@Scheduled(cron = "0 0/3 * * * ?")
    //每5分钟
    //@Scheduled(cron = "0 1/5 * * * ?")
    //每10分钟
    //@Scheduled(cron = "0 1/10 * * * ?")
    //每15分钟
    //@Scheduled(cron = "0 0/15 * * * ?")
    //每60分钟
    //@Scheduled(cron = "25 1 * * * ?")
    //每30分钟
    //@Scheduled(cron = "25 1/30 * * * ?")
    public void autosendforwechat1() throws InterruptedException {
        String alexnowcon = null;
        try {
            alexnowcon = alexnow();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String alexonehourcon = null;
        try {
            alexonehourcon = alexonehour();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String alex20tonowcon = null;
        try {
            alex20tonowcon = alex20tonow();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //发送时时数据
        if (alexnowcon != null){
            try {
                //将wx置于前面，防止失去焦点
                openWehat();
                copy(alexnowcon);
                alexCtrlWithV('V');
                alexEnter();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //发送1小时数据
        if (alexonehourcon != null){
            try {
                //将wx置于前面，防止失去焦点
                Thread.sleep(5000);
                openWehat();
                copy(alexonehourcon);
                alexCtrlWithV('V');
                alexEnter();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //发送20--今数据
        if (alex20tonowcon != null){
            try {
                //将wx置于前面，防止失去焦点
                Thread.sleep(5000);
                openWehat();
                copy(alex20tonowcon);
                alexCtrlWithV('V');
                alexEnter();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

//**************************************robot模拟键盘操作******************************************

    //将需要输入的内容复制到裁剪版
    public static void copy(String text) {
        Clipboard sysc = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable tText = new StringSelection(text);
        sysc.setContents(tText, null);
        //System.out.println("已复制");
    }

    //通过Robot模拟Ctrl+V，粘贴文字内容,调用 sendCtrlWith('v');
    public static void alexCtrlWithV(char c) {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();

        }
        robot.keyPress(KeyEvent.VK_CONTROL);
        pressKey(c);
        robot.keyRelease(KeyEvent.VK_CONTROL);
    }

    //enter 发送
    public static void alexEnter() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);
    }

    public static void pressKey(char c) {
        try {
            String KeyName = (c+"").toUpperCase();
            Field f = KeyEvent.class.getDeclaredField("VK_" + KeyName);
            Object o = f.get(null);
            int keyCode = Integer.parseInt(o + "");
            robot.keyPress(keyCode);
            robot.keyRelease(keyCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**ctrl+alt+w 打开微信（窗口）
     *
     * 暂时未用，下有更好的方法
     */
    public static void openwx(){
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();

        }
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_ALT);
        pressKey('V');
        robot.keyRelease(KeyEvent.VK_CONTROL);
        robot.keyRelease(KeyEvent.VK_ALT);
    }

    /**
     * 打开微信,通过ctrl+f搜索群名字，再enter，再ctrl+v再enter发送
     *
     */
    public static void openWehat() {
        Process process = null;
        try {
            //1、打开微信
            process = runtime.exec(wchatexeaddr2);
            //延迟一点时间500ms,再ctrl+F搜索群名字 ljygroup
            robot.delay(500);
            robot.keyPress(KeyEvent.VK_CONTROL);
            pressKey('F');
            robot.keyRelease(KeyEvent.VK_CONTROL);
            robot.delay(100);
            //复制、粘贴群名称搜索
            copy(ljygroup);
            robot.delay(100);
            robot.keyPress(KeyEvent.VK_CONTROL);
            pressKey('V');
            robot.keyRelease(KeyEvent.VK_CONTROL);
            robot.delay(2000);
            //再选中，enter进入聊天界面并获取光标
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);
            robot.delay(100);
        } catch (final Exception e) {
            logger.info("打开微信执行失败!");
        }
    }

//**************************************robot模拟键盘操作******************************************


    public String alexnow(){
        List<MessageVo> messageVo = new ArrayList<>();
        messageVo = messagesService.alexnowdata(AlexUtil.getTableName(new Date()));
        String con = "【四川恒宣】最新水雨情实况通报("+ AlexUtil.getdiytm2() +"至未来24小时): 截至目前["+ AlexUtil.formatDatealex(new Date()) + "]水雨情数据：";
        String c1 = "";
        if (messageVo != null && messageVo.size() > 0){
            for(int n= 0;n<messageVo.size();n++){
                if (n==messageVo.size()-1){
                    if ("0066668806".equals(messageVo.get(n).getStcd())){
                        if (messageVo.get(n).getZ() >= 770){
                            //有雨量
                            c1 = messageVo.get(n).getStname() + messageVo.get(n).getPj() + "mm" + " / 水位" + messageVo.get(n).getZ() + "m" + " / 流量" + getq(messageVo.get(n).getZ()) + "m³/s(770m流量)," + c1;
                            //无雨量
                            //c1 = messageVo.get(n).getStname() + "水位" + messageVo.get(n).getZ() + "m" + " / 流量" + getq(messageVo.get(n).getZ()) + "m³/s(770m流量)," + c1;
                        }else {
                            //有雨量
                            c1 = messageVo.get(n).getStname() + messageVo.get(n).getPj() + "mm" + " / 水位" + messageVo.get(n).getZ() + "m" + " / 流量" + getq(messageVo.get(n).getZ()) + "m³/s," + c1;
                            //无雨量
                            //c1 = messageVo.get(n).getStname() + "水位" + messageVo.get(n).getZ() + "m" + " / 流量" + getq(messageVo.get(n).getZ()) + "m³/s," + c1;
                        }
                    }else if ("0066668805".equals(messageVo.get(n).getStcd())){
                        if (messageVo.get(n).getZ() != null){
                            //有水位
                            c1 = messageVo.get(n).getStname() + messageVo.get(n).getPj() + "mm" + " / 水位" + gettwoq(messageVo.get(n).getZ()) + "m," + c1;
                        }else {
                            //无水位
                            c1 = messageVo.get(n).getStname() + messageVo.get(n).getPj() + "mm," + c1;
                        }
                    }else {
                        c1 += messageVo.get(n).getStname() + messageVo.get(n).getPj() + "mm.";
                    }
                }else {
                    if ("0066668806".equals(messageVo.get(n).getStcd())){
                        if (messageVo.get(n).getZ() >= 770){
                            //有雨量
                            c1 = messageVo.get(n).getStname() + messageVo.get(n).getPj() + "mm" + " / 水位" + messageVo.get(n).getZ() + "m" + " / 流量" + getq(messageVo.get(n).getZ()) + "m³/s(770m流量)," + c1;
                            //无雨量
                            //c1 = messageVo.get(n).getStname() + "水位" + messageVo.get(n).getZ() + "m" + " / 流量" + getq(messageVo.get(n).getZ()) + "m³/s(770m流量)," + c1;
                        }else {
                            //有雨量
                            c1 = messageVo.get(n).getStname() + messageVo.get(n).getPj() + "mm" + " / 水位" + messageVo.get(n).getZ() + "m" + " / 流量" + getq(messageVo.get(n).getZ()) + "m³/s," + c1;
                            //无雨量
                            //c1 = messageVo.get(n).getStname() + "水位" + messageVo.get(n).getZ() + "m" + " / 流量" + getq(messageVo.get(n).getZ()) + "m³/s," + c1;
                        }
                    }else if ("0066668805".equals(messageVo.get(n).getStcd())){
                        if (messageVo.get(n).getZ() != null){
                            //有水位
                            c1 = messageVo.get(n).getStname() + messageVo.get(n).getPj() + "mm" + " / 水位" + gettwoq(messageVo.get(n).getZ()) + "m," + c1;
                        }else {
                            //无水位
                            c1 = messageVo.get(n).getStname() + messageVo.get(n).getPj() + "mm," + c1;
                        }
                    }else {
                        c1 += messageVo.get(n).getStname() + messageVo.get(n).getPj() + "mm,";
                    }
                }
            }
        }
        con = con + c1.replaceAll("李家岩水情-","");
        return con;
    }

    public String alexonehour(){
        List<MessageVo> messageVo = new ArrayList<>();
        messageVo = messagesService.shitljyone(AlexUtil.getTableName(new Date()));
        String con = "【四川恒宣】过去1小时水雨情实况通报：("+ AlexUtil.getonehourfront() +")水雨情数据：";
        String c1 = "";
        if (messageVo != null && messageVo.size() > 0){
            for(int n= 0;n<messageVo.size();n++){
                if (n==messageVo.size()-1){
                    if ("0066668806".equals(messageVo.get(n).getStcd())){
                        if (messageVo.get(n).getZ() >= 770){
                            //有雨量
                            c1 = messageVo.get(n).getStname() + messageVo.get(n).getPj() + "mm" + " / 水位" + messageVo.get(n).getZ() + "m" + " / 流量" + getq(messageVo.get(n).getZ()) + "m³/s(770m流量)," + c1;
                            //无雨量
                            //c1 = messageVo.get(n).getStname() + "水位" + messageVo.get(n).getZ() + "m" + " / 流量" + getq(messageVo.get(n).getZ()) + "m³/s(770m流量)," + c1;
                        }else {
                            //有雨量
                            c1 = messageVo.get(n).getStname() + messageVo.get(n).getPj() + "mm" + " / 水位" + messageVo.get(n).getZ() + "m" + " / 流量" + getq(messageVo.get(n).getZ()) + "m³/s," + c1;
                            //无雨量
                            //c1 = messageVo.get(n).getStname() + "水位" + messageVo.get(n).getZ() + "m" + " / 流量" + getq(messageVo.get(n).getZ()) + "m³/s," + c1;
                        }
                    }else if ("0066668805".equals(messageVo.get(n).getStcd())){
                        if (messageVo.get(n).getZ() != null){
                            //有水位
                            c1 = messageVo.get(n).getStname() + messageVo.get(n).getPj() + "mm" + " / 水位" + gettwoq(messageVo.get(n).getZ()) + "m," + c1;
                        }else {
                            //无水位
                            c1 = messageVo.get(n).getStname() + messageVo.get(n).getPj() + "mm," + c1;
                        }
                    }else {
                        c1 += messageVo.get(n).getStname() + messageVo.get(n).getPj() + "mm.";
                    }
                }else {
                    if ("0066668806".equals(messageVo.get(n).getStcd())){
                        if (messageVo.get(n).getZ() >= 770){
                            //有雨量
                            c1 = messageVo.get(n).getStname() + messageVo.get(n).getPj() + "mm" + " / 水位" + messageVo.get(n).getZ() + "m" + " / 流量" + getq(messageVo.get(n).getZ()) + "m³/s(770m流量)," + c1;
                            //无雨量
                            //c1 = messageVo.get(n).getStname() + "水位" + messageVo.get(n).getZ() + "m" + " / 流量" + getq(messageVo.get(n).getZ()) + "m³/s(770m流量)," + c1;
                        }else {
                            //有雨量
                            c1 = messageVo.get(n).getStname() + messageVo.get(n).getPj() + "mm" + " / 水位" + messageVo.get(n).getZ() + "m" + " / 流量" + getq(messageVo.get(n).getZ()) + "m³/s," + c1;
                            //无雨量
                            //c1 = messageVo.get(n).getStname() + "水位" + messageVo.get(n).getZ() + "m" + " / 流量" + getq(messageVo.get(n).getZ()) + "m³/s," + c1;
                        }
                    }else if ("0066668805".equals(messageVo.get(n).getStcd())){
                        if (messageVo.get(n).getZ() != null){
                            //有水位
                            c1 = messageVo.get(n).getStname() + messageVo.get(n).getPj() + "mm" + " / 水位" + gettwoq(messageVo.get(n).getZ()) + "m," + c1;
                        }else {
                            //无水位
                            c1 = messageVo.get(n).getStname() + messageVo.get(n).getPj() + "mm," + c1;
                        }
                    }else {
                        c1 += messageVo.get(n).getStname() + messageVo.get(n).getPj() + "mm,";
                    }
                }
            }
        }
        con = con + c1.replaceAll("李家岩水情-","");
        return con;
    }

    public String alex20tonow(){
        List<MessageVo> messageVo = new ArrayList<>();
        messageVo = messagesService.shitljy(AlexUtil.getTableName(new Date()));
        String con = "【四川恒宣】水雨情实况通报：" + AlexUtil.getyear() + "年" + AlexUtil.getmonth() + "月" + (AlexUtil.getday() - 1) + "日20时至今["+ AlexUtil.formatDatealex(new Date()) + "]水雨情数据：";
        String c1 = "";
        if (messageVo != null && messageVo.size() > 0){
            for(int n= 0;n<messageVo.size();n++){
                if (n==messageVo.size()-1){
                    if ("0066668806".equals(messageVo.get(n).getStcd())){
                        if (messageVo.get(n).getZ() >= 770){
                            //有雨量
                            c1 = messageVo.get(n).getStname() + messageVo.get(n).getPj() + "mm" + " / 水位" + messageVo.get(n).getZ() + "m" + " / 流量" + getq(messageVo.get(n).getZ()) + "m³/s(770m流量)," + c1;
                            //无雨量
                            //c1 = messageVo.get(n).getStname() + "水位" + messageVo.get(n).getZ() + "m" + " / 流量" + getq(messageVo.get(n).getZ()) + "m³/s(770m流量)," + c1;
                        }else {
                            //有雨量
                            c1 = messageVo.get(n).getStname() + messageVo.get(n).getPj() + "mm" + " / 水位" + messageVo.get(n).getZ() + "m" + " / 流量" + getq(messageVo.get(n).getZ()) + "m³/s," + c1;
                            //无雨量
                            //c1 = messageVo.get(n).getStname() + "水位" + messageVo.get(n).getZ() + "m" + " / 流量" + getq(messageVo.get(n).getZ()) + "m³/s," + c1;
                        }
                    }else if ("0066668805".equals(messageVo.get(n).getStcd())){
                        if (messageVo.get(n).getZ() != null){
                            //有水位
                            c1 = messageVo.get(n).getStname() + messageVo.get(n).getPj() + "mm" + " / 水位" + gettwoq(messageVo.get(n).getZ()) + "m," + c1;
                        }else {
                            //无水位
                            c1 = messageVo.get(n).getStname() + messageVo.get(n).getPj() + "mm," + c1;
                        }
                    }else {
                        c1 += messageVo.get(n).getStname() + messageVo.get(n).getPj() + "mm.";
                    }
                }else {
                    if ("0066668806".equals(messageVo.get(n).getStcd())){
                        if (messageVo.get(n).getZ() >= 770){
                            //有雨量
                            c1 = messageVo.get(n).getStname() + messageVo.get(n).getPj() + "mm" + " / 水位" + messageVo.get(n).getZ() + "m" + " / 流量" + getq(messageVo.get(n).getZ()) + "m³/s(770m流量)," + c1;
                            //无雨量
                            //c1 = messageVo.get(n).getStname() + "水位" + messageVo.get(n).getZ() + "m" + " / 流量" + getq(messageVo.get(n).getZ()) + "m³/s(770m流量)," + c1;
                        }else {
                            //有雨量
                            c1 = messageVo.get(n).getStname() + messageVo.get(n).getPj() + "mm" + " / 水位" + messageVo.get(n).getZ() + "m" + " / 流量" + getq(messageVo.get(n).getZ()) + "m³/s," + c1;
                            //无雨量
                            //c1 = messageVo.get(n).getStname() + "水位" + messageVo.get(n).getZ() + "m" + " / 流量" + getq(messageVo.get(n).getZ()) + "m³/s," + c1;
                        }
                    }else if ("0066668805".equals(messageVo.get(n).getStcd())){
                        if (messageVo.get(n).getZ() != null){
                            //有水位
                            c1 = messageVo.get(n).getStname() + messageVo.get(n).getPj() + "mm" + " / 水位" + gettwoq(messageVo.get(n).getZ()) + "m," + c1;
                        }else {
                            //无水位
                            c1 = messageVo.get(n).getStname() + messageVo.get(n).getPj() + "mm," + c1;
                        }
                    }else {
                        c1 += messageVo.get(n).getStname() + messageVo.get(n).getPj() + "mm,";
                    }
                }
            }
        }
        con = con + c1.replaceAll("李家岩水情-","");
        return con;
    }

    public Double getq(Double z){
        Map<String,Object> map = new HashMap<>();
        map = noticeService.getmapq(z);
        Alexhqline up = (Alexhqline) map.get("up");
        Alexhqline down;
        if (up.getZ() == 770){
            down = (Alexhqline) map.get("up");
        }else {
            down = (Alexhqline) map.get("down");
        }
        if (down.getZ() - up.getZ() == 0.0){
            return up.getQ();
        }else {
            Double a = (down.getQ() - up.getQ()) / (down.getZ() - up.getZ());
            Double k = down.getQ() - (a * down.getZ());
            Double resq = a * z + k;
            return gettwoq(resq);
        }
    }

    public Double gettwoq(Double q){
        BigDecimal bg = new BigDecimal(q);
        double f1 = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return f1;
    }

    /**
     * 使用redis 缓存accesstoken
     * @return
     */
    public AzureToken getWxAccessTokenx(){
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
            String tokenurl = AccessTokenUri.replace("APPID", appid).replace("APPSECRET", appsecret);
            //JSONObject jsonObject = new JSONObject();
            /*jsonObject.put("access_token","32_qs8HrBzwM6zLU14vBKXU3m7cwtkQpI8i-LY9dHBWRTwcp9iYMnB8fTTChTFvAfr8E22qN15p-hmOqup4UvpHgMhYseL5gfhEFb-3JD47eSJ4jziuNfhjHlvK4ANRfxOVX4jl3cYxnOyPNXugQYYbAGATBB");
            jsonObject.put("expires_in",7200);*/
            JSONObject jsonObject = httpRequest(tokenurl, "GET", null);
            if (null != jsonObject) {
                accesstoken = new AzureToken();
                accesstoken.setToken(jsonObject.getString("access_token"));
                String ticketurl = jsapi_ticket_url.replace("ACCESS_TOKEN", accesstoken.getToken());
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

    @Cacheable(value = "wxtoken")
    public AzureToken getWxAccessToken(){
        AzureToken accesstoken;
        try {
            String tokenurl = AccessTokenUri.replace("APPID", appid).replace("APPSECRET", appsecret);
            //JSONObject jsonObject = new JSONObject();
            /*jsonObject.put("access_token","32_qs8HrBzwM6zLU14vBKXU3m7cwtkQpI8i-LY9dHBWRTwcp9iYMnB8fTTChTFvAfr8E22qN15p-hmOqup4UvpHgMhYseL5gfhEFb-3JD47eSJ4jziuNfhjHlvK4ANRfxOVX4jl3cYxnOyPNXugQYYbAGATBB");
            jsonObject.put("expires_in",7200);*/
            JSONObject jsonObject = httpRequest(tokenurl, "GET", null);
            if (null != jsonObject) {
                accesstoken = new AzureToken();
                accesstoken.setToken(jsonObject.getString("access_token"));
                String ticketurl = jsapi_ticket_url.replace("ACCESS_TOKEN", accesstoken.getToken());
                JSONObject jsonticket = httpRequest(ticketurl, "GET", null);
                if (null != jsonticket){
                    accesstoken.setTicket(jsonticket.getString("ticket"));
                }
                accesstoken.setAddTime(System.currentTimeMillis());
                accesstoken.setExpiresIn(jsonObject.getIntValue("expires_in"));
                return accesstoken;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

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

    private static URI buildRequestURI() throws MalformedURLException {
        return URI.create(AccessTokenUri.replace("APPID", appid).replace("APPSECRET", appsecret));
    }

    public static void main(String[] args) throws Exception {
        /*List<String> ph = new ArrayList<>();
        //ph.add("18628905593");
        ph.add("13980024835");
        String content = "李家岩水情-铁索站站点测量降雨量(雨量157.5mm，水位768.1)超过预警值30mm(水位768m)，请注意关注和防范山洪和其次生灾害！";
        PhoneMessage.messageJST(ph,content);*/
        openWehat();
        System.out.println("第一次");
        Thread.sleep(10000);
        openWehat();
        System.out.println("第二次");
    }
}
