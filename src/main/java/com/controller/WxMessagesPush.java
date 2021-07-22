package com.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.model.MessageVo;
import com.model.Openid;
import com.service.NoticeService;
import com.utils.AlexUtil;
import com.wechat.AzureToken;
import com.wechat.WxMessagePushUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class WxMessagesPush {

    @Autowired
    private NoticeService noticeService;

    private static String toopenid1 = "o68oCwhlk7oPBtRRFgJnb8TDBn6w";
    private static String toopenid2 = "o68oCwgcO6UOGMWr7i_PpfxEU3zo";
    //播报用
    private static String templeteid = "r9dgv85-uiKGeFlDbSEq6sOoVA64JrufcrSqIUclZdA";
    //预警用
    private static String templeteid1 = "x0nEqcIaglF5Cz8WB5flRXTr-acBlRCjKHBPD9WXbYs";
    private static String postUrl = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=";


    /**
     * 降雨量、水位、站点、时间、url地址--平时水情播报用
     * @param con
     * @param accesstoken
     * @param pushopenid
     * @throws IOException
     */
    public static void pushmessage(String con, AzureToken accesstoken, Openid pushopenid) throws IOException {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("touser", pushopenid.getWxopenid());   // openid
        jsonObject.put("template_id", templeteid);
        //jsonObject.put("url", "http://117.176.184.118:9696/alex/index.html");
        jsonObject.put("url", "http://117.176.184.118:8688/alex/index.html");

        JSONObject data = new JSONObject();
        JSONObject alexdate = new JSONObject();
        JSONObject alexcont = new JSONObject();
        alexdate.put("value", AlexUtil.formatDatealex(new Date()));
        alexdate.put("color", "#157efb");
        alexcont.put("value", con);
        alexcont.put("color", "#FF0000");
        data.put("alexdate", alexdate);
        data.put("alexcont", alexcont);
        jsonObject.put("data", data);
        String string = WxMessagePushUtil.sendPostJsonStr(postUrl + accesstoken.getToken(), jsonObject.toJSONString());
        JSONObject result = JSON.parseObject(string);
        int errcode = result.getIntValue("errcode");
        if(errcode == 0){
            // 发送成功
            /*msv.setUsername(pushopenid.getWxuser());
            msv.setPhone(pushopenid.getPhone());
            msv.setTm(new Date());
            messagesService.addwxpushlog(msv);*/
            System.out.println("发送成功");
        } else {
            // 发送失败
            System.out.println("发送失败");
        }
    }

    /**
     * 降雨量、水位、站点、时间、url地址--预警用
     * @param msv
     * @param accesstoken
     * @param pushopenid
     * @throws IOException
     */
    public static void pushmessagex(MessageVo msv, AzureToken accesstoken, Openid pushopenid) throws IOException {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("touser", pushopenid.getWxopenid());   // openid
        jsonObject.put("template_id", templeteid1);
        jsonObject.put("url", "http://117.176.184.118:8688/alex/index.html");

        JSONObject data = new JSONObject();
        JSONObject alexdate = new JSONObject();
        JSONObject alexid = new JSONObject();
        JSONObject alexname = new JSONObject();
        JSONObject alexrain = new JSONObject();
        JSONObject alexrainal = new JSONObject();
        JSONObject alexwater = new JSONObject();
        JSONObject alexwateral = new JSONObject();
        JSONObject remark = new JSONObject();
        alexdate.put("value", AlexUtil.formatDatealex(msv.getTm()));
        alexdate.put("color", "#157efb");
        alexid.put("value", msv.getStcd());
        alexid.put("color", "#157efb");
        alexname.put("value", msv.getStname() + "");
        alexname.put("color", "#157efb");
        alexrain.put("value", getoneq(msv.getPj()) + "");
        alexrain.put("color", "#FFA500");
        alexrainal.put("value", getoneq(msv.getRainRanges()) + "");
        alexrainal.put("color", "#FFA500");
        alexwater.put("value", gettwoq(msv.getZ()) + "");
        alexwater.put("color", "#FFA500");
        alexwateral.put("value", gettwoq(msv.getWaterRanges()) + "");
        alexwateral.put("color", "#FFA500");
        String tip = msv.getContent();
        //String tipen = "Please handle the order checking and delivery in time ... if you already know, please ignore it!!!";
        remark.put("value", tip);
        remark.put("color", "#FF0000");
        data.put("alexdate", alexdate);
        data.put("alexid", alexid);
        data.put("alexname", alexname);
        data.put("alexrain", alexrain);
        data.put("alexwater", alexwater);
        data.put("alexrainal", alexrainal);
        data.put("alexwateral", alexwateral);
        data.put("remark", remark);
        jsonObject.put("data", data);
        String string = WxMessagePushUtil.sendPostJsonStr(postUrl + accesstoken.getToken(), jsonObject.toJSONString());
        JSONObject result = JSON.parseObject(string);
        int errcode = result.getIntValue("errcode");
        if(errcode == 0){
            // 发送成功
            /*msv.setUsername(pushopenid.getWxuser());
            msv.setPhone(pushopenid.getPhone());
            msv.setTm(new Date());
            messagesService.addwxpushlog(msv);*/
            System.out.println("发送成功");
        } else {
            // 发送失败
            System.out.println("发送失败");
        }
    }

    public static Double gettwoq(Double q){
        BigDecimal bg = new BigDecimal(q);
        double f1 = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return f1;
    }

    public static Double getoneq(Double q){
        BigDecimal bg = new BigDecimal(q);
        double f1 = bg.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
        return f1;
    }

    public static void main(String[] args) throws IOException {
        MessageVo messageVo = new MessageVo();
        messageVo.setId("0066668803");
        messageVo.setStname("李家岩水情-琉璃村");
        messageVo.setPj(33.5);
        messageVo.setRainRanges(30.0);
        messageVo.setZ(524.0);
        messageVo.setWaterRanges(500.0);
        messageVo.setContent("截止目前"+ messageVo.getStname() +"站点雨量（水位）值已达到预警值，请注意做好山洪及次生灾害预防准备！！！");
        messageVo.setTm(new Date());
        List<Openid> openids = new ArrayList<>();
        List<MessageVo> messageVos = new ArrayList<>();
        messageVos.add(messageVo);
        Openid s = new Openid();
        s.setWxopenid("o68oCwhlk7oPBtRRFgJnb8TDBn6w");
        openids.add(s);
        //openids.add("o68oCwsZ5NkLIlCUCy2ZqPhV5Mvw");
        AzureToken token = new AccessTokenTimer().getWxAccessToken();
        //openids.add("o68oCwuBOtJaytKe4Ok4AiQEvdxQ");
        //openids.add("o68oCwl-qKWCuBIsuMpu_8HsVqoY");
        for(MessageVo v : messageVos){
            for (Openid x : openids){
                WxMessagesPush.pushmessagex(v, token, x);
            }
        }
    }

}
