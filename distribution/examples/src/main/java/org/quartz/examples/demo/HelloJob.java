package org.quartz.examples.demo;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@Slf4j
public class HelloJob implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        //通过jobDetail获取数据
        String payload = context.getTrigger().getJobDataMap().getString(CwConstant.PAYLOAD);
        System.out.println(payload);
        System.out.println("hello world!!!");
    }
}

