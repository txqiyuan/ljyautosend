package com.mapper;

import com.model.Alexhqline;
import com.model.MessageVo;
import com.model.Openid;
import com.model.StAlarmPerson;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface NoticeMapper {

    List<MessageVo> getlist(@Param("tabname") String tabname);

    List<Openid> getopenids();

    List<StAlarmPerson> getperson();

    Integer addmeslog(MessageVo messageVo);

    Alexhqline getupq(@Param("z") Double z);

    Alexhqline getdownq(@Param("z") Double z);
}
