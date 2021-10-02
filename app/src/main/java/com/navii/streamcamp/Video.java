package com.navii.streamcamp;

public class Video {
    private String videoUrl, title, desc,campMasterID,campFireID,campMaster,campMasterTitle,imageURl;
    boolean isPremium;
    public Video(String videoUrl, String title, String desc,String campMasterID,String campFireID,String campMaster,String campMasterTitle,Boolean isPremium,String imageURl) {
        this.videoUrl = videoUrl;
        this.title = title;
        this.desc = desc;
        this.campMasterID = campMasterID;
        this.campFireID = campFireID;
        this.campMaster = campMaster;
        this.campMasterTitle = campMasterTitle;
        this.isPremium = isPremium;
        this.imageURl = imageURl;


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

    public boolean isPremium() {
        return isPremium;
    }

    public String getImageURl() {
        return imageURl;
    }

    public String getCampMasterTitle() {
        return campMasterTitle;
    }
}
