package com.zjw.apps3pluspro.module.device.weather.openweather;

import android.util.Log;

import com.google.gson.Gson;
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.sharedpreferences.BleDeviceTools;

/**
 * Created by android
 * on 2021/5/31
 */
public class WeatherManager {
    private static final String TAG = WeatherManager.class.getSimpleName();
    private static WeatherManager weatherManager;
    private BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();

    public static WeatherManager getInstance() {
        if (weatherManager == null) {
            weatherManager = new WeatherManager();
        }
        return weatherManager;
    }

    public interface GetOpenWeatherListener {
        void onSuccess();

        void onFail();
    }

    private static final String appid = "55927c151597caba67e863d486282616";
    public static final String lang = "en";
    private static final String units = "metric";
    //api.openweathermap.org/data/2.5/weather?lat=22.547&lon=114.085947&appid=55927c151597caba67e863d486282616&lang=en&units=metric
    private static final String getCurrentWeatherUrl = "http://api.openweathermap.org/data/2.5/weather?";

    public CurrentWeather currentWeather;

    double lat = 0.0;
    double lon = 0.0;

    private GetOpenWeatherListener getOpenWeatherListener;

    public void getCurrentWeather(boolean getCityOnly, boolean isSaveName, double lat, double lon, GetOpenWeatherListener getOpenWeatherListener) {
        this.getOpenWeatherListener = getOpenWeatherListener;
        this.lat = lat;
        this.lon = lon;
        Log.i(TAG, "getCurrentWeather = " + "lat = " + lat + " lon = " + lon);
        String url = getCurrentWeatherUrl + "lat=" + lat + "&lon=" + lon + "&appid=" + appid + "&lang=" + lang + "&units=" + units;
        MyOkHttpClient2.getInstance().asynGetCall(new DisposeDataHandle(new DisposeDataListener() {
            @Override
            public void onSuccess(Object responseObj) {
                Log.w(TAG, "getCurrentWeather onSuccess = " + responseObj);
                Gson gson = new Gson();
                currentWeather = gson.fromJson(responseObj.toString(), CurrentWeather.class);
                if (isSaveName) {
                    mBleDeviceTools.setWeatherCity(currentWeather.name);
                    mBleDeviceTools.setWeatherCityID(String.valueOf(currentWeather.id));
                }
                if (getCityOnly) {
                    getOpenWeatherListener.onSuccess();
                    return;
                }
                // get 5 days weather data
                getEveryDayWeather(String.valueOf(currentWeather.id));

            }

            @Override
            public void onFailure(Object reasonObj) {
                Log.w(TAG, "getCurrentWeather onFailure = " + reasonObj);
                if (getOpenWeatherListener != null) {
                    getOpenWeatherListener.onFail();
                }
            }
        }), url);
    }

    //api.openweathermap.org/data/2.5/forecast/daily?id=6958812&cnt=5&appid=55927c151597caba67e863d486282616&lang=en&units=metric
    private static final String getEveryDayWeatherUrl = "http://api.openweathermap.org/data/2.5/forecast/daily?";

    public WeatherDays weatherDays;

    public void getEveryDayWeather(String cityId) {
        String url = getEveryDayWeatherUrl + "id=" + cityId + "&cnt=" + 5 + "&appid=" + appid + "&lang=" + lang + "&units=" + units;

        MyOkHttpClient2.getInstance().asynGetCall(new DisposeDataHandle(new DisposeDataListener() {
            @Override
            public void onSuccess(Object responseObj) {
                Log.w(TAG, "getEveryDayWeather onSuccess = " + responseObj);
                Gson gson = new Gson();
                weatherDays = gson.fromJson(responseObj.toString(), WeatherDays.class);

                // get 4 days 96 hours data
                getWeatherPerHour(mBleDeviceTools.getWeatherCityID());
            }

            @Override
            public void onFailure(Object reasonObj) {
                Log.w(TAG, "getEveryDayWeather onFailure = " + reasonObj);
                if (getOpenWeatherListener != null) {
                    getOpenWeatherListener.onFail();
                }
            }
        }), url);
    }

