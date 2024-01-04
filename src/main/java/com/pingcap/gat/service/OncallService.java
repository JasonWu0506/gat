package com.pingcap.gat.service;

import com.pingcap.gat.model.Oncall;

import java.util.List;

public interface OncallService {
    List<Oncall> getAllOncall();
    int addOncall(Oncall oncall);
}
