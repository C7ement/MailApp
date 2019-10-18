package com.example.smartmail_v2_androidx.database.converters;

import android.text.TextUtils;

import androidx.room.TypeConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class StringListConverter {
    @TypeConverter
    public static List<String> toList(String strList) {
        String[] array = strList.split("\n");
        return new ArrayList<>(Arrays.asList(array));
    }
    @TypeConverter
    public static String fromList(List<String> list){
        return TextUtils.join("\n",list);
    }
}
