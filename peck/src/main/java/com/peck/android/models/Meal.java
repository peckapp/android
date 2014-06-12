package com.peck.android.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.view.View;

import com.peck.android.R;
import com.peck.android.database.helper.MealOpenHelper;
import com.peck.android.interfaces.DBOperable;
import com.peck.android.interfaces.HasFeedLayout;
import com.peck.android.interfaces.SelfSetup;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by mammothbane on 6/10/2014.
 */
public class Meal extends DBOperable implements SelfSetup, HasFeedLayout {
    private int localId;
    private int serverId;

    private int color;
    private int type;

    private Date mealtime;
    private Date updated;

    public Date getUpdated() {
        return updated;
    }

    public Meal setUpdated(Date updated) {
        this.updated = updated;
        return this;
    }

    private String title;

    private int location;

    public int getColor() {
        return color;
    }

    public int getType() {
        return type;
    }

    public Meal setType(int type) {
        this.type = type;
        return this;
    }

    public Meal setColor(int color) {
        this.color = color;
        return this;
    }

    private ArrayList<Food> courses;

    public int getLocalId() {
        return localId;
    }

    public Meal setLocalId(int id) {
        this.localId = id;
        return this;
    }

    public int getServerId() {
        return serverId;
    }

    public Meal setServerId(int serverId) {
        this.serverId = serverId;
        return this;
    }

    public Date getMealtime() {
        return mealtime;
    }

    public Meal setMealtime(Date mealtime) {
        this.mealtime = mealtime;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Meal setTitle(String title) {
        this.title = title;
        return this;
    }

    public int getLocation() {
        return location;
    }

    public Meal setLocation(int location) {
        this.location = location;
        return this;
    }

    public ArrayList<Food> getCourses() {
        return courses;
    }

    public Meal setCourses(ArrayList<Food> courses) {
        this.courses = courses;
        return this;
    }

    public int hashCode() {
        return (int)(mealtime.getTime()*13+location*17+getServerId()*307-getLocalId());
    }

    @Override
    public int getResourceId() { //TODO: implement, create layout
        return R.layout.frag_meal;
    }

    @Override
    public void setUp(View v) { //TODO: set up a layout that's passed in with the correct information


    }

    public Meal link(Food food) {
        courses.add(food);
        food.setMealId(getLocalId());
        return this;
    }

    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(MealOpenHelper.COLUMN_SERVER_ID, getServerId());
        cv.put(MealOpenHelper.COLUMN_LOC_ID, getLocalId());
        cv.put(MealOpenHelper.COLUMN_COLOR, getColor());
        cv.put(MealOpenHelper.COLUMN_MEAL_TYPE, getType());
        cv.put(MealOpenHelper.COLUMN_TITLE, getTitle());
        cv.put(MealOpenHelper.COLUMN_TIME, getMealtime().getTime());
        cv.put(MealOpenHelper.COLUMN_UPDATED, getUpdated().getTime());
        cv.put(MealOpenHelper.COLUMN_LOCATION_ID, getLocation());

        return cv;

    }

    @Override
    public Meal fromCursor(Cursor cursor) {
        return this.setServerId(cursor.getInt(cursor.getColumnIndex(MealOpenHelper.COLUMN_SERVER_ID)))
                .setType(cursor.getInt(cursor.getColumnIndex(MealOpenHelper.COLUMN_MEAL_TYPE)))
                .setColor(cursor.getInt(cursor.getColumnIndex(MealOpenHelper.COLUMN_COLOR)))
                .setServerId(cursor.getInt(cursor.getColumnIndex(MealOpenHelper.COLUMN_SERVER_ID)))
                .setMealtime(new Date(cursor.getInt(cursor.getColumnIndex(MealOpenHelper.COLUMN_TIME))))
                .setUpdated(new Date(cursor.getInt(cursor.getColumnIndex(MealOpenHelper.COLUMN_UPDATED))))
                .setLocalId(cursor.getInt(cursor.getColumnIndex(MealOpenHelper.COLUMN_LOC_ID)))
                .setLocation(cursor.getInt(cursor.getColumnIndex(MealOpenHelper.COLUMN_LOCATION_ID)))
                .setTitle(cursor.getString(cursor.getColumnIndex(MealOpenHelper.COLUMN_TITLE)));
    }
}
