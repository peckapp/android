package com.peck.android.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.peck.android.annotations.DBType;
import com.peck.android.annotations.Header;
import com.peck.android.annotations.NoMod;

/**
 * Created by mammothbane on 7/28/2014.
 */

@NoMod
@Header(singular = "menu_item", plural = "menu_items")
public class MenuItem extends DBOperable {
    public static final transient String NAME = "name";
    public static final transient String DETAILS_LINK = "details_link";
    public static final transient String SMALL_PRICE = "small_price";
    public static final transient String LARGE_PRICE = "large_price";
    public static final transient String COMBO_PRICE = "combo_price";
    public static final transient String OPPORTUNITY_ID = "dining_opportunity_id";
    public static final transient String PLACE_ID = "dining_place_id";
    public static final transient String DATE_AVAILABLE = "date_available";
    public static final transient String CATEGORY = "category";
    public static final transient String SERVING_SIZE = "serving_size";

    @Expose
    @SerializedName(NAME)
    String name;

    @Expose
    @SerializedName(DETAILS_LINK)
    String detailsLink;

    @Expose
    @SerializedName(SMALL_PRICE)
    String smallPrice;

    @Expose
    @SerializedName(LARGE_PRICE)
    String largePrice;

    @Expose
    @SerializedName(COMBO_PRICE)
    String comboPrice;

    @Expose
    @DBType("integer")
    @SerializedName(OPPORTUNITY_ID)
    long opportunityId;

    @Expose
    @DBType("integer")
    @SerializedName(PLACE_ID)
    long placeId;

    @Expose
    @DBType("integer")
    @SerializedName(DATE_AVAILABLE)
    String dateAvailable;

    @Expose
    @SerializedName(CATEGORY)
    String category;

    @Expose
    @SerializedName(SERVING_SIZE)
    String servingSize;

}
