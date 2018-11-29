package com.example.forrestsu.zhdaily.beans;

public class NewsTitle {

    private String id;  //新闻id
    private String title;  //新闻标题
    private String imageUri;  //图片url
    private String type;  //

    public NewsTitle(String id, String title, String imageUri, String type) {
        this.id = id;
        this.title = title;
        this.imageUri = imageUri;
        this.type = type;
    }

    public String getImageUri() {
        return imageUri;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }
}
