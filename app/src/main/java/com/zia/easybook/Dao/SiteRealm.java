package com.zia.easybook.Dao;

import io.realm.RealmObject;

/**
 * author: kang4
 * Date: 2019/10/10
 * Description:
 */
public class SiteRealm extends RealmObject {
    public String url = "";
    public String siteName = "";

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
