package com.stanislav_xyz.simplenote_2.utils;

import android.content.Context;
import android.media.MediaScannerConnection;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public class NoteFileManager {

    private static final String TAG = "myTag";
    private Context mContext;

    public NoteFileManager(Context context) {
        mContext = context;
    }

    public void exportTextToFile(File file, String body) {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(body.getBytes());
            fos.close();
            scanFile(mContext, file, getMime(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeObjectsToFile(File file, Object... objects) {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            // Сперва записывем число, равное количеству записываемых объектов
            oos.writeInt(objects.length);
            // Затем записываем сами объекты
            for (Object object : objects)
                oos.writeObject(object);
            fos.close();
            oos.close();
            Utils.showToast(mContext, "File Saved!");
            scanFile(mContext, file, getMime(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Object[] readObjectsFromFile(File file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            // Читаем из файла int, в котором записано число находящихся в файле объектов
            int arrayLength = ois.readInt();
            Object[] objects = new Object[arrayLength];
            // Затем читаем сами объекты
            for (int i = 0; i < arrayLength; i++)
                objects[i] = ois.readObject();
            fis.close();
            ois.close();
            Utils.showToast(mContext, "File Loaded!");
            return objects;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Позволяет проводнику Windows находить созданный файл
    private void scanFile(Context context, File file, String mimeType) {
        MediaScannerConnection.scanFile(context, new String[]{file.getAbsolutePath()},
                new String[]{mimeType}, null);
    }

    // Возвращает mime из имени файла
    private String getMime(File file) {
        String[] parts = file.getName().split("\\.");
        return parts[parts.length - 1];
    }

}
