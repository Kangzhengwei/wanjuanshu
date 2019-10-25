package com.zia.easybook

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.zia.easybook.Dao.MergerBook
import com.zia.easybook.utils.SPUtils
import com.zia.easybook.utils.StatusBarUtil
import com.zia.easybook.widget.*
import com.zia.easybookmodule.bean.Book
import com.zia.easybookmodule.bean.Catalog
import kotlinx.android.synthetic.main.activity_preview.*
import kotlinx.android.synthetic.main.top_menu_layout.view.*

class ReadActivity : AppCompatActivity() {


    private lateinit var catalog: Catalog
    private lateinit var mergerBook: MergerBook
    private lateinit var book: Book

    private lateinit var adapter: CatalogAdapter

    private val readBookControl = ReadBookControl.getInstance()
    private var chapterList: List<Catalog> = ArrayList()
    private lateinit var mPageLoader: PageLoader

    private lateinit var menuTopIn: Animation
    private lateinit var menuTopOut: Animation
    private lateinit var menuBottomIn: Animation
    private lateinit var menuBottomOut: Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview)
        StatusBarUtil.fullScreen(this)
        StatusBarUtil.StatusBarLightMode(this, true)
        setSupportActionBar(topmenu.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        initData()
        initBook()
        initView()
    }

    private fun initData() {
        catalog = intent.getSerializableExtra("catalog") as Catalog
        mergerBook = intent.getSerializableExtra("book") as MergerBook
        readBookControl.initTextDrawableIndex()
        readBookControl.pageMode = 3
    }

    private fun initView() {
        menuBottomIn = AnimationUtils.loadAnimation(this, R.anim.anim_readbook_bottom_in)
        menuTopIn = AnimationUtils.loadAnimation(this, R.anim.anim_readbook_top_in)
        menuTopOut = AnimationUtils.loadAnimation(this, R.anim.anim_readbook_top_out)
        menuBottomOut = AnimationUtils.loadAnimation(this, R.anim.anim_readbook_bottom_out)
        menuTopIn.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                menu_layout.setOnClickListener {
                    menuMiss()
                }
            }

            override fun onAnimationStart(animation: Animation?) {
            }

        })
        menuBottomIn.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                menu_layout.setOnClickListener {
                    menuMiss()
                }
            }

            override fun onAnimationStart(animation: Animation?) {
            }

        })
        menuTopOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
            }

            override fun onAnimationStart(animation: Animation?) {
                menu_layout.setOnClickListener(null)
            }

        })
        menuBottomOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
            }

            override fun onAnimationStart(animation: Animation?) {
                menu_layout.setOnClickListener(null)
            }

        })
        supportActionBar?.title = mergerBook.bookName
        drawerlayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)//禁止滑动菜单

        //recyclerView
        adapter = CatalogAdapter()
        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.adapter = adapter
        adapter.setOnItemClickListener(object : CatalogAdapter.onItemClickListener {
            override fun onItemClick(catalog: Catalog) {
                this@ReadActivity.catalog = catalog
                this@ReadActivity.catalog.siteName = book.siteName
                this@ReadActivity.catalog.siteUrl = book.url
                drawerlayout.closeDrawer(slideLayout)
                mPageLoader.skipToChapter(catalog.index, catalog.durChapterPage)
            }
        })

        //pageview
        pageView.background = readBookControl.getTextBackground(this)
        mPageLoader = pageView.getPageLoader(this, book, catalog, object : PageLoader.Callback {

            override fun getChapterList(): List<Catalog> {
                return this@ReadActivity.chapterList
            }

            override fun onChapterChange(pos: Int) {
                this@ReadActivity.catalog = chapterList[pos]
                this@ReadActivity.catalog.siteName = book.siteName
                this@ReadActivity.catalog.siteUrl = book.url

            }

            override fun onCategoryFinish(chapters: List<Catalog>) {
                this@ReadActivity.chapterList = chapters
                adapter.freshCatalogs(ArrayList(chapters))
                recyclerview.scrollToPosition(catalog.index)
            }

            override fun onPageCountChange(count: Int) {

            }

            override fun onPageChange(chapterIndex: Int, pageIndex: Int, resetReadAloud: Boolean) {
                this@ReadActivity.catalog.durChapterPage = pageIndex
            }

            override fun vipPop() {
            }

        })
        pageView.setTouchListener(object : PageView.TouchListener {
            override fun onTouch() {
            }

            override fun onTouchClearCursor() {
            }

            override fun onLongPress() {
            }

            override fun center() {
                menuShow()
            }

        })
        bottommenu.setListener(object : BottomMenuLayout.Callback {
            override fun skipToPage(page: Int) {}

            override fun onMediaButton() {}

            override fun autoPage() {}

            override fun setNightTheme() {}

            override fun openReplaceRule() {}

            override fun openAdjust() {}

            override fun openReadInterface() {}

            override fun openMoreSetting() {}

            override fun toast(id: Int) {}

            override fun dismiss() {}

            override fun skipPreChapter() {}

            override fun skipNextChapter() {}

            override fun openChapterList() {
                drawerlayout.openDrawer(slideLayout)
                menuMiss()
            }
        })
        mPageLoader.refreshChapterList()
    }

    private fun initBook() {
        book = if (!TextUtils.isEmpty(catalog.siteName) && !TextUtils.isEmpty(catalog.siteUrl)) {
            Book(
                mergerBook.bookName,
                mergerBook.author,
                catalog.siteUrl,
                mergerBook.imageUrl,
                mergerBook.chapterSize,
                mergerBook.lastUpdateTime,
                mergerBook.lastChapterName,
                catalog.siteName,
                mergerBook.introduce,
                mergerBook.classify
            )
        } else {
            Book(
                mergerBook.bookName,
                mergerBook.author,
                mergerBook.list[0].url,
                mergerBook.imageUrl,
                mergerBook.chapterSize,
                mergerBook.lastUpdateTime,
                mergerBook.lastChapterName,
                mergerBook.list[0].siteName,
                mergerBook.introduce,
                mergerBook.classify
            )
        }
        Log.d("ReadActivity", book.toString())
    }

    fun menuShow() {
        menu_layout.visibility = View.VISIBLE
        topmenu.visibility = View.VISIBLE
        topmenu.startAnimation(menuTopIn)
        bottommenu.visibility = View.VISIBLE
        bottommenu.startAnimation(menuBottomIn)
    }

    fun menuMiss() {
        menu_layout.visibility = View.INVISIBLE
        topmenu.visibility = View.INVISIBLE
        topmenu.startAnimation(menuTopOut)
        bottommenu.visibility = View.INVISIBLE
        bottommenu.startAnimation(menuBottomOut)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_menu_bar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
            R.id.action_change_source -> {
                menuMiss()
                ChangeSourceDialog.builder(this@ReadActivity)
                    .setData(mergerBook.list, book.url)
                    .setListener(object : ChangeSourceDialog.itemClickListener {
                        override fun itemClick(site: MergerBook.Site) {
                            book.url = site.url
                            book.siteName = site.siteName
                            catalog.siteName = site.siteName
                            catalog.siteUrl = site.url
                            mPageLoader.setStatus(TxtChapter.Status.CHANGE_SOURCE)
                            if (mPageLoader is PageLoaderNet) {
                                (mPageLoader as PageLoaderNet).changeSourceFinish(book)
                            }
                        }
                    }).show()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        SPUtils.setObject(this, mergerBook.bookName + mergerBook.author, catalog)
        if (mPageLoader is PageLoaderNet) {
            mPageLoader.closeBook()
        }
        super.onBackPressed()

    }

}
