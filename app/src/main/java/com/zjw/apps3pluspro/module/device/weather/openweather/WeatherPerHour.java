package com.zjw.apps3pluspro.module.device.weather.openweather;

import java.util.ArrayList;

/**
 * Created by android
 * on 2021/5/31
 */
public class WeatherPerHour {
    public String cod;
    public String message;
    public String cnt;
    public ArrayList<WeatherHour> list;
    public WeatherCity city;

    public class WeatherHour {
        public String dt;
        public WeatherMain main;
        public ArrayList<WeatherItem> weather;
        public WeatherClouds clouds;
        public WeatherWind wind;
        public String visibility;
        public String pop;
        public WeatherRain rain;
        public WeatherSys sys;
    }


    /*
    *
    *  "cod": "200",
    "message": 0,
    "cnt": 96,
    "list": [
        {
            "dt": 1622448000,
            "main": {
                "temp": 32.06,
                "feels_like": 45.46,
                "temp_min": 30.13,
                "temp_max": 32.06,
                "pressure": 1002,
                "sea_level": 1002,
                "grnd_level": 1001,
                "humidity": 82,
                "temp_kf": 1.93
            },
            "weather": [
                {
                    "id": 500,
                    "main": "Rain",
                    "description": "light rain",
                    "icon": "10d"
                }
            ],
            "clouds": {
                "all": 36
            },
            "wind": {
                "speed": 8.58,
                "deg": 183,
                "gust": 11.17
            },
            "visibility": 10000,
            "pop": 0.65,
            "rain": {
                "1h": 0.73
            },
            "sys": {
                "pod": "d"
            },
            "dt_txt": "2021-05-31 08:00:00"
        },
    * */
}
