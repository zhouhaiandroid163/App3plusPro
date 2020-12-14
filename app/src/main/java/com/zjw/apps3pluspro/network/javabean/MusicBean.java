package com.zjw.apps3pluspro.network.javabean;

import com.zjw.apps3pluspro.network.ResultJson;
import java.util.List;

public class MusicBean {


    /**
     * result : 1
     * msg : 请求成功
     * code : 0000
     * data : {"count":1,"list":[{"fileSize":1031791,"id":114,"songName":"demo1","url":"http:\/\/file.genius.liuqingzhi.com\/upload\/mp3\/20190911\/83bdd4d290844d338a6f73d26c29c35f.mp3","userId":779988}]
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
         * list : [{"fileSize":1031791,"id":114,"songName":"demo1","url":"http:\/\/file.genius.liuqingzhi.com\/upload\/mp3\/20190911\/83bdd4d290844d338a6f73d26c29c35f.mp3","userId":779988}]
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
             * filesize : 1031791
             * id : 114
             * songName : demo1
             * url : http:\/\/file.genius.liuqingzhi.com\/upload\/mp3\/20190911\/83bdd4d290844d338a6f73d26c29c35f.mp3
             * userid : 779988
             */

            private int filesize;
            private int id;
            private String songName;
            private String url;
            private int userid;

            public int getFilesize() {
                return filesize;
            }

            public void setFilesize(int filesize) {
                this.filesize = filesize;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getSongName() {
                return songName;
            }

            public void setSongName(String songName) {
                this.songName = songName;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public int getUserid() {
                return userid;
            }

            public void setUserid(int userid) {
                this.userid = userid;
            }

            @Override
            public String toString() {
                return "ListBean{" +
                        "filesize=" + filesize +
                        ", id=" + id +
                        ", songName='" + songName + '\'' +
                        ", url='" + url + '\'' +
                        ", userid=" + userid +
                        '}';
            }
        }

    }

    @Override
    public String toString() {
        return "MusicBean{" +
                "result=" + result +
                ", msg='" + msg + '\'' +
                ", code='" + code + '\'' +
                ", data=" + data.getList().toString() +
                ", codeMsg='" + codeMsg + '\'' +
                '}';
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
