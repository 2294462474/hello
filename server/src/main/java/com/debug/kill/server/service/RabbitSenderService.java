package com.debug.kill.server.service;


import com.debug.kill.model.dto.KillSuccessUserInfo;
import com.debug.kill.model.mapper.ItemKillSuccessMapper;
import jodd.util.StringUtil;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.AbstractJavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.core.env.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RabbitSenderService {

    private static final Logger log= LoggerFactory.getLogger(RabbitSenderService.class);
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private Environment env;

    @Autowired
    private ItemKillSuccessMapper itemKillSuccessMapper;


    public void sendKillSuccessEmailMsg(String orderNo){
         log.info("秒杀成功异步发送邮件通知消息-准备发送消息：{}",orderNo);
         try{
            if(StringUtil.isNotBlank(orderNo))
            {
               KillSuccessUserInfo info= itemKillSuccessMapper.selectByCode(orderNo);
               if(info!=null)
               {
                   rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
                   rabbitTemplate.setExchange(env.getProperty("mq.kill.item.success.email.exchange"));
                   rabbitTemplate.setRoutingKey(env.getProperty("mq.kill.item.success.email.routing.key"));

                   rabbitTemplate.convertAndSend(info, new MessagePostProcessor() {
                       @Override
                       public Message postProcessMessage(Message message) throws AmqpException {
                           MessageProperties messageProperties=message.getMessageProperties();
                           messageProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                           messageProperties.setHeader(AbstractJavaTypeMapper.DEFAULT_CONTENT_CLASSID_FIELD_NAME,
                                   KillSuccessUserInfo.class);
                           return message;
                       }
                   });
               }
            }


         }catch (Exception e){
             log.error("秒杀成功异步发送邮件通知消息-发生异常，消息为：{}",orderNo,e.fillInStackTrace());
         }

    }

    /**
     * 秒杀成功后生成抢购订单-发送信息入死信队列，等待着一定时间失效超时未支付的订单
     * @param orderCode
     */
    public void sendKillSuccessOrderExpireMsg(final String orderCode){

       try {
           if(StringUtil.isNotBlank(orderCode)){
               KillSuccessUserInfo info=itemKillSuccessMapper.selectByCode(orderCode);
               if(info!=null){

                   rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
                   rabbitTemplate.setExchange(env.getProperty("mq.kill.item.success.kill.dead.prod.exchange"));
                   rabbitTemplate.setRoutingKey(env.getProperty("mq.kill.item.success.kill.dead.prod.routing.key"));
                   rabbitTemplate.convertAndSend(info, new MessagePostProcessor() {
                       @Override
                       public Message postProcessMessage(Message message) throws AmqpException {
                           MessageProperties messageProperties=message.getMessageProperties();
                           messageProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                           messageProperties.setHeader(AbstractJavaTypeMapper.DEFAULT_CONTENT_CLASSID_FIELD_NAME,
                                   KillSuccessUserInfo.class);
                           final  Long ttl=10000L;
                           messageProperties.setExpiration(env.getProperty("mq.kill.item.success.kill.expire"));
                           return message;
                       }
                   });
               }

           }
       }catch (Exception e) {
           log.error("秒杀成功异步发送邮件通知消息-发生异常，消息为：{}", orderCode, e.fillInStackTrace());
       }
    }


}
