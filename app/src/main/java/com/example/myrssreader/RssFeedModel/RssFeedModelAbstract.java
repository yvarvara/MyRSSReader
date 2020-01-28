package com.example.myrssreader.RssFeedModel;

public abstract class RssFeedModelAbstract {
    private final String title;
    private final String description;
    private final String pubDate;

    RssFeedModelAbstract(final String title,
                         final String description,
                         final String pubDate) {
        this.title = title;
        this.description = description;
        this.pubDate = pubDate;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getPubDate() {
        return pubDate;
    }

}
