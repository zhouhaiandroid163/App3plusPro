package com.zjw.apps3pluspro.module.device.entity;

public class PhoneDtoModel {
    private String name;        //联系人姓名
    private String telPhone;    //电话号码

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTelPhone() {
        return telPhone;
    }

    public void setTelPhone(String telPhone) {
        this.telPhone = telPhone;
    }

    @Override
    public String toString() {
        return "PhoneDtoModel{" +
                "name='" + name + '\'' +
                ", telPhone='" + telPhone + '\'' +
                '}';
    }

    public PhoneDtoModel() {
    }

    public PhoneDtoModel(String name, String telPhone) {
        this.name = name;
        this.telPhone = telPhone;
    }

}
