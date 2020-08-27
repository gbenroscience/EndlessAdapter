package com.itis.libs.examples;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.itis.libs.R;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class Main extends AppCompatActivity {

    ListView list;
    Button loadMore;
    ModelAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadMore = findViewById(R.id.load_more);
        list = findViewById(R.id.list);

        adapter = new ModelAdapter() {
            @Override
            public void onScrollToBottom(int bottomIndex, boolean moreItemsCouldBeAvailable) {
                if (moreItemsCouldBeAvailable) {
                    makeYourServerCallForMoreItems();
                } else {
                    if (loadMore.getVisibility() != View.VISIBLE) {
                        loadMore.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onScrollAwayFromBottom(int currentIndex) {
                loadMore.setVisibility(View.GONE);
            }

            @Override
            public void onFinishedLoading(boolean moreItemsReceived) {
                if (!moreItemsReceived) {
                    loadMore.setVisibility(View.VISIBLE);
                }
            }
        };

        list.setAdapter(adapter);

        loadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeYourServerCallForMoreItems();
            }
        });

    }


    private void makeYourServerCallForMoreItems() {
        int offset = adapter.getCount();//Send this offset to your server..


        String json = Server.call(offset);// Your server will return the next set of available items


        try {
            JSONArray array = new JSONArray(json);
            int sz = array.length();

            List<Model> models = new ArrayList<>();

            for (int i = 0; i < sz; i++) {
                models.add(new Model(array.optString(i)));
            }

            adapter.loadMoreItems(models, models.isEmpty(), list);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
