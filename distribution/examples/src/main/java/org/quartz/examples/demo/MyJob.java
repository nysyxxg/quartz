package org.quartz.examples.demo;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * 然后创建一个Job类
 *
 * @author
 * @date 2019/12/19 16:27
 */
@Slf4j
public class MyJob implements Job {
    
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("==================开始执行任务==================" + System.currentTimeMillis()/1000);
    }
}