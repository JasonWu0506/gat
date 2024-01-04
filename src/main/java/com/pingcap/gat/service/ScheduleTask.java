package com.pingcap.gat.service;

public interface ScheduleTask {
    int syncIssuesStatus();
    int syncDailyOpenClose();
}
