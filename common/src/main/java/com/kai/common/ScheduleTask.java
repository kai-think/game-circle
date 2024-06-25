package com.kai.common;

import com.example.demo.common.intercepter.FlowControlIntercepter;
import com.example.demo.common.service.FlushActivationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration      //1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling   // 2.开启定时任务
@Slf4j
public class ScheduleTask {
    @Autowired
    FlushActivationService flushActivationService;

    //或直接指定时间间隔，例如：60秒
    @Scheduled(fixedRate=60000)
    @Async
    void configureTasks() {
        log.debug("定期更新主机黑名单");
        FlowControlIntercepter.clearHostVisMap();
    }

    //每天凌晨0点更新一次
    @Scheduled(cron = "0 0 0 * * ?")
    @Async
    void updateActivationTable() {
        log.debug("新增今天与明天的 圈子活跃度表数据");
        flushActivationService.flush();
    }

}
