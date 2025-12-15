package com.github.abrarshakhi.mmap.core.constants;

import java.util.Arrays;
import java.util.List;

public class Currencies {
    private final static List<String> currencies = Arrays.asList(
        "USD", "EUR", "INR", "BDT"
    );
    public static List<String> asList() {
        return currencies;
    }
}
