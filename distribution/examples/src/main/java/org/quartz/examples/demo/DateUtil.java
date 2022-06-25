package org.quartz.examples.demo;

import org.quartz.CronExpression;

import java.text.ParseException;
import java.util.Date;

public class DateUtil {
    public static Date parseYYYYMMDD(String cron) {
        CronExpression cronExpression = null;
        try {
            cronExpression = new CronExpression(cron);
        } catch (ParseException e) {
            e.printStackTrace();
        }
// 转换 new Date 是为了给最近一次执行时间一个初始时间，这里给当前时间
        Date date = cronExpression.getNextValidTimeAfter(new Date());
        return date;
    }
    
    public static void main(String[] args) throws ParseException {
        // 需要转换的 cron 表达式
        String cron = "0 0 1 * * ?";
// 加载包之后直接引用这个方法
        CronExpression cronExpression = new CronExpression(cron);
// 转换 new Date 是为了给最近一次执行时间一个初始时间，这里给当前时间
        Date date = cronExpression.getNextValidTimeAfter(new Date());
        System.out.println(date);
    }
}
