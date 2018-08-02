package org.kodluyoruz.milliyet_watchface.api.model;

public class Tags {
    private String id;

    private String isPrimary;

    private String name;

    private String followingType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIsPrimary() {
        return isPrimary;
    }

    public void setIsPrimary(String isPrimary) {
        this.isPrimary = isPrimary;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFollowingType() {
        return followingType;
    }

    public void setFollowingType(String followingType) {
        this.followingType = followingType;
    }

    @Override
    public String toString() {
        return "ClassPojo [id = " + id + ", isPrimary = " + isPrimary + ", name = " + name + ", followingType = " + followingType + "]";
    }
}