package com.mooc.commonbusiness.scheme;

import android.os.Bundle;

import java.io.Serializable;

public class MenuItemEvent implements Serializable {
    private boolean isPush;
    private Bundle bundle;

    public MenuItemEvent(Bundle bundle) {
        this.bundle = bundle;
    }

    /**
     * @return the bundle
     */
    public Bundle getBundle() {
        return bundle;
    }

    public boolean isPush() {
        return isPush;
    }

    public void setPush(boolean push) {
        isPush = push;
    }
}
