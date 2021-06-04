package com.zjw.apps3pluspro.bleservice.anaylsis;

import com.google.protobuf.InvalidProtocolBufferException;
import com.xiaomi.wear.protobuf.CommonProtos;
import com.xiaomi.wear.protobuf.WearProtos;
import com.xiaomi.wear.protobuf.WeatherProtos;
import com.zjw.apps3pluspro.module.device.weather.openweather.CurrentWeather;
import com.zjw.apps3pluspro.module.device.weather.openweather.WeatherAQI;
import com.zjw.apps3pluspro.module.device.weather.openweather.WeatherDays;
import com.zjw.apps3pluspro.module.device.weather.openweather.WeatherManager;
import com.zjw.apps3pluspro.module.device.weather.openweather.WeatherPerHour;

import java.util.ArrayList;

public class WeatherTools {


    static final int latest = 1; //绑定状态
    static final int forecast = 2;//绑定key?=和“AccountID”可以对应上？？？？？


    public static String analysisWeather(WearProtos.WearPacket wear) {

        WeatherProtos.Weather weather = wear.getWeather();
        int id = wear.getId();
        int pos = weather.getPayloadCase().getNumber();

        String result_str = "";

        System.out.println("数据封装 = " + "wear/type = " + wear.getType());
        System.out.println("数据封装 = " + "wear/id = " + wear.getId());
        System.out.println("数据封装 = " + "pos = " + pos);


        result_str += "wear/type = " + wear.getType() + "\n";
        result_str += "wear/id = " + wear.getId() + "\n";
        result_str += "pos = " + pos + "\n";

        result_str += "描述(参考):天气" + "-";

        if (id == 0x00) {
            result_str += "最新的天气" + "\n";
        } else if (id == 0x01) {
            result_str += "每日预测" + "\n";
        } else if (id == 0x02) {
            result_str += "小时预测" + "\n";
        } else {
            result_str += "未知" + "\n";
        }


        switch (pos) {
            case latest:

                result_str += "最新天气-今天" + "\n";

                WeatherProtos.WeatherLatest weather_latest = weather.getLatest();
                System.out.println("数据封装 = " + "weather/" + "weather_latest===========");
                result_str += "weather" + "/weather_latestt===========" + "\n";

                System.out.println("数据封装 = " + "weather/" + "weather_latest/WeatherId======");
                result_str += "weather" + "/weather_latestt/WeatherId======" + "\n";
                WeatherProtos.WeatherId weather_id = weather_latest.getId();
                result_str += getWeatherId(weather_id);


                System.out.println("数据封装 = " + "weather/" + "weather_latest/Weather = " + weather_latest.getWeather());
                result_str += "weather" + "/weather_latestt/Weather(天气类型) = " + weather_latest.getWeather() + "\n";


                System.out.println("数据封装 = " + "weather/" + "weather_latest/Temperature=====");
                result_str += "weather" + "/weather_latest/Temperature======" + "\n";
                result_str += CommonTools.getKeyValue(weather_latest.getTemperature());


                System.out.println("数据封装 = " + "weather/" + "weather_latest/Humidity=====");
                result_str += "weather" + "/weather_latest/Humidity======" + "\n";
                result_str += CommonTools.getKeyValue(weather_latest.getHumidity());

                System.out.println("数据封装 = " + "weather/" + "weather_latest/WindInfo=====");
                result_str += "weather" + "/weather_latest/WindInfo======" + "\n";
                result_str += CommonTools.getKeyValue(weather_latest.getWindInfo());


                System.out.println("数据封装 = " + "weather/" + "weather_latest/Uvindex======");
                result_str += "weather" + "/weather_latest/Uvindex======" + "\n";
                result_str += CommonTools.getKeyValue(weather_latest.getUvindex());

                System.out.println("数据封装 = " + "weather/" + "weather_latest/Aqi=====");
                result_str += "weather" + "/weather_latest/Aqi======" + "\n";
                result_str += CommonTools.getKeyValue(weather_latest.getAqi());

                System.out.println("数据封装 = " + "weather/" + "weather_latest/AlertsList=====");
                result_str += "weather" + "/weather_latest/AlertsList======" + "\n";
                WeatherProtos.Alerts.List alerts_list = weather_latest.getAlertsList();
                result_str += getAlertsList(alerts_list);

                break;

            case forecast:
                result_str += "天气预报-未来日期" + "\n";

                WeatherProtos.WeatherForecast weather_forecast = weather.getForecast();
                System.out.println("数据封装 = " + "weather/" + "weather_forecast/WeatherId=====");
                result_str += "weather" + "/weather_forecast/WeatherId======" + "\n";

                WeatherProtos.WeatherId weather_id2 = weather_forecast.getId();
                result_str += getWeatherId(weather_id2);

                System.out.println("数据封装 = " + "weather/" + "weather_forecast/data_list=====");
                result_str += "weather" + "/weather_forecast/data_list======" + "\n";

                WeatherProtos.WeatherForecast.Data.List data_list = weather_forecast.getDataList();
                result_str += getDataList(data_list);
                break;
        }
        return result_str;
    }

