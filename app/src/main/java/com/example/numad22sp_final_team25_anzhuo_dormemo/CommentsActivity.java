package com.example.numad22sp_final_team25_anzhuo_dormemo;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.numad22sp_final_team25_anzhuo_dormemo.board.Comment.CommentViewAdaptor;
import com.example.numad22sp_final_team25_anzhuo_dormemo.board.Comment.Comments;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class CommentsActivity extends AppCompatActivity {
    private RecyclerView commentsRecyclerView;
    private CommentViewAdaptor commentViewAdaptor;
    private ImageButton postCommentButton;
    private EditText commentInputText;
    private String postKey;
    private String dormName;
    private String currentUserID;
    private FirebaseAuth mAuth;
    private DatabaseReference userReference;
    private DatabaseReference messageReference;
    private HashMap<String, Comments> commentRecords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        // get postKey (message) from intent
        postKey = getIntent().getExtras().get("postKey").toString();

        // get dormName from intent
        dormName = getIntent().getExtras().get("dormName").toString();

        commentRecords = new HashMap<>();

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        userReference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        messageReference = FirebaseDatabase.getInstance().getReference().child("Dorms").child(dormName).child("Messages").child(postKey);

        init();

        // create recyclerView
        commentsRecyclerView = findViewById(R.id.comments_list);
        commentsRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        commentViewAdaptor = new CommentViewAdaptor(commentRecords, CommentsActivity.this);
        commentsRecyclerView.setAdapter(commentViewAdaptor);
        commentsRecyclerView.setLayoutManager(linearLayoutManager);

        // bind input
        postCommentButton = findViewById(R.id.post_comment_button);
        commentInputText = findViewById(R.id.comment_input);

        // set click listener
        postCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String username = snapshot.child("Username").getValue().toString();
                            String profileImage;
                            try {
                                profileImage = snapshot.child("UserPic").getValue().toString();
                            } catch (java.lang.NullPointerException e) {
                                profileImage = "https://firebasestorage.googleapis.com/v0/b/numad22sp-final-dormemo.appspot.com/o/images%2Fdefault_avatar.png?alt=media&token=a92b6a69-cc1d-46dc-8c3e-b98b1fa4682a";
                            }

                            String commentText = commentInputText.getText().toString();
                            if (TextUtils.isEmpty(commentText)) {
                                Toast.makeText(CommentsActivity.this,"Comment cannot be empty!",Toast.LENGTH_SHORT).show();
                            } else {
                                String currentDate = getCurrentDate();
                                String currentTime = getCurrentTime();

                                String postCommentKey = messageReference.push().getKey();

                                HashMap hashmap = new HashMap();
                                hashmap.put("uid", currentUserID);
                                hashmap.put("comment", commentText);
                                hashmap.put("date", currentDate);
                                hashmap.put("time", currentTime);
                                hashmap.put("username", username);
                                hashmap.put("profileImage", profileImage);

                                messageReference.child("Comments").child(postCommentKey).updateChildren(hashmap).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(CommentsActivity.this,"Successfully post a new comment!",Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(CommentsActivity.this,"Error occurred when posting a new comment!",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }

                            commentInputText.setText("");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    private void init() {
        messageReference.child("Comments").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Comments newComment = snapshot.getValue(Comments.class);
                commentRecords.put(snapshot.getKey(), newComment);
                commentViewAdaptor.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMM-yy");
        return currentDate.format(calendar.getTime());
    }

    private String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        return currentTime.format(calendar.getTime());
    }
}
