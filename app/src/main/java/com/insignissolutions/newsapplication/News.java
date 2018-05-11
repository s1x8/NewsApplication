package com.insignissolutions.newsapplication;

import android.graphics.Bitmap;

public class News {

    private String title;
    private String section;
    private String date;
    private String author;
    private Bitmap thumbnail;
    private String articleUrl;

    public News(String title, String section, String date, String articleUrl,
                String author, Bitmap thumbnail){
        this.title = title;
        this.section = section;
        this.date = date;
        this.author = author;
        this.thumbnail = thumbnail;
        this.articleUrl = articleUrl;
    }

    public String getTitle(){return title;}
    public String getAuthor() {
        return author;
    }
    public String getArticleUrl() {
        return articleUrl;
    }
    public String getSection(){
        return section;
    }
    public String getDate(){
        return date;
    }

    public Bitmap getThumbnail(){
        return thumbnail;
    }
}
