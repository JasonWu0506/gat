package com.pingcap.gat.service;

import com.pingcap.gat.model.CodeCoverage;

import java.util.Date;
import java.util.List;

public interface CodeCoverageService {
    List<CodeCoverage> getRepoCoverage(String repo);
    int insertCodeCoverageRecord(CodeCoverage codeCoverage);
    List<String> getNameList(String org, String repo, String branch, Date date);
    List<CodeCoverage> getCoverageItems(String org, String repo, String branch, Date date, String nameLike);
    boolean contains(String repo, String name, )
}
