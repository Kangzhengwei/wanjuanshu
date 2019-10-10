package com.zia.easybook

import android.app.Activity
import com.zia.easybookmodule.bean.Catalog

import java.util.ArrayList

/**
 * author: kang4
 * Date: 2019/9/20
 * Description:
 */
open class CacheManager {

    companion object {
        val instance: CacheManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            CacheManager()
        }
    }

}
