package com.jorotayo.algorubickrevamped;

import android.app.PendingIntent;
import android.content.Context;
import android.util.Log;

import com.jorotayo.algorubickrevamped.data.MyObjectBox;

import io.objectbox.BoxStore;
import io.objectbox.android.AndroidObjectBrowser;

public class ObjectBox {
    private static BoxStore boxStore;

    public static void init(Context context) {
        if (boxStore == null) {
            boxStore = MyObjectBox.builder().androidContext(context.getApplicationContext()).build();
        }

        if (BuildConfig.DEBUG) {
            new AndroidObjectBrowser(boxStore).start(context.getApplicationContext());
        }
    }

    public static BoxStore getBoxStore() {
        return boxStore;
    }
}