    public static byte[] getWeather(int id) {
        WearProtos.WearPacket.Builder wear1 = WearProtos.WearPacket.newBuilder();
        wear1.setType(WearProtos.WearPacket.Type.WEATHER);

        WeatherProtos.Weather.Builder weather = WeatherProtos.Weather.newBuilder();
        switch (id) {
            case 0:
                weather.setLatest(getWeatherLatest());
                wear1.setId((byte) WeatherProtos.Weather.WeatherID.LATEST_WEATHER.getNumber());
                break;
            case 1:
                weather.setForecast(getWeatherDailyForecast());
                wear1.setId((byte) WeatherProtos.Weather.WeatherID.DAILY_FORECAST.getNumber());
                break;
            case 2:
                weather.setHourforecast(getWeatherHourlyForecast());
                wear1.setId((byte) WeatherProtos.Weather.WeatherID.HOURLY_FORECAST.getNumber());
                break;
        }
        wear1.setWeather(weather);

        //解析自己传输的数据
//        try {
//            WearProtos.WearPacket wear = WearProtos.WearPacket.parseFrom(wear1.build().toByteArray());
//            WeatherTools.analysisWeather(wear);
//        } catch (InvalidProtocolBufferException e) {
//            e.printStackTrace();
//        }

        return wear1.build().toByteArray();
    }

    private static WeatherProtos.WeatherHourForecast getWeatherHourlyForecast() {
        WeatherPerHour weatherPerHour = WeatherManager.getInstance().weatherPerHour;
        WeatherAQI weatherAQI = WeatherManager.getInstance().weatherAQI;

        WeatherProtos.WeatherHourForecast.Data.List.Builder list = WeatherProtos.WeatherHourForecast.Data.List.newBuilder();
        for (int i = 0; i < weatherPerHour.list.size(); i++) {
            WeatherPerHour.WeatherHour weatherHour = weatherPerHour.list.get(i);

            WeatherProtos.WeatherHourForecast.Data.Builder data = WeatherProtos.WeatherHourForecast.Data.newBuilder();
            data.setWeather(Integer.parseInt(weatherHour.weather.get(0).id));

            CommonProtos.RangeValue.Builder temp = CommonProtos.RangeValue.newBuilder();
            temp.setFrom((int) Float.parseFloat(weatherHour.main.temp_min));
            temp.setTo((int) Float.parseFloat(weatherHour.main.temp_max));
            data.setTemperature(temp);
            data.setTemperatureUnit("℃");

            if (weatherAQI.list.size() > i) {
                int aqi = Integer.parseInt(weatherAQI.list.get(i).main.aqi);
                data.setAqi(CommonTools.getKeyValue("aqi", aqi));
            }

            list.addList(data);
        }

        WeatherProtos.WeatherHourForecast.Builder weather = WeatherProtos.WeatherHourForecast.newBuilder();
        weather.setDataList(list);
        return weather.build();
    }

