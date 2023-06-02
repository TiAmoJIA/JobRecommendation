package com.lxc.job.config;

import com.lxc.job.entity.SparkSubmit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class AlsRec {

    @Scheduled(cron = "0 0 0 * * ?")
    public void alsSubmit() {
        SparkSubmit sparkSubmit = new SparkSubmit();
        sparkSubmit.submit();

    }

    public AlsRec alsRec() {
        return new AlsRec();
    }

}
