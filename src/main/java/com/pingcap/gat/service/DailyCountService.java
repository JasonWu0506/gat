package com.pingcap.gat.service;

import com.pingcap.gat.model.GhDailyCount;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface DailyCountService {

    Map<String, List<Integer>> getDailyCountByRepo(String org, String repo);
    List<String> getDatesForDailyBugs();
    int isDailyCountExisted(GhDailyCount ghDailyCount);
    int insertDailyCount(GhDailyCount ghDailyCount);
    int updateDailyCount(GhDailyCount ghDailyCount);
}
