package com.example.numad22sp_final_team25_anzhuo_dormemo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.numad22sp_final_team25_anzhuo_dormemo.bill.BillCard;
import com.example.numad22sp_final_team25_anzhuo_dormemo.bill.BillCardClickListener;
import com.example.numad22sp_final_team25_anzhuo_dormemo.bill.BillRviewAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class BillFragment extends Fragment {
    //View component
    private View billFragView;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private BillRviewAdapter adapter;
    private ArrayList<BillCard> cardList = new ArrayList<>();

    private FloatingActionButton addBillButton;

    private static final String KEY_OF_INSTANCE = "KEY_OF_INSTANCE";
    private static final String NUMBER_OF_ITEMS = "NUMBER_OF_ITEMS";

    //firebase component
    private String currentUserID, currentUserName, currentDormName, currentDate, currentTime;

    private FirebaseUser currentUser;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference usersRef, dormRef, groupMessageKeyRef;

    public BillFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        billFragView = inflater.inflate(R.layout.fragment_bill, container, false);

        //part1. initiate the field
        init(savedInstanceState);

        //part2. set up add button
        addBillButton = billFragView.findViewById(R.id.add_bill_button);
        addBillButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createDialog();
            }
        });

        //part3. touch helper (minor task)
//        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
//            @Override
//            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
//                return false;
//            }
//
//            @Override
//            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
//                int position = viewHolder.getLayoutPosition();
//                BillCard card = adapter.getItem(position);
//                cardList.remove(position);
//                adapter.notifyItemChanged(position);
//                Snackbar.make(billFragView.findViewById(R.id.bill_recycler_view), "Bill Deleted", Snackbar.LENGTH_LONG)
//                        .setAction("Undo", new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                cardList.add(position, card);
//                                adapter.notifyItemChanged(position);
//                            }
//                        }).show();
//            }
//        });
//        itemTouchHelper.attachToRecyclerView(recyclerView);
        return billFragView;

    }

    private void init(Bundle savedInstanceState){
        //initialItemData(savedInstanceState);
        createRview();
    }



//    private void initialItemData(Bundle savedInstanceState) {
//
//        // Not the first time to open this Activity, Extract data from saved instance state
//        if (savedInstanceState != null && savedInstanceState.containsKey(NUMBER_OF_ITEMS)) {
//            if (cardList == null || cardList.size() == 0) {
//
//                int size = savedInstanceState.getInt(NUMBER_OF_ITEMS);
//
//                // Retrieve keys we stored in the instance
//                for (int i = 0; i < size; i++) {
//                    Integer imgId = savedInstanceState.getInt(KEY_OF_INSTANCE + i + "0");
//                    String itemName = savedInstanceState.getString(KEY_OF_INSTANCE + i + "1");
//                    String itemDesc = savedInstanceState.getString(KEY_OF_INSTANCE + i + "2");
//                    boolean isChecked = savedInstanceState.getBoolean(KEY_OF_INSTANCE + i + "3");
//
//                    // We need to make sure names such as "XXX(checked)" will not duplicate
//                    // Use a tricky way to solve this problem, not the best though
//                    if (isChecked) {
//                        itemName = itemName.substring(0, itemName.lastIndexOf("("));
//                    }
//                    BillCard billCard = new BillCard(imgId, itemName, itemDesc, isChecked);
//
//                    cardList.add(billCard);
//                }
//            }
//        }
//
//    }
    private void createRview(){
        recyclerView = billFragView.findViewById(R.id.bill_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(billFragView.getContext());
        adapter = new BillRviewAdapter(cardList);


        BillCardClickListener billCardClickListener = new BillCardClickListener() {
            @Override
            public String onBillCardClick(int position) {
                //TODO: set on bill card click operation
                return null;
            }

            @Override
            public void onCheckBoxClick(int position) {
                //TODO: implement click gray
                cardList.get(position).onCheckBoxClick(position);
                adapter.notifyItemChanged(position);
            }
        };

        adapter.setOnBillCardClickListener(billCardClickListener);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
    }

    //dialog to create bill card
    private void createDialog(){
        View view = LayoutInflater.from(this.getContext()).inflate(R.layout.add_bill_dialog, null, false);
        EditText enterBillName = view.findViewById(R.id.enter_bill_name);
        EditText enterBillAmount = view.findViewById(R.id.enter_bill_amount);
        EditText enterBillDesc = view.findViewById(R.id.enter_bill_desc);
        //TODO: payee to be implemented
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext()).setTitle("Add a New Bill")
                .setView(view)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setCancelable(true);

        AlertDialog dialog = builder.create();
        dialog.show();
        //to prevent dialog from dismiss we need to override the positive button outside the builder
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = enterBillName.getText().toString();
                String amount = enterBillAmount.getText().toString();
                String desc = enterBillDesc.getText().toString();
                if(name.isEmpty())
                    Toast.makeText(getContext(), "must enter a name", Toast.LENGTH_LONG).show();
                if(amount.isEmpty())
                    Toast.makeText(getContext(), "must enter an amount", Toast.LENGTH_LONG).show();
                if(desc.isEmpty())
                    Toast.makeText(getContext(), "must enter a description", Toast.LENGTH_LONG).show();


                if(!name.isEmpty() && !amount.isEmpty() && !desc.isEmpty()){
                    addBill(0, name, amount, desc);
                    dialog.dismiss();
                }



            }
        });
    }

    void addBill(int position, String name, String amount, String desc){
        cardList.add(position, new BillCard(name, "payee", desc, amount, false));
        adapter.notifyItemChanged(position);
    }

}