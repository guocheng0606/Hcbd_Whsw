package com.android.hcbd.whsw.utils;

/**
 * Created by guocheng on 2017/6/19.
 */

public class HttpUrlUtils {
    //public static String BASEURL = "http://112.124.108.24:8685/whsw";

    public static String login_url = "/swApp/loginAction!login.action";//登录url
    public static String device_state_list_url = "/swApp/deviceAction!getDeviceList.action";//获得设备状态列表
    public static String get_device_list_url = "/swApp/deviceAction!list.action";//获得设备列表
    public static String edit_device_url = "/swApp/deviceAction!edit.action";//添加或修改设备
    public static String del_device_url = "/swApp/deviceAction!delete.action";//删除设备
    public static String report_data_list_url = "/swApp/dataAction!list.action";//报表数据查询
    public static String report_data_export_url = "/swApp/dataAction!export.action";//报表数据导出
    public static String warning_list_url = "/swApp/dataAction!warnList.action";//预警列表查询
    public static String warning_deal_url = "/swApp/dataAction!dealData.action";//预警处理
    public static String warning_data_export_url = "/swApp/dataAction!exportWarn.action";//预警导出
    public static String configure_type_list_url = "/swApp/typeAction!list.action";//首页刷新频率列表
    public static String configure_type_edit_url = "/swApp/typeAction!edit.action";//首页刷新频率编辑
    public static String edit_password_url = "/swApp/loginAction!editPwd.action";//修改密码

    public static String get_index_rate_url = "/swApp/mapAction!getIndexRate.action";//获取刷新频率
    public static String query_device_data_url = "/swApp/mapAction!queryDeviceData.action";//查询数据



}
