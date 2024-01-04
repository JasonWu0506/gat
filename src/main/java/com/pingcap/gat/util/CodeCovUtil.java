package com.pingcap.gat.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pingcap.gat.constant.ConfigConstant;
import com.pingcap.gat.model.CodeCoverage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class CodeCovUtil {
    //https://api.codecov.io/api/v2/github/pingcap/repos/tidb/branches/master/?format=json
    private static final Logger log = LoggerFactory.getLogger(CodeCovUtil.class);
    public static void main(String[] args) throws IOException {
        LocalDate currentUtcDate = LocalDate.now(ZoneId.of("UTC"));
        Date date = Date.from(currentUtcDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        // 打印当前的 UTC 日期
        System.out.println("Current UTC Date: " + date);

    }
    public static List<CodeCoverage> getCodeCoverageList(String org, String repo, String branch) {
        String strRequest = String.format("https://api.codecov.io/api/v2/github/%s/repos/%s/branches/%s/?format=json",org,repo,branch);
        List<CodeCoverage> codeCoverageList = new ArrayList<>();
        JsonElement jsonElement = HttpUtil.doRestRequest(strRequest, ConfigConstant.CCTOKEN);
        JsonObject jsonObject;
        new JsonArray();
        JsonArray jsonArray;
        assert jsonElement != null;
        if(jsonElement.isJsonObject()){
            jsonObject = jsonElement.getAsJsonObject();
            System.out.println("result = "+jsonObject.getAsJsonObject("head_commit").getAsJsonObject("report").getAsJsonArray("files").size());
            jsonArray = jsonObject.getAsJsonObject("head_commit").getAsJsonObject("report").getAsJsonArray("files");
            //repo level, root node
            CodeCoverage codeCoverageRepo = new CodeCoverage();
            codeCoverageRepo.setOrg(org);
            codeCoverageRepo.setRepo(repo);
            codeCoverageRepo.setName(repo);
            codeCoverageRepo.setBranch(branch);
            codeCoverageRepo.setCoverage(jsonObject.getAsJsonObject("head_commit").getAsJsonObject("totals").get("coverage").getAsFloat());
            codeCoverageRepo.setCreateAt(DateUtil.getDate(jsonObject.get("updatestamp").getAsString(),ConfigConstant.DATE_FORMAT));
            codeCoverageRepo.setLoc(jsonObject.getAsJsonObject("head_commit").getAsJsonObject("totals").get("lines").getAsInt());
            codeCoverageRepo.setHits(jsonObject.getAsJsonObject("head_commit").getAsJsonObject("totals").get("hits").getAsInt());

            try{
                codeCoverageRepo.setInsertAt(DateUtil.getCurrentUtcTime());
            }catch(Exception ec){
                log.error("error setting insert time for {}", codeCoverageRepo);
            }

            codeCoverageList.add(codeCoverageRepo);
            //file level, leaf node
            for(int i=0;i< jsonArray.size();i++){
                CodeCoverage codeCoverageFile = new CodeCoverage();
                new JsonObject();
                JsonObject jsonObjectFile;
                jsonObjectFile = jsonArray.get(i).getAsJsonObject();
                codeCoverageFile.setOrg(org);
                codeCoverageFile.setRepo(repo);
                codeCoverageFile.setName(jsonObjectFile.get("name").getAsString());
                codeCoverageFile.setBranch(branch);
                codeCoverageFile.setCreateAt(DateUtil.getDate(jsonObject.get("updatestamp").getAsString(),ConfigConstant.DATE_FORMAT));
                codeCoverageFile.setCoverage(jsonObjectFile.get("totals").getAsJsonObject().get("coverage").getAsFloat());
                codeCoverageFile.setLoc(jsonObjectFile.get("totals").getAsJsonObject().get("lines").getAsInt());
                codeCoverageFile.setHits(jsonObjectFile.get("totals").getAsJsonObject().get("hits").getAsInt());
                try{
                    codeCoverageFile.setInsertAt(DateUtil.getCurrentUtcTime());
                }catch(Exception ec){
                    log.error("error setting insert time for {}", codeCoverageRepo);
                }
                codeCoverageList.add(codeCoverageFile);
            }
        }
        System.out.println("codeCoverageList.size = "+ codeCoverageList.size());
        return codeCoverageList;
    }
    public static List<String> extractDirectories(List<String> fileName){
        return fileName.stream()
                .map(Path::of)
                .map(Path::getParent)
                .map(Path::toString)
                .distinct()
                .collect(Collectors.toList());
    }
}
