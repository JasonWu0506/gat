package com.pingcap.gat.mapper;

import com.pingcap.gat.model.SprintPlanObj;

import java.util.List;

public interface SprintPlanObjMapper {
     int insertSprintPlanObj(SprintPlanObj sprintPlanObj);
     List<SprintPlanObj> listAllRecord();
     int updateBySprint(SprintPlanObj sprintPlanObj);
}
