package com.pingcap.gat.mapper;

import com.pingcap.gat.model.CodeCoverage;

import java.util.Date;
import java.util.List;

public interface CodeCoverageMapper {
    List<CodeCoverage> getRepoCoverage(String repo);
    int insertCodeCoverageRecord(CodeCoverage codeCoverage);
    List<String> getNameList(String org, String repo, String branch, Date insertAt);
    List<CodeCoverage> getCoverageItems(String org, String repo, String branch, Date insertAt, String nameLike);
}
