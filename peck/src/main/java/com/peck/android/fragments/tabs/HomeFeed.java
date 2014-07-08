package com.peck.android.fragments.tabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.peck.android.R;
import com.peck.android.enums.CommentType;
import com.peck.android.fragments.CommentFeed;
import com.peck.android.fragments.FeedTab;
import com.peck.android.managers.EventManager;
import com.peck.android.models.Event;

/**
 * Created by mammothbane on 6/9/2014.
 */

public class HomeFeed extends FeedTab<Event> {

    private final static String tag = "EventFeed";
    private final static int resId = R.layout.tab_homefeed;
    private final static int lvId = R.id.lv_events;

    private ListView comments;
    private CommentFeed commentFeed = new CommentFeed();

    {
        commentFeed.setType(CommentType.SIMPLE_EVENT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.rl_comments, commentFeed).commit();
        getActivity().getSupportFragmentManager().executePendingTransactions();
        comments = ((ListView)view.findViewById(lvId));
        comments.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                Event event = feedAdapter.getItem(i);
                commentFeed.setParent(event.getServerId());
                commentFeed.notifyDatasetChanged();
                comments.setVisibility(View.VISIBLE);
            }
        });
        return view;
    }

    public int getLayoutRes() {
        return resId;
    }

    public String tag() {
        return tag;
    }

    public int getListViewRes() {
        return lvId;
    }

    public Class<EventManager> getManagerClass() { return EventManager.class; }

}
