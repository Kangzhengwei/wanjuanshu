package com.zia.easybook.Dao;

import io.realm.RealmList;
import io.realm.RealmObject;

import java.io.Serializable;

/**
 * author: kang4
 * Date: 2019/10/9
 * Description:
 */
public class BookRealm extends RealmObject implements Serializable {

    public String bookName = "";
    public String author = "";
    //小说目录页地址

    public String imageUrl = "";
    //章节数量
    public String chapterSize = "";
    //最后更新时间
    public String lastUpdateTime = "";
    //最新章节名
    public String lastChapterName = "";
    //小说网站名字
    //小说简介
    public String introduce = "";
    //小说分类
    public String classify = "";
    //小说状态，连载，完结等
    public String status = "";

    public RealmList<SiteRealm> list = new RealmList<>();


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


   /* public static class Site {
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
    }*/

    public static int compare(String targetName, BookRealm o1, BookRealm o2) {
        if (o1.bookName == targetName && o2.bookName != targetName) {
            return -1;
        } else if (o1.bookName != targetName && o2.bookName == targetName) {
            return 1;
        } else if (o1.bookName.contains(targetName) && !o2.bookName.contains(targetName)) {
            return -1;
        } else if (!o1.bookName.contains(targetName) && o2.bookName.contains(targetName)) {
            return 1;
        } else if (o1.bookName.contains(targetName) && o2.bookName.contains(targetName)) {
            return o1.bookName.indexOf(targetName) - o2.bookName.indexOf(targetName);
        } else if (o1.bookName.length() == targetName.length() && o2.bookName.length() != targetName.length()) {
            return -1;
        } else if (o1.bookName.length() != targetName.length() && o2.bookName.length() == targetName.length()) {
            return 1;
        }
        return 0;
    }

}
