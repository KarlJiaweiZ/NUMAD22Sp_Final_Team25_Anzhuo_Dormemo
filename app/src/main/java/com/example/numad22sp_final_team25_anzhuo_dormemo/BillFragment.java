package com.example.numad22sp_final_team25_anzhuo_dormemo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.numad22sp_final_team25_anzhuo_dormemo.bill.BillCard;
import com.example.numad22sp_final_team25_anzhuo_dormemo.bill.BillCardClickListener;
import com.example.numad22sp_final_team25_anzhuo_dormemo.bill.BillRviewAdapter;
import com.example.numad22sp_final_team25_anzhuo_dormemo.bill.FetchData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;

public class BillFragment extends Fragment {
    //View component
    private View billFragView;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private BillRviewAdapter adapter;
    private ArrayList<BillCard> cardList = new ArrayList<>();

    private FloatingActionButton addBillButton;

    //firebase component
    private String currentUserID, currentUserName, currentDormName, currentDate, currentTime;

    private FirebaseUser currentUser;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference usersRef, dormRef;

    FetchData fd;
    boolean[] checkedRoommates;
    ArrayList<Integer> selectedRoommatesIndex;
    int ir;

    public BillFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        billFragView = inflater.inflate(R.layout.fragment_bill, container, false);

        //firebase components initiate
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
            startActivity(loginIntent);
        }
        currentUserID = currentUser.getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        dormRef = FirebaseDatabase.getInstance().getReference().child("Dorms");
        currentDormName = MainActivity.dormName;
        getUserInfo();

        //part1. initiate the field
        init(savedInstanceState);

        //part2. set up add button
        addBillButton = billFragView.findViewById(R.id.add_bill_button);
        addBillButton.setOnClickListener(view -> createDialog());

        //retrieve the db and get all roommates
        ir = 0;
        fd = new FetchData();
        checkedRoommates = new boolean[100];
        selectedRoommatesIndex = new ArrayList<>();

        //display bills from firebase
        dormRef.child(currentDormName).child("bills").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    cardList.clear();
                    adapter.notifyDataSetChanged();
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        String payer = dataSnapshot.child("payer").getValue(String.class);
                        String payee = dataSnapshot.child("payee").getValue(String.class);
                        String amount = dataSnapshot.child("amount").getValue(String.class);
                        String desc = dataSnapshot.child("desc").getValue(String.class);
                        String uid = dataSnapshot.child("uid").getValue(String.class);
                        boolean isChecked = dataSnapshot.child("isChecked").getValue(boolean.class);
                        if(isChecked)
                            addBillLocal(cardList.size(), payer, amount, payee, desc, uid, isChecked);
                        else
                            addBillLocal(0, payer, amount, payee, desc, uid, isChecked);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //part3. touch helper (minor task)
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getLayoutPosition();
                BillCard card = adapter.getItem(position);
                dormRef.child("bills").child(card.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(billFragView.getContext(), "Bill Deleted", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(billFragView.getContext(), "Remove Failed", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
                cardList.remove(position);
                adapter.notifyDataSetChanged();
//                Snackbar.make(billFragView.findViewById(R.id.bill_recycler_view), "Bill Deleted", Snackbar.LENGTH_LONG)
//                        .setAction("Undo", new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                cardList.add(position, card);
//                                adapter.notifyItemChanged(position);
//                            }
//                        }).show();
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);


        return billFragView;

    }

    private void getUserInfo() {
        //get current user name
        usersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    currentUserName = Objects.requireNonNull(snapshot.child("Username").getValue()).toString();
                    adapter.setCurrentUserName(currentUserName);
                    //currentDormName = Objects.requireNonNull(snapshot.child("DormName").getValue()).toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //get leader name
        dormRef.child(currentDormName).child("Members").child("Leader").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                     fd.addRoommates(dataSnapshot.getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //get other members name
        dormRef.child(currentDormName).child("Members").child("OtherMembers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    fd.addRoommates(dataSnapshot.getValue().toString());                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void init(Bundle savedInstanceState) {
        //initialItemData(savedInstanceState);
        createRview();
    }

    private void createRview() {
        recyclerView = billFragView.findViewById(R.id.bill_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(billFragView.getContext());
        adapter = new BillRviewAdapter(cardList);

        BillCardClickListener billCardClickListener = new BillCardClickListener() {
//            @Override
//            public String onBillCardClick(int position) {
//                return null;
//            }

            @Override
            public void onCheckBoxClick(int position) {
                BillCard billCard = cardList.get(position);
                billCard.onCheckBoxClick(position);
                cardList.remove(billCard);
                cardList.add(cardList.size(), billCard);
                adapter.notifyDataSetChanged();
                String billId = billCard.getUid();
                dormRef.child(currentDormName).child("bills").child(billId).child("isChecked").setValue(billCard.isChecked());
            }
        };

        adapter.setOnBillCardClickListener(billCardClickListener);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
    }

    //dialog to create bill card
    private void createDialog() {
        Log.d("roommates", Arrays.toString(fd.getAllRoommates()));
        View view = LayoutInflater.from(this.getContext()).inflate(R.layout.add_bill_dialog, null, false);
        EditText enterBillAmount = view.findViewById(R.id.enter_bill_amount);
        EditText enterBillDesc = view.findViewById(R.id.enter_bill_desc);
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext()).setTitle("Select Payer")
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
                .setCancelable(false)
                .setMultiChoiceItems(fd.getAllRoommates(), checkedRoommates, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                        if(isChecked){
                            if(!selectedRoommatesIndex.contains(position)){
                                selectedRoommatesIndex.add(position);
                            }else{
                                selectedRoommatesIndex.remove(position);
                            }
                        }
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();

        //to prevent dialog from dismiss we need to override the positive button outside the builder
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String amount = enterBillAmount.getText().toString();
                String desc = enterBillDesc.getText().toString();
                if (amount.isEmpty())
                    Toast.makeText(getContext(), "must enter bill amount", Toast.LENGTH_SHORT).show();
                else if (desc.isEmpty())
                    Toast.makeText(getContext(), "must enter description", Toast.LENGTH_SHORT).show();
                else {
                    String rm = "";
                    for(int i = 0; i < selectedRoommatesIndex.size(); i++){
                        rm += fd.getAllRoommates()[selectedRoommatesIndex.get(i)];
                        if(i != selectedRoommatesIndex.size()-1)
                            rm += ", ";
                    }
                    selectedRoommatesIndex.clear();
                    addBillToDB(amount, rm, desc);
                    dialog.dismiss();
                }
            }
        });
    }

    //upload this bill to firebase, under the specified dorm
    private void addBillToDB(String amount, String payee, String desc) {
        DatabaseReference dormBillRef = dormRef.child(currentDormName).child("bills");
        String billKey = dormBillRef.push().getKey();
        DatabaseReference dormBillKeyRef = dormBillRef.child(billKey);
        HashMap<String, Object> billInfo = new HashMap<>();
        billInfo.put("payer", adapter.getCurrentUserName());
        billInfo.put("payee", payee);
        billInfo.put("amount", amount);
        billInfo.put("desc", desc);
        billInfo.put("isChecked", false);
        billInfo.put("uid", billKey);
        dormBillKeyRef.updateChildren(billInfo);
    }

    private void addBillLocal(int position, String payer, String amount, String payee, String desc, String uid, boolean isChecked){
        BillCard billCard = new BillCard("Payer: " + payer, payee, "Desc: " + desc, "$"+amount, uid,isChecked);
        cardList.add(position, billCard);
        adapter.notifyItemChanged(position);
    }

}