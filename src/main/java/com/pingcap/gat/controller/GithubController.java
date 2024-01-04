package com.pingcap.gat.controller;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pingcap.gat.constant.ConfigConstant;
import com.pingcap.gat.mapper.GithubIssueMapper;
import com.pingcap.gat.model.GhDailyCount;
import com.pingcap.gat.model.GithubIssue;
import com.pingcap.gat.model.SprintPlanObj;
import com.pingcap.gat.service.DailyCountService;
import com.pingcap.gat.service.GithubService;

import com.pingcap.gat.service.impl.GithubServiceImpl;
import com.pingcap.gat.util.DateUtil;
import com.pingcap.gat.util.ExcelUtil;
import com.pingcap.gat.util.GithubUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.*;

@RestController
@RequestMapping("/ghops")
public class GithubController {
    @Value("${com.pingcap.gat.repos}")
    private String repos;
    private static final Logger log = LoggerFactory.getLogger(GithubController.class);
    @Autowired
    private GithubService githubService;

    @Autowired
    DailyCountService dailyCountService;

    @Autowired
    GithubIssueMapper githubIssueMapper;

    @RequestMapping("/insertIssues")
    //don't call it
    public String insertIssues(@RequestParam("org") String org, @RequestParam("repo") String repo ){
        List<GithubIssue> githubIssueList = GithubUtil.getAllGHIssues(org, repo);
        log.info("githubIssueList.size = {}", githubIssueList.size());
        int error = 0;
        for(GithubIssue githubIssue:githubIssueList){
            try {
             error =    githubService.insertIssue(githubIssue);
                // log.info("insertIssue: " + githubIssue.toString()+"\t error code:" + error);
            }catch(Exception ec){
                log.info("insertIssue:{} failed, " , githubIssue.toString()+"\t error code:" + error);
            }finally {
                continue;
            }
        }
        return "0";
    }
    @RequestMapping("/insertIssuesById")
    public String insertIssues(@RequestParam("org") String org, @RequestParam("repo") String repo, @RequestParam("Id") int Id ){
        GithubIssue githubIssue = GithubUtil.getGhIssueById(org, repo,Id);
        log.info("githubIssue = {}", githubIssue.toString());
        int error = 0;

        try {
            error =    githubService.insertIssue(githubIssue);
            // log.info("insertIssue: " + githubIssue.toString()+"\t error code:" + error);
        }catch(Exception ec){
            log.info("insertIssue:{} failed, " , githubIssue.toString()+"\t error code:" + error);
        }finally {

        }
        return "0";
    }

