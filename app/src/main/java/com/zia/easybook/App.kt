package com.zia.easybook

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration

/**
 * Created by zia on 2018/11/15.
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        /* if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);*/
        initDataBase()
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
}
