/*
 * Copyright (c) 2014 Peck LLC.
 * All rights reserved.
 */

package com.peck.android.fragments;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
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
 * feed fragment class, constructed using a builder. if you don't use a builder, make sure you supply the necessary data in setArguments.
 */

public class Feed extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LV_RES = "list view resource identifier";
    private static final String LAYOUT_RES = "layout identifier";
    private static final String FEED_ITEM_LAYOUT = "feed item layout identifier";
    private static final String LOADER_BUNDLE = "loader bundle";
    private static final String BINDS_FROM = "bindings from";
    private static final String BINDS_TO = "bindings to";
    private static final String DIVIDERS = "dividers";

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
    private boolean dividers;

    private AdapterView.OnItemClickListener listener;
    private AdapterView mAdapterView;

    private SimpleCursorAdapter.ViewBinder viewBinder;
    private Bundle loaderBundle;
    private static final int LOADER_ID = 0;

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

        /**
         * the selection to query {@link com.peck.android.database.InternalContentProvider} for
         * @param selection the where string, with arguments replaced by '?'s
         * @param selectionArgs an array of arguments, to substitute '?'s in the selection string
         */
        public Builder withSelection(@NonNull String selection, @Nullable String[] selectionArgs) {
            loaderBundle.putString(LOADER_SELECTION, selection);
            if (selectionArgs != null) loaderBundle.putStringArray(LOADER_SELECT_ARGS, selectionArgs);
            return this;
        }

        /**
         * the projection to query. if null, queries all columns. generally, this should be narrowed down
         * to whatever the feed needs in order to improve load times.
         * you must *always* include [Model].LOCAL_ID or the feed won't be able to load.
         * @param projection an array of column names in the database. the names correspond to static fields on models, so if you want a {@link com.peck.android.models.Comment}'s
         *                   text, include Column.TEXT. can include indices built on demand. e.g. 'DBOperable.LOCAL_ID - DBOperable.SV_ID as TEST' will result in the inclusion
         *                   of a TEST column for the feed.
         */
        public Builder withProjection (String[] projection) {
            loaderBundle.putStringArray(LOADER_PROJECTION, projection);
            return this;
        }

        /**
         * order the feed by the specified column. '[column] asc' sorts ascending, '[column] desc' sorts descending.
         * @param ordering the column to order by
         */
        public Builder orderedBy(@NonNull String ordering) {
            loaderBundle.putString(LOADER_SORT_ORDER, ordering);
            return this;
        }

        /**
         * the layout to use to build
         * @param layout the {@link android.support.annotation.LayoutRes} to use to lay out the cells
         */
        public Builder layout(@LayoutRes int layout) {
            feedBundle.putInt(LAYOUT_RES, layout);
            return this;
        }

        /**
         * the field/id bindings to use. bindings *must* be the same length or the feed will break.
         * @param bindsFrom the column names to bind from. defines the column name for the {@link android.support.v4.widget.SimpleCursorAdapter.ViewBinder}
         *                  to get and hand back to the bindView method. not necessary that these fields be meaningful/relevant.
         * @param bindsTo the ids to bind to. the {@link android.support.v4.widget.SimpleCursorAdapter.ViewBinder} will iterate through these views and try to set them.
         */
        public Builder withBindings(@NonNull String[] bindsFrom, @NonNull @IdRes int[] bindsTo) {
            feedBundle.putStringArray(BINDS_FROM, bindsFrom);
            feedBundle.putIntArray(BINDS_TO, bindsTo);
            return this;
        }

        /**
         * don't include dividers in the list
         */
        public Builder withoutDividers() {
            feedBundle.putBoolean(DIVIDERS, false);
            return this;
        }

        /**
         * set a {@link com.peck.android.fragments.Feed.RecycleRunnable} to execute every time a view is loaded.
         * @param runnable the runnable
         */
        public Builder withRecycleRunnable(@NonNull RecycleRunnable runnable) {
            this.runnable = runnable;
            return this;
        }

        /**
         * the {@link android.support.v4.widget.SimpleCursorAdapter.ViewBinder} that handles view configuration based on cursor row
         * @param viewBinder the viewbinder to set
         */
        public Builder withViewBinder(@NonNull SimpleCursorAdapter.ViewBinder viewBinder) {
            this.viewBinder = viewBinder;
            return this;
        }

        /**
         * an {@link android.widget.AdapterView.OnItemClickListener} to handle clicks on list items
         * @param listener the listener
         */
        public Builder setOnItemClickListener(@NonNull AdapterView.OnItemClickListener listener) {
            this.listener = listener;
            return this;
        }

        /**
         * adds a list of headers for the feed in order
         * @param header the header views
         */
        public Builder withHeader(@NonNull View... header) {
            this.header = header;
            return this;
        }

        /**
         * adds a list of footers for the feed in order
         * @param footer the header views
         */
        public Builder withFooter(@NonNull View... footer) {
            this.footer = footer;
            return this;
        }

        /**
         * build a feed from the given arguments
         * @return the feed
         */
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

    /**
     * add a header view to the feed.
     * @param header the header to add.
     */
    public void addHeader(View header) {
        headers.add(header);
    }

    /**
     * add a footer view to the feed.
     * @param footer the header to add.
     */
    public void addFooter(View footer) {
        footers.add(footer);
    }

    /**
     * set a runnable to execute on every listview item load. it will have the view passed to it as an argument.
     * runs after view is generated.
     * @param runnable the runnable
     */
    public void setRunnable(RecycleRunnable runnable) { this.runnable = runnable; }

    /**
     * bind the fragment to the actual view.
     * constructs the {@link android.support.v4.widget.CursorAdapter} and assigns the {@link com.peck.android.fragments.Feed.RecycleRunnable} to it, if it exists.
     * assigns the {@link android.support.v4.widget.SimpleCursorAdapter.ViewBinder} and {@link android.widget.AdapterView.OnItemClickListener}
     * initializes the {@link android.support.v4.content.CursorLoader}
     * @param adapterView the view to bind to
     */
    public void bindToAdapterView(AdapterView adapterView) {
        mAdapter = new SimpleCursorAdapter(getActivity(), listItemRes, null, (binds_from == null) ? new String[] {} : binds_from, (binds_to == null) ? new int[] {} : binds_to, 0) {
            public boolean wasEmpty = false;
            @Override
            public void notifyDataSetChanged() {
                super.notifyDataSetChanged();
                updateVisibility();
            }

            private void updateVisibility() {
                /*View mView = Feed.this.getView();
                if (mView != null) {
                    if (isEmpty()) {
                            wasEmpty = true;
                            mView.findViewById(getListViewRes()).setVisibility(View.GONE);
                            mView.findViewById(R.id.tv_nothing_here).setVisibility(View.VISIBLE);
                    } else if (wasEmpty) {
                        wasEmpty = false;
                        mView.findViewById(getListViewRes()).setVisibility(View.GONE);
                        mView.findViewById(R.id.tv_nothing_here).setVisibility(View.VISIBLE);
                    }
                }*/
            }

            @Override
            public void notifyDataSetInvalidated() {
                super.notifyDataSetInvalidated();
                updateVisibility();
            }

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

        getLoaderManager().initLoader(LOADER_ID, loaderBundle, this);
    }

    /**
     * {@link android.support.v4.content.CursorLoader} callback
     */
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        if (i == LOADER_ID) return new CursorLoader(getActivity(), (Uri)bundle.getParcelable(LOADER_URI), bundle.getStringArray(LOADER_PROJECTION),
                bundle.getString(LOADER_SELECTION), bundle.getStringArray(LOADER_SELECT_ARGS), bundle.getString(LOADER_SORT_ORDER));
        else return null;
    }

    /**
     * {@link android.support.v4.content.CursorLoader} callback
     */
    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mAdapter.changeCursor(cursor);
    }

    /**
     * {@link android.support.v4.content.CursorLoader} callback
     */
    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mAdapter.changeCursor(null);
    }


    /**
     * set the arguments for this feed
     * @param args the bundle of args
     */
    @Override
    @SuppressWarnings("unchecked")
    public void setArguments(Bundle args) {
        super.setArguments(args);

        binds_from = args.getStringArray(BINDS_FROM);
        binds_to = args.getIntArray(BINDS_TO);

        dividers = args.getBoolean(DIVIDERS, true);

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
        mAdapterView = ((AdapterView) view.findViewById(listViewRes));
        if (mAdapterView instanceof ListView) {
            for (View header : headers) ((ListView) mAdapterView).addHeaderView(header);
            for (View footer : footers) ((ListView) mAdapterView).addFooterView(footer);
            if (!dividers) {
                ((ListView) mAdapterView).setDividerHeight(0);
                ((ListView) mAdapterView).setDivider(null);
            }
        }

        bindToAdapterView(mAdapterView);
        return view;
    }

    public void setSelection(String selection, String[] args) {
        loaderBundle.putString(LOADER_SELECTION, selection);
        loaderBundle.putStringArray(LOADER_SELECT_ARGS, args);
        getLoaderManager().initLoader(LOADER_ID, loaderBundle, this);
    }


    public int getListViewRes() {
        return listViewRes;
    }

    public AdapterView getListView() {
        return mAdapterView;
    }

    public void setViewBinder(SimpleCursorAdapter.ViewBinder binder) {
        this.viewBinder = binder;
        if (mAdapter != null) mAdapter.setViewBinder(viewBinder);
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) { this.listener = listener; }

}