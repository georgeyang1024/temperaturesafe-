package cn.georgeyang.temperaturesafe;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by george.yang on 16/9/1.
 */
public class LoopThreadService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    Task task = new Task();

    @Override
    public void onCreate() {
        super.onCreate();
//        task.execute()
    }

    private class Task extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            return null;
        }
    }
}
