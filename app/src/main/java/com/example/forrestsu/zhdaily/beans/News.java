package com.example.forrestsu.zhdaily.beans;

public class News {

    private String date; //新闻日期
    private String body; //新闻主体，html
    private String imageSource; //图片来源
    private String title;  //新闻标题
    private String imageUrl;  //大图url
    private String shareUrl;  //分享链接
    private String imagesUrl;  //缩略图url
    private int type;  //类型  0为普通新闻，-1为用于标记日期的News（只含有日期，没有其他内容）
    private String id;  //新闻id
    private String[] css;  //供手机端的 WebView(UIWebView) 使用,文章浏览界面可以利用 WebView(UIWebView) 实现

    //用于新闻列表和置顶新闻列表
    public News(String date, String title, String imagesUrl, int type, String id) {
        this.date = date;
        this.title = title;
        this.imagesUrl = imagesUrl;
        this.type = type;
        this.id = id;
    }

    /*用于置顶新闻列表
    public News(String title, String imageUrl, String type, String id) {
        this.title = title;
        this.imageUrl = imageUrl;
        this.type = type;
        this.id = id;
    }
    */

    //用于新闻详情
    public News(String body, String imageSource, String title, String imageUrl, String shareUrl,
                String imagesUrl, int type, String id,String[] css) {
        this.body = body;
        this.imageSource = imageSource;
        this.title = title;
        this.imageUrl = imageUrl;
        this.shareUrl = shareUrl;
        this.imagesUrl = imagesUrl;
        this.type = type;
        this.id = id;
        this.css = css;
    }

    //用于标记日期
    public News(String date, int type) {
        this.date = date;
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public String getBody() {
        return body;
    }

    public String getImageSource() {
        return imageSource;
    }

    public String getTitle() {
        return title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public String getImagesUrl() {
        return imagesUrl;
    }

    public int getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public String[] getCss() {
        return css;
    }

}
