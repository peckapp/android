package com.peck.android.models.postItems;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.view.View;

import com.peck.android.interfaces.DBOperable;

/**
 * Created by mammothbane on 6/17/2014.
 */
public class PhotoPost extends Post<Bitmap> {


    @Override
    public ContentValues toContentValues() {
        return null;
    }

    @Override
    public MessagePost fromCursor(Cursor cursor) {
        return null;
    }

    @Override
    public void setUpPost(View v) {

    }
}
