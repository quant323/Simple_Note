package com.stanislav_xyz.simplenote_2.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.stanislav_xyz.simplenote_2.R;
import com.stanislav_xyz.simplenote_2.model.Folder;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class WorkWithSharedPref {

    Context mContext;

    // Конструктор
    public WorkWithSharedPref(Context context) {
        mContext = context;
    }

    private static final String APP_PREFERENCES = "APP_PREFERENCES";
    private static final String APP_PREFERENCES_FOLDERS = "APP_PREFERENCES_FOLDERS";

    public void saveInSharedPref(List<Folder> folderList) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(folderList);
        editor.putString(APP_PREFERENCES_FOLDERS, json);
        editor.apply();
    }

    public List<Folder> loadFromSharedPref() {
        List<Folder> folderList;
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(APP_PREFERENCES_FOLDERS, null);
        Type type = new TypeToken<List<Folder>>(){}.getType();
        folderList = gson.fromJson(json, type);

//        if (folderList == null) {
//            Folder folder = new Folder(getString(R.string.default_folder_name), System.currentTimeMillis());
//            folderList = new ArrayList<>();
//            folderList.add(folder);
//        }
        return folderList;
    }

    public void cleanFromSharedPref() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor preferencesEditor = sharedPreferences.edit();
        preferencesEditor.clear();
        preferencesEditor.apply();
    }

}
