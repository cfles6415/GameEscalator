package com.example.gameescalator.Classes;

public class Messages {

    //Model Class
    private String postKey, name, imageUrl, creatorName;
    private int reviewersCount;
    private double rateScale;

    //Constructor
    public Messages(){

    }
    public Messages(String name, String imageUrl, String postKey, String creatorName, int reviewersCount, double rateScale){
        this.name = name;
        this.imageUrl = imageUrl;
        this.postKey = postKey;
        this.creatorName = creatorName;
        this.reviewersCount = reviewersCount;
        this.rateScale = rateScale;
    }


    //Getters And Setters
    public String getPostKey() {
        return postKey;
    }
    public void setPostKey(String postKey) {
        this.postKey = postKey;
    }

    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getCreatorName() { return creatorName; }
    public void setCreatorName(String creatorName) { this.creatorName = creatorName; }

    public int getReviewersCount() { return reviewersCount; }
    public void setReviewersCount(int reviewersCount) { this.reviewersCount = reviewersCount; }

    public double getRateScale() { return rateScale; }
    public void setRateScale(double rateScale) { this.rateScale = rateScale; }
}
