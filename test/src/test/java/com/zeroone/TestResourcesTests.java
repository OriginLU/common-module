package com.zeroone;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zeroone.entity.Xa01;
import com.zeroone.service.XaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
public class TestResourcesTests {


    @Autowired
    private XaService xaService;


    @Test
    void  test(){

        List<Xa01> content = xaService.findSpecification().getContent();

        try {
            System.out.println(new ObjectMapper().writeValueAsString(content));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
