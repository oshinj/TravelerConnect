package edu.ucsb.cs184.oshin.travelerconnect;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Main_User_MyFriend extends AppCompatActivity{

    private String MyUID;
    private Map<ImageButton,String> hm;
    private Handler handler = new Handler();
    private double timeLeft = 0;
    private Map<ProgressBar, Integer> pbBar = new HashMap<ProgressBar, Integer>();
    private ArrayList<Integer> ProgessStatus = new ArrayList<Integer>();
    private int i = 0;

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
        reff = FirebaseDatabase.getInstance().getReference();

        reff.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                linearLayout.removeAllViews();
                for (DataSnapshot snapshot : dataSnapshot.child("FriendTable").child(MyUID).getChildren()) {

                    String UID = snapshot.getKey();
                    LinearLayout linearLayout2 = new LinearLayout(context);
                    linearLayout2.setOrientation(LinearLayout.HORIZONTAL);
                    LinearLayout.LayoutParams linearLayoutParam2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);


                    linearLayout2.setLayoutParams(linearLayoutParam2);
                    linearLayout.addView(linearLayout2);

                    final ImageButton ib = new ImageButton(context);
                    hm.put(ib, UID);



                    ib.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ImageButtonClicked(ib, MyUID);
                        }
                    });

                    ib.setImageResource(R.drawable.ic_android_black_);
                    LinearLayout.LayoutParams imageViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    ib.setLayoutParams(imageViewParams);
                    linearLayout2.addView(ib);

                    final TextView tv = new TextView(context);
                    LinearLayout.LayoutParams testViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                    String Bio = dataSnapshot.child("UserInfo").child(UID).child("Bio").getValue().toString();
                    tv.setText(Bio + " Hit to get their number");
                    tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
                    tv.setLayoutParams(testViewParams);
                    linearLayout2.addView(tv);

                    final ProgressBar pb = new ProgressBar(context,
                                                            null,
                                                            android.R.attr.progressBarStyleHorizontal);
                    linearLayout.addView(pb);


                    final String status = snapshot.child("status").getValue().toString();


                    String since = snapshot.child("since").getValue().toString();
                    String nextToConfirm = snapshot.child("nextTimeConfirm").getValue().toString();
                    SimpleDateFormat formatter1 = new SimpleDateFormat("dd/MM/yyyy,HH:mm:ss");
                    Date dateSince = new Date();
                    Date dateToConfirm = new Date();
                    Date now = new Date();
                    try {
                        dateSince = formatter1.parse(since);
                        dateToConfirm = formatter1.parse(nextToConfirm);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy,HH:mm:ss");

                    //System.out.println("date since " + sincedate);

                    final long DiffSinceAndConfirm = getDateDiff(dateSince, dateToConfirm, TimeUnit.SECONDS);
                    long DiffSinceAndNow = getDateDiff(dateSince, now, TimeUnit.SECONDS);

                    pbBar.put(pb,(int)DiffSinceAndNow);
                    pb.setMax(((int) DiffSinceAndConfirm));
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while(pbBar.get(pb) < DiffSinceAndConfirm){
                                pbBar.put(pb, pbBar.get(pb) + 1);
                                android.os.SystemClock.sleep(1000);
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        pb.setProgress(pbBar.get(pb));
                                    }
                                });
                            }
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    TextView warning = new TextView(context);
                                    tv.setText( tv.getText() + "\n" + "Hit the image to enlonger the friend limit! Otherwise your friend will be deleted after 24 hours");
                                    LinearLayout.LayoutParams warningViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                    tv.setTextColor(Color.RED);
                                    //linearLayout.addView(warning);



                                    ib.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            ImageButtonClicked_FriendExpired(ib, status);
                                        }
                                    });
                                }
                            });
                        }
                    }).start();
/*
                    if(DiffSinceAndNow > DiffSinceAndConfirm){
                        TextView warning = new TextView(context);
                        warning.setText("Hit the image to enlonger the friend limit! Otherwise your friend will be deleted after 24 hours");
                        LinearLayout.LayoutParams warningViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        warning.setTextColor(Color.RED);
                        linearLayout.addView(warning);

                        ib.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ImageButtonClicked_FriendExpired();
                            }
                        });
                    }*/


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void ImageButtonClicked_FriendExpired(ImageButton ib, String status){
        Main_User_MyFriend_Dialog mud = new Main_User_MyFriend_Dialog( hm.get(ib), MyUID, status);
        mud.show(this.getSupportFragmentManager(), "Diag");

    }


    private void ImageButtonClicked(ImageButton ib, String MyUID){
        Main_User_MyFriend_ContactDialog mumc = new Main_User_MyFriend_ContactDialog(hm.get(ib), MyUID);
        mumc.show(this.getSupportFragmentManager(), "Diag");
    }

    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
    }



}
