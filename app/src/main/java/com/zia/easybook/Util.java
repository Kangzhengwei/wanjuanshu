package com.zia.easybook;

/**
 * author: kang4
 * Date: 2019/9/20
 * Description:
 */
public class Util {

    private static long lastTime = 0;

    public static boolean getInterval() {
        if ((System.currentTimeMillis() - lastTime) > 2000) {
            lastTime = System.currentTimeMillis();
            return true;
        } else {
            return false;
        }
    }

}
