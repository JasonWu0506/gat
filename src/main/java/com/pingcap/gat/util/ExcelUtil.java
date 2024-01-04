package com.pingcap.gat.util;

import com.pingcap.gat.model.GithubIssue;
import com.pingcap.gat.model.SprintPlanObj;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ExcelUtil {
    public static void main(String[] args){
        String fileName = "all-major-issue.xlsx";
        Set<String> links = new ExcelUtil().readFile(fileName);
        for(String link: links){
            GithubIssue githubIssue = GithubUtil.getGhIssueByLink(link);
           // SprintPlanObj sprintPlan = GithubUtil.convertToSprintPlanObj(githubIssue);
        }
    }

    public Set<String> readFile(String fileName){
        // Try block to check for exceptions
        Set<String> links = new HashSet<>();
        try {
            // Reading file from local directory
            //FileInputStream file = new FileInputStream(new File("sprint17-open-major-bugs.0714.xlsx"));
            // Create Workbook instance holding reference to
            // .xlsx file

           // Workbook workbookTwo = new XSSFWorkbook(getClass().getClassLoader().getResourceAsStream("importExcel.xlsx"));

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
                System.out.println("link ="+link);
                if((link.compareTo("link") == 0) || (link.compareTo("URL")==0)) continue;
                boolean b = links.add(link);
                if(!b) System.out.println("there is a duplicated = "+link);

            }
            //file.close();
        }
        // Catch block to handle exceptions
        catch (Exception e) {
            e.printStackTrace();
        }
        return links;
    }
}
