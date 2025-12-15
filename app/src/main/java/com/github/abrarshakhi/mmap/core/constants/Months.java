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
}
