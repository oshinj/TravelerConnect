package edu.ucsb.cs184.oshin.travelerconnect;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
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

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Main_User_MyFriend_Dialog extends DialogFragment {
    private Context context;
    private String ClickedUID;
    private String MyUID;
    private String status;

    public Main_User_MyFriend_Dialog(String ClickedUID, String MyUID, String status){
        this.ClickedUID = ClickedUID;
        this.MyUID = MyUID;
        this.status = status;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Main_UserLook_NewView view = new Main_UserLook_NewView(getContext());
        this.context = this.getContext();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        ScrollView sv = new ScrollView(this.getContext());
        LinearLayout linearLayout = new LinearLayout(this.getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        //linearLayout.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);
        ScrollView.LayoutParams svlp = new ScrollView.LayoutParams(ScrollView.LayoutParams.MATCH_PARENT, ScrollView.LayoutParams.MATCH_PARENT);
        LinearLayout.LayoutParams linearLayoutParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        sv.setLayoutParams(svlp);
        linearLayout.setLayoutParams(linearLayoutParam);
        sv.addView(linearLayout);

        TextView tv = new TextView(this.getContext());
        tv.setText("Hit Confirm to Extend Friend Limit, or Hit Delete to Delete Your Friend!");
        LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tv.setLayoutParams(tvParams);
        linearLayout.addView(tv);


        AlertDialog ad = new AlertDialog.Builder(getActivity())
                .setView(sv)
                .setPositiveButton("confirm",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ExtendFriendLimit(ClickedUID, MyUID);
                    }
                })
                .setNegativeButton("delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DeleteFriend();
                    }
                })
                .create();

        ad.getWindow().setLayout(600, 400);

        return ad;

    }

    private void ExtendFriendLimit(final String ClickedUID, final String MyUID){

        DatabaseReference reff;
        reff = FirebaseDatabase.getInstance().getReference().child("FriendRequestTable");
        //this.status = String.valueOf(Integer.valueOf(this.status) + 1);

        reff.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(ClickedUID).child(MyUID).exists()){
                     status = dataSnapshot.child(ClickedUID).child(MyUID).child("status").getValue().toString();
                     if(status.substring(0, 7).equalsIgnoreCase("confirm")) {
                         status = status.substring(status.length() - 1);

                         SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy,HH:mm:ss");
                         Date date = new Date();
                         String since = formatter.format(date);
                         System.out.println("status is " + status);
                         int friendStatus = Integer.valueOf(status) + 1;

                         Calendar cal = Calendar.getInstance();
                         cal.setTime(date);
                         if (friendStatus == 2) {
                             cal.add(Calendar.HOUR_OF_DAY, 24);
                         } else if (friendStatus == 3) {
                             cal.add(Calendar.HOUR_OF_DAY, 72);
                         } else if (friendStatus == 4) {
                             cal.add(Calendar.HOUR_OF_DAY, 168);
                         } else if (friendStatus == 5) {
                             cal.add(Calendar.HOUR_OF_DAY, 720);
                         } else if (friendStatus == 6) {
                             cal.add(Calendar.HOUR_OF_DAY, 4320);
                         } else if (friendStatus == 7) {
                             cal.add(Calendar.HOUR_OF_DAY, 8640);
                         } else if (friendStatus == 8) {
                             cal.add(Calendar.HOUR_OF_DAY, 432000);
                         }
                         Date newDate = cal.getTime();
                         String nextTimeConfirm = formatter.format(newDate);

                         DatabaseReference reff3;
                         reff3 = FirebaseDatabase.getInstance().getReference().child("FriendTable").child(MyUID).child(ClickedUID);

                         Main_User_MyFriend_Dialog.PostFriendConfirm data = new Main_User_MyFriend_Dialog.PostFriendConfirm(Integer.toString(friendStatus), since, nextTimeConfirm);
                         reff3.setValue(data);

                         reff3 = FirebaseDatabase.getInstance().getReference().child("FriendTable").child(ClickedUID).child(MyUID);
                         reff3.setValue(data);

                         DatabaseReference reff2 = FirebaseDatabase.getInstance().getReference().child("FriendRequestTable").child(ClickedUID).child(MyUID);
                         reff2.removeValue();

                         reff2 = FirebaseDatabase.getInstance().getReference().child("FriendRequestTable").child(MyUID).child(ClickedUID);
                         reff2.removeValue();
                     }
                }else{
                    DatabaseReference reff;
                    reff = FirebaseDatabase.getInstance().getReference().child("FriendRequestTable").child(MyUID).child(ClickedUID);
                    Main_UserLook_Dialog.PostFriendRequest data = new Main_UserLook_Dialog.PostFriendRequest("confirm"+status);
                    reff.setValue(data);

                    reff = FirebaseDatabase.getInstance().getReference().child("FriendRequestTable").child(ClickedUID).child(MyUID);
                    Main_UserLook_Dialog.PostFriendRequest data2 = new Main_UserLook_Dialog.PostFriendRequest("pending"+status);
                    reff.setValue(data2);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }

    private void DeleteFriend(){
        DatabaseReference reff2 = FirebaseDatabase.getInstance().getReference().child("FriendRequestTable").child(ClickedUID).child(MyUID);
        reff2.removeValue();

        reff2 = FirebaseDatabase.getInstance().getReference().child("FriendRequestTable").child(MyUID).child(ClickedUID);
        reff2.removeValue();

        System.out.println("UID is " + MyUID);
        System.out.println("Clicked UID is "+ ClickedUID);

        reff2 = FirebaseDatabase.getInstance().getReference().child("FriendTable").child(ClickedUID).child(MyUID);
        reff2.removeValue();

        reff2 = FirebaseDatabase.getInstance().getReference().child("FriendTable").child(MyUID).child(ClickedUID);
        reff2.removeValue();
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
