package com.example.myrssreader;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myrssreader.Helpers.FetchFeedTask;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static boolean runDownload = false;
    private static boolean isConnected = false;

    private static SharedPreferences settings;
    private static SharedPreferences cachePreferences = null;
    private static File root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cachePreferences = getSharedPreferences("CACHE", Context.MODE_PRIVATE);
        settings = getSharedPreferences("MY_SETTINGS", Context.MODE_PRIVATE);

        root = getApplicationContext().getExternalFilesDir(null);

        RecyclerView mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        SwipeRefreshLayout mSwipeLayout = findViewById(R.id.swipeRefreshLayout);
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new FetchFeedTask(MainActivity.this, settings.getString("LINK", "")).execute();
            }
        });

        ConnectionState connectionState = new ConnectionState(this);
        connectionState.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isAvailable) {
                if (isAvailable){
                    Toast.makeText(getApplicationContext(), "Online", Toast.LENGTH_LONG).show();
                    setIsConnected(true);
                }else{
                    Toast.makeText(getApplicationContext(), "Offline", Toast.LENGTH_LONG).show();
                    setIsConnected(false);
                }
            }
        });

        if (settings.getString("LINK", "").equals(""))
            showDialogSettings();
        else
            new FetchFeedTask(MainActivity.this, settings.getString("LINK", "")).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                showDialogSettings();
                break;
        }
        return true;
    }

    private void showDialogSettings() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter RSS feed URL");

        final View dialogView = getLayoutInflater().inflate(R.layout.settings_dialog_layout, null);
        builder.setView(dialogView);

        final EditText editText = dialogView.findViewById(R.id.feedUrlEditText);
        editText.setText(settings.getString("LINK", ""));

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                settings.edit().putString("LINK", editText.getText().toString()).apply();
                new FetchFeedTask(MainActivity.this, settings.getString("LINK", "")).execute();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static SharedPreferences getCachePreferences() {
        return cachePreferences;
    }

    public static File getRoot() {
        return root;
    }

    public static boolean getRunDownload() {
        return runDownload;
    }

    public static void setRunDownload(boolean runDownload) {
        MainActivity.runDownload = runDownload;
    }

    public static boolean getIsConnected() {
        return isConnected;
    }

    public static void setIsConnected(boolean isConnected) {
        MainActivity.isConnected = isConnected;
    }
}
