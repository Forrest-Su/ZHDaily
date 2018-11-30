package com.example.forrestsu.zhdaily.beans;

public class News {

    private String body; //新闻主体，html
    private String imageSource; //图片来源
    private String title;  //新闻标题
    private String imageUrl;  //大图url
    private String shareUrl;  //分享链接
    private String imagesUrl;  //缩略图url
    private String type;  //
    private String id;  //新闻id
    private String[] css;  //供手机端的 WebView(UIWebView) 使用,文章浏览界面可以利用 WebView(UIWebView) 实现

    //用于新闻列表和置顶新闻列表
    public News(String title, String imagesUrl, String type, String id) {
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
                String imagesUrl, String type, String id,String[] css) {
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

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public String[] getCss() {
        return css;
    }

}
