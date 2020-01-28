package com.example.myrssreader.Helpers;

import android.util.Xml;

import com.example.myrssreader.MainActivity;
import com.example.myrssreader.RssFeedModel.CacheRssFeedModel;
import com.example.myrssreader.RssFeedModel.OnlineRssFeedModel;
import com.example.myrssreader.RssFeedModel.RssFeedModelAbstract;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class FeedParsers {
    private static void saveInputStream(final InputStream inputStream) throws IOException {
        final File file = new File(MainActivity.getRoot(), "xml.xml");
        final Scanner s = new Scanner(inputStream).useDelimiter("\\A");

        FileOutputStream out = new FileOutputStream(file);
        out.write(s.next().getBytes());

        out.flush();
        out.close();
    }

    static List<RssFeedModelAbstract> parseFeed(final InputStream inputStream,
                                                final URL url,
                                                final boolean isCache) throws XmlPullParserException, IOException {
        String title = null;
        String link = null;
        String description = null;
        String linkToImage = null;
        String pubDate = null;
        boolean isItem = false;
        final List<RssFeedModelAbstract> items = new ArrayList<>();

        try {
            XmlPullParser xmlPullParser = Xml.newPullParser();
            xmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            xmlPullParser.setInput(inputStream, null);

            while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT) {
                int eventType = xmlPullParser.getEventType();
                String name = xmlPullParser.getName();

                if (name == null)
                    continue;

                if (eventType == XmlPullParser.END_TAG) {
                    if (name.equalsIgnoreCase("item")) {
                        isItem = false;
                    }
                    continue;
                }

                if (eventType == XmlPullParser.START_TAG) {
                    if (name.equalsIgnoreCase("item")) {
                        isItem = true;
                        continue;
                    }
                    if (name.equalsIgnoreCase("enclosure")) {
                        linkToImage = xmlPullParser.getAttributeValue(null, "url");
                        continue;
                    }
                }

                String result = "";
                if (xmlPullParser.next() == XmlPullParser.TEXT) {
                    result = xmlPullParser.getText();
                    xmlPullParser.nextTag();
                }

                if (name.equalsIgnoreCase("title")) {
                    title = result;
                } else if (name.equalsIgnoreCase("link")) {
                    link = result;
                } else if (name.equalsIgnoreCase("pubDate")) {
                    pubDate = result;
                } else if (name.equalsIgnoreCase("description")) {
                    description = result;

                    if (result.indexOf('>') != -1)
                        description = description.substring(result.indexOf('>') + 1);
                }

                if (title != null && link != null && description != null && linkToImage != null && pubDate != null) {
                    if (isItem) {
                        if (!isCache)
                            items.add(new OnlineRssFeedModel(title, link, description, pubDate, linkToImage));
                        else if (MainActivity.getCachePreferences().getBoolean(title.hashCode() + "", false))
                            items.add(new CacheRssFeedModel(title, description, pubDate));
                    }

                    title = null;
                    link = null;
                    description = null;
                    linkToImage = null;
                    isItem = false;
                    pubDate = null;
                }
            }

            if (!isCache)
                saveInputStream(url.openConnection().getInputStream());

            return items;
        } finally {
            inputStream.close();
        }
    }
}
