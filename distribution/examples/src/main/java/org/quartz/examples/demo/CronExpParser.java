package org.quartz.examples.demo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.quartz.CronExpression;


public class CronExpParser {
    
    /**
     * @param cronExpression cron表达式
     * @return
     * @方法名：parser
     * @方法描述【cron表达式装换成时间格式】
     * @修改描述【修改描述】
     * @版本：1.0
     * @创建人：Administrator
     * @创建时间：2018年8月17日 下午2:46:43
     * @修改人：Administrator
     * @修改时间：2018年8月17日 下午2:46:43
     */
    public static List<String> parser(String cronExpression, int days) {
        List<String> result = new ArrayList<String>();
        if (cronExpression == null || cronExpression.length() < 1) {
            return result;
        } else {
            CronExpression exp = null;
            try {
                exp = new CronExpression(cronExpression);
            } catch (ParseException e) {
                e.printStackTrace();
                return result;
            }
            Calendar calendar = Calendar.getInstance();
            String cronDate = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DATE);
            String sStart = cronDate + " 00:00:00";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date dStart = null;
            Date dEnd = null;
            try {
                dStart = sdf.parse(sStart);
                calendar.setTime(dStart);
                calendar.add(Calendar.DATE, days);
                dEnd = calendar.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Date dd = new Date();
            dd = exp.getNextValidTimeAfter(dd);
            while (dd.getTime() < dEnd.getTime()) {
                result.add(sdf.format(dd));
                dd = exp.getNextValidTimeAfter(dd);
            }
            exp = null;
        }
        return result;
    }
    
    public static void main(String[] args) {
        String CRON_EXPRESSION = "0/30 * * * * ? *";
        int days = 1;
        String startDay = "";
        String endDay = "";
        System.out.println(CRON_EXPRESSION);
        List<String> lTime = new ArrayList<String>();
        lTime = CronExpParser.parser(CRON_EXPRESSION, days);
        for (int i = 0; i < lTime.size(); i++) {
            System.out.println(lTime.get(i));
        }
        
    }
}