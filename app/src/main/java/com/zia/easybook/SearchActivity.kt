package com.zia.easybook

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.zia.easybookmodule.bean.Book
import com.zia.easybookmodule.engine.EasyBook
import com.zia.easybookmodule.rx.Disposable
import com.zia.easybookmodule.rx.StepSubscriber
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : AppCompatActivity(), SearchAdapter.BookSelectListener {

    private lateinit var bookAdapter: SearchAdapter
    private var searchDisposable: Disposable? = null
    private lateinit var progressDialog :CustomProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        bookAdapter = SearchAdapter(this,this)
        progressDialog= CustomProgressDialog(this)
        searchRv.layoutManager = LinearLayoutManager(this)
        searchRv.adapter = bookAdapter

        //添加自定义站点
        CustomSite.addCustomSite()
        back.setOnClickListener { finish() }
        search.setOnClickListener {
            progressDialog.show()
            val bookName = main_et.text.toString()
            if (bookName.isEmpty()) return@setOnClickListener
            bookAdapter.clear()
            searchDisposable = EasyBook.search(bookName)
                .subscribe(object : StepSubscriber<List<Book>> {
                    override fun onPart(t: List<Book>) {
                        //注意rv不能并发操作
                        searchRv.post {
                            progressDialog.dismiss()
                            bookAdapter.addBooks(bookName, t)
                            searchRv.scrollToPosition(0)
                        }
                    }

                    override fun onFinish(t: List<Book>) {
                        Log.e("SearchActivity", ArrayList<Book>(t).toString())
//                        bookAdapter.freshBooks(ArrayList(t))
                    }

                    override fun onError(e: Exception) {
                        if (e.message != null) {
                            Log.e("SearchActivity", e.message)
                        }
                        Toast.makeText(this@SearchActivity, e.message, Toast.LENGTH_SHORT).show()
                    }

                    override fun onMessage(message: String) {
                    }

                    override fun onProgress(progress: Int) {
                    }
                })
        }
    }

    override fun onBookSelect(itemView: View, book: Book) {
        val intent = Intent(this@SearchActivity, BookDetailActivity::class.java)
        intent.putExtra("book", book)
        startActivity(intent)
    }

    override fun onDestroy() {
        searchDisposable?.dispose()
        super.onDestroy()
    }
}