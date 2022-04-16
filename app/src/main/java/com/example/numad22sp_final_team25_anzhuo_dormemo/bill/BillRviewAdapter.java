package com.example.numad22sp_final_team25_anzhuo_dormemo.bill;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.numad22sp_final_team25_anzhuo_dormemo.R;

import java.util.ArrayList;

public class BillRviewAdapter extends RecyclerView.Adapter<BillRviewHolder>{
    private ArrayList<BillCard> cardList;
    private BillCardClickListener listener;

    public BillRviewAdapter(ArrayList<BillCard> cardList) {
        this.cardList = cardList;
    }

    public void setOnBillCardClickListener(BillCardClickListener listener){
        this.listener = listener;
    }

    //This method will be called by the RecyclerView to obtain a ViewHolder object. It inflates the view hierarchy
    //xml file and creates an instance of our ViewHolder class initialized with the view hierarchy before
    //returning it to the RecyclerView
    @NonNull
    @Override
    public BillRviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bill_card, parent, false);
        return new BillRviewHolder(view, listener);
    }

    //The purpose of this method is to populate the view hierarchy within the ViewHolder object
    //with the data to be displayed. It is passed the ViewHolder object and an integer value indicating the list item that
    //is to be displayed. This method should now be added, using the item number as an index into the data arrays.
    //This data is then displayed on the layout views using the references created in the constructor method of the
    //ViewHolder class
    @Override
    public void onBindViewHolder(@NonNull BillRviewHolder holder, int position) {
        BillCard currentCard = cardList.get(position);
        holder.billName.setText(currentCard.getBillName());
        holder.billPayee.setText(currentCard.getBillPayee());
        holder.billDesc.setText(currentCard.getBillDesc());
        holder.billAmount.setText(currentCard.getBillFee());
        holder.checkBox.setChecked(currentCard.isChecked());
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }
}