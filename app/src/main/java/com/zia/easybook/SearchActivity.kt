package com.zia.easybook

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.zia.easybook.Dao.MergerBook
import com.zia.easybook.utils.CustomSite
import com.zia.easybook.widget.CustomProgressDialog
import com.zia.easybookmodule.bean.Book
import com.zia.easybookmodule.engine.EasyBook
import com.zia.easybookmodule.rx.Disposable
import com.zia.easybookmodule.rx.StepSubscriber
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : AppCompatActivity(), SearchAdapter.BookSelectListener {

    private lateinit var bookAdapter: SearchAdapter
    private var searchDisposable: Disposable? = null
    private lateinit var progressDialog: CustomProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        bookAdapter = SearchAdapter(this, this)
        progressDialog = CustomProgressDialog(this)
        searchRv.layoutManager = LinearLayoutManager(this)
        searchRv.adapter = bookAdapter

        //添加自定义站点
        CustomSite.addCustomSite()
        back.setOnClickListener { finish() }
        main_et.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                //关闭软键盘
                search()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        search.setOnClickListener {
            search()
        }
    }


    private fun search() {
        progressDialog.show()
        val bookName = main_et.text.toString()
        if (bookName.isEmpty()) return

        bookAdapter.clear()
        searchDisposable = EasyBook.search(bookName)
            .subscribe(object : StepSubscriber<List<Book>> {
                override fun onPart(t: List<Book>) {
                    //注意rv不能并发操作
                   /* searchRv.post {
                        progressDialog.dismiss()
                        val list = bookAdapter.merge(bookName, ArrayList(t))
                        bookAdapter.freshBooks(list)
                        searchRv.scrollToPosition(0)
                    }*/
                }

                override fun onFinish(t: List<Book>) {
                    Log.e("SearchActivity", ArrayList<Book>(t).toString())
                    searchRv.post {
                        progressDialog.dismiss()
                        val list = bookAdapter.merge(bookName, ArrayList(t))
                        bookAdapter.freshBooks(list)
                        searchRv.scrollToPosition(0)
                    }
                }

                override fun onError(e: Exception) {
                }

                override fun onMessage(message: String) {
                }

                override fun onProgress(progress: Int) {
                }
            })
    }

    override fun onBookSelect(itemView: View, book: MergerBook) {
        val intent = Intent(this@SearchActivity, BookDetailActivity::class.java)
        intent.putExtra("book", book)
        startActivity(intent)
    }

    override fun onDestroy() {
        searchDisposable?.dispose()
        super.onDestroy()
    }
}