    //pro.openweathermap.org/data/2.5/forecast/hourly?id=6958812&appid=55927c151597caba67e863d486282616&lang=en&units=metric
    private static final String getWeatherPerHourUrl = "http://pro.openweathermap.org/data/2.5/forecast/hourly?";

    public WeatherPerHour weatherPerHour;

    public void getWeatherPerHour(String cityId) {
        String url = getWeatherPerHourUrl + "id=" + cityId + "&appid=" + appid + "&lang=" + lang + "&units=" + units;

        MyOkHttpClient2.getInstance().asynGetCall(new DisposeDataHandle(new DisposeDataListener() {
            @Override
            public void onSuccess(Object responseObj) {
                Log.w(TAG, "getWeatherPerHour onSuccess = " + responseObj);
                Gson gson = new Gson();
                weatherPerHour = gson.fromJson(responseObj.toString(), WeatherPerHour.class);

                getWeatherAQI();
            }

            @Override
            public void onFailure(Object reasonObj) {
                Log.w(TAG, "getWeatherPerHour onFailure = " + reasonObj);
                if (getOpenWeatherListener != null) {
                    getOpenWeatherListener.onFail();
                }
            }
        }), url);
    }

    //api.openweathermap.org/data/2.5/air_pollution/history?lat=22.63157957374291&lon=113.83335400582953&appid=55927c151597caba67e863d486282616&start=1622444442&end=1622790042
    private static final String getWeatherAQIUrl = "http://api.openweathermap.org/data/2.5/air_pollution/history?";

    public WeatherAQI weatherAQI;

    public void getWeatherAQI() {
        long start = System.currentTimeMillis() / 1000;
        long end = start + 96 * 3600;
        Log.i(TAG, "getWeatherAQI start = " + start + " end = " + end + " lon = " + lon + " lat = " + lat);
        String url = getWeatherAQIUrl + "lat=" + lat + "&lon=" + lon + "&appid=" + appid + "&start=" + start + "&end=" + end;
        MyOkHttpClient2.getInstance().asynGetCall(new DisposeDataHandle(new DisposeDataListener() {
            @Override
            public void onSuccess(Object responseObj) {
                Log.w(TAG, "getWeatherAQI onSuccess = " + responseObj);
                Gson gson = new Gson();
                weatherAQI = gson.fromJson(responseObj.toString(), WeatherAQI.class);
                Log.i(TAG, "getWeatherAQI onSuccess size = " + weatherAQI.list.size());

                if (getOpenWeatherListener != null) {
                    getOpenWeatherListener.onSuccess();
                }
            }

            @Override
            public void onFailure(Object reasonObj) {
                Log.w(TAG, "getWeatherAQI onFailure = " + reasonObj);
                if (getOpenWeatherListener != null) {
                    getOpenWeatherListener.onFail();
                }
            }
        }), url);
    }

    //api.openweathermap.org/data/2.5/find?q=shenzhen&appid=55927c151597caba67e863d486282616&lang=en&units=metric
    private static final String getWeatherFindBySearchUrl = "http://api.openweathermap.org/data/2.5/find?";
    public WeatherFind weatherFind;

    public void getWeatherFindBySearch(String city, GetOpenWeatherListener getOpenWeatherListener) {
        String url = getWeatherFindBySearchUrl + "q=" + city + "&appid=" + appid + "&lang=" + lang + "&units=" + units;
        MyOkHttpClient2.getInstance().asynGetCall(new DisposeDataHandle(new DisposeDataListener() {
            @Override
            public void onSuccess(Object responseObj) {
                Log.w(TAG, "getWeatherFindBySearch onSuccess = " + responseObj);
                Gson gson = new Gson();
                weatherFind = gson.fromJson(responseObj.toString(), WeatherFind.class);
                getOpenWeatherListener.onSuccess();
            }

            @Override
            public void onFailure(Object reasonObj) {
                Log.w(TAG, "getWeatherFindBySearch onFailure = " + reasonObj);
                getOpenWeatherListener.onFail();
            }
        }), url);
    }


}
