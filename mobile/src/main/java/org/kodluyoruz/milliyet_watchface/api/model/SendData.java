package org.kodluyoruz.milliyet_watchface.api.model;

public class SendData {

    private String id;
    private String title;
    private String summary;
    private String imageURL;
    private String content;

    public SendData(String id, String title, String summary, String imageURL, String content) {
        this.id = id;
        this.title = title;
        this.summary = summary;
        this.imageURL = imageURL;
        this.content = content;
    }

    public SendData() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
