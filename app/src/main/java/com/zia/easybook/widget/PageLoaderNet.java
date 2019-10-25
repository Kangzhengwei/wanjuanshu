package com.zia.easybook.widget;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;

import com.zia.easybook.utils.NetworkUtils;
import com.zia.easybookmodule.bean.Book;
import com.zia.easybookmodule.bean.Catalog;
import com.zia.easybookmodule.engine.EasyBook;
import com.zia.easybookmodule.rx.Disposable;
import com.zia.easybookmodule.rx.Observer;
import com.zia.easybookmodule.rx.Subscriber;

import java.util.ArrayList;
import java.util.List;

/**
 * 网络页面加载器
 */

public class PageLoaderNet extends PageLoader {

    private List<String> downloadingChapterList = new ArrayList<>();
    private Disposable disposable;

    PageLoaderNet(PageView pageView, Book book, Catalog catalog, Callback callback) {
        super(pageView, book, catalog, callback);
    }

    @Override
    public void refreshChapterList() {
        if (!callback.getChapterList().isEmpty()) {
            isChapterListPrepare = true;
            // 打开章节
            skipToChapter(catalog.getIndex(), catalog.getDurChapterPage());
        } else {
            disposable = EasyBook.getCatalog(book).subscribe(new Subscriber<List<Catalog>>() {
                @Override
                public void onFinish(@NonNull List<Catalog> catalogs) {
                    book.setChapterSize(String.valueOf(catalogs.size()));
                    isChapterListPrepare = true;
                    // 目录加载完成
                    callback.onCategoryFinish(catalogs);
                    // 加载并显示当前章节
                    skipToChapter(catalog.getIndex(), catalog.getDurChapterPage());
                    Log.d("ReadActivity", book.getSiteName() + ":" + book.getUrl());
                }

                @Override
                public void onError(@NonNull Exception e) {

                }

                @Override
                public void onMessage(@NonNull String message) {

                }

                @Override
                public void onProgress(int progress) {

                }
            });

        }
    }


    public void changeSourceFinish(Book bookShelfBean) {
        if (bookShelfBean == null) {
            openChapter(catalog.getDurChapterPage());
        } else {
            this.book = bookShelfBean;
            refreshChapterList();
        }
    }

    @SuppressLint("DefaultLocale")
    private synchronized void loadContent(final int chapterIndex) {
        if (downloadingChapterList.size() >= 20) return;
        if (chapterIndex >= callback.getChapterList().size() || DownloadingList(listHandle.CHECK, callback.getChapterList().get(chapterIndex).getChapterName()))
            return;
        if (null != book && callback.getChapterList().size() > 0) {

        }
    }

    /**
     * 编辑下载列表
     */
    private synchronized boolean DownloadingList(listHandle editType, String value) {
        if (editType == listHandle.ADD) {
            downloadingChapterList.add(value);
            return true;
        } else if (editType == listHandle.REMOVE) {
            downloadingChapterList.remove(value);
            return true;
        } else {
            return downloadingChapterList.indexOf(value) != -1;
        }
    }

    /**
     * 章节下载完成
     */
    private void finishContent(int chapterIndex) {
        if (chapterIndex == mCurChapterPos) {
            super.parseCurChapter();
        }
        if (chapterIndex == mCurChapterPos - 1) {
            super.parsePrevChapter();
        }
        if (chapterIndex == mCurChapterPos + 1) {
            super.parseNextChapter();
        }
    }

    /**
     * 刷新当前章节
     */
    @SuppressLint("DefaultLocale")
    public void refreshDurChapter() {
        if (callback.getChapterList().isEmpty()) {
            updateChapter();
            return;
        }
        if (callback.getChapterList().size() - 1 < mCurChapterPos) {
            mCurChapterPos = callback.getChapterList().size() - 1;
        }
        skipToChapter(mCurChapterPos, 0);
    }

    @Override
    protected Observer<List<String>> getChapterContent(Catalog chapter) throws Exception {
        return EasyBook.getContent(book, chapter);
    }

    @Override
    protected boolean noChapterData(Catalog chapter) {
        return false;
    }


    private boolean shouldRequestChapter(Integer chapterIndex) {
        return NetworkUtils.isNetWorkAvailable() && noChapterData(callback.getChapterList().get(chapterIndex));
    }

    public boolean canParseInt(String str) {
        if (str == null) { //验证是否为空
            return false;
        }
        return str.matches("\\d+"); //使用正则表达式判断该字符串是否为数字，第一个\是转义符，\d+表示匹配1个或 //多个连续数字，"+"和"*"类似，"*"表示0个或多个
    }

    // 装载上一章节的内容
    @Override
    void parsePrevChapter() {
        if (mCurChapterPos >= 1) {
            loadContent(mCurChapterPos - 1);
        }
        super.parsePrevChapter();
    }

    // 装载当前章内容。
    @Override
    void parseCurChapter() {
        if (book == null || book.getChapterSize() == null || !canParseInt(book.getChapterSize())) {
            return;
        }
        for (int i = mCurChapterPos; i < Math.min(mCurChapterPos + 5, Integer.valueOf(book.getChapterSize())); i++) {
            loadContent(i);
        }
        super.parseCurChapter();
    }


    // 装载下一章节的内容
    @Override
    void parseNextChapter() {
        for (int i = mCurChapterPos; i < Math.min(mCurChapterPos + 5, Integer.valueOf(book.getChapterSize())); i++) {
            loadContent(i);
        }
        super.parseNextChapter();
    }

    @Override
    public void updateChapter() {
        // mPageView.getActivity().toast("目录更新中");

    }

    @Override
    public void closeBook() {
        if (disposable != null) {
            disposable.dispose();
        }
        super.closeBook();
    }

    public enum listHandle {
        ADD, REMOVE, CHECK
    }

}
