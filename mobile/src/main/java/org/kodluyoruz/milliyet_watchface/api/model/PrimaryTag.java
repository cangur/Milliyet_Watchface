package org.kodluyoruz.milliyet_watchface.api.model;

public class PrimaryTag {
    private String id;

    private String name;

    private String followingType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
        return "ClassPojo [id = " + id + ", name = " + name + ", followingType = " + followingType + "]";
    }
}