package org.quartz.examples.demo;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.quartz.CronScheduleBuilder.cronSchedule;

@Slf4j
public class QuartUtil {
    
    // 首先创建一个调度工厂
    private static SchedulerFactory schedulerFactory = new StdSchedulerFactory();
    
    /**
     * 添加定时任务
     *
     * @param jobName
     * @param jobGroupName
     * @param triggerName
     * @param triggerGroupName
     * @param jobClass
     * @param cron
     */
    public static void addJob(String jobName, String jobGroupName,
                              String triggerName, String triggerGroupName, Class jobClass, String cron) {
        try {
            Scheduler sched = schedulerFactory.getScheduler();
            // 任务名，任务组，任务执行类
//            Trigger.TriggerState state = sched.getTriggerState();
            
            JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(jobName, jobGroupName).build();
            // 触发器
            TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
            // 触发器名,触发器组
            triggerBuilder.withIdentity(triggerName, triggerGroupName);
            triggerBuilder.startNow();
            // 触发器时间设定
            triggerBuilder.withSchedule(cronSchedule(cron));
            // 创建Trigger对象
            CronTrigger trigger = (CronTrigger) triggerBuilder.build();
            
            // 调度容器设置JobDetail和Trigger
            sched.scheduleJob(jobDetail, trigger);
            
            // 启动
            if (!sched.isShutdown()) {
                sched.start();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public static void update(JobPayload jobPayload) {
        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            TriggerKey triggerKey = new TriggerKey(jobPayload.getTriggerName(), jobPayload.getTriggerGroup());
            switch (jobPayload.getScheduleType()){
                case  JobType.ONCE:
                    //获取trigger,修改执行频率
                    Trigger trigger = scheduler.getTrigger(triggerKey);
                    Date startTime = trigger.getStartTime();
                    Date date = DateUtil.parseYYYYMMDD(jobPayload.getCron());
                    boolean equals = date.equals(startTime);
                    if(!equals){
                        Trigger build = TriggerBuilder.newTrigger().withIdentity(triggerKey).startAt(date)
                                .withDescription(jobPayload.getDesc())
                                .usingJobData(CwConstant.PAYLOAD,jobPayload.getDataMap())
                                .build();
                        scheduler.rescheduleJob(triggerKey,build);
                    }
                case  JobType.REPEAT:
                    CronTrigger cronTrigger = (CronTrigger)scheduler.getTrigger(triggerKey);
                    String cronExpression = cronTrigger.getCronExpression();
                    if( jobPayload.getCron().equalsIgnoreCase(cronExpression)){
                        CronTrigger build = TriggerBuilder.newTrigger().withIdentity(triggerKey)
                                .withDescription(jobPayload.getDesc())
                                .usingJobData(CwConstant.PAYLOAD,jobPayload.getDataMap())
                                .withSchedule(cronSchedule(jobPayload.getCron()))
                                .build();
                        scheduler.rescheduleJob(triggerKey,build);
                    }
            }
            
        }catch (Exception e){
            log.info("修改失败:{}",e);
        }
    }
    
    // 初始化
    public static void initiate(JobPayload payload) {
        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            JobDetail jobDetail = JobBuilder.newJob(HelloJob.class)
                    .withIdentity(payload.getJobName(), payload.getJobGroup()).build();
            switch (payload.getScheduleType()){
                case  JobType.NOW:
                    Trigger build = TriggerBuilder.newTrigger()
                            .withIdentity(payload.getTriggerName(), payload.getTriggerGroup())
                            .startNow().usingJobData( CwConstant.PAYLOAD,payload.getDataMap()).withDescription(payload.getDesc()).build();
                    scheduler.scheduleJob(jobDetail,build);
                    scheduler.start();
                    log.info("成功创建即刻执行任务!!!");
                    break;
                case JobType.ONCE:
                    Trigger trigger = TriggerBuilder.newTrigger().withIdentity(payload.getTriggerName(), payload.getTriggerGroup())
                            .startAt(DateUtil.parseYYYYMMDD(payload.getCron()))
                            .usingJobData(CwConstant.PAYLOAD,payload.getDataMap())
                            .withDescription(payload.getDesc())
                            .build();
                    scheduler.scheduleJob(jobDetail,trigger);
                    scheduler.start();
                    log.info("成功创建未来执行一次任务!!!");
                    break;
                case JobType.REPEAT:
                    CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(payload.getTriggerName(), payload.getTriggerGroup())
                            .usingJobData(CwConstant.PAYLOAD,payload.getDataMap()).withDescription(payload.getDesc())
                            .withSchedule(CronScheduleBuilder.cronSchedule(payload.getCron())).build();
                    scheduler.scheduleJob(jobDetail,cronTrigger);
                    scheduler.start();
                    log.info("成功创建重复任务!!!");
                    break;
            }
        } catch (Exception e) {
            log.info("job initial failure:{}",e);
        }
    }
    
    //  查询
    public static List getJobList(String triggerName, String triggerGroupName) {
        List<JobPayload> jobs = new ArrayList<>();
        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            Set<JobKey> jobKeys;
            if (StringUtils.isEmpty(triggerGroupName)) {
                if (!StringUtils.isEmpty(triggerName)) {
                    CronTrigger trigger = (CronTrigger) scheduler.getTrigger(new TriggerKey(triggerName, triggerGroupName));
                    jobs.add(JobPayload.getInstance(trigger));
                    return jobs;
                }
                jobKeys = scheduler.getJobKeys(GroupMatcher.anyGroup());
            } else {
                jobKeys = scheduler.getJobKeys(GroupMatcher.jobGroupEquals(triggerGroupName));
            }
            jobKeys.forEach(jobKey -> {
                try {
                    CronTrigger trigger = (CronTrigger) scheduler.getTrigger(new TriggerKey(jobKey.getName(), jobKey.getGroup()));
                    JobPayload instance = JobPayload.getInstance(trigger);
                    jobs.add(instance);
                } catch (SchedulerException e) {
                    log.info("任务不存在：{}", e);
                }
            });
        } catch (Exception e) {
            log.info("任务列表获取失败{}", e);
            return jobs;
        }
        return jobs;
    }
    
    //  删除
    public static void delete(String triggerName, String triggerGroupName, String jobName, String jobGroupName) {
        Scheduler scheduler = null;
        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
            TriggerKey triggerKey = new TriggerKey(triggerName, triggerGroupName);
            if (scheduler.checkExists(triggerKey)) {
                scheduler.pauseTrigger(triggerKey);
                scheduler.unscheduleJob(triggerKey);
                JobKey jobKey = new JobKey(jobName, jobGroupName);
                boolean bl = scheduler.checkExists(jobKey);
                if (bl) {
                    System.out.println("----deleteJob-------");
                    scheduler.deleteJob(jobKey);
                }
            }
        } catch (Exception e) {
            log.info("删除任务失败:{}", e.getMessage());
        } finally {
            try {
                scheduler.shutdown();
            } catch (SchedulerException e) {
                e.printStackTrace();
            }
        }
    }
    
    //  暂停
    public static void pauseJob(String triggerName, String triggerGroupName) {
        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.pauseTrigger(new TriggerKey(triggerName, triggerGroupName));
        } catch (Exception e) {
            log.info("暂停任务失败:{}", e);
        }
    }
    
    // 恢复
    public  static void resumeJob(String triggerName, String triggerGroupName) {
        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.resumeTrigger(new TriggerKey(triggerName, triggerGroupName));
        } catch (Exception e) {
            log.info("恢复任务失败:{}", e);
        }
    }
    
}
