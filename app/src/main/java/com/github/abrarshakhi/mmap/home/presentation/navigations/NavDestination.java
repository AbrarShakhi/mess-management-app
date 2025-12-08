package com.github.abrarshakhi.mmap.home.presentation.navigations;

import com.github.abrarshakhi.mmap.R;

public enum NavDestination {
    HOME("Home"),
    MEALS("Meals Tracking"),
    GROCERY("Grocery List"),
    MANAGEMENT("Mess Management"),
    PROFILE("Profile");

    private final String title;

    NavDestination(String title) {
        this.title = title;
    }

    public static NavDestination fromMenuId(int id) {
        if (id == R.id.bniHome) return NavDestination.HOME;
        if (id == R.id.bniMeals) return NavDestination.MEALS;
        if (id == R.id.bniGrocery) return NavDestination.GROCERY;
        if (id == R.id.bniManagement) return NavDestination.MANAGEMENT;
        if (id == R.id.bniProfile) return NavDestination.PROFILE;
        return NavDestination.HOME;
    }

    public String getTitle() {
        return title;
    }
}