package com.example.numad22sp_final_team25_anzhuo_dormemo.bill;

//This class represents the object of each bill card in recycler view, where the data stored.
public class BillCard implements BillCardClickListener{
    private final String billName;
    private final String billPayee;
    private final String billDesc;
    private final String billFee;
    private boolean isChecked;
    private final String uid;

    public BillCard(String billName, String billPayee, String billDesc, String billFee, String uid, boolean isChecked) {
        this.billName = billName;
        this.billPayee = billPayee;
        this.billDesc = billDesc;
        this.billFee = billFee;
        this.uid = uid;
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
        return billFee + (isChecked?"(Payed)":"");
    }

    public boolean isChecked() {
        return isChecked;
    }

    public String getUid() {
        return uid;
    }

    //    @Override
//    public String onBillCardClick(int position) {
//        return null;
//    }

    @Override
    public void onCheckBoxClick(int position) {
        isChecked = !isChecked;
    }
}
