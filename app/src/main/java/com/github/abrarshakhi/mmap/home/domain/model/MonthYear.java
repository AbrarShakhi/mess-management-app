package com.github.abrarshakhi.mmap.home.domain.model;

import androidx.annotation.NonNull;

import com.github.abrarshakhi.mmap.core.constants.Months;
import com.github.abrarshakhi.mmap.core.utils.Outcome;

import org.jetbrains.annotations.Contract;

public class MonthYear {
    private final int month;
    private final int year;

    private MonthYear(int month, int year) {
        this.month = month;
        this.year = year;
    }

    public static Outcome<MonthYear, String> newValidInstance(int month, int year) {
        return validateInstance(new MonthYear(month, year));
    }

    public static Outcome<MonthYear, String> validateInstance(MonthYear instance) {
        try {
            instance.toString();
        } catch (IllegalArgumentException e) {
            return Outcome.err("Invalid month or year");
        }
        return Outcome.ok(instance);
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    @NonNull
    @Override
    public String toString() {
        return Months.getMonthName(month) + ", " + year;
    }


    @NonNull
    @Contract(" -> new")
    public MonthYear previous() {
        if (month > 0) {
            return new MonthYear(month - 1, year);
        } else {
            return new MonthYear(11, year - 1);
        }
    }

    @NonNull
    @Contract(" -> new")
    public MonthYear next() {
        if (month < 11) {
            return new MonthYear(month + 1, year);
        } else {
            return new MonthYear(0, year + 1);
        }
    }

}
