package com.example.myrssreader.Helpers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.example.myrssreader.MainActivity;
import com.example.myrssreader.RssFeedModel.OnlineRssFeedModel;
import com.example.myrssreader.RssFeedModel.RssFeedModelAbstract;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class DownloadTask extends AsyncTask<String, Void, Void> {
    private List<RssFeedModelAbstract> list;

    public DownloadTask(List<RssFeedModelAbstract> list) {
        this.list = list;
    }

    private void saveImage(final Bitmap finalBitmap,
                           final int hash) {
        final File file = new File(MainActivity.getRoot(),  hash + ".jpg");

        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveHtml(final String link,
                                   final int hash) {
        final File file = new File(MainActivity.getRoot(), hash + ".html");

        try {
            final FileOutputStream out = new FileOutputStream(file);

            URLConnection connection = (new URL(link)).openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.connect();

            InputStream in = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder html = new StringBuilder();
            for (String line; (line = reader.readLine()) != null; )
                html.append(line);
            in.close();

            out.write(html.toString().getBytes());
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Void doInBackground(String... data) {
        if (list.size() > 0 && list.get(0) instanceof OnlineRssFeedModel) {
            for (RssFeedModelAbstract i : list) {
                OnlineRssFeedModel onlineRssFeedModel = (OnlineRssFeedModel) i;

                if (MainActivity.getCachePreferences().getBoolean(onlineRssFeedModel.getTitle().hashCode() + "", false))
                    continue;

                try {
                    final InputStream in = new URL(onlineRssFeedModel.getLinkToImage()).openStream();
                    final Bitmap bitmap = BitmapFactory.decodeStream(in);

                    saveImage(bitmap, onlineRssFeedModel.getTitle().hashCode());
                    saveHtml(onlineRssFeedModel.getLink(), onlineRssFeedModel.getTitle().hashCode());

                    MainActivity.getCachePreferences().edit().putBoolean(onlineRssFeedModel.getTitle().hashCode() + "", true).apply();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        MainActivity.setRunDownload(false);
        return null;
    }
}
