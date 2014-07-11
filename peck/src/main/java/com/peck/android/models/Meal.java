package com.peck.android.models;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by mammothbane on 6/10/2014.
 */
public class Meal extends DBOperable {


    private Date mealtime = new Date(-1);

    private String title = "";
    private int location = -1;


    private ArrayList<Food> courses;

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


}
