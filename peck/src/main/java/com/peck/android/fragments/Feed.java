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

import java.util.ArrayList;

/**
 * Created by mammothbane on 6/9/2014.
 *
 * class to handle feed fragments
 *
 */

public class Feed extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

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

    private int listViewRes = R.id.lv_content;
    private int layoutRes = R.layout.feed;
    private int listItemRes;
    private String[] binds_from;
    private int[] binds_to;
    private SimpleCursorAdapter mAdapter;

    private AdapterView.OnItemClickListener listener;

    private SimpleCursorAdapter.ViewBinder viewBinder;
    private Bundle loaderBundle;
    private static int loader_static = 0;
    private final int URL_LOADER = loader_static++;

    private ArrayList<View> footers = new ArrayList<View>();
    private ArrayList<View> headers = new ArrayList<View>();

    private RecycleRunnable runnable;

    public abstract static class RecycleRunnable implements Runnable {
        protected View recycledView;
        private void setRecycledView(@NonNull View recycledView) { this.recycledView = recycledView; }
    }

    public static class Builder {
        private Bundle loaderBundle = new Bundle();
        private Bundle feedBundle = new Bundle();
        private AdapterView.OnItemClickListener listener;
        private SimpleCursorAdapter.ViewBinder viewBinder;
        private View[] header;
        private View[] footer;
        private RecycleRunnable runnable;

        public Builder(@NonNull Uri loaderUri, int itemLayout) {
            feedBundle.putInt(FEED_ITEM_LAYOUT, itemLayout);
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

        public Builder withBindings(@NonNull String[] bindsFrom, @NonNull int[] bindsTo) {
            //if (BuildConfig.DEBUG && bindsFrom.length < bindsTo.length) throw new IllegalArgumentException("too few fields to bind from");
            feedBundle.putStringArray(BINDS_FROM, bindsFrom);
            feedBundle.putIntArray(BINDS_TO, bindsTo);
            return this;
        }

        public Builder withRecycleRunnable(@NonNull RecycleRunnable runnable) {
            this.runnable = runnable;
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

        public Builder withHeader(@NonNull View... header) {
            this.header = header;
            return this;
        }

        public Builder withFooter(@NonNull View... footer) {
            this.footer = footer;
            return this;
        }

        public Feed build() {
            Feed ret = new Feed();

            feedBundle.putBundle(LOADER_BUNDLE, loaderBundle);
            ret.setArguments(feedBundle);
            if (header != null) for (View view : header) ret.addHeader(view);
            if (footer != null) for (View view : footer) ret.addFooter(view);
            if (viewBinder != null) ret.setViewBinder(viewBinder);
            if (listener != null) ret.setOnItemClickListener(listener);
            if (runnable != null) ret.setRunnable(runnable);

            return ret;
        }

    }

    public Cursor getAuxCursor() {
        return null;
    }

    public void addHeader(View header) {
        headers.add(header);
    }

    public void addFooter(View footer) {
        footers.add(footer);
    }

    public void setRunnable(RecycleRunnable runnable) { this.runnable = runnable; }

    public void bindToAdapterView(ListView adapterView) {
        mAdapter = new SimpleCursorAdapter(getActivity(), listItemRes, null, (binds_from == null) ? new String[] {} : binds_from, (binds_to == null) ? new int[] {} : binds_to, 0) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                if (runnable != null) {
                    runnable.setRecycledView(view);
                    runnable.run();}

                return view;
            }
        };
        if (viewBinder != null) mAdapter.setViewBinder(viewBinder);

        adapterView.setAdapter(mAdapter);
        if (listener != null) (adapterView).setOnItemClickListener(listener);

        getLoaderManager().initLoader(URL_LOADER, loaderBundle, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        if (i == URL_LOADER) return new CursorLoader(getActivity(), (Uri)bundle.getParcelable(LOADER_URI), bundle.getStringArray(LOADER_PROJECTION),
                bundle.getString(LOADER_SELECTION), bundle.getStringArray(LOADER_SELECT_ARGS), bundle.getString(LOADER_SORT_ORDER));
        else return null;
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
        ListView lv = ((ListView) view.findViewById(listViewRes));
        for (View header : headers) lv.addHeaderView(header);
        for (View footer : footers) lv.addFooterView(footer);
        bindToAdapterView(lv);
        return view;
    }

    public int getListViewRes() {
        return listViewRes;
    }

    public void setViewBinder(SimpleCursorAdapter.ViewBinder binder) {
        this.viewBinder = binder;
        if (mAdapter != null) mAdapter.setViewBinder(viewBinder);
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) { this.listener = listener; }

}