    @RequestMapping("/initDB")
    //don't call it
    public String initiateDB(){
        List<GithubIssue> githubIssueList = new ArrayList<GithubIssue>();
        JsonArray jsonArray = (JsonArray) new JsonParser().parse(repos);
        Iterator<JsonElement> jsonElementIterator  = jsonArray.iterator();
        while(jsonElementIterator.hasNext()){
            JsonObject jsonObject = jsonElementIterator.next().getAsJsonObject();
            String org = jsonObject.get("org").getAsString();
            String repo = jsonObject.get("repo").getAsString();
            List<GithubIssue> githubIssueListIteration = GithubUtil.getAllGHIssues(org, repo);
            for(GithubIssue githubIssue:githubIssueList){
                int error = githubService.insertIssue(githubIssue);
                // log.info("insertIssue: " + githubIssue.toString()+"\t error code:" + error);
            }
            githubIssueList.addAll(githubIssueListIteration);
        }
        log.info("githubIssueList.size = {}", githubIssueList.size());
        return "0";
    }
    @RequestMapping("/updateTikv")
    public String updateTikv(){
        String fileName = "pdtikv-all-1030.xlsx";
        Set<String> links = new ExcelUtil().readFile(fileName);
        for(String link: links){
            GithubIssue githubIssue = GithubUtil.getGhIssueByLink(link);
            int error= githubService.updateIssue(githubIssue);
            if(error == 0) log.info("update issue {} successfuly ", githubIssue);
            else log.info("update issue {} failed",githubIssue);

        }

        return "";

    }
    @RequestMapping("/initDailyTable")
    public String initDailyTable(){

        JsonArray jsonArray = (JsonArray) new JsonParser().parse(repos);
        Iterator<JsonElement> jsonElementIterator  = jsonArray.iterator();
        while(jsonElementIterator.hasNext()) {
            JsonObject jsonObject = jsonElementIterator.next().getAsJsonObject();
            String org = jsonObject.get("org").getAsString();
            String repo = jsonObject.get("repo").getAsString();
            List<Date> lastmodified = githubService.getLastModifiedAtFromDailyCount(org,repo,'y');//issues
            GhDailyCount ghDailyCountIssue = new GhDailyCount();
            ghDailyCountIssue.setOrg(org);
            ghDailyCountIssue.setRepo(repo);
            if(lastmodified != null) {
                ghDailyCountIssue.setCreated(githubService.searchForNewCount(org, repo, "issue", lastmodified.get(lastmodified.size()-1)));
                ghDailyCountIssue.setClosed(githubService.searchForCloseCount(org, repo, "issue", lastmodified.get(lastmodified.size()-1)));
                ghDailyCountIssue.setOpen(githubService.searchForOpenCount(org, repo, "issue", lastmodified.get(lastmodified.size()-1)));
                ghDailyCountIssue.setTime(DateUtil.getBeginOfCurrentUTCTime(DateUtil.formatDateString(lastmodified.get(lastmodified.size()-1),ConfigConstant.DATE_FORMAT)));

            }else{
                ghDailyCountIssue.setCreated(githubService.searchForNewCount(org, repo, "issue", DateUtil.getBeginOfCurrentUTCTime("null")));
                ghDailyCountIssue.setClosed(githubService.searchForCloseCount(org, repo, "issue", DateUtil.getBeginOfCurrentUTCTime("null")));
                ghDailyCountIssue.setOpen(githubService.searchForOpenCount(org, repo, "issue", DateUtil.getBeginOfCurrentUTCTime("null")));
                ghDailyCountIssue.setTime(DateUtil.getBeginOfCurrentUTCTime("null"));
            }

            ghDailyCountIssue.setType('y');//y for issue
            GhDailyCount ghDailyCountPR = new GhDailyCount();
            ghDailyCountPR.setOrg(org);
            ghDailyCountPR.setRepo(repo);

            lastmodified = githubService.getLastModifiedAtFromDailyCount(org,repo,'n');//PR
            if(lastmodified != null) {
                ghDailyCountPR.setCreated(githubService.searchForNewCount(org, repo, "pr", lastmodified.get(lastmodified.size()-1)));
                ghDailyCountPR.setClosed(githubService.searchForCloseCount(org, repo, "pr", lastmodified.get(lastmodified.size()-1)));
                ghDailyCountPR.setOpen(githubService.searchForOpenCount(org, repo, "pr", lastmodified.get(lastmodified.size()-1)));
                ghDailyCountPR.setTime(DateUtil.getBeginOfCurrentUTCTime(DateUtil.formatDateString(lastmodified.get(lastmodified.size()-1),ConfigConstant.DATE_FORMAT)));
            }else{
                ghDailyCountPR.setCreated(githubService.searchForNewCount(org, repo, "pr", DateUtil.getBeginOfCurrentUTCTime("null")));
                ghDailyCountPR.setClosed(githubService.searchForCloseCount(org, repo, "pr", DateUtil.getBeginOfCurrentUTCTime("null")));
                ghDailyCountPR.setOpen(githubService.searchForOpenCount(org, repo, "pr", DateUtil.getBeginOfCurrentUTCTime("null")));
                ghDailyCountPR.setTime(DateUtil.getBeginOfCurrentUTCTime("null"));
            }

            ghDailyCountPR.setType('n');//y for issue
            dailyCountService.insertDailyCount(ghDailyCountPR);
            dailyCountService.insertDailyCount(ghDailyCountIssue);
        }
        return "succeed";
    }
    @RequestMapping("/insertSprintPlan")
    public String initSprintPlan(@RequestParam(value="sprint") String sprint){
        String fileName = "all-major-issue.xlsx";
        Set<String> links = new ExcelUtil().readFile(fileName);
        for(String link: links){
            GithubIssue githubIssue = GithubUtil.getGhIssueByLink(link);
            SprintPlanObj sprintPlan = GithubUtil.convertToSprintPlanObj(githubIssue,'1');
            int error = 0;
            try{
                error = githubService.insertSprintPlanObj(sprintPlan);
                log.info("insertSprintPlanObj: {}",sprintPlan.toString());

            }catch(Exception ec){
                log.info("insert {} as sprintgoal failed");
            }

        }
        
        return "succeed";
    }

