package com.github.abrarshakhi.mmap.presentation.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.abrarshakhi.mmap.R;
import com.github.abrarshakhi.mmap.data.dto.PaymentDto;

import java.util.List;

public class PaymentHistoryAdapter extends ArrayAdapter<PaymentDto> {

    private final LayoutInflater inflater;
    private OnPaymentClickListener listener;

    public PaymentHistoryAdapter(@NonNull Context context, @NonNull List<PaymentDto> objects, OnPaymentClickListener listener) {
        super(context, R.layout.payment_history, objects);
        this.inflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    public void setListener(OnPaymentClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.payment_history, parent, false);
            holder = new ViewHolder();
            holder.tvPaymentType = convertView.findViewById(R.id.tvPaymentType);
            holder.tvPaymentAmount = convertView.findViewById(R.id.tvPaymentAmount);
            holder.tvPaymentDate = convertView.findViewById(R.id.tvPaymentDate);
            holder.tvPaymentNote = convertView.findViewById(R.id.tvPaymentNote);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        PaymentDto payment = getItem(position);
        if (payment != null) {
            holder.tvPaymentType.setText(payment.type);
            holder.tvPaymentAmount.setText("BDT " + payment.amount);
            holder.tvPaymentDate.setText(payment.month + "/" + payment.year);
            holder.tvPaymentNote.setText(payment.note != null ? payment.note : "");

            // Item click listener
            convertView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onPaymentClick(payment);
                }
            });
        }

        return convertView;
    }

    public interface OnPaymentClickListener {
        void onPaymentClick(PaymentDto payment);
    }

    static class ViewHolder {
        TextView tvPaymentType;
        TextView tvPaymentAmount;
        TextView tvPaymentDate;
        TextView tvPaymentNote;
    }
}
