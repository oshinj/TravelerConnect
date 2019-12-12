package edu.ucsb.cs184.oshin.travelerconnect;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Main_UserMenu extends AppCompatActivity {
    private Button look;
    private Button myFriend;
    private Button friendRequestBox;
    private FloatingActionButton fab;
    private String UID;
    private Handler handler = new Handler();
    private int request = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitry_main_user_menu);

        Intent intent = getIntent();
        this.UID = intent.getStringExtra("uid");

        look = findViewById(R.id.main_user_page_look);
        myFriend = findViewById(R.id.main_user_page_myFriend);
        friendRequestBox = findViewById(R.id.main_user_page_friendRequestBox);
        friendRequestBox.setVisibility(View.INVISIBLE);

        look.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                openMain_UserLook();
            }
        });

        myFriend.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                openMain_User_MyFriend();
            }
        });


        DatabaseReference reff;
        reff = FirebaseDatabase.getInstance().getReference().child("FriendRequestTable");
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                request = 0;
                for(DataSnapshot snapshot: dataSnapshot.child(UID).getChildren()){
                    if(snapshot.child("status").getValue() != null) {
                        if (snapshot.child("status").getValue().toString().equalsIgnoreCase("pending")) {
                            request++;
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    System.out.println("request is " + request);
                    android.os.SystemClock.sleep(100);
                    if (request > 0) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                friendRequestBox.setVisibility(View.VISIBLE);
                            }
                        });
                    } else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                friendRequestBox.setVisibility(View.INVISIBLE);
                            }
                        });
                    }

                }
            }
        }).start();



        friendRequestBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMain_UserFriendRequestBox();
            }
        });

        fab = this.findViewById(R.id.main_user_page_setting);
        this.fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                openSettings();
            }
        });
    }

    private void openMain_User_MyFriend(){
        Intent intent = new Intent(this, Main_User_MyFriend.class);
        intent.putExtra("uid", this.UID);
        startActivity(intent);
    }


    private void openMain_UserLook(){
        Intent intent = new Intent(this, Main_UserLook.class);
        intent.putExtra("uid", this.UID);
        startActivity(intent);
    }

    private void openMain_UserFriendRequestBox(){
        Intent intent = new Intent(this, Main_UserFriendRequestBox.class);
        intent.putExtra("uid", this.UID);
        startActivity(intent);
    }

    public void openSettings() {
        Intent intent = new Intent(this, SignUp.class);
        startActivity(intent);
    }
}
