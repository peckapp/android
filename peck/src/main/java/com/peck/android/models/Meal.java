package com.peck.android.models;

import android.view.View;
import android.widget.TextView;

import com.peck.android.R;
import com.peck.android.interfaces.HasFeedLayout;
import com.peck.android.interfaces.SelfSetup;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by mammothbane on 6/10/2014.
 */
public class Meal extends DBOperable implements SelfSetup, HasFeedLayout {


    private Date mealtime = new Date(-1);

    private String title = "";
    private int location = -1;


    private ArrayList<Food> courses;

    public Date getMealtime() {
        return mealtime;
    }

    public Meal setMealtime(Date mealtime) {
        this.mealtime = mealtime;
        updated();
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Meal setTitle(String title) {
        this.title = title;
        updated();
        return this;
    }

    public int getLocation() {
        return location;
    }

    public Meal setLocation(int location) {
        this.location = location;
        updated();
        return this;
    }

    public ArrayList<Food> getCourses() {
        return courses;
    }

    public Meal setCourses(ArrayList<Food> courses) {
        this.courses = courses;
        updated();
        return this;
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


}
