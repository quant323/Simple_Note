package com.stanislav_xyz.simplenote_2.utils;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.util.Log;

import com.stanislav_xyz.simplenote_2.R;
import com.stanislav_xyz.simplenote_2.activities.SettingsActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import androidx.core.content.ContextCompat;

public class NoteFileManager {

    private static final String TAG = "myTag";
    private Context mContext;

    public NoteFileManager(Context context) {
        mContext = context;
    }

    public void exportTextToFile(File file, String body) {
        if (isExternalStorageWritable() && checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            try {
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(body.getBytes());
                fos.close();
                Log.d(TAG, "File Saved!");
                scanFile(mContext, file, SettingsActivity.MIME_TEXT);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else Utils.showToast(mContext, R.string.mes_permission_denied);
    }

    public void writeObjectsToFile(File file, Object... objects) {
        if (isExternalStorageWritable() && checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
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
              scanFile(mContext, file, SettingsActivity.MIME_FILE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else Utils.showToast(mContext, R.string.mes_permission_denied);
    }

    public Object[] readObjectsFromFile(File file) {
        if (isExternalStorageReadable() && checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
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
        } else Utils.showToast(mContext, R.string.mes_permission_denied);
        return null;
    }

    // Позволяет проводнику Windows находить созданный файл
    private void scanFile(Context context, File file, String mimeType) {
        MediaScannerConnection.scanFile(context, new String[] {file.getAbsolutePath()},
                new String[] {mimeType}, null);
    }

    private boolean isExternalStorageWritable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    private boolean isExternalStorageReadable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(Environment.getExternalStorageState());
    }

    private boolean checkPermission(String permission) {
        int check = ContextCompat.checkSelfPermission(mContext, permission);
        return (check == PackageManager.PERMISSION_GRANTED);
    }

}
