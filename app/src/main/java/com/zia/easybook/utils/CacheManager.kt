package com.zia.easybook.utils

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
