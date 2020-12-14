package com.zjw.apps3pluspro.module.device.entity;

import java.util.ArrayList;

/**
 * Created by android
 * on 2020/10/9
 */
public class ThemeDetails {
    public long dialId;
    public String dialCode;
    public String dialName;
//    public int styleTypeId;
//    public String styleTypeName;
    public String authorName;
    public String dialDescribe;
    public int deviceWidth;
    public int deviceHeight;
    public int deviceShape;
    public int deviceIsHeart;
    public int binSize;
    public String dialGroupCode;
    public String languageCode;
    public String languageName;
    public int clockDialType;
    public int clockDialDataFormat;
    public int downNum;
    public ArrayList<DialFileList> dialFileList;
    public ArrayList<GroupDialList> groupDialList;

    public static class DialFileList{
        public String dialFileUrl;
        public int dialFileType;
        public String md5Value;
    }
    public static class GroupDialList{
        public long dialId;
        public String dialName;
        public String effectImgUrl;
        public String thumbnailUrl;
    }
}
