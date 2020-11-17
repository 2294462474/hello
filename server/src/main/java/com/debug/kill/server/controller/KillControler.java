package com.debug.kill.server.controller;


import com.debug.kill.api.enums.StatucCode;
import com.debug.kill.api.response.BaseResponse;
import com.debug.kill.model.dto.KillSuccessUserInfo;
import com.debug.kill.model.mapper.ItemKillSuccessMapper;
import com.debug.kill.server.dto.KillDto;
import com.debug.kill.server.service.IKillService;
import jodd.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

@Controller
public class KillControler {

    private static final Logger log= LoggerFactory.getLogger(KillControler.class);
    private static final String prefix="kill";
    @Autowired
    private IKillService killService;

    @Autowired
    private ItemKillSuccessMapper itemKillSuccessMapper;

    @RequestMapping(value = prefix+"/execute",method= RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public BaseResponse excude(@RequestBody @Validated KillDto dto, BindingResult result)
    {
        if(result.hasErrors()||dto.getKillId()<=0)
        {
            return new BaseResponse(StatucCode.InvalidParams);
        }
        BaseResponse response=new BaseResponse(StatucCode.Success);
        try{

            Boolean res=killService.killItem(dto.getKillId(),dto.getUserId());
            if(!res){
                return new BaseResponse(StatucCode.Fail.getCode(),"哈哈~商品已抢购完毕或者不在抢购时间段哦!");
            }
        }catch (Exception e)
        {

            response=new BaseResponse(StatucCode.Fail.getCode(),e.getMessage());
        }
        return response;

    }

    @RequestMapping(value = prefix+"/execute/lock",method= RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public BaseResponse excudeLock(@RequestBody @Validated KillDto dto, BindingResult result)
    {
        if(result.hasErrors()||dto.getKillId()<=0)
        {
            return new BaseResponse(StatucCode.InvalidParams);
        }
        BaseResponse response=new BaseResponse(StatucCode.Success);
        try{
            //不加分布式锁
            /*
            Boolean res=killService.killItemV2(dto.getKillId(),dto.getUserId());
            if(!res){
                return new BaseResponse(StatucCode.Fail.getCode(),"哈哈不加分布式锁~商品已抢购完毕或者不在抢购时间段哦!");
            }*/
            Boolean res=killService.killItemV3(dto.getKillId(),dto.getUserId());
            if(!res){
                return new BaseResponse(StatucCode.Fail.getCode(),"哈哈不加分布式锁~商品已抢购完毕或者不在抢购时间段哦!");
            }/*
            Boolean res=killService.killItemV4(dto.getKillId(),dto.getUserId());
            if(!res){
                return new BaseResponse(StatucCode.Fail.getCode(),"哈哈不加分布式锁~商品已抢购完毕或者不在抢购时间段哦!");
            }*/

        }catch (Exception e)
        {

            response=new BaseResponse(StatucCode.Fail.getCode(),e.getMessage());
        }
        return response;

    }

    @RequestMapping(value = prefix+"/record/detail{orderNo}",method = RequestMethod.GET)
    public String killRecordDetail(@PathVariable String orderNo , ModelMap modelMap){
       KillSuccessUserInfo info= itemKillSuccessMapper.selectByCode(orderNo);
       if(StringUtil.isBlank(orderNo))
       {
           return "error";
       }

       if(info==null){
            return "error";
        }
       modelMap.put("info",info);

        return "killRecord";
    }

    @RequestMapping(value = prefix+"/execute/success",method=RequestMethod.GET)
    public String excudeSuccess(){
        return "executeSuccess";
    }
    @RequestMapping(value = prefix+"/execute/fail",method=RequestMethod.GET)
    public String excudefali(){
        return "executeFail";
    }

}
