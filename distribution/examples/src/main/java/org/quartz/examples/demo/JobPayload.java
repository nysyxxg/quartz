package org.quartz.examples.demo;

import com.google.gson.Gson;
import lombok.Data;
import org.quartz.CronTrigger;

@Data
public class JobPayload {
    private String jobName;
    private String jobGroup;
    
    private String triggerName;
    private String triggerGroup;
    
    private String scheduleType;
    private String cron;
    private String desc;
    private String dataMap;

    public static JobPayload getInstance(CronTrigger trigger){
        JobPayload jobPayload = new JobPayload();
        jobPayload.setTriggerName(trigger.getKey().getName());
        jobPayload.setTriggerGroup(trigger.getKey().getGroup());
        
        Gson gson = new Gson();
        jobPayload.setDataMap(gson.toJson(trigger.getJobDataMap()));
        
        jobPayload.setDesc(trigger.getDescription());
        jobPayload.setCron(trigger.getCronExpression());
        return jobPayload;
    }
    
    @Override
    public String toString() {
        return "JobPayload{" +
                "jobName='" + jobName + '\'' +
                ", jobGroup='" + jobGroup + '\'' +
                ", triggerName='" + triggerName + '\'' +
                ", triggerGroup='" + triggerGroup + '\'' +
                ", scheduleType='" + scheduleType + '\'' +
                ", cron='" + cron + '\'' +
                ", desc='" + desc + '\'' +
                ", dataMap='" + dataMap + '\'' +
                '}';
    }
}