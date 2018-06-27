package com.android.hcbd.whsw.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by guocheng on 2017/4/19.
 */

public class DeviceStateListInfo implements Serializable {
    private String indexRate; //刷新频率
    private String danger; //危险个数
    private String normal; //连接个数
    private String disconnect;  //断开个数
    private List<DataInfo> dataInfoList;

    public String getIndexRate() {
        return indexRate;
    }

    public void setIndexRate(String indexRate) {
        this.indexRate = indexRate;
    }

    public String getDanger() {
        return danger;
    }

    public void setDanger(String danger) {
        this.danger = danger;
    }

    public String getNormal() {
        return normal;
    }

    public void setNormal(String normal) {
        this.normal = normal;
    }

    public String getDisconnect() {
        return disconnect;
    }

    public void setDisconnect(String disconnect) {
        this.disconnect = disconnect;
    }

    public List<DataInfo> getDataInfoList() {
        return dataInfoList;
    }

    public void setDataInfoList(List<DataInfo> dataInfoList) {
        this.dataInfoList = dataInfoList;
    }

    public static class DataInfo implements Serializable{


        /**
         * img : /resource/img/mapdevice.png
         * name : 洪山2.4KM
         * point : 114.336037567,30.506744700
         * remark :
         * sn : 898607B2091770090674
         * state : 正常
         * x : 114.336037567
         * y : 30.506744700
         */

        private String img;
        private String name;
        private String point;
        private String remark;
        private String sn;
        private String state;
        private String x;
        private String y;

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPoint() {
            return point;
        }

        public void setPoint(String point) {
            this.point = point;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public String getSn() {
            return sn;
        }

        public void setSn(String sn) {
            this.sn = sn;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getX() {
            return x;
        }

        public void setX(String x) {
            this.x = x;
        }

        public String getY() {
            return y;
        }

        public void setY(String y) {
            this.y = y;
        }
    }

}
