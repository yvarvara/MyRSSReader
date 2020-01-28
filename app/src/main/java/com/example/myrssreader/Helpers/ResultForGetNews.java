package com.example.myrssreader.Helpers;

import android.graphics.Bitmap;

//todo: rename
public class ResultForGetNews {
    private String title;
    private String description;
    private Bitmap imageBitmap;
    private String html;
    private String pubDate;

    public ResultForGetNews() {}

    String getTitle() {
        return title;
    }

    String getDescription() {
        return description;
    }

    String getPubDate() {
        return pubDate;
    }

    Bitmap getImageBitmap() {
        return imageBitmap;
    }

    public String getHtml() {
        return html;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    void setImageBitmap(Bitmap imageBitmap) {
        this.imageBitmap = imageBitmap;
    }

    void setHtml(String html) {
        this.html = html;
    }
}
