package com.stanislav_xyz.simplenote_2.data;

import android.os.AsyncTask;

// Выполняет запросы к БД в отдельном потоке
public class QueryAsyncTask extends AsyncTask<Void, Void, Void> {

    private AsyncBack mAsyncBack;

    // Конструктор
    public QueryAsyncTask(AsyncBack asyncBack) {
        mAsyncBack = asyncBack;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        mAsyncBack.workInBackground();
        return null;
    }


    // Интерфейс
    public interface AsyncBack {
        void workInBackground();
    }

}
