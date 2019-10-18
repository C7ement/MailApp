package com.example.smartmail_v2_androidx.database.converters;

import androidx.room.TypeConverter;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Date;

public class JsonConverter {
    @TypeConverter
    public static JSONArray toJson(String string) {
        try {
            return new JSONArray(string);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONArray();
    }
    @TypeConverter
    public static String toString(JSONArray jsonArray){
        return jsonArray.toString();
    }
}