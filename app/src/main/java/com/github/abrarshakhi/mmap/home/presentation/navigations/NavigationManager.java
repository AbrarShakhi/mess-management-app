package com.github.abrarshakhi.mmap.home.presentation.navigations;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.github.abrarshakhi.mmap.home.presentation.fragments.GroceryFragment;
import com.github.abrarshakhi.mmap.home.presentation.fragments.HomeFragment;
import com.github.abrarshakhi.mmap.home.presentation.fragments.ManagementFragment;
import com.github.abrarshakhi.mmap.home.presentation.fragments.MealsFragment;
import com.github.abrarshakhi.mmap.home.presentation.fragments.ProfileFragment;

import org.jetbrains.annotations.Contract;

import java.util.HashMap;
import java.util.Map;

public class NavigationManager {

    private final FragmentManager fragmentManager;
    private final int containerId;
    private final ActionBar actionBar;

    private final Map<NavDestination, Fragment> fragmentCache = new HashMap<>();

    public NavigationManager(FragmentManager fragmentManager, int containerId, ActionBar actionBar) {
        this.fragmentManager = fragmentManager;
        this.containerId = containerId;
        this.actionBar = actionBar;
    }

    public void navigate(NavDestination destination) {
        Fragment fragment = getFragment(destination);

        if (actionBar != null) {
            actionBar.setTitle(destination.getTitle());
        }

        fragmentManager
            .beginTransaction()
            .replace(containerId, fragment)
            .commit();
    }


    private Fragment getFragment(NavDestination destination) {
        if (fragmentCache.containsKey(destination)) {
            return fragmentCache.get(destination);
        }

        Fragment fragment = createFragment(destination);
        fragmentCache.put(destination, fragment);
        return fragment;
    }

    @NonNull
    @Contract("_ -> new")
    private Fragment createFragment(NavDestination destination) {
        switch (destination) {
            case HOME:
                return HomeFragment.newInstance();
            case MEALS:
                return MealsFragment.newInstance();
            case GROCERY:
                return GroceryFragment.newInstance();
            case MANAGEMENT:
                return ManagementFragment.newInstance();
            case PROFILE:
                return ProfileFragment.newInstance();
            default:
                throw new IllegalArgumentException("Unknown destination");
        }
    }
}
