package edu.ucsb.cs184.oshin.travelerconnect;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class Main_UserFriendRequestBox extends AppCompatActivity {

    private String MyUID;
    private Map<ImageButton,String> hm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_look);

        Intent intent = getIntent();
        this.MyUID = intent.getStringExtra("uid");

        final Context context = this;
        hm = new HashMap<ImageButton,String>();


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

        DatabaseReference reff;
        reff = FirebaseDatabase.getInstance().getReference().child("FriendRequestTable").child(MyUID);

        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                linearLayout.removeAllViews();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(snapshot.child("status").getValue().toString().equalsIgnoreCase("pending")) {
                        String UID = snapshot.getKey();

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
                        tv.setText(UID + "wants to add you as friends, hit confirm to get his/her number!");
                        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 35);
                        tv.setLayoutParams(testViewParams);
                        linearLayout2.addView(tv);

                    }


                }


                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void ImageButtonClicked(ImageButton ib){
        Main_UserLook_Dialog mud = new Main_UserLook_Dialog(hm.get(ib), MyUID, 2);
        mud.show(this.getSupportFragmentManager(), "Diag");
    }
}
