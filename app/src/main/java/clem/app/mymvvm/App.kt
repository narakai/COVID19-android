package clem.app.mymvvm

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import clem.app.mymvvm.ui.MainActivity
import jonathanfinerty.once.Once
import kotlin.properties.Delegates

/**
 * Created by leon
 * on 2018/3/13 13:35
 */
class App : MultiDexApplication() {

    companion object {
        var CONTEXT: Context by Delegates.notNull()
    }

    override fun onCreate() {
        super.onCreate()
        CONTEXT = applicationContext
        Once.initialise(this)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }
    //
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}