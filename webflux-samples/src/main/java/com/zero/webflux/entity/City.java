package com.zero.webflux.entity;

public class City {



    private String cityName;

    private String code;




    public static City build(String cityName, String code){
        return new City(cityName,code);
    }

    public City() {
    }

    public City(String cityName, String code) {
        this.cityName = cityName;
        this.code = code;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
