package com.service.imp;

import com.mapper.NoticeMapper;
import com.model.Alexhqline;
import com.model.MessageVo;
import com.model.Openid;
import com.model.StAlarmPerson;
import com.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("noticeService")
public class NoticeServiceImp implements NoticeService {

    @Autowired
    private NoticeMapper noticeMapper;

    @Override
    public List<MessageVo> getlist(String tabname) {
        return noticeMapper.getlist(tabname);
    }

    @Override
    public List<Openid> getopenids() {
        return noticeMapper.getopenids();
    }

    @Override
    public List<StAlarmPerson> getperson() {
        return noticeMapper.getperson();
    }

    @Override
    public Integer addmeslog(MessageVo messageVo) {
        return noticeMapper.addmeslog(messageVo);
    }

    @Override
    public Map<String, Object> getmapq(Double z) {
        Alexhqline upq = noticeMapper.getupq(z);
        Alexhqline downq = noticeMapper.getdownq(z);
        Map<String, Object> res = new HashMap<>();
        res.put("up",upq);
        res.put("down",downq);
        return res;
    }
}
