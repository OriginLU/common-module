package com.zeroone.seata.resource;

import com.zeroone.seata.service.ToService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ToResource {


    @Autowired
    private ToService toService;


    @GetMapping("/account/{id}/{balance}")
    public String account(@PathVariable("id") Long id,@PathVariable("balance")Long balance){

        toService.tryUpdate(id,balance);
        return "true";
    }
}
