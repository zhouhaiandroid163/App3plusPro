package com.zjw.apps3pluspro.module.home.sport;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import com.zjw.apps3pluspro.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SportModleUtils {

    public static String getSportTypeStr(Context context, String sport_type) {

        String result = "";

        switch (sport_type) {
            //跑步
            case "0":
                result = context.getString(R.string.sport_modle_type0);
                break;

            //健走
            case "1":
                result = context.getString(R.string.sport_modle_type1);
                break;

            //爬山
            case "2":
                result = context.getString(R.string.sport_modle_type2);
                break;

            //骑行
            case "3":
                result = context.getString(R.string.sport_modle_type3);
                break;

            //乒乓球
            case "4":
                result = context.getString(R.string.sport_modle_type4);
                break;

            //篮球
            case "5":
                result = context.getString(R.string.sport_modle_type5);
                break;

            //羽毛球
            case "6":
                result = context.getString(R.string.sport_modle_type6);
                break;

            //足球
            case "7":
                result = context.getString(R.string.sport_modle_type7);
                break;

            //游泳
            case "8":
                result = context.getString(R.string.sport_modle_type8);
                break;

            //游泳
            case "9":
                result = context.getString(R.string.sport_modle_type9);
                break;

            //GPS
            case "100":
                result = context.getString(R.string.sport_modle_type100);
                break;
        }

        return result;

    }

    public static Drawable getSportTypeDefaultImg(Context context) {
        Resources resources = context.getResources();
        return resources.getDrawable(R.mipmap.data_sport);
    }


    public static Drawable getSportTypeImg(Context context, String my_type) {
        Resources resources = context.getResources();

        Drawable imageDrawable = resources.getDrawable(R.drawable.icon_sport_type_img100); //图片在drawable文件夹下

        if (my_type.equals("0")) {
            imageDrawable = resources.getDrawable(R.drawable.icon_sport_type_img0); //图片在drawable文件夹下
        } else if (my_type.equals("1")) {
            imageDrawable = resources.getDrawable(R.drawable.icon_sport_type_img1); //图片在drawable文件夹下
        } else if (my_type.equals("2")) {
            imageDrawable = resources.getDrawable(R.drawable.icon_sport_type_img2); //图片在drawable文件夹下
        } else if (my_type.equals("3")) {
            imageDrawable = resources.getDrawable(R.drawable.icon_sport_type_img3); //图片在drawable文件夹下
        } else if (my_type.equals("4")) {
            imageDrawable = resources.getDrawable(R.drawable.icon_sport_type_img4); //图片在drawable文件夹下
        } else if (my_type.equals("5")) {
            imageDrawable = resources.getDrawable(R.drawable.icon_sport_type_img5); //图片在drawable文件夹下
        } else if (my_type.equals("6")) {
            imageDrawable = resources.getDrawable(R.drawable.icon_sport_type_img6); //图片在drawable文件夹下
        } else if (my_type.equals("7")) {
            imageDrawable = resources.getDrawable(R.drawable.icon_sport_type_img7); //图片在drawable文件夹下
        } else if (my_type.equals("8")) {
            imageDrawable = resources.getDrawable(R.drawable.icon_sport_type_img8); //图片在drawable文件夹下
        } else if (my_type.equals("9")) {
            imageDrawable = resources.getDrawable(R.drawable.icon_sport_type_img9); //图片在drawable文件夹下
        } else {
            imageDrawable = resources.getDrawable(R.drawable.icon_sport_type_img100); //图片在drawable文件夹下
        }

        return imageDrawable;
    }


    public static Drawable getSportTypeTopImg(Context context, String my_type) {
        Resources resources = context.getResources();

        Drawable imageDrawable = resources.getDrawable(R.drawable.img_sport_type_top0); //图片在drawable文件夹下

        //跑步
        if (my_type.equals("0")) {
            imageDrawable = resources.getDrawable(R.drawable.img_sport_type_top0); //图片在drawable文件夹下
        }
        //健走
        else if (my_type.equals("1")) {
            imageDrawable = resources.getDrawable(R.drawable.img_sport_type_top1); //图片在drawable文件夹下
        }
        //爬山
        else if (my_type.equals("2")) {
            imageDrawable = resources.getDrawable(R.drawable.img_sport_type_top2); //图片在drawable文件夹下
        }
        //骑行
        else if (my_type.equals("3")) {
            imageDrawable = resources.getDrawable(R.drawable.img_sport_type_top3); //图片在drawable文件夹下
        }
        //乒乓球
        else if (my_type.equals("4")) {
            imageDrawable = resources.getDrawable(R.drawable.img_sport_type_top4); //图片在drawable文件夹下
        }
        //篮球
        else if (my_type.equals("5")) {
            imageDrawable = resources.getDrawable(R.drawable.img_sport_type_top5); //图片在drawable文件夹下
        }
        //羽毛球
        else if (my_type.equals("6")) {
            imageDrawable = resources.getDrawable(R.drawable.img_sport_type_top6); //图片在drawable文件夹下
        }
        //足球
        else if (my_type.equals("7")) {
            imageDrawable = resources.getDrawable(R.drawable.img_sport_type_top7); //图片在drawable文件夹下
        }
        //游泳
        else if (my_type.equals("8")) {
            imageDrawable = resources.getDrawable(R.drawable.img_sport_type_top8); //图片在drawable文件夹下
        }
        //室内运动
        else if (my_type.equals("9")) {
            imageDrawable = resources.getDrawable(R.drawable.img_sport_type_top9); //图片在drawable文件夹下
        }

        return imageDrawable;
    }


    public static int getCalory(float sport_distance) {
        float stepLenght = 0.40f;
        int sport_step = (int) (sport_distance / stepLenght);
        int sport_calory = (int) (Float.parseFloat("65") * 1.036 * Float.parseFloat("170") * 0.32 * sport_step * 0.00001);

        return sport_calory;
    }

    /**
     * 分钟转换成小时
     * 90分钟 = 》 1.5 小时
     *
     * @param totalSec
     * @return
     */
    public static String getMyMinute(String totalSec) {
        DecimalFormat df = new DecimalFormat("######0.00");
        double secs = Integer.parseInt(totalSec);

        if (secs <= 0) {
            secs = 0;
        } else if (secs <= 60) {
            secs = 60;
        }
        int min2 = (int) (secs / 60);

//        String result = df.format(min2);
//
//        result = result.replace(",", ".");

        return String.valueOf(min2);
    }

    /**
     * 获取当前时间
     */
    public static String getcueDate(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");
        Date curDate = new Date(time);

        String hh = String.valueOf((int) time / 60 / 60 / 1000);

        if (hh.length() < 2) {
            hh = "0" + hh;
        }
        String date = hh + ":" + formatter.format(curDate);
        return date;
    }


    public static String getDeviceSportTypeStr(Context context, int sport_type) {
        String result = "";
        switch (String.valueOf(sport_type)) {
            //户外跑步
            case "1":
                result = context.getString(R.string.more_sport_1);
                break;
            //户外健走，
            case "2":
                result = context.getString(R.string.more_sport_2);
                break;
            //室内跑步，
            case "3":
                result = context.getString(R.string.more_sport_3);
                break;
            //登山，
            case "4":
                result = context.getString(R.string.more_sport_4);
                break;
            //越野，
            case "5":
                result = context.getString(R.string.more_sport_5);
                break;
            //户外骑行，
            case "6":
                result = context.getString(R.string.more_sport_6);
                break;
            //室内骑行，
            case "7":
                result = context.getString(R.string.more_sport_7);
                break;
            //自由训练，
            case "8":
                result = context.getString(R.string.more_sport_8);
                break;
            //泳池游泳，
            case "9":
                result = context.getString(R.string.more_sport_9);
                break;
            //开放水域游泳，
            case "10":
                result = context.getString(R.string.more_sport_10);
                break;
            //泳池游泳，
            case "11":
                result = context.getString(R.string.more_sport_11);
                break;
            //开放水域游泳，
            case "12":
                result = context.getString(R.string.more_sport_12);
                break;
        }
        return result;

    }


    public static Drawable getDeviceSportTypeImg(Context context, int recordPointSportType) {
        Drawable imageDrawable = context.getResources().getDrawable(R.mipmap.more_sport_1);
        switch (String.valueOf(recordPointSportType)) {
            //户外跑步
            case "1":
                imageDrawable = context.getResources().getDrawable(R.mipmap.more_sport_1);
                break;
            //户外健走，
            case "2":
                imageDrawable = context.getResources().getDrawable(R.mipmap.more_sport_2);
                break;
            //室内跑步，
            case "3":
                imageDrawable = context.getResources().getDrawable(R.mipmap.more_sport_3);
                break;
            //登山，
            case "4":
                imageDrawable = context.getResources().getDrawable(R.mipmap.more_sport_4);
                break;
            //越野，
            case "5":
                imageDrawable = context.getResources().getDrawable(R.mipmap.more_sport_5);
                break;
            //户外骑行，
            case "6":
                imageDrawable = context.getResources().getDrawable(R.mipmap.more_sport_6);
                break;
            //室内骑行，
            case "7":
                imageDrawable = context.getResources().getDrawable(R.mipmap.more_sport_7);
                break;
            //自由训练，
            case "8":
                imageDrawable = context.getResources().getDrawable(R.mipmap.more_sport_8);
                break;
            //泳池游泳，
            case "9":
                imageDrawable = context.getResources().getDrawable(R.mipmap.more_sport_9);
                break;
            //开放水域游泳，
            case "10":
                imageDrawable = context.getResources().getDrawable(R.mipmap.more_sport_10);
                break;
            case "11":
                imageDrawable = context.getResources().getDrawable(R.mipmap.more_sport_11);
                break;
            //开放水域游泳，
            case "12":
                imageDrawable = context.getResources().getDrawable(R.mipmap.more_sport_12);
                break;
        }
        return imageDrawable;
    }
}
