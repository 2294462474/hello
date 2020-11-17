package com.debug.kill.server.controller;


import com.debug.kill.api.enums.StatucCode;
import com.debug.kill.api.response.BaseResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("base")
public class baseController {
    private static final Logger log= LoggerFactory.getLogger(baseController.class);
    private static final String prefix="base";

    @RequestMapping("/welcome")
    public String welcome(String name, ModelMap modelMap){
        if(StringUtils.isBlank(name))
        {
            name="这是welcome";
        }
        modelMap.put("name",name);
        return "welcome";
    }
    @RequestMapping(value = "/data",method = RequestMethod.GET)
    @ResponseBody
    public String data(String name){
        if(StringUtils.isBlank(name))
        {
            name="这是welcome";
        }
        return name;
    }

    @RequestMapping(value = "/response",method = RequestMethod.GET)
    @ResponseBody
    public BaseResponse response(String name){

        BaseResponse response=new BaseResponse(StatucCode.Success);
        if(StringUtils.isBlank(name))
        {
            name="这是welcome";
        }
        response.setData(name);//处理json
        return response;
    }

    @RequestMapping(value="error",method = RequestMethod.GET)
    public String error(){
        return "error";
    }

}
