package edu.ucsb.cs184.oshin.travelerconnect;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main_UserLook extends AppCompatActivity {

    private Map<ImageButton,String> hm;
    private String MyUID = "";
    private ArrayList<String> MyInterest = new ArrayList<String>();
    private ArrayList<String> MatchedUsers = new ArrayList<String>();
    private ArrayList<String> MyFriend = new ArrayList<String>();
    private List<String> list= Arrays.asList("Bar", "Beach", "Club", "Extreme Sports", "Gym", "Hiking", "Sightseeing");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_look);

        Intent intent = getIntent();
        this.MyUID = intent.getStringExtra("uid");

        hm = new HashMap<ImageButton,String>();
        final Context context = this;

        final ScrollView sv = new ScrollView(context);
        ScrollView.LayoutParams svlp = new ScrollView.LayoutParams(ScrollView.LayoutParams.MATCH_PARENT, ScrollView.LayoutParams.MATCH_PARENT);
        sv.setLayoutParams(svlp);
        setContentView(sv, svlp);

        final LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        //linearLayout.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);
        LinearLayout.LayoutParams linearLayoutParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        linearLayout.setLayoutParams(linearLayoutParam);
        //setContentView(linearLayout,linearLayoutParam);
        sv.addView(linearLayout);


        DatabaseReference reff2 = FirebaseDatabase.getInstance().getReference().child("FriendTable").child(MyUID);
        reff2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    MyFriend.add(snapshot.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference reff;
        reff = FirebaseDatabase.getInstance().getReference().child("UserInfo");


        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                linearLayout.removeAllViews();
                MyInterest.clear();
                MatchedUsers.clear();

                for (DataSnapshot snapshot : dataSnapshot.child(MyUID).child("Interests").getChildren()){
                    MyInterest.add(snapshot.getValue().toString());
                }

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(snapshot.getKey().equalsIgnoreCase(MyUID)){
                        continue;
                    }

                    if(MyFriend.indexOf(snapshot.getKey()) < 0) {
                        int interestMatched = 0;
                        for (DataSnapshot snapshot2 : snapshot.getChildren()) {
                            if (snapshot2.getKey().equalsIgnoreCase("Interests")) {
                                for (DataSnapshot interest : snapshot2.getChildren()) {
                                    if (ifInInterestArray(interest.getValue().toString())) {
                                        interestMatched++;
                                    }
                                }
                            }
                        }

                        MatchedUsers.add(snapshot.getKey() + "," + interestMatched);
                    }

                }

                MatchedUsers = sortMatchedUser(MatchedUsers);
                if(MatchedUsers.size() > 20)
                    MatchedUsers = shortenMatchedUser(MatchedUsers);

                for(int i = MatchedUsers.size() - 1; i >= 0; i--){
                    String UID = MatchedUsers.get(i).substring(0, MatchedUsers.get(i).indexOf(","));

                    LinearLayout linearLayout2 = new LinearLayout(context);
                    linearLayout2.setOrientation(LinearLayout.HORIZONTAL);
                    LinearLayout.LayoutParams linearLayoutParam2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                    View v = new View(context);
                    LinearLayout.LayoutParams ViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 5);
                    v.setBackgroundColor(Color.LTGRAY);
                    v.setLayoutParams(ViewParams);

                    linearLayout2.setLayoutParams(linearLayoutParam2);
                    linearLayout.addView(linearLayout2);
                    linearLayout.addView(v);

                    final ImageButton ib = new ImageButton(context);
                    hm.put(ib, UID);

                    ib.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ImageButtonClicked(ib);
                        }
                    });

                    ib.setImageResource(R.drawable.ic_android_black_);
                    LinearLayout.LayoutParams imageViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    ib.setLayoutParams(imageViewParams);
                    linearLayout2.addView(ib);

                    final TextView tv = new TextView(context);
                    LinearLayout.LayoutParams testViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    tv.setText(dataSnapshot.child(UID).child("Bio").getValue().toString());
                    tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,35);
                    tv.setLayoutParams(testViewParams);
                    linearLayout2.addView(tv);



                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    private void ImageButtonClicked(ImageButton ib){

        Main_UserLook_Dialog mud = new Main_UserLook_Dialog(hm.get(ib), MyUID, 1);
        mud.show(this.getSupportFragmentManager(), "Diag");

    }

    private Boolean ifInInterestArray(String interest){
        for(String i : MyInterest){
            if(i.equalsIgnoreCase(interest)){
                return true;
            }
        }
        return false;
    }

    private ArrayList<String> sortMatchedUser(ArrayList<String> UserList){
        //System.out.println("first call " + UserList.get(0).substring(UserList.get(0).indexOf(",") + 1));
        //selection sort
        int min = 0;
        int min_index = 0;
        for(int i = 0; i < UserList.size()-1; i++){
            min = Integer.valueOf(UserList.get(i).substring(UserList.get(i).indexOf(",") + 1));
            min_index = i;
            for(int j = i+1; j < UserList.size(); j++){
                int num = Integer.valueOf(UserList.get(j).substring(UserList.get(j).indexOf(",") + 1));
                if(min > num){
                    min_index = j;
                    min = num;
                }
            }
            String temp =UserList.get(i);
            UserList.set(i, UserList.get(min_index));
            UserList.set(min_index, temp);

        }
        return UserList;
    }

    private ArrayList<String> shortenMatchedUser(ArrayList<String> UserList){
        ArrayList<String> shortenedUserList = new ArrayList<String>();
        int count;
        for(int i = UserList.size() - 20; i < UserList.size(); i++){
            shortenedUserList.add(UserList.get(i));
        }
        return shortenedUserList;
    }
}
