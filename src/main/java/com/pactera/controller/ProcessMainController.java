package com.pactera.controller;

import org.activiti.engine.RuntimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by pactera on 2017/11/21.
 */
@Controller
@RequestMapping("process/")
public class ProcessMainController {

   @Autowired
   RuntimeService runtimeService;

   @RequestMapping("test")
   public void test(){
      System.out.println("hello world");
   }
}
