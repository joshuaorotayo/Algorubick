package com.jorotayo.algorubickrevamped;

public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ObjectBox.init(this);
    }
}
