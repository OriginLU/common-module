package com.zeroone.seata.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("to-account")
public interface ToAccountProxy {


    @GetMapping("/account/{id}/{balance}")
    String account(@PathVariable("id") Long id, @PathVariable("balance")Long balance);
}
