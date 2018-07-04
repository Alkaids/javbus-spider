package com.gravel.controller;

import com.gravel.redis.RedisService;
import com.gravel.webmagic.pageprocessor.JavProxyPoolProcessor;
import com.gravel.webmagic.pipeline.JvaSpiderPipeline;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by gravel on 2018/06/26.
 */
@Controller
public class StartUpController {

    @Autowired
    JvaSpiderPipeline jvaSpiderPipeline;

    @Autowired
    JavProxyPoolProcessor kdlProcessor;

    @Autowired
    private RedisService redisService;

    /**
     * index页面展示
     * @return
     */
    @RequestMapping("/")
    public String index() {
        new Thread(() -> kdlProcessor.start(kdlProcessor, jvaSpiderPipeline,"https://www.javbus.com")).start();
        return "/index";
    }

    /**
     * index页面展示
     * @return
     */
    @RequestMapping("/uncensored")
    public String uncensored() {
        new Thread(() -> kdlProcessor.start(kdlProcessor, jvaSpiderPipeline,"https://www.javbus.com/uncensored")).start();
        return "/index";
    }
}
