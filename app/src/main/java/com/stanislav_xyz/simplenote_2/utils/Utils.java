package com.stanislav_xyz.simplenote_2.utils;

import com.stanislav_xyz.simplenote_2.model.Folder;

import java.util.List;

public class Utils {

    // Возвращает нажатую папку по id
    public static Folder getPressedFolder(int id, List<Folder> folderList) {
        for (int i = 0; i < folderList.size(); i++) {
            if (folderList.get(i).getId() == id)
                return folderList.get(i);
        }
        return folderList.get(0);
    }

    // Возвращает массив имен папок, за исключением текущей папки
    public static String[] getFolderNames(List<Folder> folderList) {
        String[] names = new String[folderList.size()];
        for (int i = 0; i < folderList.size(); i++) {
            names[i] = folderList.get(i).getName();
        }
        return names;
    }

}
