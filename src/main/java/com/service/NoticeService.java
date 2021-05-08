package com.service;

import com.model.MessageVo;
import com.model.Openid;
import com.model.StAlarmPerson;

import java.util.List;
import java.util.Map;

public interface NoticeService {

    List<MessageVo> getlist(String tabname);

    List<Openid> getopenids();

    List<StAlarmPerson> getperson();

    Integer addmeslog(MessageVo messageVo);

    Map<String, Object> getmapq(Double z);
}
