package com.example.numad22sp_final_team25_anzhuo_dormemo.bill;

//This class represents the object of each bill card in recycler view, where the data stored.
public class BillCard implements BillCardClickListener{
    private final String billName;
    private final String billPayee;
    private final String billDesc;
    private final String billFee;
    private boolean isChecked;

    public BillCard(String billName, String billPayee, String billDesc, String billFee, boolean isChecked) {
        this.billName = billName;
        this.billPayee = billPayee;
        this.billDesc = billDesc;
        this.billFee = billFee;
        this.isChecked = isChecked;
    }

    public String getBillName() {
        return billName;
    }

    public String getBillPayee() {
        return billPayee;
    }

    public String getBillDesc() {
        return billDesc;
    }

    public String getBillFee() {
        return billFee;
    }

    public boolean isChecked() {
        return isChecked;
    }

    @Override
    public String onBillCardClick(int position) {
        return null;
    }

    @Override
    public void onCheckBoxClick(int position) {
        isChecked = !isChecked;
    }
}
