package com.zia.easybook

import android.app.Application
import android.content.SharedPreferences
import android.content.res.Resources
import io.realm.Realm
import io.realm.RealmConfiguration

/**
 * Created by zia on 2018/11/15.
 */
class App : Application() {

    private lateinit var configPreferences: SharedPreferences


    override fun onCreate() {
        super.onCreate()
        instance=this
        init()
        initDataBase()
    }

    private fun init() {
        configPreferences = getSharedPreferences("CONFIG", 0)
    }


    /**
     * 初始化数据库
     */
    private fun initDataBase() {
        Realm.init(this)
        val config = RealmConfiguration.Builder()
            .name("myrealm.realm")
            .schemaVersion(1)
            .deleteRealmIfMigrationNeeded()
            .build()
        Realm.setDefaultConfiguration(config)
    }

    companion object {
        private lateinit var instance: App

        fun getConfigPreferences(): SharedPreferences {
            return getInstance().configPreferences
        }

        fun getInstance(): App {
            return instance
        }

        fun getAppResources(): Resources {
            return getInstance().resources
        }
    }

    fun isNightTheme(): Boolean {
        return configPreferences.getBoolean("nightTheme", false)
    }
}
