package com.zia.easybook

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.zia.easybook.Dao.BookRealm
import com.zia.easybook.Dao.MergerBook
import com.zia.easybook.Dao.SiteRealm
import com.zia.easybook.utils.SPUtils
import com.zia.easybook.utils.StatusBarUtil
import com.zia.easybookmodule.bean.Book
import com.zia.easybookmodule.bean.Catalog
import com.zia.easybookmodule.engine.EasyBook
import com.zia.easybookmodule.rx.Disposable
import com.zia.easybookmodule.rx.Subscriber
import io.realm.Realm
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.android.synthetic.main.activity_book_detail.*
import java.util.*

class BookDetailActivity : AppCompatActivity() {
    private lateinit var mergerBook: MergerBook
    private lateinit var adapter: CatalogAdapter
    private lateinit var book: Book
    //控制内存泄漏
    private var downloadDisposable: Disposable? = null
    private var searchDisposable: Disposable? = null
    private var list = ArrayList<Catalog>()
    private var realm: Realm? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_detail)
        StatusBarUtil.translucentBar(this)
        setSupportActionBar(toolbar)
        initView()
        initBook()
        initData()
        initBookInfo()
    }

    @Suppress("DEPRECATION")
    private fun initView() {
        realm = Realm.getDefaultInstance()
        mergerBook = intent.getSerializableExtra("book") as MergerBook
        supportActionBar?.title = mergerBook.bookName
        toolbar.setNavigationOnClickListener { finish() }
        toolbar_layout.setContentScrimColor(resources.getColor(R.color.colorPrimaryDark))
        adapter = CatalogAdapter()
        catalog_rv.layoutManager = LinearLayoutManager(this)
        catalog_rv.adapter = adapter
        adapter.setOnItemClickListener(object : CatalogAdapter.onItemClickListener {
            override fun onItemClick(catalog: Catalog) {
                SPUtils.setObject(this@BookDetailActivity, mergerBook.bookName + mergerBook.author, catalog)
                val intent = Intent(this@BookDetailActivity, ReadActivity::class.java)
                intent.putExtra("catalog", catalog)
                intent.putExtra("book", mergerBook)
                startActivity(intent)
                finish()
            }
        })
    }

    private fun initData() {
        searchDisposable = EasyBook.getCatalog(book)
                .subscribe(object : Subscriber<List<Catalog>> {
                    @SuppressLint("SetTextI18n")
                    override fun onFinish(t: List<Catalog>) {
                        val arrayList = ArrayList<Catalog>(t)
                        list = arrayList
                        adapter.freshCatalogs(arrayList)
                    }

                    override fun onError(e: Exception) {
                        Toast.makeText(this@BookDetailActivity, e.message, Toast.LENGTH_SHORT).show()
                    }

                    override fun onMessage(message: String) {

                    }

                    override fun onProgress(progress: Int) {
                    }

                })
    }

    private fun initBook() {
        book = Book(mergerBook.bookName, mergerBook.author, mergerBook.list[0].url, mergerBook.imageUrl, mergerBook.chapterSize, mergerBook.lastUpdateTime, mergerBook.lastChapterName, mergerBook.list[0].siteName, mergerBook.introduce, mergerBook.classify)
    }

    override fun onDestroy() {
        downloadDisposable?.dispose()
        searchDisposable?.dispose()
        super.onDestroy()
    }


    @SuppressLint("SetTextI18n")
    private fun initBookInfo() {
        bookName.text = mergerBook.bookName
        bookauthor.text = mergerBook.author
        booklastUpdateChapter.text = "最新：${mergerBook.lastChapterName}"
        booklastUpdateTime.text = "更新：${mergerBook.lastUpdateTime}"
        catalogintro.text = mergerBook.introduce

        add.setOnClickListener {
            realm?.executeTransaction {
                var ishasbook = false
                val list = realm!!.where(BookRealm::class.java).findAll()
                for (item in list) {
                    if (item.bookName == mergerBook.bookName && item.author == mergerBook.author) {
                        ishasbook = true
                        Toast.makeText(this@BookDetailActivity, "已添加", Toast.LENGTH_SHORT).show()
                        break
                    }
                }
                if (!ishasbook) {
                    val data = realm!!.createObject(BookRealm::class.java)
                    if (data != null) {
                        data.author = mergerBook.author
                        data.bookName = mergerBook.bookName
                        data.chapterSize = mergerBook.chapterSize
                        data.classify = mergerBook.classify
                        data.imageUrl = mergerBook.imageUrl
                        data.introduce = mergerBook.introduce
                        data.lastChapterName = mergerBook.lastChapterName
                        data.lastUpdateTime = mergerBook.lastUpdateTime
                        data.status = mergerBook.status
                        for (site in mergerBook.list) {
                            val relmSite = SiteRealm()
                            relmSite.siteName = site.siteName
                            relmSite.url = site.url
                            data.list.add(relmSite)
                        }
                    }
                    Toast.makeText(this@BookDetailActivity, "添加成功", Toast.LENGTH_SHORT).show()
                }
            }
        }

        val options = RequestOptions()
                .centerCrop()
                .placeholder(R.mipmap.icon_default)
                .error(R.mipmap.icon_default)
                .fallback(R.mipmap.icon_default)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
        Glide.with(this).load(mergerBook.imageUrl).apply(options).into(book_bg)


        if (TextUtils.isEmpty(mergerBook.imageUrl)) {
            Glide.with(this).load(R.mipmap.icon_default)
                    .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 3)))
                    .into(bg_shape)
        } else {
            Glide.with(this).load(mergerBook.imageUrl)
                    .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 3)))
                    .into(bg_shape)
        }
    }

}
