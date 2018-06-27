package com.android.hcbd.whsw.event;

/**
 * Created by guocheng on 2017/3/17.
 */
public class MessageEvent {

    /*发出的广播类型*/
    public static final int EVENT_LOCATION_SUCCESS = 100;
    public static final int EVENT_DEVICE_SEARCH_RESULT = 101;
    public static final int EVENT_REPORT_SEARCH_RESULT = 102;
    public static final int EVENT_WARNING_SEARCH_RESULT = 103;

    public static final int EVENT_DEVICE_EDIT_RESULT = 104;
    public static final int EVENT_CONFIGURE_EDIT_RESULT = 105;
    public static final int EVENT_DEVICE_DELETE_RESULT = 106;

    public static final int EVENT_ROUTE_CHANGE_RESULT = 110;

    public static final int EVENT_IPADDRESS_DEL = 120;

    public static final int EVENT_LOGINOUT = 200;


    private int eventId;
    private Object obj;
    private Object obj2;
    public MessageEvent() {
    }

    public MessageEvent(int eventId) {
        this.eventId = eventId;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    public Object getObj2() {
        return obj2;
    }

    public void setObj2(Object obj2) {
        this.obj2 = obj2;
    }
}
