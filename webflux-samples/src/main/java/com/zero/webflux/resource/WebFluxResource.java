package com.zero.webflux.resource;

import com.zero.webflux.entity.City;
import com.zero.webflux.service.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RequestMapping("/api")
@RestController
public class WebFluxResource {




    @Autowired
    private CityService cityService;


    @GetMapping("/findCityByName")
    public Mono<City>  findCityByName(@RequestParam("name") String name){
        return cityService.findCityByName(name);
    }


    @GetMapping("/removeCityByName")
    public Mono<City>  removeCityByName(@RequestParam("name") String name){
        return cityService.removeCityByName(name);
    }

}
