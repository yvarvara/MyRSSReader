package com.example.myrssreader.Helpers;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.myrssreader.MainActivity;
import com.example.myrssreader.R;
import com.example.myrssreader.RssFeedListAdapter;
import com.example.myrssreader.RssFeedModel.RssFeedModelAbstract;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;

public class FetchFeedTask extends AsyncTask<Void, Void, Boolean> {
    private String link;
    private List<RssFeedModelAbstract> feedModelList;
    private final WeakReference<MainActivity> mainActivity;
    private final WeakReference<SwipeRefreshLayout> swipeRefreshLayout;
    private final WeakReference<RecyclerView> recyclerView;

    public FetchFeedTask(final MainActivity mainActivity,
                         final String link) {
        this.link = link;
        this.mainActivity = new WeakReference<>(mainActivity);
        this.swipeRefreshLayout = new WeakReference<>((SwipeRefreshLayout) mainActivity.findViewById(R.id.swipeRefreshLayout));
        this.recyclerView = new WeakReference<>((RecyclerView) mainActivity.findViewById(R.id.recyclerView));
    }

    @Override
    protected void onPreExecute() {
        swipeRefreshLayout.get().setRefreshing(true);
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        if (TextUtils.isEmpty(link))
            return false;

        try {
            if (!link.startsWith("http://") && !link.startsWith("https://"))
                link = "https://" + link;

            final URL url = new URL(link);

            try {
                InputStream inputStream = url.openConnection().getInputStream();
                feedModelList = FeedParsers.parseFeed(inputStream, url, false);
            } catch (UnknownHostException e) { //todo ?
                File file = new File(MainActivity.getRoot(), "xml.xml");
                InputStream inputStream = new FileInputStream(file);

                feedModelList = FeedParsers.parseFeed(inputStream, null, true);
            }

            return true;
        } catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    protected void onPostExecute(Boolean success) {
        swipeRefreshLayout.get().setRefreshing(false);

        if (success) {
            recyclerView.get().setAdapter(new RssFeedListAdapter(feedModelList, mainActivity.get().getBaseContext()));
        } else {
            Toast.makeText(mainActivity.get(),
                    "Enter a valid RSS feed URL",
                    Toast.LENGTH_LONG).show();
        }
    }
}
