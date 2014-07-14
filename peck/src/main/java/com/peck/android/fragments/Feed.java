package com.peck.android.fragments;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.peck.android.BuildConfig;
import com.peck.android.R;
import com.peck.android.models.DBOperable;

/**
 * Created by mammothbane on 6/9/2014.
 *
 * class to handle feed fragments
 *
 */

public class Feed<T extends DBOperable> extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LV_RES = "list view resource identifier";
    private static final String LAYOUT_RES = "layout identifier";
    private static final String FEED_ITEM_LAYOUT = "feed item layout identifier";
    private static final String LOADER_BUNDLE = "loader bundle";
    private static final String BINDS_FROM = "bindings from";
    private static final String BINDS_TO = "bindings to";

    //query arguments
    private static final String LOADER_URI = "uri"; //necessary
    private static final String LOADER_SELECTION = "selection";
    private static final String LOADER_PROJECTION = "projection";
    private static final String LOADER_SELECT_ARGS = "selection arguments";
    private static final String LOADER_SORT_ORDER = "sort order";

    protected String tag() {
        return ((Object)this).getClass().getName();
    }
    private int listViewRes = R.id.lv_content;
    private int layoutRes = R.layout.feed;
    private int listItemRes;
    private String[] binds_from;
    private int[] binds_to;
    private SimpleCursorAdapter mAdapter;
    private AdapterView.OnItemClickListener listener;
    private SimpleCursorAdapter.ViewBinder viewBinder;
    private Bundle loaderBundle;
    private static final int URL_LOADER = 0;

    public static class Builder {
        private Bundle loaderBundle = new Bundle();
        private Bundle feedBundle = new Bundle();
        private int itemLayout;
        private AdapterView.OnItemClickListener listener;
        private SimpleCursorAdapter.ViewBinder viewBinder;

        public Builder(@NonNull Uri loaderUri, int itemLayout) {
            feedBundle.putInt(FEED_ITEM_LAYOUT, itemLayout);
            this.itemLayout = itemLayout;
            loaderBundle.putParcelable(LOADER_URI, loaderUri);
        }

        public Builder withSelection(@NonNull String selection, @Nullable String[] selectionArgs) {
            loaderBundle.putString(LOADER_SELECTION, selection);
            if (selectionArgs != null) loaderBundle.putStringArray(LOADER_SELECT_ARGS, selectionArgs);
            return this;
        }

        public Builder withProjection (String[] projection) {
            loaderBundle.putStringArray(LOADER_PROJECTION, projection);
            return this;
        }

        public Builder orderedBy(@NonNull String ordering) {
            loaderBundle.putString(LOADER_SORT_ORDER, ordering);
            return this;
        }

        public Builder layout(int layout) {
            feedBundle.putInt(LAYOUT_RES, layout);
            return this;
        }

        public Builder bindToListView (int lvId) {
            feedBundle.putInt(LV_RES, lvId);
            return this;
        }

        public Builder withTextBindings(@NonNull String[] bindsFrom, @NonNull int[] bindsTo) {
            if (BuildConfig.DEBUG && bindsFrom.length != bindsTo.length) throw new IllegalArgumentException("must have the same number of fields assigning as are present.");
            feedBundle.putStringArray(BINDS_FROM, bindsFrom);
            feedBundle.putIntArray(BINDS_TO, bindsTo);
            return this;
        }

        public Builder withViewBinder(@NonNull SimpleCursorAdapter.ViewBinder viewBinder) {
            this.viewBinder = viewBinder;
            return this;
        }

        public Builder setOnItemClickListener(@NonNull AdapterView.OnItemClickListener listener) {
            this.listener = listener;
            return this;
        }

        public <T extends DBOperable> Feed<T> build() {
            Feed<T> ret = new Feed<T>();

            feedBundle.putBundle(LOADER_BUNDLE, loaderBundle);
            ret.setArguments(feedBundle);
            if (viewBinder != null) ret.setViewBinder(viewBinder);
            if (listener != null) ret.setOnItemClickListener(listener);

            return ret;
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        switch (i) {
            case URL_LOADER:
                return new CursorLoader(getActivity(), (Uri)bundle.getParcelable(LOADER_URI), bundle.getStringArray(LOADER_PROJECTION),
                        bundle.getString(LOADER_SELECTION), bundle.getStringArray(LOADER_SELECT_ARGS), bundle.getString(LOADER_SORT_ORDER));
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mAdapter.changeCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mAdapter.changeCursor(null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setArguments(Bundle args) {
        super.setArguments(args);

        binds_from = args.getStringArray(BINDS_FROM);
        binds_to = args.getIntArray(BINDS_TO);

        int res = args.getInt(LV_RES, -1);
        if (res != -1) listViewRes = res;

        res = args.getInt(LAYOUT_RES, -1);
        if (res != -1) layoutRes = res;

        res = args.getInt(FEED_ITEM_LAYOUT, -1);
        if (BuildConfig.DEBUG && res == -1) throw new IllegalArgumentException(getClass().getSimpleName() +
                ": You must pass a valid layout identifier into the bundle.");
        listItemRes = res;

        loaderBundle = args.getBundle(LOADER_BUNDLE);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(layoutRes, container, false);

        mAdapter = new SimpleCursorAdapter(getActivity(), listItemRes, null, binds_from, binds_to, 0);

        ((ListView)view.findViewById(listViewRes)).setAdapter(mAdapter);
        if (listener != null) ((ListView)view.findViewById(listViewRes)).setOnItemClickListener(listener);

        getLoaderManager().initLoader(URL_LOADER, loaderBundle, this);

        return view;
    }

    public void setViewBinder(SimpleCursorAdapter.ViewBinder binder) {
        this.viewBinder = binder;
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) { this.listener = listener; }

}