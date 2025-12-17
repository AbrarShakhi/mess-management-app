    package com.github.abrarshakhi.mmap.home.domain.model;


    import java.util.Objects;

    public final class GroceryBatch {
        private final String[] itemNames;
        private final float[] prices;
        private final String[] quantities;
        private long timestamp;
        private String messId;
        private String userId;
        private int month;
        private int year;
        private String []ids;

        public GroceryBatch(String[] itemNames, float[] prices, String[] quantities) {
            this.itemNames = itemNames;
            this.prices = prices;
            this.quantities = quantities;
        }

        public GroceryBatch(String[] ids, String messId, String userId, String[] itemNames, float[] prices, String[] quantities, int month, int year, long timestamp) {
            this(itemNames, prices, quantities);
            this.ids = ids;
            this.messId = messId;
            this.userId = userId;
            this.month = month;
            this.year = year;
            this.timestamp = timestamp;
        }

        // -------- Getters --------

        public String[] getIds() {
            return ids;
        }

        public void setId(String[] ids) {
            this.ids = ids;
        }

        public String getMessId() {
            return messId;
        }

        public void setMessId(String messId) {
            this.messId = messId;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String[] getItemNames() {
            return itemNames;
        }

        public float[] getPrices() {
            return prices;
        }

        public String[] getQuantities() {
            return quantities;
        }

        // -------- Domain logic --------

        public int getMonth() {
            return month;
        }

        // -------- Equality --------

        public void setMonth(int month) {
            this.month = month;
        }

        public int getYear() {
            return year;
        }

        public void setYear(int year) {
            this.year = year;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public boolean isInMonth(int month, int year) {
            return this.month == month && this.year == year;
        }
    }
