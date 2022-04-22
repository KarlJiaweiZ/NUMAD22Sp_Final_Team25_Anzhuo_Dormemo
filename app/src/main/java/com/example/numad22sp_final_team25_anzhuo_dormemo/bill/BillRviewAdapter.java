package com.example.numad22sp_final_team25_anzhuo_dormemo.bill;

import android.graphics.Color;
import android.util.Log;
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
    private String currentUserName;

    public BillRviewAdapter(ArrayList<BillCard> cardList) {
        this.cardList = cardList;
    }

    public void setOnBillCardClickListener(BillCardClickListener listener){
        this.listener = listener;
    }

    public void setCurrentUserName(String name){
        currentUserName = name;
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
        Log.d("holder user name: ", holder.billName.getText().toString());
        Log.d("current user name: ", "Payer: "+currentUserName);
        //only payer can set bill as settled
        if(holder.billName.getText().toString().equals("Payer: "+currentUserName)){
            holder.checkBox.setEnabled(true);
        }
        holder.checkBox.setChecked(currentCard.isChecked());
        if(currentCard.isChecked()){
            holder.relativeLayout.setBackgroundColor(Color.LTGRAY);
            holder.billName.setTextColor(Color.WHITE);
            holder.billPayee.setTextColor(Color.WHITE);
            holder.billDesc.setTextColor(Color.WHITE);
            holder.billAmount.setTextColor(Color.WHITE);
        }else{
            holder.relativeLayout.setBackgroundColor(Color.WHITE);
            holder.billName.setTextColor(Color.GRAY);
            holder.billPayee.setTextColor(Color.GRAY);
            holder.billDesc.setTextColor(Color.GRAY);
            holder.billAmount.setTextColor(Color.GRAY);
        }

    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    public BillCard getItem(int position){
        return cardList.get(position);
    }
}
