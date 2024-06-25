package com.kai.common.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.demo.game.entity.Circle;
import com.example.demo.game.entity.CircleActivation;
import com.example.demo.game.service.impl.CircleActivationServiceImpl;
import com.example.demo.game.service.impl.CircleServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FlushActivationService {


    @Autowired
    CircleActivationServiceImpl activationService;

    @Autowired
    CircleServiceImpl circleService;

    public void flush() {
        LocalDate nowDate = LocalDate.now();
        LocalDate nextDate = nowDate.plusDays(1);

        List<Circle> circleList = circleService.list();
        //全局的，id为-1
        Circle global = new Circle();
        global.setId(-1);
        circleList.add(global);

        List<CircleActivation> activationList = activationService
                .list(new QueryWrapper<CircleActivation>()
                        .lambda()
                        .in(CircleActivation::getCircleId, circleList.stream().map(Circle::getId).collect(Collectors.toList()))
                        .eq(CircleActivation::getRecordDate, nowDate));

        //筛选并保存到数据库
        filterAndSave(circleList, activationList, nowDate);

        activationList = activationService
                .list(new QueryWrapper<CircleActivation>()
                        .lambda()
                        .in(CircleActivation::getCircleId, circleList.stream().map(Circle::getId).collect(Collectors.toList()))
                        .eq(CircleActivation::getRecordDate, nextDate));

        //筛选并保存到数据库
        filterAndSave(circleList, activationList, nextDate);
    }

    private void filterAndSave(List<Circle> circleList, List<CircleActivation> activationList, LocalDate date) {
        List<Circle> circleListCopy = new ArrayList<>(circleList);

        //筛选出不存在的活跃表 对应circle
        Iterator<Circle> it = circleListCopy.iterator();
        while (it.hasNext()){
            Circle circle = it.next();
            for (CircleActivation activation : activationList) {
                if (circle.getId().equals(activation.getCircleId())) {
                    it.remove();
                    break;
                }
            }
        }

        List<CircleActivation> needSaveActivations = circleListCopy.stream().map(circle -> {
            CircleActivation activation = new CircleActivation();
            activation.setCircleId(circle.getId());
            activation.setRecordDate(date);
            return activation;
        }).collect(Collectors.toList());

        //新增活跃度行
        activationService.saveBatch(needSaveActivations);
    }
}
