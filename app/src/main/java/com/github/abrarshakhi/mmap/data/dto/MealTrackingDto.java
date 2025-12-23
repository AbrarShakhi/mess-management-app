package com.github.abrarshakhi.mmap.data.dto;

public class MealTrackingDto {

    public String mealId;
    public String messId;
    public String userId;

    public int month;
    public int year;
    public int day;            // 1–31

    public String mealTime;    // LUNCH, DINNER
    public int mealCount;      // usually 1, but flexible

    public long timestamp;

    public MealTrackingDto() {
    }

    public MealTrackingDto(long timestamp, int mealCount, String mealTime, int day, int year, int month, String userId, String messId, String mealId) {
        this.timestamp = timestamp;
        this.mealCount = mealCount;
        this.mealTime = mealTime;
        this.day = day;
        this.year = year;
        this.month = month;
        this.userId = userId;
        this.messId = messId;
        this.mealId = mealId;
    }
}
