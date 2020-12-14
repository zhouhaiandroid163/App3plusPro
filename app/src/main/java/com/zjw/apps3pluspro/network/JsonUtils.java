package com.zjw.apps3pluspro.network;

import android.text.TextUtils;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class JsonUtils {

    public static JsonUtils mInstance;

    public static JsonUtils getInstance() {
        if (mInstance == null) {
            mInstance = new JsonUtils();
        }
        return mInstance;
    }

    public Gson getGson() {
        return new Gson();
    }

    /**
     * 将jsonStr转换成cl对象
     *
     * @param jsonStr
     * @return
     */
    public static <T extends Object> T jsonToBean(String jsonStr, Class<?> cl) {
        Object obj = null;
        if (!TextUtils.isEmpty(jsonStr))
            obj = getInstance().getGson().fromJson(jsonStr, cl);
        return (T) obj;
    }

    /**
     * 返回cla 类型的list数组
     *
     * @param s
     * @param cla
     * @return
     */
    public static <T extends Object> T jsonToBeanList(String s, Class<?> cla) {

        List<Object> ls = new ArrayList<Object>();
        JSONArray ss;
        try {
            ss = new JSONArray(s);
            for (int i = 0; i < ss.length(); i++) {
                String str = ss.getString(i);
                Object a = jsonToBean(str, cla);
                ls.add(a);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return (T) ls;
    }

}
