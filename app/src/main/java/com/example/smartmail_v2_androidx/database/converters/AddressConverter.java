package com.example.smartmail_v2_androidx.database.converters;

import androidx.room.TypeConverter;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;


public class AddressConverter {
    @TypeConverter
    public static InternetAddress toAddress(String strAddress) {
        try {
            return strAddress == null ? null : new InternetAddress(strAddress);
        } catch (AddressException e) {
            e.printStackTrace();
        }
        return null;
    }
    @TypeConverter
    public static String fromAddress(InternetAddress address){
        return address == null ? null : address.toString();
    }
}