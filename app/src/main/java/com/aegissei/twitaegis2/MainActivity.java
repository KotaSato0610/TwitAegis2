package com.aegissei.twitaegis2;

import android.app.ListActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.image.SmartImageView;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;

import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class MainActivity extends ListActivity {

    private TweetAdapter aAdapter;
    private Twitter aTwitter;
    private static final int Refresh = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!TwitterUtils.hasAccessToken(this)) {
            Intent intent = new Intent(this, TwitterOAuthActivity.class);
            startActivity(intent);
            finish();
        } else {
            aAdapter = new TweetAdapter(this);
            setListAdapter(aAdapter);
            aTwitter = TwitterUtils.getTwitterInstance(this);
            reloadTimeLine();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    private class TweetAdapter extends ArrayAdapter<twitter4j.Status> {
        private LayoutInflater aInflater;
        public  TweetAdapter(Context context) {
            super(context, android.R.layout.simple_list_item_1);
            aInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public View getView (int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = aInflater.inflate(R.layout.list_item_tweet, null);
            }
            Status item = getItem(position);
            SmartImageView icon = (SmartImageView) convertView.findViewById(R.id.icon);
            icon.setImageUrl(item.getUser().getProfileImageURL());
            TextView screenName = (TextView) convertView.findViewById(R.id.screen_name);
            screenName.setText("@" + item.getUser().getScreenName());
            TextView name = (TextView) convertView.findViewById(R.id.name);
            name.setText(item.getUser().getName());
            TextView time = (TextView) convertView.findViewById(R.id.time);
            SimpleDateFormat df1 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            String sDate = df1.format(item.getCreatedAt());
            time.setText(sDate);
            TextView text = (TextView) convertView.findViewById(R.id.text);
            text.setText(item.getText());
            return convertView;
        }
    }

    private void reloadTimeLine() {
        AsyncTask<Void, Void, List<twitter4j.Status>> task = new AsyncTask<Void, Void, List<twitter4j.Status>>() {
            @Override
            protected List<twitter4j.Status> doInBackground(Void... params) {
                try {
                    return aTwitter.getHomeTimeline();
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<twitter4j.Status> result) {
                if (result != null) {
                    aAdapter.clear();
                    for (twitter4j.Status status : result) {
                        aAdapter.add(status);
                    }
                    getListView().setSelection(0);
                } else {
                    showToast("取得失敗");
                }
            }
        };
        task.execute();
    }

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

}

