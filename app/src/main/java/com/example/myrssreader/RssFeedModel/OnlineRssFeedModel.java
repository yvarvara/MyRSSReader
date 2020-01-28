package com.example.myrssreader.RssFeedModel;

public class OnlineRssFeedModel extends RssFeedModelAbstract {
    private String link;
    private String linkToImage;

    public OnlineRssFeedModel(final String title,
                              final String link,
                              final String description,
                              final String pubDate,
                              final String linkToImage) {
        super(title, description, pubDate);

        this.link = link;
        this.linkToImage = linkToImage;
    }

    public String getLink() {
        return link;
    }
    
    public String getLinkToImage() {
        return linkToImage;
    }
}
