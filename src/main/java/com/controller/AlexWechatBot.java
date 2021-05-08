package com.controller;

import io.github.biezhi.wechat.WeChatBot;
import io.github.biezhi.wechat.api.constant.Config;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AlexWechatBot extends WeChatBot {

    //登陆二维保存路径
    private static String assetsDir = "D:/rongxincode/qrcode/";

    //服务器
    //private static String assetsDir = "D:/rongxincode/qrcode/";

    private volatile static AlexWechatBot helloBot;

    public static void setAssetsDir(String assetsDir) {
        AlexWechatBot.assetsDir = assetsDir;
    }

    public static AlexWechatBot getInstance(){
        if(helloBot == null){
            synchronized (AlexWechatBot.class){
                if(helloBot ==null){
                    helloBot = new AlexWechatBot(Config.me().autoLogin(true).assetsDir(assetsDir).showTerminal(true));
                }
            }
        }
        return helloBot;
    }
    private AlexWechatBot(Config config) {
        super(config);
    }

    public static void main(String[] args) {
        getInstance().start();
    }
}
