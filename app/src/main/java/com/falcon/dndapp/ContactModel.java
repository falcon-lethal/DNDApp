package com.falcon.dndapp;

public class ContactModel {
    String phoneNumber,callTime;
    int simId;

    public ContactModel(String phoneNumber,String callTime,int simId) {
        this.phoneNumber = phoneNumber;
        this.callTime=callTime;
        this.simId=simId;
    }
}
