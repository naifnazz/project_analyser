package com.example.sportanalyzer;
public class VideoItem {
    private String uri;
    private String title;
    private int thumbnailResId; // Resource ID for the thumbnail image

    public VideoItem(String uri, String title, int thumbnailResId) {
        this.uri = uri;
        this.title = title;
        this.thumbnailResId = thumbnailResId;
    }

    public String getUri() {
        return uri;
    }

    public String getTitle() {
        return title;
    }

    public int getThumbnailResId() {
        return thumbnailResId;
    }
}

