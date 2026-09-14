package com.jorotayo.algorubickrevamped

class Application : android.app.Application() {
    override fun onCreate() {
        super.onCreate()
        ObjectBox.init(this)
    }
}
