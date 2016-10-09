package cn.georgeyang.temperaturesafe;

import android.app.Application;
import android.os.AsyncTask;

import com.facebook.stetho.Stetho;

import java.util.Stack;

import cn.georgeyang.temperaturesafe.impl.ReceiverControl;

/**
 * Created by george.yang on 16/9/1.
 */
public class App extends Application {
    public static Stack<ReceiverControl> receiverStack = new Stack<>();


    @Override
    public void onCreate() {
        super.onCreate();

        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                        .build());
    }
}
