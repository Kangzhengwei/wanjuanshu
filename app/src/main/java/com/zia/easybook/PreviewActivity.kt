package com.zia.easybook

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.zia.easybookmodule.bean.Book
import com.zia.easybookmodule.bean.Catalog
import com.zia.easybookmodule.bean.Chapter
import com.zia.easybookmodule.engine.EasyBook
import com.zia.easybookmodule.engine.strategy.TxtParser
import com.zia.easybookmodule.rx.Disposable
import com.zia.easybookmodule.rx.Subscriber
import kotlinx.android.synthetic.main.activity_preview.*

class PreviewActivity : AppCompatActivity(), CatalogAdapter.CatalogSelectListener {


    private lateinit var catalog: Catalog
    private lateinit var book: Book
    private val parser = TxtParser()
    private var list = ArrayList<Catalog>()
    private var searchDisposable: Disposable? = null
    private var index: Int = 0
    private lateinit var adapter: CatalogAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview)
        catalog = intent.getSerializableExtra("catalog") as Catalog
        book = intent.getSerializableExtra("book") as Book

        adapter = CatalogAdapter(this)
        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.adapter = adapter

        last_catalog.setOnClickListener {
            index -= 1
            if (index >= 0) {
                var cata = list.get(index)
                catalog = cata;
                getContent()
            }
        }
        next_catalog.setOnClickListener {
            index += 1
            var cata = list.get(index)
            catalog = cata
            getContent()
        }
        getContent()
        getCatalogList()
    }

    private fun getContent() {
        EasyBook.getContent(book, catalog)
            .subscribe(object : Subscriber<List<String>> {
                override fun onFinish(t: List<String>) {
                    Log.d("PreviewActivity", ArrayList<String>(t).toString())
                    val chapter = Chapter(catalog.chapterName, catalog.index, t)
                    preview_tv.text = parser.parseContent(chapter)
                    scrollView.smoothScrollTo(0, 0)
                }

                override fun onError(e: Exception) {
                    Toast.makeText(this@PreviewActivity, e.message, Toast.LENGTH_SHORT).show()
                }

                override fun onMessage(message: String) {
                }

                override fun onProgress(progress: Int) {
                }
            })
    }

    private fun getCatalogList() {
        searchDisposable = EasyBook.getCatalog(book)
            .subscribe(object : Subscriber<List<Catalog>> {
                @SuppressLint("SetTextI18n")
                override fun onFinish(t: List<Catalog>) {
                    list = java.util.ArrayList<Catalog>(t)
                    adapter.freshCatalogs(list)
                    for (a in list.indices) {
                        val cata = list.get(a)
                        if (cata.chapterName.equals(catalog.chapterName)) {
                            index = a
                            break
                        }
                    }
                    recyclerview.scrollToPosition(index)

                }

                override fun onError(e: Exception) {
                }

                override fun onMessage(message: String) {

                }

                override fun onProgress(progress: Int) {
                }

            })
    }

    override fun onDestroy() {
        searchDisposable?.dispose()
        super.onDestroy()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        SPUtils.setObject(this, "catalog" + book.bookName + book.author + book.siteName, catalog)

    }

    override fun onCatalogSelect(itemView: View, position: Int, catalog: Catalog) {
        this.catalog=catalog
        getContent()
    }
}
