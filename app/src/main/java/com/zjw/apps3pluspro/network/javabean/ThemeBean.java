package com.zjw.apps3pluspro.network.javabean;

import com.zjw.apps3pluspro.network.ResultJson;
import java.util.List;


public class ThemeBean {


    /**
     * result : 1
     * msg : 请求成功
     * code : 0000
     * data : {"count":1,"list":[{"authorName":"活力\u201c绿\u201d射","binSize":0,"colorType":"绿色","deviceIsHeart":"1","deviceLanNumber":13,"deviceShape":"1","deviceWidth":"128","id":5,"stypeType":"复杂","themeCode":"003","themeDesc":"红色的热情、绿色的清新、黄色的温馨。在自己行走中数字像音符般跳动、充满活力。","themeFinishDay":"2019-06-29","themeImgUrl":"http://file.genius.liuqingzhi.com/upload/20190730/3c6123e191584d61bd25cdd0265f7c86.jpg","themeLastChangeDay":"2019-7-12","themeName":"ZMY"}]}
     * codeMsg : 操作成功！
     */

    private int result;
    private String msg;
    private String code;
    private DataBean data;
    private String codeMsg;


    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getCodeMsg() {
        return codeMsg;
    }

    public void setCodeMsg(String codeMsg) {
        this.codeMsg = codeMsg;
    }


    public static class DataBean {
        /**
         * count : 1
         * list : [{"authorName":"活力\u201c绿\u201d射","binSize":0,"colorType":"绿色","deviceIsHeart":"1","deviceLanNumber":13,"deviceShape":"1","deviceWidth":"128","id":5,"stypeType":"复杂","themeCode":"003","themeDesc":"红色的热情、绿色的清新、黄色的温馨。在自己行走中数字像音符般跳动、充满活力。","themeFinishDay":"2019-06-29","themeImgUrl":"http://file.genius.liuqingzhi.com/upload/20190730/3c6123e191584d61bd25cdd0265f7c86.jpg","themeLastChangeDay":"2019-7-12","themeName":"ZMY"}]
         */

        private int count;
        private List<ListBean> list;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public static class ListBean {
            /**
             * authorName : 活力“绿”射
             * binSize : 0
             * colorType : 绿色
             * deviceIsHeart : 1
             * deviceLanNumber : 13
             * deviceShape : 1
             * deviceWidth : 128
             * id : 5
             * stypeType : 复杂
             * themeCode : 003
             * themeDesc : 红色的热情、绿色的清新、黄色的温馨。在自己行走中数字像音符般跳动、充满活力。
             * themeFinishDay : 2019-06-29
             * themeImgUrl : http://file.genius.liuqingzhi.com/upload/20190730/3c6123e191584d61bd25cdd0265f7c86.jpg
             * themeLastChangeDay : 2019-7-12
             * themeName : ZMY
             */

            private String authorName;
            private int binSize;
            private String colorType;
            private String deviceIsHeart;
            private int deviceLanNumber;
            private String deviceShape;
            private String deviceWidth;
            private int id;
            private String stypeType;
            private String themeCode;
            private String themeDesc;
            private String themeFinishDay;
            private String themeImgUrl;
            private String themeLastChangeDay;
            private String themeName;

            public String getAuthorName() {
                return authorName;
            }

            public void setAuthorName(String authorName) {
                this.authorName = authorName;
            }

            public int getBinSize() {
                return binSize;
            }

            public void setBinSize(int binSize) {
                this.binSize = binSize;
            }

            public String getColorType() {
                return colorType;
            }

            public void setColorType(String colorType) {
                this.colorType = colorType;
            }

            public String getDeviceIsHeart() {
                return deviceIsHeart;
            }

            public void setDeviceIsHeart(String deviceIsHeart) {
                this.deviceIsHeart = deviceIsHeart;
            }

            public int getDeviceLanNumber() {
                return deviceLanNumber;
            }

            public void setDeviceLanNumber(int deviceLanNumber) {
                this.deviceLanNumber = deviceLanNumber;
            }

            public String getDeviceShape() {
                return deviceShape;
            }

            public void setDeviceShape(String deviceShape) {
                this.deviceShape = deviceShape;
            }

            public String getDeviceWidth() {
                return deviceWidth;
            }

            public void setDeviceWidth(String deviceWidth) {
                this.deviceWidth = deviceWidth;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getStypeType() {
                return stypeType;
            }

            public void setStypeType(String stypeType) {
                this.stypeType = stypeType;
            }

            public String getThemeCode() {
                return themeCode;
            }

            public void setThemeCode(String themeCode) {
                this.themeCode = themeCode;
            }

            public String getThemeDesc() {
                return themeDesc;
            }

            public void setThemeDesc(String themeDesc) {
                this.themeDesc = themeDesc;
            }

            public String getThemeFinishDay() {
                return themeFinishDay;
            }

            public void setThemeFinishDay(String themeFinishDay) {
                this.themeFinishDay = themeFinishDay;
            }

            public String getThemeImgUrl() {
                return themeImgUrl;
            }

            public void setThemeImgUrl(String themeImgUrl) {
                this.themeImgUrl = themeImgUrl;
            }

            public String getThemeLastChangeDay() {
                return themeLastChangeDay;
            }

            public void setThemeLastChangeDay(String themeLastChangeDay) {
                this.themeLastChangeDay = themeLastChangeDay;
            }

            public String getThemeName() {
                return themeName;
            }

            public void setThemeName(String themeName) {
                this.themeName = themeName;
            }

            @Override
            public String toString() {
                return "ListBean{" +
                        "authorName='" + authorName + '\'' +
                        ", binSize=" + binSize +
                        ", colorType='" + colorType + '\'' +
                        ", deviceIsHeart='" + deviceIsHeart + '\'' +
                        ", deviceLanNumber=" + deviceLanNumber +
                        ", deviceShape='" + deviceShape + '\'' +
                        ", deviceWidth='" + deviceWidth + '\'' +
                        ", id=" + id +
                        ", stypeType='" + stypeType + '\'' +
                        ", themeCode='" + themeCode + '\'' +
                        ", themeDesc='" + themeDesc + '\'' +
                        ", themeFinishDay='" + themeFinishDay + '\'' +
                        ", themeImgUrl='" + themeImgUrl + '\'' +
                        ", themeLastChangeDay='" + themeLastChangeDay + '\'' +
                        ", themeName='" + themeName + '\'' +
                        '}';
            }
        }

    }




    /**
     * 是否请求成功
     *
     * @return
     */
    public boolean isRequestSuccess() {
        if (getResult() == ResultJson.Result_success) {
            return true;
        } else {
            return false;
        }

    }


    /**
     * 个人信息获取是否成功
     *
     * @return
     */
    public int isUserSuccess() {
        //获取成功
        if (ResultJson.Code_operation_success.equals(getCode())) {
            return 1;
        }
        //没有数据
        else if (ResultJson.Code_no_data.equals(getCode())) {
            return 0;

        } else {
            return -1;
        }
    }





}





