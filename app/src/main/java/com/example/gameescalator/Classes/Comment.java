package com.example.gameescalator.Classes;

import java.util.HashMap;

public class Comment {

   public final static String[] rateSystem = {
                "Unbearable","Painful"
                ,"Awful","Bad"
                ,"Mediocre","Okay"
                ,"Good","Great"
                ,"Amazing","Masterpiece" };

    private  String gameName ,content,uid,name, gameKey;
    private int rateScale;

    public Comment(){ /*NOTHING*/ }

    public Comment(String content, String uid,String uname) {
        this.content = content;
        this.uid = uid;
        this.name = uname;
    }

    public Comment(String content, String uid,String uname, String gameKey, String gameName, int rateScale) {
        this.content = content;
        this.uid = uid;
        name = uname;
        this.gameKey = gameKey;
        this.gameName = gameName;
        this.rateScale = rateScale;
    }

    public Comment(Comment other) {
        this.content = other.content;
        this.uid = other.uid;
        name = other.name;
        this.gameKey = other.gameKey;
        this.gameName = other.gameName;
        this.rateScale = other.rateScale;
    }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getUname() { return name; }
    public void setUname(String uname) { this.name = uname; }

    public String getGameKey() { return gameKey; }
    public void setGameKey(String gameKey) { this.gameKey = gameKey; }

    public String getGameName() { return gameName; }

    public int getRateScale() { return rateScale; }
    public void setRateSystem(int rateScale) { this.rateScale = rateScale; }
}
