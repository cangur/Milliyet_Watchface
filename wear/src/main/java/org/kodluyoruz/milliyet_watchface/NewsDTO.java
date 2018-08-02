package org.kodluyoruz.milliyet_watchface;

import android.graphics.Bitmap;

public class NewsDTO {

    public String newsID;
    public String newsTitle;
    public String newsSummary;
    public String newsContent;
    public Bitmap newsBitmap;

    public NewsDTO(String newsID, String newsTitle, String newsSummary, String newsContent, Bitmap newsBitmap) {
        this.newsID = newsID;
        this.newsTitle = newsTitle;
        this.newsSummary = newsSummary;
        this.newsContent = newsContent;
        this.newsBitmap = newsBitmap;
    }

    public NewsDTO() {
    }
}
