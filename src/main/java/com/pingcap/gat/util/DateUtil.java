package com.pingcap.gat.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {
    public static void main(String[] args){
        String inputDate = "2023-12-26T03:45:34.223473Z";
        String inputFormat = "yyyy-MM-dd'T'HH:mm:ss"; // This format includes "Z" for UTC

        Date utcDate = getDate(inputDate, inputFormat);
        System.out.println("UTC Date: " + utcDate);
    }
    public static Date getCurrentUTCDate(){
        LocalDate currentUtcDate = LocalDate.now(ZoneId.of("UTC"));
        //System.out.println("Current UTC Date: " + date);
        return Date.from(currentUtcDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
    public static Date getDate(String strDate, String format){
        try {
            if (strDate == null || strDate.equalsIgnoreCase("null")) {
                return null;
            }

            SimpleDateFormat formatter = new SimpleDateFormat(format);
            Date date = formatter.parse(strDate.replace("Z", ""));
            return date;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;

    }
    public static Date getCurrentUtcTime() throws ParseException {  // handling ParseException
        // create an instance of the SimpleDateFormat class
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
        // set UTC time zone by using SimpleDateFormat class
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        //create another instance of the SimpleDateFormat class for local date format
        SimpleDateFormat ldf = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
        // declare and initialize a date variable which we return to the main method
        Date d1 = null;
        // use try catch block to parse date in UTC time zone
        try {
            // parsing date using SimpleDateFormat class
            d1 = ldf.parse( sdf.format(new Date()) );
        }
        // catch block for handling ParseException
        catch (java.text.ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            //System.out.println(e.getMessage());
        }
        // pass UTC date to main method.
        return d1;
    }
    public static Date getBeginOfCurrentUTCTime(String dateStr) {
        Date date;
        try{
            if(dateStr.equals("null")) {
                date = getCurrentUtcTime();

            }else{
                date = getDate(dateStr,"yyyy-MM-dd");

            }
            date.setHours(0);
            date.setMinutes(0);
            date.setSeconds(0);
            return date;

        }catch(Exception ec){

        }

        return null;
    }
    public static float getTimeDiff(Date early, Date later) {

        long dayM = 1000 * 24 * 60 * 60;
        long hourM = 1000 * 60 * 60;
        if(later == null) return 0;
        long differ = later.getTime() - early.getTime();
        float x = differ/dayM; //# of day
        float y = differ%dayM;
        float z = y/hourM;
        float hour = z/24 + x;
        float  ab   =  (float)(Math.round(hour*100))/100;
        // System.out.println("early = "+DateUtil.formatDateString(early)+"\t later="+DateUtil.formatDateString(later)+"\tdiff = "+ab);
        return ab;
    }
    public static Date getNextDate(Date today){
        Date date = today;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, 1);
        date = calendar.getTime();
        return date;
    }
    public static Date getPrevDate(Date today){
        Date date = today;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, -1);
        date = calendar.getTime();
        return date;

    }
    public static String formatDateString(Date date, String format) {
        String dateStr = new String("0000-00-00T00:00:00Z");
        if(date == null) return dateStr;
        DateFormat dateFormat = new SimpleDateFormat(format);
        //dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        dateStr = dateFormat.format(date);
        return dateStr;
    }
}
