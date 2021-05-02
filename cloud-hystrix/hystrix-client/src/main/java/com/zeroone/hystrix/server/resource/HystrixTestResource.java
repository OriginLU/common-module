package com.zeroone.hystrix.server.resource;

import com.zeroone.hystrix.server.service.HystrixTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zero-one.lu
 * @since 2021-05-01
 */
@RestController
public class HystrixTestResource {


    @Autowired
    private HystrixTestService hystrixTestService;



    @GetMapping("/api/hystrix/{id}")
    public String findOne(@PathVariable("id")Long id) throws InterruptedException {
        return hystrixTestService.findOne(id);
    }


    @PostMapping("/api/hystrix/findList")
    public List<String> findOne(@RequestBody List<Long> id){
        return hystrixTestService.findList(id);
    }
}
