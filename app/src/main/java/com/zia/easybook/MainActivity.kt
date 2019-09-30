package com.zia.easybook


import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.zia.easybookmodule.bean.Book
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

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        startActivity(Intent(this@MainActivity, SearchActivity::class.java))
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        realm = Realm.getDefaultInstance()
        bookAdapter = RealmDataAdapter(this, this)
        book_list.layoutManager = LinearLayoutManager(this)
        book_list.adapter = bookAdapter
        initData()
        initView()
    }

    private fun initData() {
        realm?.executeTransaction { realm ->
            list = realm.where(BookRealm::class.java).findAll()
        }
        bookAdapter.freshBooks(list)
        bookAdapter.setOnLongClickListener(this)
    }

    private fun initView() {
        toolbar.setTitle(R.string.title)
        toolbar.setTitleTextColor(resources.getColor(R.color.white))
        toolbar.inflateMenu(R.menu.toolbar_menu)
        toolbar.setOnMenuItemClickListener(this)
    }

    override fun onBookSelect(itemView: View, book: BookRealm) {
        var mbook = Book(
            book.bookName,
            book.author,
            book.url,
            book.imageUrl,
            book.chapterSize,
            book.lastUpdateTime,
            book.lastChapterName,
            book.siteName,
            book.introduce,
            book.classify
        )
        val catalog =
            SPUtils.getObject(this, "catalog" + book.bookName + book.author + book.siteName, Catalog::class.java)

        if (catalog != null) {
            if (catalog is Catalog) {
                val intent1 = Intent(this@MainActivity, PreviewActivity::class.java)
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

}