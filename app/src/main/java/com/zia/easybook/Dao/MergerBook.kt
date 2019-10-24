package com.zia.easybook.Dao

import android.text.TextUtils
import java.io.Serializable

/**
 * author: kang4
 * Date: 2019/10/10
 * Description:
 */

class MergerBook : Serializable {

    var bookName = ""
    var author = ""
    //小说目录页地址
    var imageUrl = ""
    //章节数量
    var chapterSize = ""
    //最后更新时间
    var lastUpdateTime = ""
    //最新章节名
    var lastChapterName = ""

    //小说简介
    var introduce = ""
    //小说分类
    var classify = ""
    //小说状态，连载，完结等
    var status = ""

    var list = ArrayList<Site>()


    class Site : Serializable {
        var url = ""
        var siteName = ""  //小说网站名字

        override fun toString(): String {
            return "Site{" +
                    ",siteName=" + siteName +
                    ",url=" + url +
                    "}"

        }
    }


    override fun equals(other: Any?): Boolean {
        ///return super.equals(other)
        val mergerBook = other as MergerBook

        if (bookName == mergerBook.bookName && author == mergerBook.author) {
            if (!TextUtils.isEmpty(imageUrl)) {
                mergerBook.imageUrl = imageUrl
            }
            mergerBook.list.addAll(list)
        }
        return bookName == mergerBook.bookName && author == mergerBook.author
    }

    override fun hashCode(): Int {
        val str = bookName + author
        return str.hashCode()
    }

    override fun toString(): String {
        return "Book{" +
                "bookName=" + bookName +
                ", author=" + author +
                ", imageUrl=" + imageUrl +
                "," + list.toString()
        "}"
    }

    companion object {
        fun compare(targetName: String, o1: MergerBook, o2: MergerBook): Int {
            if (o1.bookName == targetName && o2.bookName != targetName) {
                return -1
            } else if (o1.bookName != targetName && o2.bookName == targetName) {
                return 1
            } else if (o1.bookName.contains(targetName) && !o2.bookName.contains(targetName)) {
                return -1
            } else if (!o1.bookName.contains(targetName) && o2.bookName.contains(targetName)) {
                return 1
            } else if (o1.bookName.contains(targetName) && o2.bookName.contains(targetName)) {
                return o1.bookName.indexOf(targetName) - o2.bookName.indexOf(targetName)
            } else if (o1.bookName.length == targetName.length && o2.bookName.length != targetName.length) {
                return -1
            } else if (o1.bookName.length != targetName.length && o2.bookName.length == targetName.length) {
                return 1
            }
            return 0
        }
    }


}
