package edu.ucsb.cs184.oshin.travelerconnect;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
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

public class Main_User_MyFriend_ContactDialog extends DialogFragment {
    private Context context;
    private String ClickedUID;
    private LinearLayout linearLayout;
    private String MyUID;

    public Main_User_MyFriend_ContactDialog(String ClickedUID, String MyUID){
        this.ClickedUID = ClickedUID;
        this.MyUID = MyUID;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Main_UserLook_NewView view = new Main_UserLook_NewView(getContext());
        this.context = this.getContext();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        ScrollView sv = new ScrollView(this.getContext());
        linearLayout = new LinearLayout(this.getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        //linearLayout.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);
        ScrollView.LayoutParams svlp = new ScrollView.LayoutParams(ScrollView.LayoutParams.MATCH_PARENT, ScrollView.LayoutParams.MATCH_PARENT);
        LinearLayout.LayoutParams linearLayoutParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        sv.setLayoutParams(svlp);
        linearLayout.setLayoutParams(linearLayoutParam);
        sv.addView(linearLayout);



        AlertDialog ad = new AlertDialog.Builder(getActivity())
                .setView(sv)
                .setPositiveButton("confirm", null)
                .setNegativeButton("delete", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DeleteFriend();
                    }
                })
                .create();

        ad.getWindow().setLayout(600, 400);

        DatabaseReference reff;
        reff = FirebaseDatabase.getInstance().getReference().child("UserInfo").child(ClickedUID);
        reff.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String phone  = dataSnapshot.child("Contact").getValue().toString();

                LinearLayout linearLayout1 = new LinearLayout(getContext());
                LinearLayout.LayoutParams linearLayoutParam1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                linearLayout1.setLayoutParams(linearLayoutParam1);
                linearLayout1.setOrientation(LinearLayout.HORIZONTAL);


                Typeface boldTypeface = Typeface.defaultFromStyle(Typeface.BOLD);
                TextView contact = new TextView(context);
                LinearLayout.LayoutParams contactParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                contact.setText("Contact: ");
                contact.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
                contact.setTypeface(boldTypeface);
                contact.setLayoutParams(contactParams);

                TextView contactText = new TextView(context);
                contactText.setText(phone);
                contactText.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
                contactText.setLayoutParams(contactParams);
                linearLayout1.addView(contact);
                linearLayout1.addView(contactText);

                linearLayout.addView(linearLayout1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        return ad;
    }

    private void DeleteFriend(){
        DatabaseReference reff2 = FirebaseDatabase.getInstance().getReference().child("FriendRequestTable").child(ClickedUID).child(MyUID);
        reff2.removeValue();

        reff2 = FirebaseDatabase.getInstance().getReference().child("FriendRequestTable").child(MyUID).child(ClickedUID);
        reff2.removeValue();

        reff2 = FirebaseDatabase.getInstance().getReference().child("FriendTable").child(ClickedUID).child(MyUID);
        reff2.removeValue();

        reff2 = FirebaseDatabase.getInstance().getReference().child("FriendTable").child(MyUID).child(ClickedUID);
        reff2.removeValue();
    }
}
