package com.example.john.mobicare_uganda.chatts;

import android.graphics.Bitmap;

/**
 * Created by john on 10/17/17.
 */

public class Messages {

    String message;
    String dates;
    Long lg;
    Bitmap bmp;
    String url;
    String mode;
    public Messages() {
    }

    public Messages(String message, String dates, Long lg, Bitmap bmp, String url, String mode) {
        this.message = message;
        this.dates = dates;
        this.lg = lg;
        this.bmp =bmp;
        this.url =url;
        this.mode = mode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDates() {
        return dates;
    }

    public void setDates(String dates) {
        this.dates = dates;
    }

    public Long getLg() {
        return lg;
    }

    public void setLg(Long lg) {
        this.lg = lg;
    }

    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }

    public Bitmap getBmp() {
        return bmp;
    }

    public void setBmp(Bitmap bmp) {
        this.bmp = bmp;
    }

    public String getMode() {
        return mode;
    }
    public void setMode(String mode) {
        this.mode = mode;
    }

}
