package edu.ucsb.cs184.oshin.travelerconnect;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Main_UserLook_Dialog extends DialogFragment {

    private String ClickedUID;
    private String MyUID;
    private String Bio;
    private String Interest;
    private String Visit;
    private String Contact;
    private String Gender;

    private int identifier;

    private ScrollView sv;
    private LinearLayout linearLayout;
    private LinearLayout linearLayout1;
    private LinearLayout linearLayout2;
    private LinearLayout linearLayout3;

    private Context context;

    public Main_UserLook_Dialog(String UserID, String MyUID, int identifier){
        this.ClickedUID = UserID;
        this.MyUID = MyUID;
        this.identifier = identifier;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Main_UserLook_NewView view = new Main_UserLook_NewView(getContext());
        this.context = this.getContext();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();


        DatabaseReference reff;
        reff = FirebaseDatabase.getInstance().getReference().child("UserInfo").child(ClickedUID);
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Bio = dataSnapshot.child("Bio").getValue().toString();

                Visit = dataSnapshot.child("Visit").getValue().toString();

                Gender = dataSnapshot.child("Gender").getValue().toString();

                Typeface boldTypeface = Typeface.defaultFromStyle(Typeface.BOLD);

                LinearLayout.LayoutParams BioParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                TextView Biography = new TextView(context);
                Biography.setText("Biography: ");
                Biography.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
                Biography.setTypeface(boldTypeface);
                Biography.setLayoutParams(BioParams);

                TextView BiotText = new TextView(context);
                BiotText.setText(Bio);
                BiotText.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
                BiotText.setLayoutParams(BioParams);
                linearLayout1.addView(Biography);
                linearLayout1.addView(BiotText);


                TextView Visit_ = new TextView(context);
                Visit_.setText("Visit: ");
                Visit_.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
                Visit_.setTypeface(boldTypeface);
                Visit_.setLayoutParams(BioParams);

                TextView VisitText = new TextView(context);
                VisitText.setText(Visit);
                VisitText.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
                VisitText.setLayoutParams(BioParams);
                linearLayout2.addView(Visit_);
                linearLayout2.addView(VisitText);


                TextView Gender_ = new TextView(context);
                Gender_.setText("Gender: ");
                Gender_.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
                Gender_.setTypeface(boldTypeface);
                Gender_.setLayoutParams(BioParams);

                TextView GenderText = new TextView(context);
                GenderText.setText(Gender);
                GenderText.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
                GenderText.setLayoutParams(BioParams);
                linearLayout3.addView(Gender_);
                linearLayout3.addView(GenderText);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        sv = new ScrollView(this.getContext());
        linearLayout = new LinearLayout(this.getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        //linearLayout.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);
        ScrollView.LayoutParams svlp = new ScrollView.LayoutParams(ScrollView.LayoutParams.MATCH_PARENT, ScrollView.LayoutParams.MATCH_PARENT);
        LinearLayout.LayoutParams linearLayoutParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        sv.setLayoutParams(svlp);
        linearLayout.setLayoutParams(linearLayoutParam);
        sv.addView(linearLayout);
        linearLayout1 = new LinearLayout(getContext());
        linearLayout2 = new LinearLayout(getContext());
        linearLayout3 = new LinearLayout(getContext());;
        LinearLayout.LayoutParams linearLayoutParam1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout1.setLayoutParams(linearLayoutParam1);
        linearLayout2.setLayoutParams(linearLayoutParam1);
        linearLayout3.setLayoutParams(linearLayoutParam1);
        linearLayout1.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout2.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout3.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.addView(linearLayout1);
        linearLayout.addView(linearLayout3);
        linearLayout.addView(linearLayout2);


        AlertDialog ad = new AlertDialog.Builder(getActivity())
                .setView(sv)
                .setPositiveButton("confirm",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(identifier == 1)
                            sendMatchRequest();
                        else if(identifier == 2)
                            sendConfirmRequest();
                    }
                })
                .setNegativeButton("back", null)
                .create();

        ad.getWindow().setLayout(600, 400);

        return ad;
    }

    private void sendMatchRequest(){
        DatabaseReference reff;
        reff = FirebaseDatabase.getInstance().getReference().child("FriendRequestTable").child(MyUID).child(ClickedUID);
        PostFriendRequest data = new PostFriendRequest("confirm");
        reff.setValue(data);

        reff = FirebaseDatabase.getInstance().getReference().child("FriendRequestTable").child(ClickedUID).child(MyUID);
        PostFriendRequest data2 = new PostFriendRequest("pending");
        reff.setValue(data2);
    }

    private void sendConfirmRequest(){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy,HH:mm:ss");
        Date date = new Date();
        String since  = formatter.format(date);
        int friendStatus = 1;

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.HOUR_OF_DAY, 12);
        Date newDate = cal.getTime();
        String nextTimeConfirm = formatter.format(newDate);

        DatabaseReference reff;
        reff = FirebaseDatabase.getInstance().getReference().child("FriendTable").child(MyUID).child(ClickedUID);


        PostFriendConfirm data = new PostFriendConfirm(Integer.toString(friendStatus), since, nextTimeConfirm);
        reff.setValue(data);

        reff = FirebaseDatabase.getInstance().getReference().child("FriendTable").child(ClickedUID).child(MyUID);
        reff.setValue(data);

        reff = FirebaseDatabase.getInstance().getReference().child("FriendRequestTable").child(MyUID).child(ClickedUID);
        reff.removeValue();

        reff = FirebaseDatabase.getInstance().getReference().child("FriendRequestTable").child(ClickedUID).child(MyUID);
        reff.removeValue();
    }

    public static class PostFriendConfirm implements Serializable {
        public String status;
        public String since;
        public String nextTimeConfirm;

        public PostFriendConfirm( String status, String since, String nextTimeConfirm){
            this.status = status;
            this.since = since;
            this.nextTimeConfirm = nextTimeConfirm;
        }
    }

    public static class PostFriendRequest implements Serializable {
        public String status;

        public PostFriendRequest( String status){
            this.status = status;
        }
    }

}
