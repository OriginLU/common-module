package com.zeroone.seata.resource;

import com.zeroone.seata.service.FromService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FromResource {



    @Autowired
    private FromService fromService;


    @GetMapping("/account/{id}/{balance}/{toId}")
    public String account(@PathVariable("id") Long id,
                          @PathVariable("balance")Long balance,
                          @PathVariable("toId")Long toId){

        fromService.tryUpdate(id,balance,toId);
        return "true";
    }
}
