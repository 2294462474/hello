package com.debug.kill.server.service;


import com.debug.kill.model.entity.ItemKillSuccess;
import com.debug.kill.model.mapper.ItemKillSuccessMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Consumer;

@Service
public class SchedulerService {
    private static final Logger log= LoggerFactory.getLogger(SchedulerService.class);

    @Autowired
    private ItemKillSuccessMapper itemKillSuccessMapper;

    @Autowired
    private Environment env;

    @Scheduled(cron="0 0/30 * * * ?")
    public void schedulerExireOrder(){
        try {

            List<ItemKillSuccess> list=itemKillSuccessMapper.selectExpireOrders();
            if(list.size()!=0&&!list.isEmpty())
            {
                list.stream().forEach(new Consumer<ItemKillSuccess>() {
                    @Override
                    public void accept(ItemKillSuccess i) {
                        if(i!=null&&i.getDiffTime()>env.getProperty("scheduler.expire.orders.time",Integer.class)){
                            itemKillSuccessMapper.expireOrder(i.getCode());
                        }
                    }
                });
            }


        }catch (Exception e)
        {
            log.error(e.fillInStackTrace().toString());
        }
    }
    /*
    @Scheduled(cron="0/11 * * * * ?")
    public void schedulerExireOrderV2(){
        log.info("v2");
    }
    @Scheduled(cron="0/10 * * * * ?")
    public void schedulerExireOrderV3(){
        log.info("v3");
    }*/
}
