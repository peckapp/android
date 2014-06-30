package com.peck.android.models;

import android.view.View;
import android.widget.TextView;

import com.peck.android.R;
import com.peck.android.interfaces.DBOperable;
import com.peck.android.interfaces.HasFeedLayout;
import com.peck.android.interfaces.SelfSetup;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by mammothbane on 6/10/2014.
 */
public class Meal extends DBOperable implements SelfSetup, HasFeedLayout {
    private int serverId = -1;

    private int color = -1;
    private int type = -1;

    private Date mealtime = new Date(-1);
    private Date updated = new Date(-1);

    private String title = "";
    private int location = -1;


    public Date getUpdated() {
        return updated;
    }

    public Meal setUpdated(Date updated) {
        this.updated = updated;
        return this;
    }

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
        return R.layout.lvitem_meal;
    }

    @Override
    public void setUp(View v) {
        ((TextView)v.findViewById(R.id.tv_title)).setText(getTitle());
        ((TextView)v.findViewById(R.id.tv_location)).setText(Integer.toString(getLocation()));

    }

    public Meal link(Food food) {
        courses.add(food);
        food.setMealId(getLocalId());
        return this;
    }

}
