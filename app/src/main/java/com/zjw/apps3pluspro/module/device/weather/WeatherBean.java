package com.zjw.apps3pluspro.module.device.weather;


import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zjw.apps3pluspro.sharedpreferences.UserSetTools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WeatherBean {

    String _c_date;
    //空气质量
    String _c_air;
    //空气质量指数
    String _c_aqi;
    //天气类型
    String _c_type;
    //当前温度
    String _c_now_temp;
    //最高温度
    String _c_high_temp;
    //最低温度
    String _c_low_temp;

    public WeatherBean() {
        super();
    }

    public WeatherBean(String date, String air, String api, String type, String now, String low, String high) {
        set_c_date(date);
        set_c_air(air);
        set_c_aqi(api);
        set_c_type(type);
        set_c_now_temp(now);
        set_c_low_temp(low);
        set_c_high_temp(high);

    }


    public String get_c_date() {
        return _c_date;
    }

    public void set_c_date(String _c_date) {
        this._c_date = _c_date;
    }

    public String get_c_air() {
        return _c_air;
    }

    public void set_c_air(String _c_air) {
        this._c_air = _c_air;
    }

    public String get_c_aqi() {
        return _c_aqi;
    }

    public void set_c_aqi(String _c_aqi) {
        this._c_aqi = _c_aqi;
    }


    public String get_c_type() {
        return _c_type;
    }

    public void set_c_type(String _c_type) {
        this._c_type = _c_type;
    }

    public String get_c_now_temp() {
        return _c_now_temp;
    }

    public void set_c_now_temp(String _c_now_temp) {
        this._c_now_temp = _c_now_temp;
    }

    public String get_c_high_temp() {
        return _c_high_temp;
    }

    public void set_c_high_temp(String _c_high_temp) {
        this._c_high_temp = _c_high_temp;
    }

    public String get_c_low_temp() {
        return _c_low_temp;
    }

    public void set_c_low_temp(String _c_low_temp) {
        this._c_low_temp = _c_low_temp;
    }

    @Override
    public String toString() {
        return "WeatherModle{" +
                "_c_date='" + _c_date + '\'' +
                ", _c_air='" + _c_air + '\'' +
                ", _c_type='" + _c_type + '\'' +
                ", _c_now_temp='" + _c_now_temp + '\'' +
                ", _c_high_temp='" + _c_high_temp + '\'' +
                ", _c_low_temp='" + _c_low_temp + '\'' +
                '}';
    }

    //================蓝牙协议============


    public static byte[] getWaeatherData(WeatherBean weatherModle) {
        byte[] result = new byte[8];

        int year = 1;
        int mon = 1;
        int day = 1;
        int air = 15;
        int type = 31;
        int now = -128;
        int high = -128;
        int low = -128;

        if (weatherModle.get_c_date() != null && !weatherModle.get_c_date().equals("")) {
            String time[] = weatherModle.get_c_date().split("-");
            year = Integer.valueOf(time[0]) - 2000;
            mon = Integer.valueOf(time[1]);
            day = Integer.valueOf(time[2]);
        }

        if (weatherModle.get_c_aqi() != null && !weatherModle.get_c_aqi().equals("")) {
            int in_aqi = Integer.parseInt(weatherModle.get_c_aqi());
            if (in_aqi <= 50) {
                air = 0;
            } else if (in_aqi <= 100) {
                air = 1;
            } else if (in_aqi <= 150) {
                air = 2;
            } else if (in_aqi <= 200) {
                air = 3;
            } else if (in_aqi <= 300) {
                air = 4;
            } else {
                air = 5;
            }
        }

        if (weatherModle.get_c_type() != null && !weatherModle.get_c_type().equals("")) {
            type = Integer.parseInt(weatherModle.get_c_type());
        }
        if (weatherModle.get_c_now_temp() != null && !weatherModle.get_c_now_temp().equals("")) {
            now = Integer.parseInt(weatherModle.get_c_now_temp());
        }
        if (weatherModle.get_c_high_temp() != null && !weatherModle.get_c_high_temp().equals("")) {
            high = Integer.parseInt(weatherModle.get_c_high_temp());
        }
        if (weatherModle.get_c_low_temp() != null && !weatherModle.get_c_low_temp().equals("")) {
            low = Integer.parseInt(weatherModle.get_c_low_temp());
        }

        //当前温度不为空
        if (now != -128) {
            //最小温度,大于当前温度
            if (low > now) {
                low = now;
            }
            if (high < now) {
                high = now;
            }
        }

        result[0] = (byte) ((year << 2) | (mon >> 2));
        result[1] = (byte) (((mon & 0x03) << 6) | (day << 1) | (air >> 3 & 0x01));
        result[2] = (byte) (((air << 5) & 0xe0) | (type & 0x1f));
        result[3] = (byte) (now & 0xff);
        result[4] = (byte) (low & 0xff);
        result[5] = (byte) (high & 0xff);
        result[6] = (byte) 0x0;
        result[7] = (byte) 0x0;

        return result;

    }

    public static byte[] getWaeatherListData(List<WeatherBean> list) {
        byte[] result = new byte[56];

        int len = list.size();
        if (len > 7) {
            len = 7;
        }

        for (int i = 0; i < len; i++) {
            byte[] t1 = WeatherBean.getWaeatherData(list.get(i));
            for (int j = 0; j < t1.length; j++) {
                result[i * 8 + j] = t1[j];
//                System.out.println("天气协议 aaaa =  " + (i * 8 + j) + "  bbbbb = " + j);
            }
        }


        return result;

    }

    //===============后台协议==================

    public static WeatherBean getErrorData(int number) {
        WeatherBean mWeatherModle = new WeatherBean();

        mWeatherModle.set_c_date("2001-01-0" + (number + 2));
        mWeatherModle.set_c_type("");
        mWeatherModle.set_c_air("");
        mWeatherModle.set_c_aqi("");
        mWeatherModle.set_c_now_temp("");
        mWeatherModle.set_c_high_temp("");
        mWeatherModle.set_c_low_temp("");

        return mWeatherModle;

    }

    public static ArrayList<WeatherBean> getHisData(Context mContext) {

        UserSetTools mUserSetTools = new UserSetTools(mContext);
        String now_data = mUserSetTools.get_weather_now_data();
        String his_data = mUserSetTools.get_weather_his_data();

        if (now_data.equals("") || his_data.equals("")) {
            return null;
        }

        JSONObject nowObj = null;
        JSONObject hisObj = null;

        System.out.println("请求天气 历史 解析数据  now_data = " + now_data);
        System.out.println("请求天气 历史 解析数据  his_data = " + his_data);

        try {
            nowObj = new JSONObject(now_data);
            hisObj = new JSONObject(his_data);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject now = nowObj.optJSONObject("data").optJSONObject("now");

        ArrayList<WeatherBean> myWeatherModle = new ArrayList<WeatherBean>();
        try {

            String weatherArray = hisObj.getJSONObject("data").getJSONArray("daily").toString();
            JSONArray arr = new JSONArray(weatherArray);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject jsonObject = (JSONObject) arr.get(i);

                String condCode1 = CondCodeMap.getCondCode(jsonObject.optString("iconDay"));
                String qlty1 = "";
                String aqi1 = "";
                String tmp1 = "";
                String tmpMax1 = (jsonObject.isNull("tempMax")) ? "" : jsonObject.optString("tempMax");
                String tmpMin1 = (jsonObject.isNull("tempMin")) ? "" : jsonObject.optString("tempMin");
                String today1 = (jsonObject.isNull("fxDate")) ? "" : jsonObject.optString("fxDate");

                if (i == 0) {
                    condCode1 = CondCodeMap.getCondCode(now.optString("icon"));
                    tmp1 = now.optString("temp");
                }

                WeatherBean mWeatherModle = new WeatherBean();
                mWeatherModle.set_c_date(today1);
                mWeatherModle.set_c_type(condCode1);
                mWeatherModle.set_c_air(qlty1);
                mWeatherModle.set_c_aqi(aqi1);
                mWeatherModle.set_c_now_temp(tmp1);
                mWeatherModle.set_c_high_temp(tmpMax1);
                mWeatherModle.set_c_low_temp(tmpMin1);

                System.out.println("weather = " + i + "  =" + mWeatherModle.toString());
                myWeatherModle.add(mWeatherModle);
            }

        } catch (JSONException e) {
            System.out.println("请求天气 历史  解析数据001 = 解析错误");
            e.printStackTrace();
        }


        if (myWeatherModle.size() < 7) {
            int error_data = 7 - myWeatherModle.size();

            for (int i = 0; i < error_data; i++) {
                myWeatherModle.add(getErrorData(i));
            }
        }

        return myWeatherModle;
    }


    public static Gson gson;
    public static GsonBuilder builder;

    public static JSONObject getWeatherBean(WeatherBean myWeatherBean) {
        JSONObject myJSONObject = new JSONObject();

        //这两句代码必须的，为的是初始化出来gson这个对象，才能拿来用
        builder = new GsonBuilder();
        gson = builder.create();

        String jsonTest = gson.toJson(myWeatherBean, WeatherBean.class);

        try {
            myJSONObject = new JSONObject(jsonTest);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return myJSONObject;


    }

    public static JSONArray getMovementListData(ArrayList<WeatherBean> myListWeatherBean) {

        //这两句代码必须的，为的是初始化出来gson这个对象，才能拿来用
        JSONArray jsonArray1 = new JSONArray();
        for (int i = 0; i < myListWeatherBean.size(); i++) {
            jsonArray1.put(getWeatherBean(myListWeatherBean.get(i)));
        }

        return jsonArray1;
    }


}