    @RequestMapping("/updateSprintPlan")
    public ModelAndView updateSprintPlan(@RequestParam(value="sprint") String sprint){
        String fileName = "all-major-issue.xlsx";
        List<SprintPlanObj> sprintPlanObjs = new ArrayList<>();
        Set<String> links = new ExcelUtil().readFile(fileName);
        int count = 0;
        for(String link: links){
            GithubIssue githubIssue = GithubUtil.getGhIssueByLink(link);
            SprintPlanObj sprintPlan = GithubUtil.convertToSprintPlanObj(githubIssue,'1');
            int error = 0;
            try{
                error = githubService.updateSprintPlanObj(sprintPlan);
                log.info("updateSprintPlanObj: {}",sprintPlan.toString());
                count ++;
                sprintPlanObjs.add(sprintPlan);
            }catch(Exception ec){
                log.info("update {} as sprintgoal failed"+ sprintPlan.toString());
            }

        }
        log.info("update {} as sprintgoal succeeded!", count);
        ModelAndView modelAndView = new ModelAndView("updateSprintPlan"); // Specify the view name
        modelAndView.addObject("dataList", sprintPlanObjs);
        modelAndView.addObject("count", count);
        return modelAndView;
    }
    @RequestMapping("/getClosedPR")
    public List<GithubIssue> getClosedPR(){
        List<GithubIssue> githubIssueList = new ArrayList<>();
        Date startDate = DateUtil.getDate("2023-04-01T00:00:00Z",ConfigConstant.DATE_FORMAT);
        Date endDate = DateUtil.getDate(("2023-08-26T15:00:00Z"),ConfigConstant.DATE_FORMAT);
        try {
            githubIssueList = githubService.searchClosedPRByTime(startDate,endDate);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        for(GithubIssue githubIssue:githubIssueList){
            int error = githubService.updatePRTested(githubIssue);
        }
        return githubIssueList;
    }
    @RequestMapping("/updatePRBugfix")
    public List<GithubIssue> updatePRBugfix(){


        //List<GithubIssue> githubIssueList = githubIssueMapper.getPRFromDB(24588);
        List<GithubIssue> githubIssueList = githubIssueMapper.getAllClosedPRFromDB();

        for(GithubIssue githubIssue:githubIssueList){
            boolean bflag = githubService.isPRBugfix(githubIssue.getOrg(),githubIssue.getRepo(),githubIssue.getAssociatedIssues());
            githubIssue.setPRBugfix(bflag);
            int error = githubService.updatePRBugfix(githubIssue);
            log.info("updatePRBugfix: {} successfully", githubIssue.toString());
        }
        return githubIssueList;
    }

    @RequestMapping("/getBugKillerDetails")
    public void getBugKillerDetails(){
        String fileName = "bug-1120.xlsx";
            try {
                XSSFWorkbook workbook = new XSSFWorkbook(getClass().getClassLoader().getResourceAsStream(fileName));
                // Get first/desired sheet from the workbook
                XSSFSheet sheet = workbook.getSheetAt(0);
                // Iterate through each rows one by one
                Iterator<Row> rowIterator = sheet.iterator();
                // Till there is an element condition holds true
                while (rowIterator.hasNext()) {
                    Row row = rowIterator.next();
                    // For each row, iterate through all the
                    Cell cell = row.getCell(1);
                    String link = cell.getStringCellValue();
                    cell = row.getCell(2);
                    String repo = cell.getStringCellValue();
                    if(repo.compareTo("repo") == 0) continue;
                    cell = row.getCell(3);
                    String org = cell.getStringCellValue();
                    cell = row.getCell(17);
                    Cell cellSeverity = row.getCell(21);
                    Cell cellComponenet = row.getCell(23);
                    Cell cellSig = row.getCell(22);
                    String bugIds = "";
                    if(cell.getCellType() == Cell.CELL_TYPE_STRING) {
                        bugIds = cell.getStringCellValue();
                    }else {
                       int bugID = (int)cell.getNumericCellValue();
                        bugIds = String.valueOf(bugID);
                    }

                    String[] bugIdList = bugIds.trim().split(",");
                    StringBuffer severity = new StringBuffer();
                    String component = "";
                    String sig = "";
                    for(int i=0;i<bugIdList.length;i++) {
                        System.out.println("repo = "+repo+"org= "+org+"id = "+bugIdList[i].trim());
                        //if(bugIdList[i] == null || bugIdList[i].compareTo())
                        GithubIssue githubIssue = githubService.getGhIssueById(repo,org,Integer.parseInt(bugIdList[i].trim()));
                        if(githubIssue == null) {
                            githubIssue = githubService.getGhIssueById("tikv",org,Integer.parseInt(bugIdList[i].trim()));
                            if(githubIssue == null) githubService.getGhIssueById("tidb","pingcap",Integer.parseInt(bugIdList[i].trim()));
                        }
                        if(githubIssue == null) {
                            severity.append("");
                            sig = "";
                            component = "";
                        }else{
                            severity.append(githubIssue.getSeverity());
                            sig = githubIssue.getSig();
                            component = githubIssue.getComponent();
                        }

                        if(i == (bugIdList.length-1)) break;
                        else severity.append(",");
                    }
                    String strSeverity = severity.toString().trim();

                    if(strSeverity.startsWith(",")) strSeverity = strSeverity.substring(1,strSeverity.length()-1);
                    if(strSeverity.length() > 2) {
                        if(strSeverity.endsWith(",")) strSeverity = strSeverity.trim().substring(0,strSeverity.length()-1);
                    }
                    //cell = row.getCell(18);
                    cellSeverity.setCellValue(strSeverity);
                    cellSig.setCellValue(sig);
                    cellComponenet.setCellValue(component);

                }
                Sheet outputSheet = workbook.createSheet("Output Sheet");
                String outputFilePath = "/Users/jasonwu/Documents/bug-1120-output.xlsx";
                try(FileOutputStream outputFileStream = new FileOutputStream(outputFilePath)) {
                    workbook.write(outputFileStream);
                }
            }
            // Catch block to handle exceptions
            catch (Exception e) {
                e.printStackTrace();
            }

        }
}
