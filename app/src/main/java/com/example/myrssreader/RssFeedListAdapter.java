package com.example.myrssreader;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myrssreader.Helpers.DownloadTask;
import com.example.myrssreader.Helpers.ReadImageTask;
import com.example.myrssreader.Helpers.ResultForGetNews;
import com.example.myrssreader.RssFeedModel.OnlineRssFeedModel;
import com.example.myrssreader.RssFeedModel.RssFeedModelAbstract;

import java.util.List;

public class RssFeedListAdapter extends RecyclerView.Adapter<RssFeedListAdapter.FeedModelViewHolder> {
    private final Context context;
    private List<RssFeedModelAbstract> rssFeedModels;

    static class FeedModelViewHolder extends RecyclerView.ViewHolder {
        private View rssFeedView;

        FeedModelViewHolder(final View v) {
            super(v);
            rssFeedView = v;
        }
    }

    public RssFeedListAdapter(final List<RssFeedModelAbstract> rssFeedModels,
                              final Context context) {
        this.context = context;
        this.rssFeedModels = rssFeedModels;

        if (!MainActivity.getRunDownload()) {
            MainActivity.setRunDownload(true);
            (new DownloadTask(rssFeedModels)).execute();
        }
    }

    @NonNull
    @Override
    public FeedModelViewHolder onCreateViewHolder(final ViewGroup parent,
                                                  final int type) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_item, parent, false);
        return new FeedModelViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final FeedModelViewHolder holder,
                                 final int position) {
        final RssFeedModelAbstract rssFeedModel = rssFeedModels.get(position);
        final ResultForGetNews resultForGetNews = new ResultForGetNews();
        resultForGetNews.setTitle(rssFeedModel.getTitle());
        resultForGetNews.setDescription(rssFeedModel.getDescription());
        resultForGetNews.setPubDate(rssFeedModel.getPubDate());

//        while (!MainActivity.getCachePreferences().getBoolean(rssFeedModel.getTitle().hashCode() + "", false)) {
//            try {
//                Thread.sleep(500); //todo 1
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }

        if (MainActivity.getCachePreferences().getBoolean(rssFeedModel.getTitle().hashCode() + "", false)) {
            (new ReadImageTask(holder.rssFeedView, resultForGetNews)).execute(
                    rssFeedModel.getTitle().hashCode() + ".jpg",
                    rssFeedModel.getTitle().hashCode() + ".html"
            );
        } else { //todo 2
            (new ReadImageTask(holder.rssFeedView, resultForGetNews)).execute(
                    null,
                    null
            );
        }

        holder.rssFeedView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent browserIntent = new Intent(context, WebViewActivity.class);
                browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // todo ?

                if (rssFeedModel instanceof OnlineRssFeedModel) {
                    if (MainActivity.getIsConnected()) {
                        browserIntent.putExtra("page", ((OnlineRssFeedModel) rssFeedModel).getLink());
                        context.startActivity(browserIntent);
                    } else if (resultForGetNews.getHtml() != null) {
                        browserIntent.putExtra("data", resultForGetNews.getHtml());
                        context.startActivity(browserIntent);
                    }
                } else {
                    if (resultForGetNews.getHtml() != null) {
                        browserIntent.putExtra("data", resultForGetNews.getHtml());
                        context.startActivity(browserIntent);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return rssFeedModels.size();
    }
}