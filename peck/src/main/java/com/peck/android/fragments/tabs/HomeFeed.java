package com.peck.android.fragments.tabs;

import android.os.Bundle;
import android.util.Log;
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

    private ListView events;
    private CommentFeed commentFeed = new CommentFeed();

    {
        commentFeed.setType(CommentType.SIMPLE_EVENT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.rl_comments, commentFeed).commit();
        events = ((ListView)view.findViewById(lvId));
        events.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                if (view.findViewById(R.id.rl_comments).getVisibility() == View.VISIBLE) {
                    events.setEnabled(true);
                    view.findViewById(R.id.rl_comments).setVisibility(View.GONE);
                }
                else {
                    Event event = feedAdapter.getItem(i);
                    commentFeed.setParent(event.getServerId());
                    commentFeed.notifyDatasetChanged();
                    view.findViewById(R.id.rl_comments).setVisibility(View.VISIBLE);
                    events.setEnabled(false);
                    Log.d(getClass().getSimpleName(), "clicked");
                }
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
