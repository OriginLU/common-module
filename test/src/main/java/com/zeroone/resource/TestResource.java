package com.zeroone.resource;

import com.zeroone.entity.Xa01;
import com.zeroone.service.XaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author zero-one.lu
 * @since 2021-04-11
 */
@RestController
public class TestResource {



    @Autowired
    private XaService xaService;

    @GetMapping("test")
    public List<Xa01> test(){
        return xaService.findAll();
    }


    @GetMapping("test1")
    public Page<Xa01> test1(){
        return xaService.findSpecification();
    }


}
