package com.zjw.apps3pluspro.bleservice.anaylsis;

import com.google.protobuf.InvalidProtocolBufferException;
import com.xiaomi.wear.protobuf.WearProtos;
import com.xiaomi.wear.protobuf.WeatherProtos;

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


    public static WeatherProtos.Weather.Builder getWeather(int pos) {

        WeatherProtos.Weather.Builder weather = WeatherProtos.Weather.newBuilder();


        switch (pos) {
            case latest:
                weather.setLatest(getWeatherLatest());
                break;

            case forecast:
                weather.setForecast(getWeatherForecast());
                break;


        }

        return weather;
    }


    public static WeatherProtos.WeatherLatest getWeatherLatest() {
        WeatherProtos.WeatherLatest.Builder weather_latst = WeatherProtos.WeatherLatest.newBuilder();
        weather_latst.setId(getWeatherId("2019-10-17T09:30:32+08:00", "shenzhen", "宝安"));
        weather_latst.setWeather(0x05);
        weather_latst.setTemperature(CommonTools.getKeyValue("℃", 26));
        weather_latst.setHumidity(CommonTools.getKeyValue("%", 95));
        weather_latst.setWindInfo(CommonTools.getKeyValue("113", 1));
        weather_latst.setUvindex(CommonTools.getKeyValue("SPF0", 1));
        weather_latst.setAqi(CommonTools.getKeyValue("优", 33));

        ArrayList<WeatherProtos.Alerts> data_list = new ArrayList<>();
        data_list.add(getAlerts("大雾", "黄色"));
//        data_list.add(getAlerts("type6", "level6"));
//        data_list.add(getAlerts("type7", "level7"));
        weather_latst.setAlertsList(getAlertsList(data_list));

        return weather_latst.build();
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
        data.setWeather(CommonTools.getRangeValue(3, 10));
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
        result_str += CommonTools.getRangeValue(data.getWeather());

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

    public static byte[] getLatestWeather() {

        WeatherProtos.Weather.Builder weather = WeatherProtos.Weather.newBuilder();

        weather.setLatest(getWeatherLatest());

        WearProtos.WearPacket.Builder wear1 = WearProtos.WearPacket.newBuilder()
                .setType(WearProtos.WearPacket.Type.WEATHER)
                .setId((byte) WeatherProtos.Weather.WeatherID.LATEST_WEATHER.getNumber())
                .setWeather(weather);

        try {
            WearProtos.WearPacket wear = WearProtos.WearPacket.parseFrom(wear1.build().toByteArray());
            SystemTools.analysisSystem(wear);

        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }


        return wear1.build().toByteArray();
    }

    public static byte[] getLatestWeather(WeatherProtos.WeatherLatest.Builder weather_latst) {

        WeatherProtos.Weather.Builder weather = WeatherProtos.Weather.newBuilder();

        weather.setLatest(weather_latst);

        WearProtos.WearPacket.Builder wear1 = WearProtos.WearPacket.newBuilder()
                .setType(WearProtos.WearPacket.Type.WEATHER)
                .setId((byte) WeatherProtos.Weather.WeatherID.LATEST_WEATHER.getNumber())
                .setWeather(weather);

        try {
            WearProtos.WearPacket wear = WearProtos.WearPacket.parseFrom(wear1.build().toByteArray());
            SystemTools.analysisSystem(wear);

        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }


        return wear1.build().toByteArray();
    }

}
