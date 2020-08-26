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
                System.out.println("Scrolled to list bottom");
                if (moreItemsCouldBeAvailable) {
                    System.out.println("Mock Server could have more items...making server call");
                    makeYourServerCallForMoreItems();
                } else {
                    System.out.println("Mock Server had no items the last time we queried it");
                    if (loadMore.getVisibility() != View.VISIBLE) {
                        System.out.println("...We better enable button for manual calls by user.");
                        loadMore.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onScrollAwayFromBottom(int currentIndex) {
                System.out.println("Scrolling away from list bottom");
                loadMore.setVisibility(View.GONE);
            }

            @Override
            public void onFinishedLoading(boolean moreItemsReceived) {
                System.out.println("onFinishedLoading called");
                if (!moreItemsReceived) {
                    System.out.println("... and nothing was received");
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

            adapter.loadMoreItems(models, list);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
