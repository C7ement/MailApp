package com.example.smartmail_v2_androidx.database.converters;

import android.text.TextUtils;

import androidx.room.TypeConverter;

public class StringArrayConverter {
    @TypeConverter
    public static String[] toArray(String strList) {
        return strList  == null ? null : strList.split("\n");
    }
    @TypeConverter
    public static String fromArray(String[] list){
        return list  == null ? null : TextUtils.join("\n",list);
    }
}
