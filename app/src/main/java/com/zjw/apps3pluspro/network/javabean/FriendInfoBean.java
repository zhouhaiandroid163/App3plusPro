package com.zjw.apps3pluspro.network.javabean;

/**
 * Created by zjw on 2018/4/2.
 */

public class FriendInfoBean {


    /**
     * result : 1
     * msg : 请求成功
     * data : {"healthLastDate":"2018-04-02 12:02:54","sportLastDate":"2018-04-02","healthDiastolic":"74","sportTarget":"10000","sleepLastDate":"2018-03-31","sportStep":"15","sleepData":"2305,2338,2851,3778,4323,4834,5507,6562,9123,9730,13411,14082,16389","healthSystolic":"115","healthEcgReport":"0","healthHeart":"67","sportCalorie":"0","sportData":"0,0,0,0,0,0,0,0,0,0,15,0,0,0,0,0,0,0,0,0,0,0,0,0"}
     */

    private int result;
    private String msg;
    private DataBean data;

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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * healthLastDate : 2018-04-02 12:02:54
         * sportLastDate : 2018-04-02
         * healthDiastolic : 74
         * sportTarget : 10000
         * sleepLastDate : 2018-03-31
         * sportStep : 15
         * sleepData : 2305,2338,2851,3778,4323,4834,5507,6562,9123,9730,13411,14082,16389
         * healthSystolic : 115
         * healthEcgReport : 0
         * healthHeart : 67
         * sportCalorie : 0
         * sportData : 0,0,0,0,0,0,0,0,0,0,15,0,0,0,0,0,0,0,0,0,0,0,0,0
         */

        private String healthLastDate;
        private String sportLastDate;
        private String healthDiastolic;
        private String sportTarget;
        private String sleepLastDate;
        private String sportStep;
        private String sleepData;
        private String healthSystolic;
        private String healthEcgReport;
        private String healthHeart;
        private String sportCalorie;
        private String sportDistance;
        private String sportData;

        public String getHealthLastDate() {
            return healthLastDate;
        }

        public void setHealthLastDate(String healthLastDate) {
            this.healthLastDate = healthLastDate;
        }

        public String getSportLastDate() {
            return sportLastDate;
        }

        public void setSportLastDate(String sportLastDate) {
            this.sportLastDate = sportLastDate;
        }

        public String getHealthDiastolic() {
            return healthDiastolic;
        }

        public void setHealthDiastolic(String healthDiastolic) {
            this.healthDiastolic = healthDiastolic;
        }

        public String getSportTarget() {
            return sportTarget;
        }

        public void setSportTarget(String sportTarget) {
            this.sportTarget = sportTarget;
        }

        public String getSleepLastDate() {
            return sleepLastDate;
        }

        public void setSleepLastDate(String sleepLastDate) {
            this.sleepLastDate = sleepLastDate;
        }

        public String getSportStep() {
            return sportStep;
        }

        public void setSportStep(String sportStep) {
            this.sportStep = sportStep;
        }

        public String getSleepData() {
            return sleepData;
        }

        public void setSleepData(String sleepData) {
            this.sleepData = sleepData;
        }

        public String getHealthSystolic() {
            return healthSystolic;
        }

        public void setHealthSystolic(String healthSystolic) {
            this.healthSystolic = healthSystolic;
        }

        public String getHealthEcgReport() {
            return healthEcgReport;
        }

        public void setHealthEcgReport(String healthEcgReport) {
            this.healthEcgReport = healthEcgReport;
        }

        public String getHealthHeart() {
            return healthHeart;
        }

        public void setHealthHeart(String healthHeart) {
            this.healthHeart = healthHeart;
        }

        public String getSportCalorie() {
            return sportCalorie;
        }

        public void setSportCalorie(String sportCalorie) {
            this.sportCalorie = sportCalorie;
        }

        public String getSportDistance() {
            return sportDistance;
        }

        public void setSportDistance(String sportDistance) {
            this.sportDistance = sportDistance;
        }

        public String getSportData() {
            return sportData;
        }

        public void setSportData(String sportData) {
            this.sportData = sportData;
        }
    }
}
