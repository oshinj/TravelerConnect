package edu.ucsb.cs184.oshin.travelerconnect;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Main_UserMenu extends AppCompatActivity {
    private Button look;
    private Button myFriend;
    private Button friendRequestBox;
    private FloatingActionButton fab;
    private String UID;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitry_main_user_menu);

        Intent intent = getIntent();
        this.UID = intent.getStringExtra("uid");

        look = findViewById(R.id.main_user_page_look);
        myFriend = findViewById(R.id.main_user_page_myFriend);
        friendRequestBox = findViewById(R.id.main_user_page_friendRequestBox);

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
                //TO DO:
                // implement setting
                System.out.println("uid is " + UID);
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


}
