package com.mapper;

import com.model.EveryDayCon;
import com.model.MessageVo;
import com.model.Openid;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MessagesMapper {

    List<MessageVo> getvopjz1(@Param("tabname") String tabname);

    List<Openid> getopenids();

    int addwxpushlog(MessageVo messageVo);

    Integer issended(MessageVo messageVo);

    List<MessageVo> alexnowdata(@Param("tabname") String tabname);

    List<MessageVo> shitljy(@Param("tabname") String tableName);

    List<MessageVo> shitljyone(@Param("tabname") String tableName);

    Integer handlewechatalarm();

    Integer issendedzx(MessageVo v);

    MessageVo getvozx(@Param("tabname") String tableName);

    List<MessageVo> getvopj2(@Param("tabname") String tabname);

    List<MessageVo> getvoz2(@Param("tabname") String tabname);

    Integer updatevecon(EveryDayCon edc);

    EveryDayCon gettypeid(@Param("id") int id);
}
