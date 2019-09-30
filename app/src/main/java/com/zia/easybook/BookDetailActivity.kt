package com.zia.easybook

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.zia.easybookmodule.bean.Book
import com.zia.easybookmodule.bean.Catalog
import com.zia.easybookmodule.bean.Type
import com.zia.easybookmodule.engine.EasyBook
import com.zia.easybookmodule.rx.Disposable
import com.zia.easybookmodule.rx.Subscriber
import io.realm.Realm
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.android.synthetic.main.activity_book_detail.*

import java.io.File
import java.util.*

class BookDetailActivity : AppCompatActivity(), CatalogAdapter.CatalogSelectListener {
    private lateinit var book: Book
    private lateinit var adapter: CatalogAdapter
    //控制内存泄漏
    private var downloadDisposable: Disposable? = null
    private var searchDisposable: Disposable? = null
    private var list = ArrayList<Catalog>()
    private var isSort: Boolean = false
    private var realm: Realm? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_detail)
        StatusBarUtil.translucentBar(this)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { finish() }
        toolbar_layout.setContentScrimColor(resources.getColor(R.color.colorPrimaryDark))
        book = intent.getSerializableExtra("book") as Book
        realm = Realm.getDefaultInstance()

        initBookInfo()

        adapter = CatalogAdapter(this)
        catalog_rv.layoutManager = LinearLayoutManager(this)
        catalog_rv.adapter = adapter

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

    /**
     * 没有添加动态权限，需要手动打开一下
     */
    private fun download(type: Type) {
        Toast.makeText(this@BookDetailActivity, "请手动打开文件读写权限", Toast.LENGTH_SHORT).show()
        downloadDisposable = EasyBook.download(book)
            .setType(type)
            .setThreadCount(150)
            .setSavePath(Environment.getExternalStorageDirectory().path + File.separator + "book")
            .subscribe(object : Subscriber<File> {
                override fun onFinish(t: File) {
                    Log.e("CatalogActivity", t.path)
                    Toast.makeText(this@BookDetailActivity, "保存成功，位置在${t.path}", Toast.LENGTH_SHORT).show()
                }

                override fun onError(e: java.lang.Exception) {
                    e.printStackTrace()
                    Toast.makeText(this@BookDetailActivity, e.message, Toast.LENGTH_SHORT).show()
                }

                override fun onMessage(message: String) {

                }

                override fun onProgress(progress: Int) {

                }
            })
    }

    override fun onDestroy() {
        downloadDisposable?.dispose()
        searchDisposable?.dispose()
        super.onDestroy()
    }

    override fun onCatalogSelect(itemView: View, position: Int, catalog: Catalog) {
        SPUtils.setObject(this, "catalog" + book.bookName + book.author + book.siteName, catalog)
        val intent = Intent(this@BookDetailActivity, PreviewActivity::class.java)
        intent.putExtra("catalog", catalog)
        intent.putExtra("book", book)
        startActivity(intent)
    }

    private fun chooseType() {
        val types = arrayOf("EPUB", "TXT")
        val style =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) android.R.style.Theme_Material_Light_Dialog
            else android.R.style.Theme_DeviceDefault_Light_Dialog
        AlertDialog.Builder(this, style)
            .setTitle("选择下载格式")
            .setItems(types) { dialog, which ->
                var type = Type.EPUB
                when (which) {
                    0 -> {
                        type = Type.EPUB
                    }
                    1 -> {
                        type = Type.TXT
                    }
                }
                download(type)
            }.show()
    }

    private fun initBookInfo() {
        bookName.text = book.bookName
        bookauthor.text = book.author
        booklastUpdateChapter.text = "最新：${book.lastChapterName}"
        booksite.text = book.siteName
        booklastUpdateTime.text = "更新：${book.lastUpdateTime}"
        catalogintro.text = book.introduce
        sort.setOnClickListener {
            isSort = !isSort
            list.reverse() // 倒序排列
            adapter.freshCatalogs(list)
            if (isSort) {
                sort.setImageResource(R.mipmap.ic_action_arrow_top)
            } else {
                sort.setImageResource(R.mipmap.ic_action_arrow_bottom)
            }
        }
        add.setOnClickListener {
            realm?.executeTransaction {
                var ishasbook = false;
                var list = realm!!.where(BookRealm::class.java).findAll()
                for (item in list) {
                    if (item.bookName.equals(book.bookName)
                        && item.author.equals(book.author)
                        && item.siteName.equals(book.siteName)
                    ) {
                        ishasbook = true;
                        break
                    }
                }
                if (!ishasbook) {
                    var data = realm!!.createObject(BookRealm::class.java)
                    if (data != null) {
                        data.author = book.author
                        data.bookName = book.bookName
                        data.chapterSize = book.chapterSize
                        data.classify = book.classify
                        data.imageUrl = book.imageUrl
                        data.introduce = book.introduce
                        data.lastChapterName = book.lastChapterName
                        data.lastUpdateTime = book.lastUpdateTime
                        data.siteName = book.siteName
                        data.status = book.status
                        data.url = book.url
                    }
                }
            }
        }

        val options = RequestOptions()
            .centerCrop()
            .placeholder(R.mipmap.icon_default)
            .error(R.mipmap.icon_default)
            .fallback(R.mipmap.icon_default)
            .diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with(this).load(book.imageUrl).apply(options).into(book_bg)


        if (TextUtils.isEmpty(book.imageUrl)) {
            Glide.with(this).load(R.mipmap.icon_default)
                .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 3)))
                .into(bg_shape)
        } else {
            Glide.with(this).load(book.imageUrl)
                .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 3)))
                .into(bg_shape)
        }


    }


}
