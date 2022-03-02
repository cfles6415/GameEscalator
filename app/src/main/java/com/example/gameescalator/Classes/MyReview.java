package com.example.gameescalator.Classes;

public class MyReview {

    private String gameName, content;
    private int rate;

    public MyReview(String gameName, String content, int rate) {

        this.gameName = gameName;
        this.content = content;
        this.rate = rate;

    }

    public String getGameName() { return gameName; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public int getRate() { return rate; }
    public void setRate(int rate) { this.rate = rate; }

    @Override
    public String toString() {
        return String.format("Game name: %s, Content: %s, Rate: %d",
                gameName,
                content,
                rate);
    }
}