    private static WeatherProtos.WeatherForecast getWeatherDailyForecast() {
        WeatherDays weatherDays = WeatherManager.getInstance().weatherDays;
        WeatherAQI weatherAQI = WeatherManager.getInstance().weatherAQI;

        WeatherProtos.WeatherId.Builder weatherId = WeatherProtos.WeatherId.newBuilder();
        weatherId.setPubTime(weatherDays.list.get(0).dt);
        weatherId.setCityName(weatherDays.city.name);
        weatherId.setLocationName(weatherDays.city.country);
        weatherId.setPubTimeStamp(Integer.parseInt(weatherDays.list.get(0).dt));

        WeatherProtos.WeatherForecast.Builder weather = WeatherProtos.WeatherForecast.newBuilder();
        weather.setId(weatherId.build());
        WeatherProtos.WeatherForecast.Data.List.Builder list = WeatherProtos.WeatherForecast.Data.List.newBuilder();

        for (int i = 1; i < weatherDays.list.size(); i++) {
            WeatherDays.WeatherDay weatherDay = weatherDays.list.get(i);

            WeatherProtos.WeatherForecast.Data.Builder data = WeatherProtos.WeatherForecast.Data.newBuilder();
            data.setAqi(CommonTools.getKeyValue("aqi", Integer.parseInt(weatherAQI.list.get(95 - (4 - i) * 24).main.aqi)));

            data.setWeather(Integer.parseInt(weatherDay.weather.get(0).id));

            CommonProtos.RangeValue.Builder temp = CommonProtos.RangeValue.newBuilder();
            temp.setFrom((int) Float.parseFloat(weatherDay.temp.min));
            temp.setTo((int) Float.parseFloat(weatherDay.temp.max));
            data.setTemperature(temp);
            data.setTemperatureUnit("℃");

            WeatherProtos.SunRiseSet.Builder sunRise = WeatherProtos.SunRiseSet.newBuilder();
            sunRise.setSunRise(weatherDay.sunrise);
            sunRise.setSunSet(weatherDay.sunset);
            data.setSunRiseSet(sunRise);

            data.setHumidity(CommonTools.getKeyValue("%", (int) Float.parseFloat(weatherDay.humidity)));
            data.setProbabilityOfRainfall(CommonTools.getKeyValue("%", (int) Float.parseFloat(weatherDay.pop)));

            list.addList(data);
        }

        weather.setDataList(list);


        return weather.build();
    }


    private static WeatherProtos.WeatherLatest getWeatherLatest() {
        CurrentWeather currentWeather = WeatherManager.getInstance().currentWeather;
        WeatherAQI weatherAQI = WeatherManager.getInstance().weatherAQI;
        WeatherPerHour weatherPerHour = WeatherManager.getInstance().weatherPerHour;

        WeatherProtos.WeatherId.Builder weatherId = WeatherProtos.WeatherId.newBuilder();
        weatherId.setPubTime(currentWeather.dt);
        weatherId.setCityName(currentWeather.name);
        weatherId.setLocationName(currentWeather.sys.country);
        weatherId.setPubTimeStamp(Integer.parseInt(currentWeather.dt));

        WeatherProtos.WeatherLatest.Builder weather = WeatherProtos.WeatherLatest.newBuilder();
        weather.setId(weatherId.build());
        weather.setWeather(Integer.parseInt(currentWeather.weather.get(0).id));
        weather.setTemperature(CommonTools.getKeyValue("℃", (int) Float.parseFloat(currentWeather.main.temp)));
        weather.setHumidity(CommonTools.getKeyValue("%", (int) Float.parseFloat(currentWeather.main.humidity)));
        weather.setWindInfo(CommonTools.getKeyValue(currentWeather.wind.speed, (int) Float.parseFloat(currentWeather.wind.deg)));
        weather.setUvindex(CommonTools.getKeyValue("SPF0", 0));
        weather.setAqi(CommonTools.getKeyValue("aqi", Integer.parseInt(weatherAQI.list.get(0).main.aqi)));
        weather.setPressure(Float.parseFloat(weatherPerHour.list.get(0).main.sea_level));
        weather.setWindSpeed(CommonTools.getKeyValue("m/s", (int) Float.parseFloat(currentWeather.wind.speed)));
        weather.setProbabilityOfRainfall(CommonTools.getKeyValue("%", (int) Float.parseFloat(weatherPerHour.list.get(0).pop)));

        ArrayList<WeatherProtos.Alerts> data_list = new ArrayList<>();
        data_list.add(getAlerts("unKnown", "level6"));
        weather.setAlertsList(getAlertsList(data_list));

        return weather.build();
    }

    public static WeatherProtos.Alerts getAlerts(String type, String level) {
        WeatherProtos.Alerts.Builder alerts = WeatherProtos.Alerts.newBuilder();
        alerts.setType(type);
        alerts.setLevel(level);
        return alerts.build();
    }

    public static WeatherProtos.Alerts.List getAlertsList(ArrayList<WeatherProtos.Alerts> alerts_list) {
        WeatherProtos.Alerts.List.Builder data = WeatherProtos.Alerts.List.newBuilder();
        for (int i = 0; i < alerts_list.size(); i++) {
            data.addList(i, alerts_list.get(i));
        }
        return data.build();
    }

    public static WeatherProtos.WeatherId getWeatherId(String pub_time, String city_name, String location_name) {
        WeatherProtos.WeatherId.Builder weather_id = WeatherProtos.WeatherId.newBuilder();
        weather_id.setPubTime(pub_time);
        weather_id.setCityName(city_name);
        weather_id.setLocationName(location_name);
        return weather_id.build();
    }

