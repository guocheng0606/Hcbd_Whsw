package com.android.hcbd.whsw.entity;

import java.util.List;

/**
 * Created by guocheng on 2017/6/19.
 */

public class LoginInfo {
    private String token;
    private UserInfo userInfo;
    private List<menuInfo> menuList;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public List<menuInfo> getMenuList() {
        return menuList;
    }

    public void setMenuList(List<menuInfo> menuList) {
        this.menuList = menuList;
    }

    public static class UserInfo{

        /**
         * code : S0001
         * comName : null
         * id : 2
         * menuPath : null
         * name : test
         * names : S0001-test
         * orgCode : 027
         * orgName : 鄂南超限检测站
         * permit : false
         */

        private String code;
        private Object comName;
        private int id;
        private Object menuPath;
        private String name;
        private String names;
        private String orgCode;
        private String orgName;
        private boolean permit;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public Object getComName() {
            return comName;
        }

        public void setComName(Object comName) {
            this.comName = comName;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public Object getMenuPath() {
            return menuPath;
        }

        public void setMenuPath(Object menuPath) {
            this.menuPath = menuPath;
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

        public String getOrgCode() {
            return orgCode;
        }

        public void setOrgCode(String orgCode) {
            this.orgCode = orgCode;
        }

        public String getOrgName() {
            return orgName;
        }

        public void setOrgName(String orgName) {
            this.orgName = orgName;
        }

        public boolean isPermit() {
            return permit;
        }

        public void setPermit(boolean permit) {
            this.permit = permit;
        }
    }

    public static class menuInfo{

        /**
         * code : 1,0,1,1
         * id : null
         * name : 首页
         * names : 1,0,1,1-首页
         */

        private String code;
        private Object id;
        private String name;
        private String names;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public Object getId() {
            return id;
        }

        public void setId(Object id) {
            this.id = id;
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
    }

}
