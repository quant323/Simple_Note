package com.stanislav_xyz.simplenote_2.utils;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.util.Log;

import com.stanislav_xyz.simplenote_2.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import androidx.core.content.ContextCompat;

public class NoteFileManager {

    private static final String TAG = "myTag";
    private Context mContext;

    public NoteFileManager(Context context) {
        mContext = context;
    }

    public void writeFile(String pathName, String fileName, String body) {
        if (isExternalStorageWritable() && checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Создаем папку по указанному пути
            File filePath = new File(pathName);
            if (!filePath.exists()) {
                filePath.mkdirs();
                Log.d(TAG, "Folder has been created!");
            } else Log.d(TAG, "Folder already exists!");

            // Создаем пустой файл в указанной папке
            File noteFile = new File(filePath, fileName);

            // Записываем текст заметки в файл
            try {
                FileOutputStream fos = new FileOutputStream(noteFile);
                fos.write(body.getBytes());
                fos.close();
                Log.d(TAG, "File Saved!");
                scanFile(mContext, noteFile, "txt");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else Utils.showToast(mContext, R.string.mes_permission_denied);

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
