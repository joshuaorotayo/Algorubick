package com.jorotayo.algorubickrevamped

import android.content.Context
import com.jorotayo.algorubickrevamped.data.MyObjectBox
import io.objectbox.BoxStore
import io.objectbox.android.AndroidObjectBrowser

object ObjectBox {
    @Volatile
    private var boxStore: BoxStore? = null

    @JvmStatic
    fun init(context: Context) {
        if (boxStore == null) {
            synchronized(this) {
                if (boxStore == null) {
                    boxStore = MyObjectBox.builder()
                        .androidContext(context.applicationContext)
                        .build()
                }
            }
        }
        if (BuildConfig.DEBUG) {
            AndroidObjectBrowser(boxStore).start(context.applicationContext)
        }
    }

    @JvmStatic
    fun getBoxStore(): BoxStore =
        boxStore ?: error("ObjectBox not initialized. Call ObjectBox.init() first.")
}
