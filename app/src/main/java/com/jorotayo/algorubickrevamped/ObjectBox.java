package com.jorotayo.algorubickrevamped;

import android.content.Context;

import com.jorotayo.algorubickrevamped.data.MyObjectBox;

import io.objectbox.BoxStore;

public class ObjectBox {
    private static BoxStore boxStore;

    public static void init(Context context) {
        if (boxStore == null) {
            boxStore = MyObjectBox.builder().androidContext(context.getApplicationContext()).build();
        }

       /* boolean started = new AndroidObjectBrowser(boxStore).start(context.getApplicationContext());
        Log.i("ObjectBoxAdmin", "Started: " + started);
*/
    }

    public static BoxStore getBoxStore() {
        return boxStore;
    }
}
