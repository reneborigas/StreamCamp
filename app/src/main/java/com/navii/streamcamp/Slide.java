package com.navii.streamcamp;

public class Slide {
    private String videoUrl,campMasterID,imageUrl,campMasterName,campMasterTitle,profileUrl,campFire,campFireDetails;
    private Boolean isChat, isSlide,isInfo,isEmpty;

    public Slide(String videoUrl,String imageUrl,String campMasterName, String campMasterTitle, String profileUrl, String campMasterID, String campFire,String campFireDetails,Boolean isChat, Boolean isSlide,Boolean isInfo , Boolean isEmpty) {
        this.videoUrl = videoUrl;
        this.imageUrl = imageUrl;

        this.campMasterID = campMasterID;
        this.campMasterTitle = campMasterTitle;
        this.campMasterName = campMasterName;
        this.profileUrl = profileUrl;
        this.campFire = campFire;
        this.campFireDetails = campFireDetails;

        this.isChat = isChat;
        this.isSlide = isSlide;
        this.isInfo = isInfo;
        this.isEmpty = isEmpty;


    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getCampMasterID() {
        return campMasterID;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getCampMasterName() {
        return campMasterName;
    }

    public String getCampMasterTitle() {
        return campMasterTitle;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public String getCampFire() {
        return campFire;
    }

    public String getCampFireDetails() {
        return campFireDetails;
    }

    public Boolean getChat() {
        return isChat;
    }

    public Boolean getSlide() {
        return isSlide;
    }

    public Boolean getInfo() {
        return isInfo;
    }

    public Boolean getEmpty() {
        return isEmpty;
    }
}
