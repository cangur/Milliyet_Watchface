package org.kodluyoruz.milliyet_watchface.api.model;

public class Images {
    private String baseUrl;

    private String name;

    private String format;

    private String contentType;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public String toString() {
        return "ClassPojo [baseUrl = " + baseUrl + ", name = " + name + ", format = " + format + ", contentType = " + contentType + "]";
    }
}

