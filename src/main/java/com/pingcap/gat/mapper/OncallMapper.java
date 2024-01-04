package com.pingcap.gat.mapper;

import com.pingcap.gat.model.Oncall;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OncallMapper {
    List<Oncall> listAllOncall();

    int addOncall(Oncall oncall);
}
