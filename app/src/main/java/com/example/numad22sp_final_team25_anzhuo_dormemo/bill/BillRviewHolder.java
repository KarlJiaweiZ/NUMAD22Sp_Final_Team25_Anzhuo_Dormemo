package com.example.numad22sp_final_team25_anzhuo_dormemo.bill;

import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.example.numad22sp_final_team25_anzhuo_dormemo.R;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BillRviewHolder extends RecyclerView.ViewHolder{
    public TextView billName;
    TextView billPayee;
    TextView billDesc;
    TextView billAmount;
    CheckBox checkBox;
    RelativeLayout relativeLayout;

    public BillRviewHolder(@NonNull View itemView, final BillCardClickListener listener) {
        super(itemView);
        billName = itemView.findViewById(R.id.bill_name);
        billPayee = itemView.findViewById(R.id.bill_payee);
        billDesc = itemView.findViewById(R.id.bill_desc);
        billAmount = itemView.findViewById(R.id.bill_fee);
        checkBox = itemView.findViewById(R.id.bill_checkbox);
        relativeLayout = itemView.findViewById(R.id.bill_relative_layout);

//        itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(listener != null){
//                    int position = getLayoutPosition();
//                    if(position != RecyclerView.NO_POSITION){
//                        listener.onBillCardClick(position);
//                    }
//                }
//            }
//        });

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener != null){
                    int position = getLayoutPosition();
                    if(position != RecyclerView.NO_POSITION){
                        listener.onCheckBoxClick(position);
                    }
                }
            }
        });
    }
}
