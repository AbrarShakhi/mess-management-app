package com.github.abrarshakhi.mmap.domain.model;

import java.util.List;

public class MealTracking {
    public int month;
    public int year;
    public int day;
    public List<IndividualMeal> individualMealList;
    public int mealCount;
    public String mealTime;

    public MealTracking(String mealTime, int mealCount, List<IndividualMeal> individualMealList, int day, int year, int month) {
        this.mealTime = mealTime;
        this.mealCount = mealCount;
        this.individualMealList = individualMealList;
        this.day = day;
        this.year = year;
        this.month = month;
    }

    public static class IndividualMeal {
        public MessMember member;
        public int mealCount;
        public String mealTime;

        public IndividualMeal(MessMember member, int mealCount, String mealTime) {
            this.member = member;
            this.mealCount = mealCount;
            this.mealTime = mealTime;
        }
    }
}
