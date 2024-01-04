package com.pingcap.gat.service.impl;

import com.pingcap.gat.mapper.CodeCoverageMapper;
import com.pingcap.gat.model.CodeCoverage;
import com.pingcap.gat.service.CodeCoverageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
@Service
public class CodeCoverageServiceImpl implements CodeCoverageService{
    @Autowired
    CodeCoverageMapper coverageMapper;
    public List<CodeCoverage> getRepoCoverage(String repo){
        return coverageMapper.getRepoCoverage(repo);

    }
    public int insertCodeCoverageRecord(CodeCoverage codeCoverage){
        return coverageMapper.insertCodeCoverageRecord(codeCoverage);


    }

    @Override
    public List<String> getNameList(String org, String repo, String branch, Date date) {
        return coverageMapper.getNameList(org, repo,branch, date);
    }

    @Override
    public List<CodeCoverage> getCoverageItems(String org, String repo, String branch, Date date, String nameLike) {
        nameLike = nameLike + "/%";
        System.out.println("nameLike = "+nameLike);
        return coverageMapper.getCoverageItems(org, repo,branch, date,nameLike);
    }
}
