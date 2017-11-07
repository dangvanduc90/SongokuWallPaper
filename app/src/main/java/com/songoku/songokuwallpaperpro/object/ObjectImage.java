package com.songoku.songokuwallpaperpro.object;

import java.io.Serializable;

public class ObjectImage implements Serializable{

    private String imgThumb;
    private String imgSourc;
    private boolean isInternet;

    public ObjectImage(String imgThumb, String imgSourc, boolean isInternet) {
        this.imgThumb = imgThumb;
        this.imgSourc = imgSourc;
        this.isInternet = isInternet;
    }

    public String getImgThumb() {
        return imgThumb;
    }

    public void setImgThumb(String imgThumb) {
        this.imgThumb = imgThumb;
    }

    public String getImgSourc() {
        return imgSourc;
    }

    public void setImgSourc(String imgSourc) {
        this.imgSourc = imgSourc;
    }

    public boolean isInternet() {
        return isInternet;
    }

    public void setInternet(boolean internet) {
        isInternet = internet;
    }
}
