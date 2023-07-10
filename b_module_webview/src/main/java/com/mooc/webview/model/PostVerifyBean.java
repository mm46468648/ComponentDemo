package com.mooc.webview.model;

import java.util.ArrayList;

public class PostVerifyBean {

    String folder_id;

    String article_id;


    verifyCode verification_code;

    public String getFolder_id() {
        return folder_id;
    }

    public void setFolder_id(String folder_id) {
        this.folder_id = folder_id;
    }

    public String getArticle_id() {
        return article_id;
    }

    public void setArticle_id(String article_id) {
        this.article_id = article_id;
    }

    public verifyCode getVerification_code() {
        return verification_code;
    }

    public void setVerification_code(verifyCode verification_code) {
        this.verification_code = verification_code;
    }

    public static class verifyCode {

        int id;
        ArrayList<Point> corrd;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }


        public ArrayList<Point> getCorrd() {
            return corrd;
        }

        public void setCorrd(ArrayList<Point> corrd) {
            this.corrd = corrd;
        }

    }
}


