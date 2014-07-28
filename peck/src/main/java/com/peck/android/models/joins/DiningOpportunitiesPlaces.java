package com.peck.android.models.joins;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.peck.android.annotations.DBType;
import com.peck.android.annotations.Header;
import com.peck.android.annotations.NoMod;
import com.peck.android.annotations.UriPath;
import com.peck.android.models.DBOperable;

/**
 * Created by mammothbane on 7/28/2014.
 */
@NoMod
@Header(plural = "dining_opportunities_dining_places", singular = "dining_opportunity_dining_place")
@UriPath("dining_opportunities_places")
public class DiningOpportunitiesPlaces {
    public static final transient String LOCAL_ID = DBOperable.LOCAL_ID;
    public static final transient String OPPORTUNITY_ID = "dining_opportunity_id";
    public static final transient String PLACE_ID = "dining_place_id";

    @DBType("integer primary key autoincrement")
    @SerializedName(LOCAL_ID)
    long id;

    @Expose
    @DBType("integer")
    @SerializedName(OPPORTUNITY_ID)
    long opportunityId;

    @Expose
    @DBType("integer")
    @SerializedName(PLACE_ID)
    long placeId;

}
