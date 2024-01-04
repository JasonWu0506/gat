package com.pingcap.gat.service.impl;

import com.pingcap.gat.mapper.OncallMapper;
import com.pingcap.gat.model.Oncall;
import com.pingcap.gat.service.OncallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class OncallServiceImpl implements OncallService {
    @Autowired
    OncallMapper oncallMapper;
    @Override
    public List<Oncall> getAllOncall() {
        List<Oncall> oncalls = oncallMapper.listAllOncall();
        return oncalls;
    }

    @Override
    public int addOncall(Oncall oncall) {
        int error= oncallMapper.addOncall(oncall);
        return 0;
    }
}
