package com.falcon.dndapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import android.Manifest;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.ContentResolver;
import android.content.Context;

public class MainActivity extends AppCompatActivity {

    ArrayList<ContactModel> contactModelArrayList=new ArrayList<>();
    private static final int READ_CALL_LOG_PERMISSION_REQUEST = 1;
    private static final int READ_CONTACT_PERMISSION_REQUEST = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check for permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission not granted, request it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CALL_LOG},
                    READ_CALL_LOG_PERMISSION_REQUEST);

        }
        else if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission not granted, request it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    READ_CONTACT_PERMISSION_REQUEST);

        }
        else {
            // Permission already granted, proceed to read call log
            readCallLog();
        }

        RecyclerView recyclerView=findViewById(R.id.recyclerContactLayout);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

//        ContactModel contactModel=new ContactModel("724173414","10:30");
//        contactModelArrayList.add(contactModel);
        RecyclerContactAdapter recyclerContactAdapter=new RecyclerContactAdapter(this,contactModelArrayList);

        recyclerView.setAdapter(recyclerContactAdapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == READ_CALL_LOG_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, read call log
                readCallLog();
            } else {
                // Permission denied, handle accordingly
                Log.e("CallLogActivity", "Permission denied to read call log");
            }
        }
        if (requestCode == READ_CONTACT_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, read call log
                readCallLog();
            } else {
                // Permission denied, handle accordingly
                Log.e("CallLogActivity", "Permission denied to read call log");
            }
        }
    }



    public boolean isPhoneNumberInContacts(Context context, String phoneNumber) {
        // Initialize a ContentResolver
        ContentResolver contentResolver = context.getContentResolver();

        // Define the projection (columns you want to retrieve)
        String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER};

        // Define the selection criteria (phone number)
        String selection = ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?";

        // Define the selection arguments (the phone number to search for)
        String[] selectionArgs = {phoneNumber};

        // Query the ContactsContract data to find a match
        Cursor cursor = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null
        );

        // Check if the cursor contains any results
        boolean numberExistsInContacts = false;
        if (cursor != null && cursor.moveToFirst()) {
            numberExistsInContacts = true; // The phone number exists in contacts
            cursor.close(); // Close the cursor
        }

        return numberExistsInContacts;
    }

    private void readCallLog() {
        // ...

        Cursor cursor = getContentResolver().query(
                CallLog.Calls.CONTENT_URI,
                null,
                null,
                null,
                CallLog.Calls.DATE + " DESC"  // Sort by date in descending order
        );

        if(cursor!=null)
        {
            int numberIndex = cursor.getColumnIndex(CallLog.Calls.NUMBER);
            int dateIndex = cursor.getColumnIndex(CallLog.Calls.DATE);
            int durationIndex = cursor.getColumnIndex(CallLog.Calls.DURATION);
            int typeIndex = cursor.getColumnIndex(CallLog.Calls.TYPE);
            int simIndex = cursor.getColumnIndex(CallLog.Calls.PHONE_ACCOUNT_ID);

            while (cursor.moveToNext()) {
                String phoneNumber = cursor.getString(numberIndex);
                long callDate = cursor.getLong(dateIndex);
                long callDuration = cursor.getLong(durationIndex);
                int callType = cursor.getInt(typeIndex);
                int simId=cursor.getInt(simIndex);

                // Check if the phone number is in contacts
                boolean isNumberSaved = isPhoneNumberInContacts(this,phoneNumber);

                if (!isNumberSaved) {
                    // This is an unknown/unsaved number, do something with it


                    Date date = new Date(callDate);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    String formattedDate = sdf.format(date);

                    LocalDate currentDate = LocalDate.now();
                    LocalDate admissibleDate = currentDate.minus(3, ChronoUnit.DAYS);

                    LocalDate localCallDate = LocalDate.ofEpochDay(callDate / (24 * 60 * 60 * 1000));

                    if (localCallDate.isAfter(admissibleDate) && callType == CallLog.Calls.INCOMING_TYPE) {
                        contactModelArrayList.add(new ContactModel(phoneNumber, formattedDate, simId));
                    }
                }
//
//                LocalDate currentDate = LocalDate.now();
//                LocalDate admissibleDate = currentDate.minus(2, ChronoUnit.DAYS);
//
//                LocalDate localCallDate = LocalDate.ofEpochDay(callDate);
//
//                if(localCallDate.isAfter(admissibleDate))
//                {
//                    contactModelArrayList.add(new ContactModel(phoneNumber,formattedDate));
//                }
            }

            cursor.close();
        }
    }

    private String getCallTypeText(int callType) {
        switch (callType) {
            case CallLog.Calls.INCOMING_TYPE:
                return "Incoming";
            case CallLog.Calls.OUTGOING_TYPE:
                return "Outgoing";
            case CallLog.Calls.MISSED_TYPE:
                return "Missed";
            default:
                return "Unknown";
        }
    }
}