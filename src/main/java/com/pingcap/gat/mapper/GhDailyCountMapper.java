package com.pingcap.gat.mapper;

import com.pingcap.gat.model.GhDailyCount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface GhDailyCountMapper {
    List<GhDailyCount> getAllDaily();
    int insertDailyCount(GhDailyCount ghDailyCount);
    List<Date> select(@Param("org") String org, @Param("repo") String repo, @Param("type") char type);
    List<GhDailyCount> getDailyCountByRepo(String org, String repo);
    List<Date> getDatesForDailyBugs();
    int isDailyCountRecordExisting(GhDailyCount ghDailyCount);
    int updateDailyCount(GhDailyCount ghDailyCount);
}
