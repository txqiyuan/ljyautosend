package com.controller;

import com.alibaba.fastjson.JSONObject;
import com.model.Alexhqline;
import com.model.MessageVo;
import com.service.MessagesService;
import com.service.NoticeService;
import com.utils.AlexUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

@Controller
@RequestMapping("/byalex")
@Api(tags = "成都李家岩水库水雨情测报系统")
public class AlexController {

    @Autowired
    private MessagesService messagesService;

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private WeixinUtil weixinUtil;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static Logger logger = LoggerFactory.getLogger(AlexController.class);

    @PostMapping("/shitljydata")
    @ApiOperation("每日固定时段水雨情实况")
    @ResponseBody
    public String getpushmessagesdata(HttpServletRequest request, HttpServletResponse response){
        Map<String,Object> res = new HashMap<>();
        String alex = null;
        try {
            if (redisTemplate.hasKey(AccessTokenTimer.meskey)) {
                alex = redisTemplate.opsForValue().get(AccessTokenTimer.meskey);
            }
        } catch (Exception e) {
            logger.error("redis connection failed!");
        }
        if (alex == null || "".equals(alex)){
            alex = "暂时还没有数据";
        }
        alex = alex.replace("\"", "");
        res.put("code",0);
        res.put("msg","");
        res.put("res",alex);
        return JSONObject.toJSONString(res);
    }

    @GetMapping("/share")
    @ApiOperation("微信分享")
    @ResponseBody
    public String share(HttpServletRequest request) {
        String urlTemp = "http://" + request.getServerName() + request.getContextPath();
        String urlpath = "http://" + request.getServerName();
        String appUrl = request.getParameter("url");
        if (request.getParameter("code") != null) {
            appUrl += "&code=" + request.getParameter("code");
        }
        if (request.getParameter("state") != null) {
            appUrl += "&state=" + request.getParameter("state");
        }
        Map<String, Object> signature = weixinUtil.getSignature(appUrl, urlTemp, urlpath);
        return JSONObject.toJSONString(signature);
    }

    @PostMapping("/alexnowdata")
    @ApiOperation("实时数据")
    @ResponseBody
    public String alexnowdata(HttpServletRequest request, HttpServletResponse response){
        Map<String,Object> res = new HashMap<>();
        List<MessageVo> messageVo = new ArrayList<>();
        messageVo = messagesService.alexnowdata(AlexUtil.getTableName(new Date()));
        String con = "【四川恒宣】水雨情实况通报("+ AlexUtil.getdiytm2() +"至未来24小时): 截至目前["+ AlexUtil.formatDatealex(new Date()) + "]水雨情数据：";
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
            res.put("code",0);
            res.put("msg","");
            res.put("res",con);
            return JSONObject.toJSONString(res);
        }else {
            res.put("code",0);
            res.put("msg","");
            res.put("res","遇到未知情况，没有数据返回！");
            return JSONObject.toJSONString(res);
        }
    }

    //昨晚20点至今
    @PostMapping("/shitljy")
    @ApiOperation("昨日8点至今数据")
    @ResponseBody
    public String shitljy(HttpServletRequest request, HttpServletResponse response){
        Map<String,Object> res = new HashMap<>();
        List<MessageVo> messageVo = new ArrayList<>();
        messageVo = messagesService.shitljy(AlexUtil.getTableName(new Date()));
        String con = "【四川恒宣】水雨情实况通报：" + AlexUtil.getyear() + "年" + AlexUtil.getmonth() + "月" + (AlexUtil.getday() - 1) + "日20时至今["+ AlexUtil.formatDatealex(new Date()) + "]水雨情数据：";
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
            res.put("code",0);
            res.put("msg","");
            res.put("res",con);
            return JSONObject.toJSONString(res);
        }else {
            res.put("code",0);
            res.put("msg","");
            res.put("res","遇到未知情况，没有数据返回！");
            return JSONObject.toJSONString(res);
        }
    }

    //最近1小时
    @PostMapping("/shitljyone")
    @ApiOperation("实时1小时数据")
    @ResponseBody
    public String shitljyone(HttpServletRequest request, HttpServletResponse response){
        Map<String,Object> res = new HashMap<>();
        List<MessageVo> messageVo = new ArrayList<>();
        messageVo = messagesService.shitljyone(AlexUtil.getTableName(new Date()));
        String con = "【四川恒宣】水雨情实况通报：1小时("+ AlexUtil.getonehourfront() +")水雨情数据：";
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
            res.put("code",0);
            res.put("msg","");
            res.put("res",con);
            return JSONObject.toJSONString(res);
        }else {
            res.put("code",0);
            res.put("msg","");
            res.put("res","遇到未知情况，没有数据返回！");
            return JSONObject.toJSONString(res);
        }
    }

    //一键处理微信预警推送
    @PostMapping("/handlewechatalarm")
    @ApiOperation("一键处理预警推送信息")
    @ResponseBody
    public String handlewechatalarm(HttpServletRequest request, HttpServletResponse response){
        Map<String,Object> res = new HashMap<>();
        Integer re = messagesService.handlewechatalarm();
        if (re > 0){
            res.put("code",0);
            res.put("msg","");
            res.put("res","处理完成");
            return JSONObject.toJSONString(res);
        }else {
            res.put("code",0);
            res.put("msg","");
            res.put("res","没有预警信息处理");
            return JSONObject.toJSONString(res);
        }
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

    public static void main(String[] args) {
        String dc = "\"zheshisihi\"";
        String dcx = "\"zheshixcvzxvxvxcvxcvsihi\"";
        String cv = "sdffdsf";
        System.out.println(dc);
        System.out.println(cv);
        String we = cv.replace("\"", "");
        System.out.println(we);
        System.out.println(we.lastIndexOf("\""));
    }
}
