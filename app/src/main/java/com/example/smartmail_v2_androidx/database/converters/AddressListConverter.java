package com.example.smartmail_v2_androidx.database.converters;

import android.text.TextUtils;

import androidx.room.TypeConverter;

import java.util.ArrayList;
import java.util.List;

import javax.mail.Address;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

public class AddressListConverter {
    @TypeConverter
    public static List<InternetAddress> toAddresses(String strAddresses) {
        if (strAddresses == null) {
            return null;
        }
        String[] list = strAddresses.split("\n");
        List<InternetAddress> addresses = new ArrayList<>();
        try {
            for (String str : list) {
                if (str.length() > 0) {
                    addresses.add(new InternetAddress(str));
                }
            }
        } catch (AddressException e) {
            e.printStackTrace();
        }
        return addresses;
    }
    @TypeConverter
    public static String fromAddresses(List<InternetAddress> addresses){
        if (addresses == null) {
            return null;
        }
        List<String> list = new ArrayList<>();
        for (InternetAddress addr : addresses) {
            list.add(addr.toString());
        }
        return TextUtils.join("\n",list);
    }
}
