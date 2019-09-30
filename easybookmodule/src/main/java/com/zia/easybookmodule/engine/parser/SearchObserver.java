package com.zia.easybookmodule.engine.parser;

import com.zia.easybookmodule.bean.Book;
import com.zia.easybookmodule.engine.Platform;
import com.zia.easybookmodule.engine.Site;
import com.zia.easybookmodule.rx.Disposable;
import com.zia.easybookmodule.rx.Observer;
import com.zia.easybookmodule.rx.StepSubscriber;
import com.zia.easybookmodule.rx.Subscriber;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zia on 2018/11/14.
 * 并发搜索
 */
public class SearchObserver implements Observer<List<Book>> {

    private String bookName;
    private List<Site> sites;
    private ConcurrentLinkedQueue<List<Book>> bookListList = new ConcurrentLinkedQueue<>();
    private ExecutorService service;
    private int maxResult = 100;

    private Platform platform = Platform.get();
    volatile private boolean attachView = true;

    private boolean used = false;

    public SearchObserver(String bookName, List<Site> sites) {
        this.bookName = bookName;
        this.sites = sites;
        service = Executors.newFixedThreadPool(sites.size() + 1);
    }

    @Override
    public Disposable subscribe(final Subscriber<List<Book>> subscriber) {
        if (used) {
            subscriber.onError(new IllegalAccessException("一个SearchObserver只能使用一次"));
            return this;
        }
        used = true;
        service.execute(new Runnable() {
            @Override
            public void run() {
                final CountDownLatch countDownLatch = new CountDownLatch(sites.size());
                //并发搜索
                for (final Site site : sites) {
                    service.execute(new Runnable() {
                        @Override
                        public void run() {
                            List<Book> results = null;
                            post(new Runnable() {
                                @Override
                                public void run() {
                                    subscriber.onMessage("正在搜索" + site.getSiteName());
                                }
                            });
                            try {
                                results = site.search(bookName);
                                for (Book book : results) {
                                    book.setBookName(book.getBookName().replaceAll("[《》]", ""));
                                }
                            } catch (final Exception e) {
                                post(new Runnable() {
                                    @Override
                                    public void run() {
                                        subscriber.onMessage(e.toString());
                                    }
                                });
                            } finally {
                                countDownLatch.countDown();
                                post(new Runnable() {
                                    @Override
                                    public void run() {
                                        subscriber.onProgress(100 - (int) (countDownLatch.getCount() / (float) sites.size() * 100));
                                    }
                                });
                            }
                            if (results == null) {
                                post(new Runnable() {
                                    @Override
                                    public void run() {
                                        subscriber.onMessage(site.getSiteName() + "搜索遇到错误");
                                    }
                                });
                                return;
                            }
                            sortBook(results);
                            if (subscriber instanceof StepSubscriber) {
                                ((StepSubscriber<List<Book>>) subscriber).onPart(results);
                            }
                            bookListList.add(results);
                        }
                    });
                }
                //等待全部搜索完毕
                try {
                    countDownLatch.await();
                } catch (final InterruptedException e) {
                    post(new Runnable() {
                        @Override
                        public void run() {
                            subscriber.onError(e);
                        }
                    });
                } finally {
                    service.shutdown();
                }
                //获得所有结果，开始解析
                int resultSize = 0;
                for (List<Book> bookList : bookListList) {
                    resultSize += bookList.size();
                }

                if (resultSize == 0) {
                    post(new Runnable() {
                        @Override
                        public void run() {
                            subscriber.onMessage("没有搜索到书籍");
                        }
                    });
                }

                //混合插入，让每个站点依次加入集合，有利于结果正确性
                final ArrayList<Book> bookList = new ArrayList<>();
                int index = 0;
                while (bookList.size() < resultSize && bookList.size() <= maxResult) {
                    for (List<Book> bl : bookListList) {
                        if (index < bl.size()) {
                            bookList.add(bl.get(index));
                        }
                    }
                    index++;
                }
                //微调排序，名字相同的在前，以搜索名开头的在前，长度相同的在前
                sortBook(bookList);

                //切换线程返回结果
                post(new Runnable() {
                    @Override
                    public void run() {
                        subscriber.onMessage("搜索到" + bookList.size() + "本相关书籍");
                        subscriber.onFinish(bookList);
                    }
                });
            }
        });
        return this;
    }

    @Override
    @Deprecated
    public List<Book> getSync() throws Exception {
        return new ArrayList<>();
    }

    /**
     * 排序，名字一样的在前面，其余依次是包含了书名，长度一样和剩下的
     *
     * @param books
     */
    private void sortBook(List<Book> books) {
        Collections.sort(books, new Comparator<Book>() {
            @Override
            public int compare(Book o1, Book o2) {
                return Book.compare(bookName, o1, o2);
            }
        });
    }

    @Override
    public void dispose() {
        if (service != null) {
            service.shutdownNow();
        }
        attachView = false;
    }

    private void post(Runnable runnable) {
        if (attachView) {
            platform.defaultCallbackExecutor().execute(runnable);
        }
    }

    public SearchObserver setMaxResult(int maxResult) {
        this.maxResult = maxResult;
        return this;
    }
}
