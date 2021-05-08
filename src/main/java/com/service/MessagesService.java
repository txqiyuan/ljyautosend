package com.service;

import com.model.MessageVo;
import com.model.Openid;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MessagesService {

    List<MessageVo> getvopjz1(String tabname);

    List<Openid> getopenids();

    int addwxpushlog(MessageVo messageVo);

    Integer issended(MessageVo messageVo);


    List<MessageVo> alexnowdata(String tabname);

    List<MessageVo> shitljy(String tableName);

    List<MessageVo> shitljyone(String tableName);

    Integer handlewechatalarm();

    Integer issendedzx(MessageVo v);

    MessageVo getvozx(String tableName);

}
