package com.zero.webflux.service;

import com.zero.webflux.entity.City;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CityService {





    private final Map<String, City> cityCache = new ConcurrentHashMap<>();

    {
        cityCache.put("福州",new City("福州", "1000"));
        cityCache.put("泉州",new City("泉州", "1000"));
        cityCache.put("龙岩",new City("龙岩", "1000"));
        cityCache.put("厦门",new City("厦门", "1000"));
    }




    public Mono<City> findCityByName(String name){
        return Mono.create(sink -> sink.success(cityCache.get(name)));
    }


    public Mono<City> removeCityByName(String name){
        return Mono.create(sink -> sink.success(cityCache.remove(name)));
    }


    public Mono<Void> updateCityByName(String name,String value){
        cityCache.put(name,City.build(value,"1000"));
        return Mono.empty();
    }




}
