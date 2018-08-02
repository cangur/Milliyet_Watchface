package org.kodluyoruz.milliyet_watchface.api.model;

public class Data {
    private String summary;

    private Tags[] tags;

    private String seoLink;

    private String weight;

    private String presentationType;

    private String status;

    private String updatedDate;

    private String updatedDateFormatted;

    private String publishedDateFormatted;

    private String url;

    private String id;

    private String content;

    private String title;

    private Source source;

    private Images[] images;

    private PrimaryTag primaryTag;

    private String publishedDate;

    private Media[] media;

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Tags[] getTags() {
        return tags;
    }

    public void setTags(Tags[] tags) {
        this.tags = tags;
    }

    public String getSeoLink() {
        return seoLink;
    }

    public void setSeoLink(String seoLink) {
        this.seoLink = seoLink;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getPresentationType() {
        return presentationType;
    }

    public void setPresentationType(String presentationType) {
        this.presentationType = presentationType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(String updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getUpdatedDateFormatted() {
        return updatedDateFormatted;
    }

    public void setUpdatedDateFormatted(String updatedDateFormatted) {
        this.updatedDateFormatted = updatedDateFormatted;
    }

    public String getPublishedDateFormatted() {
        return publishedDateFormatted;
    }

    public void setPublishedDateFormatted(String publishedDateFormatted) {
        this.publishedDateFormatted = publishedDateFormatted;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public Images[] getImages() {
        return images;
    }

    public void setImages(Images[] images) {
        this.images = images;
    }

    public PrimaryTag getPrimaryTag() {
        return primaryTag;
    }

    public void setPrimaryTag(PrimaryTag primaryTag) {
        this.primaryTag = primaryTag;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    public Media[] getMedia() {
        return media;
    }

    public void setMedia(Media[] media) {
        this.media = media;
    }

    @Override
    public String toString() {
        return "ClassPojo [summary = " + summary + ", tags = " + tags + ", seoLink = " + seoLink + ", weight = " + weight + ", presentationType = " + presentationType + ", status = " + status + ", updatedDate = " + updatedDate + ", updatedDateFormatted = " + updatedDateFormatted + ", publishedDateFormatted = " + publishedDateFormatted + ", url = " + url + ", id = " + id + ", content = " + content + ", title = " + title + ", source = " + source + ", images = " + images + ", primaryTag = " + primaryTag + ", publishedDate = " + publishedDate + ", media = " + media + "]";
    }
}