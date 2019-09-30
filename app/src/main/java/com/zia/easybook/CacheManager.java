package com.zia.easybook;

import android.app.Activity;
import com.zia.easybookmodule.bean.Catalog;

import java.util.ArrayList;
import java.util.List;

/**
 * author: kang4
 * Date: 2019/9/20
 * Description:
 */
public class CacheManager {

    public static CacheManager manager;
    public ArrayList<Catalog> mlist=new ArrayList<>();

    public static CacheManager getSingleton() {
        if (manager == null) {
            synchronized (CacheManager.class) {
                if (manager == null) {
                    manager = new CacheManager();
                }
            }
        }
        return manager;
    }

    public void setCatalogList(ArrayList<Catalog> list){
        this.mlist=list;
    }

    public ArrayList<Catalog> getList(){
        return mlist;
    }
}
