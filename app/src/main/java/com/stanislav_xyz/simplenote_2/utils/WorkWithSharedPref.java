package com.stanislav_xyz.simplenote_2.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.stanislav_xyz.simplenote_2.model.Folder;

import java.lang.reflect.Type;
import java.util.List;

public class WorkWithSharedPref {

    private Context mContext;

    // Конструктор
    public WorkWithSharedPref(Context context) {
        mContext = context;
    }

    private static final String APP_PREFERENCES = "APP_PREFERENCES";
    private static final String PREFERENCES_FOLDERS = "APP_PREFERENCES_FOLDERS";

    public void saveInSharedPref(List<Folder> folderList) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        java.lang.String json = gson.toJson(folderList);
        editor.putString(PREFERENCES_FOLDERS, json);
        editor.apply();
    }

    public List<Folder> getFromSharedPref() {
        List<Folder> folderList;
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        java.lang.String json = sharedPreferences.getString(PREFERENCES_FOLDERS, null);
        Type type = new TypeToken<List<Folder>>(){}.getType();
        folderList = gson.fromJson(json, type);
        return folderList;
    }

    public void cleanSharedPref() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor preferencesEditor = sharedPreferences.edit();
        preferencesEditor.clear();
        preferencesEditor.apply();
    }

}
