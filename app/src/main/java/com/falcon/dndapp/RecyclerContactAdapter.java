package com.falcon.dndapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerContactAdapter extends RecyclerView.Adapter<RecyclerContactAdapter.MyViewHolder> {


    private Context context;
    private ArrayList<ContactModel> contactModelArrayList;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0 ;
    public static final String SMS_SENT_ACTION = "SMS_SENT";

    BroadcastReceiver sentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(RecyclerContactAdapter.SMS_SENT_ACTION)) {
                int resultCode = getResultCode();
                if (resultCode == Activity.RESULT_OK) {
                    // SMS was sent successfully
                    Toast.makeText(context, "SMS sent", Toast.LENGTH_SHORT).show();
                } else {
                    // SMS sending failed
                    Toast.makeText(context, "SMS sending failed", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };



    public RecyclerContactAdapter(Context context, ArrayList<ContactModel> contactModelArrayList) {
        this.context = context;
        this.contactModelArrayList = contactModelArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.contact_layout, parent, false);
        MyViewHolder myViewHolder=new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ContactModel model = contactModelArrayList.get(position);
        holder.phoneNumber.setText(model.phoneNumber);
        holder.callTime.setText(model.callTime);
        holder.cardLinearLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Dialog confirmDialog = new Dialog(context);
                confirmDialog.setContentView(R.layout.dialog_confirmation);
                Button submitButton = confirmDialog.findViewById(R.id.complainSubmitButton);
                EditText et = confirmDialog.findViewById(R.id.detailText);
                submitButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String phoneNo = new String("1909");
                        String message = new String(et.getText().toString()+","+model.phoneNumber +","+model.callTime);


                        try{
                            //SmsManager smsManager=SmsManager.getDefault();
                            SmsManager smsManager=SmsManager.getSmsManagerForSubscriptionId(model.simId);
                            smsManager.sendTextMessage(phoneNo,null,message,null,null);
                            Toast.makeText(context,"Message Sent",Toast.LENGTH_LONG).show();
                            confirmDialog.dismiss();
                        }catch (Exception ex)
                        {
                            Toast.makeText(context,"Message Not Sent",Toast.LENGTH_LONG).show();
                        }
                    }
                });
                confirmDialog.show();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactModelArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView phoneNumber,callTime;
        LinearLayout cardLinearLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            phoneNumber=itemView.findViewById(R.id.phoneNumber);
            callTime=itemView.findViewById(R.id.callTime);
            cardLinearLayout = itemView.findViewById(R.id.cardLinearLayout);
        }
    }
}
