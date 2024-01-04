package com.pingcap.gat.service.impl;

import com.pingcap.gat.mapper.GhDailyCountMapper;
import com.pingcap.gat.model.GhDailyCount;
import com.pingcap.gat.service.DailyCountService;
import com.pingcap.gat.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
@Service
public class DailyCountServiceImpl implements DailyCountService {
    private static final Logger log = LoggerFactory.getLogger(DailyCountServiceImpl.class);
    @Autowired
    GhDailyCountMapper ghDailyCountMapper;
    @Override
    public Map<String, List<Integer>> getDailyCountByRepo(String org, String repo) {

        List<GhDailyCount> ghDailyCountList = ghDailyCountMapper.getDailyCountByRepo(org, repo);
        Map<String, List<Integer>> dailyMap = new HashMap<>();
        List<Integer> dailyOpenCount = new ArrayList<>();
        List<Integer> dailyCreatedCount = new ArrayList<>();
        List<Integer> dailyClosedCount = new ArrayList<>();
        for(GhDailyCount ghDailyCount:ghDailyCountList){
            //dailyMap = new HashMap<>();
            String date = DateUtil.formatDateString(ghDailyCount.getTime(),"yyyy-MM-dd");
            int open = ghDailyCount.getOpen();
            int created = ghDailyCount.getCreated();
            int closed = ghDailyCount.getClosed();

            dailyOpenCount.add(open);
            dailyCreatedCount.add(created);
            dailyClosedCount.add(closed);

        }
        dailyMap.put("Open",dailyOpenCount);
        dailyMap.put("Created",dailyCreatedCount);
        dailyMap.put("Closed",dailyClosedCount);

        return dailyMap;
    }

    @Override
    public List<String> getDatesForDailyBugs() {
        List<Date> dates = ghDailyCountMapper.getDatesForDailyBugs();
        List<String> dateStr = new ArrayList<>();
        for(Date date: dates) {
            dateStr.add(DateUtil.formatDateString(date, "yyyy-MM-dd"));
        }
        return dateStr;
    }

    @Override
    public int isDailyCountExisted(GhDailyCount ghDailyCount) {
        log.info("DailyCountServiceImpl.isDailyCountExisted beginning");
        int count = ghDailyCountMapper.isDailyCountRecordExisting(ghDailyCount);
        log.info("DailyCountServiceImpl.isDailyCountExisted = {}", count);
        return count;
    }
    @Override
    public int updateDailyCount(GhDailyCount ghDailyCount){
        int count = ghDailyCountMapper.updateDailyCount(ghDailyCount);
        return count;
    }
    public int insertDailyCount(GhDailyCount ghDailyCount) {
        int count = ghDailyCountMapper.insertDailyCount(ghDailyCount);
        log.info("DailyCountServiceImpl.insertDailyCount = {}", count);
        return count;
    }

}
