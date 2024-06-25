package com.kai.common.intercepter;

import com.example.demo.common.throwable.FlowException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

@Component
@Slf4j
public class FlowControlIntercepter extends HandlerInterceptorAdapter {
    private static final ReentrantLock lock = new ReentrantLock();

    private int flowCnt = 0;
    private static final int MAX_FLOW = 2000;   //全局最大流量
    private static final int MAX_HOST_FLOW_PER_MINUETE = 500;  //当个主机在一分钟内的最大流量
    private static final int BLACK_MINUTE = 5;  //主机被拉黑后在5分钟内禁止访问

    private static final Map<String, Integer> hostAndVisNumMap = Collections.synchronizedMap(new HashMap<>());
    private static final Map<String, LocalDateTime> hostBlackList = Collections.synchronizedMap(new HashMap<>());

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        lock.lock();
        try {
            //超出流量就返回
            if (flowCnt > MAX_FLOW) {
                log.error("流量超出");
                throw new FlowException("流量超出");
            }

            String host = request.getRemoteHost();

            //处理黑名单就返回
            LocalDateTime deadtime = hostBlackList.get(host);
            if (deadtime != null) {
                LocalDateTime now = LocalDateTime.now();
                if (now.plusMinutes(BLACK_MINUTE).compareTo(deadtime) < 0) {
                    log.error("主机: " + host + " 位于黑名单");
                    throw new FlowException("主机访问次数过于频繁");
                }

                hostBlackList.remove(host);
            }

            //处理主机流量限制
            Integer hostVisCnt;
            hostVisCnt = hostAndVisNumMap.get(host);
            if (hostVisCnt == null) {
                hostVisCnt = 0;
            }

            //单主机访问过于频繁就限制
            if (hostVisCnt > MAX_HOST_FLOW_PER_MINUETE) {
                log.error("主机: " + host + " 访问次数过于频繁");
                throw new FlowException("主机访问次数过于频繁");
            }

            hostAndVisNumMap.put(host, ++hostVisCnt);
            ++flowCnt;

            log.info("当前全局流量 " + flowCnt);
            log.info("当前主机流量 " + host + " " + hostVisCnt);

            return true;
        } finally {
            lock.unlock();
        }
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        super.afterCompletion(request, response, handler, ex);
        lock.lock();
        flowCnt--;
//        log.info("全局流量-1 " + flowCnt);
        lock.unlock();
    }

    public static void clearHostVisMap() {
        lock.lock();
        for (String host : hostAndVisNumMap.keySet()) {
            int cnt = hostAndVisNumMap.get(host);
            cnt = Math.max(0, cnt - MAX_HOST_FLOW_PER_MINUETE);
            hostAndVisNumMap.put(host, cnt);
        }
        lock.unlock();
    }
}
