package com.github.abrarshakhi.mmap.core.constants;

import java.util.Arrays;
import java.util.List;

public class Months {
    private final static List<String> months = Arrays.asList(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    );

    public static List<String> asList() {
        return months;
    }

    public static String getMonthName(int index) {
        if (index < 0 || index > 11) {
            throw new IllegalArgumentException("Index expected between 0 to 11. Got " + index);
        }
        return months.get(index);
    }
}
