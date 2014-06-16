package com.peck.android.fragments.tabs;

import com.peck.android.fragments.Feed;
import com.peck.android.interfaces.DBOperable;
import com.peck.android.interfaces.HasFeedLayout;
import com.peck.android.interfaces.SelfSetup;

/**
 * Created by mammothbane on 6/13/2014.
 */
public abstract class FeedTab<T extends DBOperable & HasFeedLayout & SelfSetup> extends Feed<T> implements BaseTab {


}
