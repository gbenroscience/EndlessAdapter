package com.itis.endlessloader.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

/**
 * Defines a simple generic BaseAdapter for an android {@link AdapterView}
 * that can be loaded with data continuously when the {@link AdapterView}
 * is scrolled to the bottom.
 * @param <T> A generic class type for the data to be rendered in this {@link android.widget.Adapter}
 */
public abstract class EndlessAdapter<T> extends BaseAdapter {

  protected List<T> items = new ArrayList<>();
    /**
     * If true, the last call that was made for data via {@link EndlessAdapter#loadMoreItems(List, AdapterView)}
     * returned data from the server.
     *
     * We set it to true by default to reflect our calm optimism
     * about data sources ability to at least have 1 unit of data by default, in general...lol
     *
     */
  private boolean srcHadItemsAtLastCall = true;

  private boolean busyLoading;




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
     * {@link EndlessAdapter#getView(int, View, ViewGroup)} IMPLEMENTATION.
     * @param position The position of the data of the view to be generated in the adapter
     * @param convertView The view to be generated
     * @param parent The parent of the view to be generated.
     * @return the generated view.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

       if(position == this.getCount() - 1){
           if(!busyLoading) {
               onScrollToBottom(position , srcHadItemsAtLastCall);
               busyLoading = true;
           }
        }else{
            AdapterView adapterView = (AdapterView) parent;
            int count = adapterView.getCount();
            if(adapterView.getLastVisiblePosition() == count - 1){
                if(!busyLoading) {
                    onScrollToBottom(count - 1 , srcHadItemsAtLastCall);
                    busyLoading = true;
                }
            }else {
                onScrollAwayFromBottom(position);
            }
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
     * This method MUST be the only way to add more items to the list
     * ESPECIALLY when the list is being updated by being scrolled to the bottom.
     * @param moreItems A list of more items to add to the adapter
     */
    public void loadMoreItems(final @Nullable List<T> moreItems , final AdapterView parent) {

boolean updateDidCome = moreItems != null && !moreItems.isEmpty();
      final int lastVisible = parent.getLastVisiblePosition();

      if(updateDidCome) {
          srcHadItemsAtLastCall = true;
          this.items.addAll(moreItems);
          notifyDataSetChanged();


//updates came, position the adapterview well, so user can consume updates.

          parent.post(new Runnable() {
              @Override
              public void run() {
                  parent.setSelection(lastVisible);
                  onFinishedLoading(true );
                  busyLoading = false;
              }
          });


      }else{//No updates.
          onFinishedLoading(false );
          busyLoading = false;
          srcHadItemsAtLastCall = false;
      }


    }

    public void clear(){
     this.items.clear();
     this.notifyDataSetChanged();
    }

    /**
     *
     * @param bottomIndex The index of the last item.
     * @param moreItemsCouldBeAvailable If true, the server or other source of data has not yet reported to this
     *                   adapter that it has no data again. So the user can call for more data
     *                   here. If false, the last time the server was contacted, there was no data
     *                   available.
     *                   Spontaneous or timed calls to the server or other source for more data should
     *                   only be made if this parameter is true. If false, no calls should be made to the server
     *                                  from this method. Instead initiate a manual call to the server via a
     *                                  <b>load-more</b> button or other UI element.
     */
    public abstract void onScrollToBottom(int bottomIndex , boolean moreItemsCouldBeAvailable);
    public abstract void onScrollAwayFromBottom(int currentIndex);

    /**
     * The network calls that updated the {@link EndlessAdapter}
     * @param moreItemsReceived If true, the adapter received updates.
     */
    public abstract void onFinishedLoading(boolean moreItemsReceived);

}
