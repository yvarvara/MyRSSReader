package com.example.myrssreader.Helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myrssreader.MainActivity;
import com.example.myrssreader.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;

public class ReadImageTask extends AsyncTask<String, Void, ResultForGetNews> {
    private WeakReference<View> view;
    private ResultForGetNews resultForGetNews;

    public ReadImageTask(final View view,
                         final ResultForGetNews resultForGetNews) {

        this.view = new WeakReference<>(view);
        this.resultForGetNews = resultForGetNews;

        ((TextView) view.findViewById(R.id.titleTextView)).setText(resultForGetNews.getTitle());
        ((TextView) view.findViewById(R.id.descriptionTextView)).setText(Html.fromHtml(resultForGetNews.getDescription()));
        ((TextView) view.findViewById(R.id.dateTextView)).setText(resultForGetNews.getPubDate());
        ((ImageView) view.findViewById(R.id.imageView)).setImageBitmap(null);
    }

    protected ResultForGetNews doInBackground(final String... pathsToFile) {
        try {
            final File root = MainActivity.getRoot();
            File file;
            InputStream in;

            Bitmap bitmap = null;
            if (pathsToFile[0] != null) {
                file = new File(root, pathsToFile[0]);
                in = new FileInputStream(file);
                bitmap = BitmapFactory.decodeStream(in);
            }

            String html = null;

            if (pathsToFile[1] != null) {
                file = new File(root, pathsToFile[1]);
                FileInputStream fis = new FileInputStream(file);
                byte[] data = new byte[fis.available()];
                if (fis.read(data) == -1)
                    return null;
                fis.close();
                html = new String(data);
            }
            resultForGetNews.setImageBitmap(bitmap);
            resultForGetNews.setHtml(html);

            return resultForGetNews;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    protected void onPostExecute(final ResultForGetNews result) {
        if (result == null) {
            view.clear();
            return;
        }

        try {
            ((ImageView) view.get().findViewById(R.id.imageView)).setImageBitmap(result.getImageBitmap());
        } catch (Exception e) {
            e.printStackTrace();
        }

        view.clear();
    }
}
