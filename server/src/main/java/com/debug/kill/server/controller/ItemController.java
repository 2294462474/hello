package com.debug.kill.server.controller;


import com.debug.kill.model.entity.ItemKill;
import com.debug.kill.server.service.IItemService;
import jdk.nashorn.internal.ir.RuntimeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.xml.stream.FactoryConfigurationError;
import java.util.List;

@Controller
public class ItemController {
    private  static final Logger log= LoggerFactory.getLogger(ItemController.class);
    private  static final String prefix="item";
    @Autowired
    private IItemService iItemService;

    @RequestMapping(value = {"/","/index",prefix+"/list",prefix+"/index.html"},method = RequestMethod.GET)
    public String list(ModelMap modelMap){
        try{
            List<ItemKill> list=iItemService.getKillItems();
            modelMap.put("list",list);
            log.info("获取带秒杀商品:{}",list);
        }catch (Exception e)
        {
            log.error("获取秒杀商品发生异常",e.fillInStackTrace());
            return "redirect:/base/errpr";
        }
        return "list";
    }

    @RequestMapping(value =prefix+"/detail/{id}",method = RequestMethod.GET)
    public String detail(@PathVariable Integer id,ModelMap modelMap){

        if(id==null ||id==0){
            return "redirect:/base/errpr";

        }
        try{
                ItemKill detail=iItemService.getKillDetail(id);
                modelMap.put("detail",detail);

        }catch (Exception e){
            log.error("获取待秒杀商品发生异常id={}",id,e.fillInStackTrace());
        }

        return "info";


    }

}
