package com.example.mehedi.qrcode;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by mehedi on 11/29/17.
 */

public class SMSAdapter extends RecyclerView.Adapter<SMSAdapter.CardViewHolder>{

    List<SMS> messages;
    OTPListener itemClickListener;

    public SMSAdapter(Context context, List<SMS> messages) {
        itemClickListener = (OTPListener) context;
        this.messages = messages;
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sms_item, parent, false);

        return new CardViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        holder.bind(messages.get(position), position+1, itemClickListener);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class CardViewHolder extends RecyclerView.ViewHolder {

        TextView serial;
        TextView name;
        TextView date;
        TextView message_body;

        public CardViewHolder(View itemView) {
            super(itemView);
            serial = (TextView) itemView.findViewById(R.id.serial);
            name = (TextView) itemView.findViewById(R.id.name);
            date = (TextView) itemView.findViewById(R.id.date);
            message_body = (TextView) itemView.findViewById(R.id.message_body);
        }

        public void bind(final SMS sms, int number, final OTPListener itemClickListener) {

            serial.setText("" + number);
            name.setText(sms.get_address());
            date.setText(sms.get_time());
            message_body.setText(sms.get_msg());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.itemClick(sms);
                }
            });

        }
    }
}
