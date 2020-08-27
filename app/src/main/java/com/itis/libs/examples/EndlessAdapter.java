package com.itis.libs.examples;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines a simple generic BaseAdapter for an android {@link AdapterView}
 * that can be loaded with data continuously when the {@link AdapterView}
 * is scrolled to the bottom.
 *
 * @param <T> A generic class type for the data to be rendered in this {@link android.widget.Adapter}
 */
public abstract class EndlessAdapter<T> extends BaseAdapter {

    protected List<T> items = new ArrayList<>();
    /**
     * If true, the last call that was made for data via {@link EndlessAdapter#loadMoreItems(List, boolean, AdapterView)}
     * returned data from the server.
     * <p>
     * We set it to true by default to reflect our calm optimism
     * about data sources ability to at least have 1 unit of data by default, in general...lol
     */
    private boolean sourceExhausted = true;

    private boolean busyLoading;


    private boolean atBottom;


    @Override
    public T getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return items.size();
    }


    /**
     * SUBCLASSES MUST ALWAYS CALL THIS METHOD AT THE BOTTOM OF THEIR
     * {@link com.itis.endlessloader.adapters.EndlessAdapter#getView(int, View, ViewGroup)} IMPLEMENTATION.
     *
     * @param position    The position of the data of the view to be generated in the adapter
     * @param convertView The view to be generated
     * @param parent      The parent of the view to be generated.
     * @return the generated view.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        AdapterView adapterView = (AdapterView) parent;

        int lastVisiblePosition = adapterView.getLastVisiblePosition();
        int count = items.size();
        if (position == count - 1 || lastVisiblePosition == count - 1) {
            if (!busyLoading) {
                busyLoading = true;
                onScrollToBottom(position, !sourceExhausted);
                if (sourceExhausted) {//The server call will trigger loadMoreItems which will set busyLoading to false if srcHadItemsAtLastCall = true.
                    busyLoading = false;
                }
                atBottom = true;
            }
        } else if (position < count - 1) {
            if (atBottom) {
                onScrollAwayFromBottom(position);
            }
            atBottom = false;
        }


        return convertView;
    }

    public void add(T item, boolean refresh) {

        for (T t : items) {
            if (item.equals(t)) {
                return;
            }
        }
        items.add(item);
        if (refresh) {
            notifyDataSetChanged();
        }
    }


    public void add(List<T> modelData) {

        for (T t : modelData) {
            int indexOfDuplicate = items.indexOf(t);
            if (indexOfDuplicate != -1) {
                items.set(indexOfDuplicate, t);
            } else {
                items.add(t);
            }
        }

        notifyDataSetChanged();
    }

    public boolean isBusyLoading() {
        return busyLoading;
    }


    /**
     *
     * This method MUST be the only way to add more items to the list
     * ESPECIALLY when the list is being updated by being scrolled to the bottom.
     *
     * @param moreItems A list of more items to add to the adapter
     * @param sourceExhausted An hint from the data source that data is no more available... try and send this hint from your server or other data source
     * @param parent The listview that renders this data
     */
    public void loadMoreItems(final @Nullable List<T> moreItems, boolean sourceExhausted, final AdapterView parent) {


        boolean updateDidCome = moreItems != null && !moreItems.isEmpty();
        final int lastVisible = parent.getLastVisiblePosition();

        this.sourceExhausted = !updateDidCome || sourceExhausted;

        if (updateDidCome) {
            this.items.addAll(moreItems);
            notifyDataSetChanged();


//updates came, position the adapterview well, so user can consume updates.

            parent.post(new Runnable() {
                @Override
                public void run() {
                    parent.setSelection(lastVisible);
                    onFinishedLoading(true);
                    busyLoading = false;
                }
            });


        } else {//No updates.
            onFinishedLoading(false);
            busyLoading = false;
        }


    }

    public void clear() {
        this.items.clear();
        this.notifyDataSetChanged();
    }

    /**
     * @param bottomIndex               The index of the last item.
     * @param moreItemsCouldBeAvailable If true, the server or other source of data has not yet reported to this
     *                                  adapter that it has no data again. So the user can call for more data
     *                                  here. If false, the last time the server was contacted, there was no data
     *                                  available.
     *                                  Spontaneous or timed calls to the server or other source for more data should
     *                                  only be made if this parameter is true. If false, no calls should be made to the server
     *                                  from this method. Instead initiate a manual call to the server via a
     *                                  <b>load-more</b> button or other UI element.
     */
    public abstract void onScrollToBottom(int bottomIndex, boolean moreItemsCouldBeAvailable);

    public abstract void onScrollAwayFromBottom(int currentIndex);

    /**
     * The network calls that updated the {@link com.itis.endlessloader.adapters.EndlessAdapter}
     *
     * @param moreItemsReceived If true, the adapter received updates.
     */
    public abstract void onFinishedLoading(boolean moreItemsReceived);

}
