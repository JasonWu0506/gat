package com.pingcap.gat.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pingcap.gat.model.CodeCoverage;
import com.pingcap.gat.service.CodeCoverageService;
import com.pingcap.gat.util.CodeCovUtil;
import com.pingcap.gat.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@RestController
@RequestMapping("/cc")
public class CodeCoverController {
    @Value("${com.pingcap.gat.ccrepos}")
    private String ccRepos;
    @Autowired
    CodeCoverageService coverageService;
    private static final Logger log = LoggerFactory.getLogger(CodeCoverController.class);
    @RequestMapping("/insert")
    public int insertCCRecord(@RequestParam("org") String org, @RequestParam("repo") String repo, @RequestParam("branch") String branch) {
        int error = 0;
        List<CodeCoverage> coverageList = CodeCovUtil.getCodeCoverageList(org, repo, branch);
        for (CodeCoverage codeCoverage : coverageList) {
            error = coverageService.insertCodeCoverageRecord(codeCoverage);
            if(error == 1) {
                log.info("insert CC record succeeded: {}", codeCoverage.toString());
            }else{
                log.error("insert CC record failed: {}", codeCoverage.toString());
            }
        }
        return 0;
    }
    @RequestMapping("/insertAll")
    //to-do: add support for different branch, here hardcoded "master"
    public int insertCCRecord() {
        JsonArray jsonArray = (JsonArray) new JsonParser().parse(ccRepos);
        Iterator<JsonElement> jsonElementIterator  = jsonArray.iterator();
        while(jsonElementIterator.hasNext()){
            JsonObject jsonObject = jsonElementIterator.next().getAsJsonObject();
            String org = jsonObject.get("org").getAsString();
            String repo = jsonObject.get("repo").getAsString();
            //getcodecoveragelist from remote
            List<CodeCoverage> coverageList = CodeCovUtil.getCodeCoverageList(org, repo, "master");
            for (CodeCoverage codeCoverage : coverageList) {
//                while(codeCoverage.getName().compareTo("pkg/disttask/framework/scheduler/scheduler.go") ==0){
//                    System.out.println("codecoverage - "+codeCoverage.toString());
//                }
                coverageService.contains(codeCoverage)
                int error = coverageService.insertCodeCoverageRecord(codeCoverage);
                if(error == 1) {
                    log.info("insert CC record succeeded: {}", codeCoverage.toString());
                }else{
                    log.error("insert CC record failed: {}", codeCoverage.toString());
                }
            }
        }
        return 0;
    }
    @RequestMapping("/extract")
    public void extractPackages() {
        JsonArray jsonArray = (JsonArray) new JsonParser().parse(ccRepos);
        Iterator<JsonElement> jsonElementIterator  = jsonArray.iterator();
        while(jsonElementIterator.hasNext()){
            JsonObject jsonObject = jsonElementIterator.next().getAsJsonObject();
            String org = jsonObject.get("org").getAsString();
            String repo = jsonObject.get("repo").getAsString();
            //if(repo.compareTo("tidb") != 0) continue;
            List<String> uniqueNames = coverageService.getNameList(org, repo, "master", DateUtil.getCurrentUTCDate());
            List<String> dirList = CodeCovUtil.extractDirectories(uniqueNames);
            List<CodeCoverage> coverageListOutput = new ArrayList<>();
            for(int i=0; i<dirList.size();i++){
                CodeCoverage codeCoverage = new CodeCoverage();
                String dir = dirList.get(i);
                List<CodeCoverage> coverageList = coverageService.getCoverageItems(org, repo, "master",DateUtil.getCurrentUTCDate(),dir);
                log.info("coveragelist = {}"+ coverageList.size());
                int sumLoc = coverageList.stream().mapToInt(CodeCoverage::getLoc).sum();
                int sumHits = coverageList.stream().mapToInt(CodeCoverage::getHits).sum();
                float coverage = (sumHits*100)/sumLoc;
                log.info("coverage = {}/{} = {}", sumHits,sumLoc,coverage);
                codeCoverage.setLoc(sumLoc);
                codeCoverage.setCoverage(coverage);
                codeCoverage.setName(dir);
                codeCoverage.setInsertAt(DateUtil.getCurrentUTCDate());
                codeCoverage.setCreateAt(DateUtil.getCurrentUTCDate());

                codeCoverage.setBranch("master");
                codeCoverage.setRepo(repo);
                codeCoverage.setOrg(org);
                codeCoverage.setHits(sumHits);
                coverageListOutput.add(codeCoverage);

            }
            for(int i=0;i< coverageListOutput.size();i++){
                int error = coverageService.insertCodeCoverageRecord(coverageListOutput.get(i));
                if( error != 0) {
                    log.info("insert dir node suceeeded: {}", coverageListOutput.get(i).toString());
                }else {
                    log.error("insert dir node failed: {}", coverageListOutput.get(i).toString());
                }
            }
        }
        //return 0;
    }
}