    public static WeatherProtos.WeatherForecast getWeatherForecast() {
        //绑定信息
        WeatherProtos.WeatherForecast.Builder weather_forecast = WeatherProtos.WeatherForecast.newBuilder();
        weather_forecast.setId(getWeatherId("2019-08-29 10:10:10", "北京", "朝阳区"));
        ArrayList<WeatherProtos.WeatherForecast.Data> data_list = new ArrayList<>();

        data_list.add(getData(80));
        data_list.add(getData(90));
        data_list.add(getData(95));

        weather_forecast.setDataList(getDataList(data_list));

        return weather_forecast.build();
    }


    public static WeatherProtos.WeatherForecast.Data.List getDataList(ArrayList<WeatherProtos.WeatherForecast.Data> data_list) {
        WeatherProtos.WeatherForecast.Data.List.Builder data = WeatherProtos.WeatherForecast.Data.List.newBuilder();
        for (int i = 0; i < data_list.size(); i++) {
            data.addList(i, data_list.get(i));
        }
        return data.build();
    }


    public static WeatherProtos.WeatherForecast.Data getData(int aqi) {
        WeatherProtos.WeatherForecast.Data.Builder data = WeatherProtos.WeatherForecast.Data.newBuilder();
        data.setAqi(CommonTools.getKeyValue("api", aqi));
//        data.setWeather(CommonTools.getRangeValue(3, 10));
        data.setTemperature(CommonTools.getRangeValue(20, 30));
        data.setTemperatureUnit("摄氏度");
        data.setSunRiseSet(getSunRiseSet());
        return data.build();
    }

    public static WeatherProtos.SunRiseSet getSunRiseSet() {
        WeatherProtos.SunRiseSet.Builder sun_rise = WeatherProtos.SunRiseSet.newBuilder();
        sun_rise.setSunRise("07:35");
        sun_rise.setSunSet("18:21");

        return sun_rise.build();
    }


    public static String getWeatherId(WeatherProtos.WeatherId weather_id) {

        String result_str = "";

        System.out.println("数据封装 = " + "weather/" + "WeatherId/PubTime = " + weather_id.getPubTime());
        System.out.println("数据封装 = " + "weather/" + "WeatherId/CityName = " + weather_id.getCityName());
        System.out.println("数据封装 = " + "weather/" + "WeatherId/LocationName = " + weather_id.getLocationName());


        result_str += "weather" + "/WeatherId/PubTime = " + weather_id.getPubTime() + "\n";
        result_str += "weather" + "/WeatherId/CityName = " + weather_id.getCityName() + "\n";
        result_str += "weather" + "/WeatherId/LocationName = " + weather_id.getLocationName() + "\n";


        return result_str;

    }

    public static String getDataList(WeatherProtos.WeatherForecast.Data.List data_list) {

        String result_str = "";

        System.out.println("数据封装 = " + "weather/" + "weather_latest/list_count = " + data_list.getListCount());
        result_str += "weather/" + "weather_latest/list_count = " + data_list.getListCount() + "\n";

        for (int i = 0; i < data_list.getListCount(); i++) {
            System.out.println("数据封装 = " + "weather/" + "weather_latest/pos = " + i + " =========");
            result_str += "\n" + "weather/" + "weather_latest/pos = " + i + " =========" + "\n";
            result_str += getData(data_list.getList(i));
        }

        return result_str;

    }

    public static String getData(WeatherProtos.WeatherForecast.Data data) {
        String result_str = "";

        System.out.println("数据封装 = " + "weather/" + "weather_latest/api=====");
        result_str += "weather/" + "weather_latest/api=====" + "\n";
        result_str += CommonTools.getKeyValue(data.getAqi());

        System.out.println("数据封装 = " + "weather/" + "weather_latest/weather====");
        result_str += "weather/" + "weather_latest/weather=====" + "\n";
        result_str += data.getWeather();

        System.out.println("数据封装 = " + "weather/" + "weather_latest/temperature=====");
        result_str += "weather/" + "weather_latest/temperature=====" + "\n";
        result_str += CommonTools.getRangeValue(data.getTemperature());

        System.out.println("数据封装 = " + "weather/" + "weather_latest/temperature_unit = " + data.getTemperatureUnit());
        result_str += "weather/" + "weather_latest/temperature_unit = " + data.getTemperatureUnit() + "\n";

        WeatherProtos.SunRiseSet sun_riseset = data.getSunRiseSet();
        result_str += getSunRiseSet(sun_riseset);

        return result_str;
    }


