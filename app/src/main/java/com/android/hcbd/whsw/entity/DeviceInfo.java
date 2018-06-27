package com.android.hcbd.whsw.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by guocheng on 2017/8/25.
 */

public class DeviceInfo implements Serializable {

    /**
     * beginPower : 0.0
     * code : 18771131528
     * createTime : 2017-08-23T11:00:29
     * endPower : 100.0
     * id : 3
     * img : /resource/img/mapdevice.png
     * modelContent : ["设备信息","/sw/deviceAction!"]
     * name : 洪山2.4KM
     * names : 18771131528-洪山2.4KM
     * operNames : S0000-Eingabe
     * orgCode : 027
     * paramsObj : null
     * power : null
     * remark :
     * showState : 正常
     * sn : 898607B2091770090674
     * state : 1
     * stateContent : 启用
     * upload : null
     * uploadContentType : null
     * uploadFileName : null
     * url :
     * x : 114.336037567
     * y : 30.506744700
     */

    private double beginPower;
    private String code;
    private String createTime;
    private double endPower;
    private int id;
    private String img;
    private String name;
    private String names;
    private String operNames;
    private String orgCode;
    private Object paramsObj;
    private Object power;
    private String remark;
    private String showState;
    private String sn;
    private String state;
    private String stateContent;
    private Object upload;
    private Object uploadContentType;
    private Object uploadFileName;
    private String url;
    private String x;
    private String y;
    private List<String> modelContent;

    public double getBeginPower() {
        return beginPower;
    }

    public void setBeginPower(double beginPower) {
        this.beginPower = beginPower;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public double getEndPower() {
        return endPower;
    }

    public void setEndPower(double endPower) {
        this.endPower = endPower;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public String getNames() {
        return names;
    }

    public void setNames(String names) {
        this.names = names;
    }

    public String getOperNames() {
        return operNames;
    }

    public void setOperNames(String operNames) {
        this.operNames = operNames;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public Object getParamsObj() {
        return paramsObj;
    }

    public void setParamsObj(Object paramsObj) {
        this.paramsObj = paramsObj;
    }

    public Object getPower() {
        return power;
    }

    public void setPower(Object power) {
        this.power = power;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getShowState() {
        return showState;
    }

    public void setShowState(String showState) {
        this.showState = showState;
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

    public String getStateContent() {
        return stateContent;
    }

    public void setStateContent(String stateContent) {
        this.stateContent = stateContent;
    }

    public Object getUpload() {
        return upload;
    }

    public void setUpload(Object upload) {
        this.upload = upload;
    }

    public Object getUploadContentType() {
        return uploadContentType;
    }

    public void setUploadContentType(Object uploadContentType) {
        this.uploadContentType = uploadContentType;
    }

    public Object getUploadFileName() {
        return uploadFileName;
    }

    public void setUploadFileName(Object uploadFileName) {
        this.uploadFileName = uploadFileName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public List<String> getModelContent() {
        return modelContent;
    }

    public void setModelContent(List<String> modelContent) {
        this.modelContent = modelContent;
    }
}
