package com.navii.streamcamp;

public class Video {
    private String videoUrl, title, desc,campMasterID,campFireID,campMaster,campMasterTitle;

    public Video(String videoUrl, String title, String desc,String campMasterID,String campFireID,String campMaster,String campMasterTitle) {
        this.videoUrl = videoUrl;
        this.title = title;
        this.desc = desc;
        this.campMasterID = campMasterID;
        this.campFireID = campFireID;
        this.campMaster = campMaster;
        this.campMasterTitle = campMasterTitle;


    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    public String getCampMasterID() {
        return campMasterID;
    }

    public String getCampFireID() {
        return campFireID;
    }

    public String getCampMaster() {
        return campMaster;
    }

    public String getCampMasterTitle() {
        return campMasterTitle;
    }
}
