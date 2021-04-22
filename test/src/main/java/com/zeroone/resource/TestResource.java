package com.zeroone.resource;

import com.zeroone.bean.LazyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zero-one.lu
 * @since 2021-04-11
 */
@RestController
public class TestResource {



    @Autowired
    private LazyService lazyService;

    @GetMapping("test")
    public void test(){
        lazyService.say0();
    }
}
