package com.mooc.commonbusiness.net;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class EncryptBodyBean {


    /**
     * data : {"data":{"resource_type":2,"resource_id":154},"client_type":1,"token_check":"token)"}
     */

    private String data;

    public static EncryptBodyBean objectFromData(String str) {

        return new Gson().fromJson(str, EncryptBodyBean.class);
    }

    public static List<EncryptBodyBean> arrayEncryptBodyBeanFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);
            Type listType = new TypeToken<ArrayList<EncryptBodyBean>>() {
            }.getType();

            return new Gson().fromJson(jsonObject.getString(str), listType);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new ArrayList();


    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return
                "{\"data\":\"" + data +"\"}";
    }

    public static class DataBeanX {
        /**
         * data : {"resource_type":2,"resource_id":154}
         * client_type : 1
         * token_check : token)
         */

        private Object data;
        private int client_type;
        private String token_check;
        private long ts;

        public static DataBeanX objectFromData(String str) {

            return new Gson().fromJson(str, DataBeanX.class);
        }

        public static List<DataBeanX> arrayDataBeanXFromData(String str, String key) {

            try {
                JSONObject jsonObject = new JSONObject(str);
                Type listType = new TypeToken<ArrayList<DataBeanX>>() {
                }.getType();

                return new Gson().fromJson(jsonObject.getString(str), listType);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return new ArrayList();


        }

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }

        public int getClient_type() {
            return client_type;
        }

        public void setClient_type(int client_type) {
            this.client_type = client_type;
        }

        public String getToken_check() {
            return token_check;
        }

        public void setToken_check(String token_check) {
            this.token_check = token_check;
        }

        public long getTs() {
            return ts;
        }

        public void setTs(long ts) {
            this.ts = ts;
        }

        @Override
        public String toString() {
            return "{\"" +
                    "data\":" + data +
                    ",\"client_type\":\"" + client_type +
                    "\",\"token_check\":\"" + token_check +
                    "\",\"ts\":\"" + ts +
                    "\"}";
        }
    }
}