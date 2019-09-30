package com.zia.easybook;

import com.zia.easybookmodule.bean.Catalog;
import io.realm.RealmObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * author: kang4
 * Date: 2019/9/18
 * Description:
 */
public class BookRealm extends RealmObject implements Serializable {
    private String bookName = "";
    private String author = "";
    //小说目录页地址
    private String url = "";
    private String imageUrl = "";
    //章节数量
    private String chapterSize = "";
    //最后更新时间
    private String lastUpdateTime = "";
    //最新章节名
    private String lastChapterName = "";
    //小说网站名字
    private String siteName = "";
    //小说简介
    private String introduce = "";
    //小说分类
    private String classify = "";
    //小说状态，连载，完结等
    private String status = "";


    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getChapterSize() {
        return chapterSize;
    }

    public void setChapterSize(String chapterSize) {
        this.chapterSize = chapterSize;
    }

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getLastChapterName() {
        return lastChapterName;
    }

    public void setLastChapterName(String lastChapterName) {
        this.lastChapterName = lastChapterName;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getClassify() {
        return classify;
    }

    public void setClassify(String classify) {
        this.classify = classify;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static int compare(String targetName, BookRealm o1, BookRealm o2) {
        if (o1.getBookName().equals(targetName) && !o2.getBookName().equals(targetName)) {
            return -1;
        } else if (!o1.getBookName().equals(targetName) && o2.getBookName().equals(targetName)) {
            return 1;
        }
        //包含了字符
        else if (o1.getBookName().contains(targetName) && !o2.getBookName().contains(targetName)) {
            return -1;
        } else if (!o1.getBookName().contains(targetName) && o2.getBookName().contains(targetName)) {
            return 1;
        } else if (o1.getBookName().contains(targetName) && o2.getBookName().contains(targetName)) {
            return o1.getBookName().indexOf(targetName) - o2.getBookName().indexOf(targetName);
        }
        //长度相同
        else if (o1.getBookName().length() == targetName.length()
                && o2.getBookName().length() != targetName.length()) {
            return -1;
        } else if (o1.getBookName().length() != targetName.length()
                && o2.getBookName().length() == targetName.length()) {
            return 1;
        }
        return 0;
    }
}
