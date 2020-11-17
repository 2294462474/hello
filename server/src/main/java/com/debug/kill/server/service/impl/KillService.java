package com.debug.kill.server.service.impl;

import com.debug.kill.model.entity.ItemKill;
import com.debug.kill.model.entity.ItemKillSuccess;
import com.debug.kill.model.mapper.ItemKillMapper;
import com.debug.kill.model.mapper.ItemKillSuccessMapper;
import com.debug.kill.server.enums.SysConstant;
import com.debug.kill.server.service.IKillService;
import com.debug.kill.server.service.RabbitReceiverService;
import com.debug.kill.server.service.RabbitSenderService;
import com.debug.kill.server.utils.RandomUtil;
import com.debug.kill.server.utils.SnowFlake;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.joda.time.DateTime;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;


@Service
public class KillService implements IKillService {


    private static final Logger log= LoggerFactory.getLogger(KillService.class);



    @Autowired
    private ItemKillSuccessMapper itemKillSuccessMapper;

    @Autowired
    private ItemKillMapper itemKillMapper;

    private SnowFlake snowFlake=new SnowFlake(2,3);

    @Autowired
    private RabbitSenderService rabbitSenderService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedissonClient redissonClient;



    @Override
    public Boolean killItem(Integer killId, Integer userId) throws Exception {
        Boolean result=false;

        //TODO:判断当前用户是否已经抢购过当前商品
        if (itemKillSuccessMapper.countByKillUserId(killId,userId) <= 0){//从数据库判断count是否大于1
            //TODO:查询待秒杀商品详情
            ItemKill itemKill=itemKillMapper.selectById(killId);

            //TODO:判断是否可以被秒杀canKill=1?
            if (itemKill!=null && 1==itemKill.getCanKill() ){
                //TODO:扣减库存-减一
                int res=itemKillMapper.updateKillItem(killId);

                //TODO:扣减是否成功?是-生成秒杀成功的订单，同时通知用户秒杀成功的消息
                if (res>0){
                    commonRecordKillSuccessInfo(itemKill,userId);

                    result=true;
                }
            }
        }else{
            throw new Exception("您已经抢购过该商品了!");
        }
        return result;
    }

    /*mysql 优化*/
    @Override
    public Boolean killItemV2(Integer killId, Integer userId) throws Exception {
        Boolean result=false;

        //TODO:判断当前用户是否已经抢购过当前商品
        if (itemKillSuccessMapper.countByKillUserId(killId,userId) <= 0){
            //TODO:查询待秒杀商品详情
            ItemKill itemKill=itemKillMapper.selectByIdV2(killId);// 库存>0

            //TODO:判断是否可以被秒杀canKill=1?
            if (itemKill!=null && 1==itemKill.getCanKill() &&itemKill.getTotal()>0){
                //TODO:扣减库存-减一
                int res=itemKillMapper.updateKillItemV2(killId);

                //TODO:扣减是否成功?是-生成秒杀成功的订单，同时通知用户秒杀成功的消息
                if (res>0){
                    commonRecordKillSuccessInfo(itemKill,userId);

                    result=true;
                }
            }
        }else{
            throw new Exception("您已经抢购过该商品了!");
        }
        return result;
    }

    /*redis分布式锁*/
    @Override
    public Boolean killItemV3(Integer killId, Integer userId) throws Exception {
        Boolean result=false;

        //TODO:判断当前用户是否已经抢购过当前商品
        if (itemKillSuccessMapper.countByKillUserId(killId,userId) <= 0)
        //if(true)
        {
            //借助redia的原子操作
            ValueOperations valueOperations=stringRedisTemplate.opsForValue();
            final String key=new StringBuffer().append(killId).append(userId).append("-RedisLock").toString();
            final String value=RandomUtil.generateOrderCode();
            Boolean cacheRes=valueOperations.setIfAbsent(key,value);//setnx lua脚本实现setnx和expire并行 set if not exist
            //宕机了傻了吧哈哈哈 用redission
            if(cacheRes){
                stringRedisTemplate.expire(key,10, TimeUnit.SECONDS);
               try{
                   ItemKill itemKill=itemKillMapper.selectByIdV2(killId);// 库存>0
                   if (itemKill!=null && 1==itemKill.getCanKill() &&itemKill.getTotal()>0){
                       int res=itemKillMapper.updateKillItemV2(killId);
                       if (res>0){
                           commonRecordKillSuccessInfo(itemKill,userId);
                           result=true;
                       }
                   }
               }catch (Exception e) {
                   throw new Exception("aaaaaaaaa");

               }finally {
                   //释放锁
                   if(value.equals(valueOperations.get(key)))
                   {
                       stringRedisTemplate.delete(key);
                   }
               }
            }

        }else{
            throw new Exception("redis-您已经抢购过该商品了!");
        }
        return result;
    }

    /*
    redisson 分布式锁
    * */
    @Override
    public Boolean killItemV4(Integer killId, Integer userId) throws Exception {
        Boolean result=false;
        final String lockKey=new StringBuffer().append(killId).append(userId).append("-RedissonLock").toString();
        RLock lock=redissonClient.getLock(lockKey);//宕机傻了吧
        try {
            //lock.lock(10,TimeUnit.SECONDS);
            Boolean cacheRes=lock.tryLock(30,10,TimeUnit.SECONDS);//等待30s，10s自动释放。
            if(cacheRes) {
                if (itemKillSuccessMapper.countByKillUserId(killId, userId) <= 0) {
                    ItemKill itemKill = itemKillMapper.selectByIdV2(killId);// 库存>0
                    if (itemKill != null && 1 == itemKill.getCanKill() && itemKill.getTotal() > 0) {
                        int res = itemKillMapper.updateKillItemV2(killId);
                        if (res > 0) {
                            commonRecordKillSuccessInfo(itemKill, userId);
                            result = true;
                        }
                    }
                }
            }
        }catch (Exception e){
            throw new Exception("aaaaaaaaa");
        }finally {
            lock.unlock();
//            lock.forceUnlock();
        }

        return result;
    }


    /**
     * 通用的方法-记录用户秒杀成功后生成的订单-并进行异步邮件消息的通知
     * @param kill
     * @param userId
     * @throws Exception
     */

    private void commonRecordKillSuccessInfo(ItemKill kill,Integer userId) throws Exception{
        //TODO:记录抢购成功后生成的秒杀订单记录

        ItemKillSuccess entity=new ItemKillSuccess();

        //entity.setCode(RandomUtil.generateOrderCode());   //传统时间戳+N位随机数

        String orderNo=String.valueOf(String.valueOf(snowFlake.nextId()));
        entity.setCode(orderNo);
        entity.setItemId(kill.getItemId());
        entity.setKillId(kill.getId());
        entity.setUserId(userId.toString());
        entity.setStatus(SysConstant.OrderStatus.SuccessNotPayed.getCode().byteValue());
        entity.setCreateTime(DateTime.now().toDate());
        //TODO:学以致用，举一反三 -> 仿照单例模式的双重检验锁写法

        if (itemKillSuccessMapper.countByKillUserId(kill.getId(),userId) <= 0) {
            int res = itemKillSuccessMapper.insertSelective(entity);
            if (res > 0) {
                //TODO:进行异步邮件消息的通知=rabbitmq+mail
                rabbitSenderService.sendKillSuccessEmailMsg(orderNo);
                //TODO:用于入死信队列，超过TTL失效
                rabbitSenderService.sendKillSuccessOrderExpireMsg(orderNo);


            }
        }
    }
}
