package com.service.imp;

import com.mapper.MessagesMapper;
import com.model.EveryDayCon;
import com.model.MessageVo;
import com.model.Openid;
import com.service.MessagesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("messagesService")
public class MessagesServiceImpl implements MessagesService {

    @Autowired
    private MessagesMapper messagesMapper;


    @Override
    public List<MessageVo> getvopjz1(String tabname) {
        List<MessageVo> list = new ArrayList<>();
        list = messagesMapper.getvopjz1(tabname);
        if (list.size() == 0){
            list = messagesMapper.getvopj2(tabname);
            if (list.size() == 0){
                list = messagesMapper.getvoz2(tabname);
            }
        }
        return list;
    }

    @Override
    public List<Openid> getopenids() {
        return messagesMapper.getopenids();
    }

    @Override
    public int addwxpushlog(MessageVo messageVo) {
        return messagesMapper.addwxpushlog(messageVo);
    }

    @Override
    public Integer issended(MessageVo messageVo) {
        return messagesMapper.issended(messageVo);
    }

    @Override
    public List<MessageVo> alexnowdata(String tabname) {
        return messagesMapper.alexnowdata(tabname);
    }

    @Override
    public List<MessageVo> shitljy(String tableName) {
        return messagesMapper.shitljy(tableName);
    }

    @Override
    public List<MessageVo> shitljyone(String tableName) {
        return messagesMapper.shitljyone(tableName);
    }

    @Override
    public Integer handlewechatalarm() {
        return messagesMapper.handlewechatalarm();
    }

    @Override
    public Integer issendedzx(MessageVo v) {
        return messagesMapper.issendedzx(v);
    }

    @Override
    public MessageVo getvozx(String tableName) {
        return messagesMapper.getvozx(tableName);
    }

    @Override
    public Integer updatevecon(EveryDayCon edc) {
        return messagesMapper.updatevecon(edc);
    }

    @Override
    public EveryDayCon gettypeid(int id) {
        return messagesMapper.gettypeid(id);
    }
}
