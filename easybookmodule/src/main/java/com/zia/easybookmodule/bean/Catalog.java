package com.zia.easybookmodule.bean;

import java.io.Serializable;

/**
 * Created By zia on 2018/10/30.
 */
public class Catalog implements Serializable {
    private String chapterName;
    private String url;
    private int index;//目前章节数
    private int durChapterPage = 0;  // 当前章节位置   用页码
    private String siteName;
    private String siteUrl;


    public Catalog(String chapterName, String url, int index) {
        this.chapterName = chapterName;
        this.url = url;
        this.index = index;
    }

    public Catalog() {
    }

    @Override
    public String toString() {
        return "Catalog{" +
                "chapterName='" + chapterName + '\'' +
                ", url='" + url + '\'' +
                ", index=" + index +
                '}';
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getDurChapterPage() {
        return durChapterPage < 0 ? 0 : durChapterPage;
    }

    public void setDurChapterPage(int durChapterPage) {
        this.durChapterPage = durChapterPage;
    }


    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getSiteUrl() {
        return siteUrl;
    }

    public void setSiteUrl(String siteUrl) {
        this.siteUrl = siteUrl;
    }
}
