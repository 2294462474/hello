package com.debug.kill.server.service;


import com.debug.kill.model.dto.KillSuccessUserInfo;
import com.debug.kill.model.entity.ItemKillSuccess;
import com.debug.kill.model.mapper.ItemKillSuccessMapper;
import com.debug.kill.server.dto.MailDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class RabbitReceiverService {
    private static final Logger log= LoggerFactory.getLogger(RabbitReceiverService.class);

    @Autowired
    private MailService mailService;

    @Autowired
    private Environment env;

    @Autowired
    private ItemKillSuccessMapper itemKillSuccessMapper;


    /*接受消息异步通知*/
    @RabbitListener(queues = {"${mq.kill.item.success.email.queue}"},containerFactory = "singleListenerContainer")
    public void consumeEmailMsg(KillSuccessUserInfo info){

        try{
            log.info("秒杀异步邮件通知-接收消息:{}",info);
            MailDto dto=new MailDto(env.getProperty("mail.kill.item.success.subject"),"这是测试内容",
                    new String[]{info.getEmail()});
           // mailService.sendSimpleEmail(dto);
            mailService.sendHttpMail(dto);

        }catch (Exception e){

            log.error("秒杀异步邮件通知-接收消息-发生异常：",e.fillInStackTrace());
        }
    }
    /*
    * 秒杀成功超时未支付
    * */
    @RabbitListener(queues = {"${mq.kill.item.success.kill.dead.real.queue}"},
            containerFactory = "singleListenerContainer")

    public void consumeExpireOrder(KillSuccessUserInfo info){

        try{
            log.info("用户秒杀成功后超时未支付-监听者-接收消息:{}",info);
            if(info!=null)
            {
                ItemKillSuccess entity =itemKillSuccessMapper.selectByPrimaryKey(info.getCode());
                if(entity!=null&&entity.getStatus().intValue()==0)
                    itemKillSuccessMapper.expireOrder(info.getCode());

            }


        }catch (Exception e){
            log.error("用户秒杀成功后超时未支付-监听者-发生异常：",e.fillInStackTrace());
        }
    }

}
