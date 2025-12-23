package com.github.abrarshakhi.mmap.data.dto;

public class PaymentDto {

    public String paymentId;
    public String messId;

    public String userId;      // who paid
    public float amount;

    public int month;
    public int year;

    public String type;        // RENT, UTILITY, GROCERY, ADJUSTMENT
    public String note;        // optional

    public long timestamp;

    public PaymentDto() {
    }

    public PaymentDto(String paymentId, String messId, String userId, float amount, int month, int year, String type, String note, long timestamp) {
        this.paymentId = paymentId;
        this.messId = messId;
        this.userId = userId;
        this.amount = amount;
        this.month = month;
        this.year = year;
        this.type = type;
        this.note = note;
        this.timestamp = timestamp;
    }
}