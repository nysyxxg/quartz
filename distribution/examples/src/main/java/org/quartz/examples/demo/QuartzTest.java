package org.quartz.examples.demo;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.List;

/**
 * //​分布式定时任务框架Quartz
 * //  https:blog.51cto.com/u_14558366/3109785
 * https://blog.51cto.com/u_14558366/3109785
 * 创建一个工具类然后进行定时任务的增删改
 */
public class QuartzTest {
    // 首先创建一个调度工厂
    private static SchedulerFactory schedulerFactory = new StdSchedulerFactory();
    
    
    public static void main(String[] args) throws SchedulerException {
        Class jobClass = MyJob.class;
        String jobName = "测试定时任务";
        String jobGroupName = "testJob";
        String triggerName = "测试定时任务触发器";
        String triggerGroupName = "testTrigger";
        String cron = "0/5 * * * * ?";
//        QuartUtil.addJob(jobName, jobGroupName, triggerName, triggerGroupName, jobClass, cron);
        List<JobPayload> jobs = QuartUtil.getJobList(triggerName, triggerGroupName);
        jobs.stream().forEach(job -> {
            System.out.println(job);
        });
//        QuartUtil.delete(triggerName,triggerGroupName,jobName,jobGroupName);
        
    }
    
    public static void demo(String[] args) throws SchedulerException {
//        QuartUtil.addJob("测试定时任务", "test", "测试定时任务", "testTrigger", MyJob.class, "0/5 * * * * ?");
//
        Class jobClass = MyJob.class;
        String jobName = "测试定时任务-2";
        String jobGroupName = "test-2";
        String triggerName = "测试定时任务-2";
        String triggerGroupName = "testTrigger-2";
        String cron = "0/5 * * * * ?";
        
        /**
         * 创建流程
         * 通过工厂获取 Scheduler对象
         * 设置Job的实现类和一些静态信息
         */
        Scheduler sched = schedulerFactory.getScheduler();
        Scheduler sched2 = StdSchedulerFactory.getDefaultScheduler();
        //jobClass 设置Job的实现类
        //jobName Job名称
        //jobGroupName Job组名称
        JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(jobName, jobGroupName).build();

//        构建触发器
//       触发器
        TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
        // 触发器名,触发器组
        triggerBuilder.withIdentity(triggerName, triggerGroupName);
        triggerBuilder.startNow();
// 触发器时间设定
        triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(cron));
// 创建Trigger对象
        CronTrigger trigger = (CronTrigger) triggerBuilder.build();
//        然后把Job和触发器都设置到Scheduler对象中

// 调度容器设置JobDetail和Trigger
        sched.scheduleJob(jobDetail, trigger);
// 启动
        sched.start();
    }
}