    public static String getSunRiseSet(WeatherProtos.SunRiseSet sun_riseset) {
        String rsult_str = "";
        System.out.println("数据封装 = " + "weather/" + "SunRiseSet===========");
        System.out.println("数据封装 = " + "weather/" + "SunRiseSet/CityName = " + sun_riseset.getSunRise());
        System.out.println("数据封装 = " + "weather/" + "SunRiseSet/LocationName = " + sun_riseset.getSunSet());

        rsult_str += "weather/" + "SunRiseSet===========" + "\n";
        rsult_str += "weather/" + "SunRiseSet/sun_rise(日出) = " + sun_riseset.getSunRise() + "\n";
        rsult_str += "weather/" + "SunRiseSet/sun_set(日落) = " + sun_riseset.getSunSet() + "\n";

        return rsult_str;
    }


    public static String getAlertsList(WeatherProtos.Alerts.List alerts_list) {
        String resut_str = "";

        System.out.println("数据封装 = " + "weather/" + "Alerts/alerts_list = " + alerts_list.getListCount());
        resut_str += "weather/" + "Alerts/alerts_list = " + alerts_list.getListCount() + "\n";

        for (int i = 0; i < alerts_list.getListCount(); i++) {

            System.out.println("数据封装 = " + "weather/" + "Alerts/pos = " + i + " =========");
            resut_str += "\n" + "weather/" + "Alerts/pos = " + i + " =========" + "\n";
            resut_str += getAlerts(alerts_list.getList(i));
        }

        return resut_str;

    }

    public static String getAlerts(WeatherProtos.Alerts alerts) {
        String resut_str = "";
        System.out.println("数据封装 = " + "weather/" + "Alerts/Type = " + alerts.getType());
        System.out.println("数据封装 = " + "weather/" + "Alerts/Level = " + alerts.getLevel());

        resut_str += "weather/" + "Alerts/Type = " + alerts.getType() + "\n";
        resut_str += "weather/" + "Alerts/Level = " + alerts.getLevel() + "\n";

        return resut_str;

    }

    public static void showLog(WeatherProtos.Weather weather) {


        int pos = weather.getPayloadCase().getNumber();


        System.out.println("数据封装 = pos = " + pos);

        switch (pos) {
            case latest:

                WeatherProtos.WeatherLatest weather_latest = weather.getLatest();

                System.out.println("数据封装 = " + "weather/" + "weather_latest===========");
                System.out.println("数据封装 = " + "weather/" + "weather_latest/WeatherId======");
                WeatherProtos.WeatherId weather_id = weather_latest.getId();
                getWeatherId(weather_id);

                System.out.println("数据封装 = " + "weather/" + "weather_latest/Weather = " + weather_latest.getWeather());
                System.out.println("数据封装 = " + "weather/" + "weather_latest/Temperature=====");
                CommonTools.getKeyValue(weather_latest.getTemperature());
                System.out.println("数据封装 = " + "weather/" + "weather_latest/Humidity=====");
                CommonTools.getKeyValue(weather_latest.getHumidity());
                System.out.println("数据封装 = " + "weather/" + "weather_latest/WindInfo=====");
                CommonTools.getKeyValue(weather_latest.getWindInfo());
                System.out.println("数据封装 = " + "weather/" + "weather_latest/Uvindex======");
                CommonTools.getKeyValue(weather_latest.getUvindex());
                System.out.println("数据封装 = " + "weather/" + "weather_latest/Aqi=====");
                CommonTools.getKeyValue(weather_latest.getAqi());

                System.out.println("数据封装 = " + "weather/" + "weather_latest/AlertsList=====");
                WeatherProtos.Alerts.List alerts_list = weather_latest.getAlertsList();
                getAlertsList(alerts_list);

                break;

            case forecast:
                WeatherProtos.WeatherForecast weather_forecast = weather.getForecast();
                System.out.println("数据封装 = " + "weather/" + "weather_forecast/WeatherId=====");
                WeatherProtos.WeatherId weather_id2 = weather_forecast.getId();
                getWeatherId(weather_id2);
                System.out.println("数据封装 = " + "weather/" + "weather_forecast/data_list=====");
                WeatherProtos.WeatherForecast.Data.List data_list = weather_forecast.getDataList();
                getDataList(data_list);
                break;


        }
    }


}
