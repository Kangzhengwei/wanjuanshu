package com.zia.easybook


import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.zia.easybook.Dao.BookRealm
import com.zia.easybook.Dao.MergerBook
import com.zia.easybook.utils.SPUtils
import com.zia.easybookmodule.bean.Catalog
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_main.*

/**
 * author: kang4
 * Date: 2019/9/17
 * Description:
 */

class MainActivity : AppCompatActivity(), androidx.appcompat.widget.Toolbar.OnMenuItemClickListener,
        RealmDataAdapter.BookSelectListener, RealmDataAdapter.OnLongClickListener {


    private var realm: Realm? = null
    private var list: List<BookRealm> = ArrayList<BookRealm>()
    private lateinit var bookAdapter: RealmDataAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        realm = Realm.getDefaultInstance()
        initView()
        initData()
    }

    private fun initData() {
        realm?.executeTransaction { realm ->
            list = realm.where(BookRealm::class.java).findAll()
        }
        bookAdapter.freshBooks(list)
    }

    private fun initView() {
        toolbar.setTitle(R.string.title)
        toolbar.setTitleTextColor(resources.getColor(R.color.white))
        toolbar.inflateMenu(R.menu.toolbar_menu)
        toolbar.setOnMenuItemClickListener(this)
        bookAdapter = RealmDataAdapter(this, this)
        book_list.layoutManager = GridLayoutManager(this,3)
        book_list.adapter = bookAdapter
        bookAdapter.setOnLongClickListener(this)
        swipeRefresh.setOnRefreshListener {
            initData()
            Handler().postDelayed({
                swipeRefresh.isRefreshing = false
            }, 500)
        }
    }

    override fun onBookSelect(itemView: View, book: BookRealm) {
        val mbook = MergerBook()
        mbook.author = book.author
        mbook.bookName = book.bookName
        mbook.chapterSize = book.chapterSize
        mbook.classify = book.classify
        mbook.imageUrl = book.imageUrl
        mbook.introduce = book.introduce
        mbook.lastChapterName = book.lastChapterName
        mbook.lastUpdateTime = book.lastUpdateTime
        mbook.status = book.status
        for (site in book.list) {
            val relmSite = MergerBook.Site()
            relmSite.siteName = site.siteName
            relmSite.url = site.url
            mbook.list.add(relmSite)
        }

        val catalog = SPUtils.getObject(this, book.bookName + book.author, Catalog::class.java)

        if (catalog != null) {
            if (catalog is Catalog) {
                val intent1 = Intent(this@MainActivity, ReadActivity::class.java)
                intent1.putExtra("catalog", catalog)
                intent1.putExtra("book", mbook)
                startActivity(intent1)
            }
        } else {
            val intent = Intent(this@MainActivity, BookDetailActivity::class.java)
            intent.putExtra("book", mbook)
            startActivity(intent)
        }

    }

    override fun onLongClickListener(itemView: View, book: BookRealm, position: Int) {
        realm?.executeTransaction { realm ->
            book.deleteFromRealm()
            list = realm.where(BookRealm::class.java).findAll()
            bookAdapter.notifyItemRemoved(position)
            bookAdapter.notifyItemRangeRemoved(position, list.size - position)
        }

    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        startActivity(Intent(this@MainActivity, SearchActivity::class.java))
        return true
    }


}