package com.stanislav_xyz.simplenote_2.utils;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.stanislav_xyz.simplenote_2.model.Folder;
import com.stanislav_xyz.simplenote_2.model.Note;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    // Возвращает нажатую папку по id
    public static Folder getFolderById(int id, List<Folder> folderList) {
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

    // Возвращает лист заметок, относящихся к папке
    public static List<Note> getNotesFromFolder(List<Note> notes, Folder folder) {
        List<Note> notesInFolder = new ArrayList<>();
        for (int i = 0; i < notes.size(); i++) {
            if (notes.get(i).getFolderName().equals(folder.getName())) {
                notesInFolder.add(notes.get(i));
            }
        }
        return notesInFolder;
    }

    // Проверяет, имеется ли в листе папок папка с указанным именем.
    // Возвращает true, если такая папка имеется
    public static boolean isFolderNameExists(String name, List<Folder> folders) {
        for(Folder folder : folders) {
            if (folder.getName().toLowerCase().trim().equals(name.toLowerCase().trim()))
                return true;
        }
        return false;
    }

    // Возвращает title указанной длины из тела заметки
    public static String getTitle(String body, int length) {
        String[] arr = body.split("\n");
        if (arr[0].length() < length) {
            return arr[0].trim();
        } else {
            return arr[0].substring(0, length).trim();
        }
    }

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(Context context, int mesId) {
        Toast.makeText(context, mesId, Toast.LENGTH_SHORT).show();
    }

    // Создает EditText с параметрами
    public static EditText createEditText(Context context, String text, String hint) {
        EditText newEditText = new EditText(context);
        newEditText.setLayoutParams(new LinearLayout.LayoutParams
                (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        newEditText.setText(text);
        newEditText.setHint(hint);
        newEditText.setSingleLine(true);
        return newEditText;
    }

}
