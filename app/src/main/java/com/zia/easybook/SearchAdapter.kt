package com.zia.easybook

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.zia.easybook.Dao.MergerBook
import com.zia.easybookmodule.bean.Book
import kotlinx.android.synthetic.main.item_search.view.item_book_author
import kotlinx.android.synthetic.main.item_search.view.item_book_name
import kotlinx.android.synthetic.main.item_search.view.roundImageView
import kotlinx.android.synthetic.main.item_search_view.view.*

/**
 * Created by zia on 2018/11/1.
 */
class SearchAdapter(private val bookSelectListener: BookSelectListener, private var mContext: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var books = ArrayList<MergerBook>()

    fun freshBooks(books: ArrayList<MergerBook>) {
        this.books = books
        notifyDataSetChanged()
    }

    /**
     * 使用diffUtil分步添加数据
     * targetName:目标小说名字，排序依据这个名字排
     */
    /* fun addBooks(targetName: String, newDatas: List<Book>?) {
         if (newDatas == null || newDatas.isEmpty()) return
         val l = mergeBooks(targetName, newDatas)
         val diffResult = DiffUtil.calculateDiff(DiffCallBack(books, l), true)
         books = l
         diffResult.dispatchUpdatesTo(this)
     }*/

    //排序
    /*  private fun mergeBooks(bookName: String, newDatas: List<Book>): ArrayList<Book> {
          val result = ArrayList<Book>(books)
          result.addAll(newDatas)
          result.sortWith(Comparator { o1, o2 ->
              Book.compare(bookName, o1, o2)
          })
          return result
      }*/

    fun merge(bookName: String, t: ArrayList<Book>): ArrayList<MergerBook> {
        val list = ArrayList<MergerBook>()
        for (book in t) {
            val mergerBook = MergerBook()
            mergerBook.author = book.author
            mergerBook.bookName = book.bookName
            mergerBook.chapterSize = book.chapterSize
            mergerBook.classify = book.classify
            mergerBook.imageUrl = book.imageUrl
            mergerBook.introduce = book.introduce
            mergerBook.lastChapterName = book.lastChapterName
            mergerBook.lastUpdateTime = book.lastUpdateTime
            mergerBook.status = book.status
            val innersite = MergerBook.Site()
            innersite.siteName = book.siteName
            innersite.url = book.url
            mergerBook.list.add(innersite)
            list.add(mergerBook)
        }
        val set = HashSet<MergerBook>()
        val listNew = ArrayList<MergerBook>()
        set.addAll(list)
        listNew.addAll(set)
        listNew.sortWith(Comparator { o1, o2 ->
            MergerBook.compare(bookName, o1, o2)
        })
        return listNew
    }

    /*  private fun merge(t: ArrayList<Book>) {
          val treeSet = TreeSet(Comparator<Book> { o1, o2 ->
              val compareToName = o1.bookName.compareTo(o2.bookName)
              val compareToAuther = o1.author.compareTo(o2.author)
              if (compareToName == 0 && compareToAuther == 0) {
                  0
              } else {
                  1
              }
          })
          treeSet.addAll(t)
          val list = ArrayList<Book>(treeSet)
      }*/

    fun clear() {
        this.books.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.item_search_view, p0, false)
        return BookHolder(view)
    }

    override fun getItemCount(): Int {
        return books.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is BookHolder -> {
                val book = books[position]
                val options = RequestOptions()
                        .centerCrop()
                        .placeholder(R.mipmap.icon_default)
                        .error(R.mipmap.icon_default)
                        .fallback(R.mipmap.icon_default)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                Glide.with(mContext).load(book.imageUrl).apply(options).into(holder.itemView.roundImageView)
                holder.itemView.item_book_name.text = book.bookName
                holder.itemView.item_book_author.text = book.author
                holder.itemView.item_book_lastUpdateTime.text = "最后更新:" + book.lastUpdateTime
                holder.itemView.item_book_lastUpdateChapter.text = "最新:" + book.lastChapterName
                holder.itemView.setOnClickListener { bookSelectListener.onBookSelect(holder.itemView, book) }

            }
        }
    }

    class BookHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    interface BookSelectListener {
        fun onBookSelect(itemView: View, book: MergerBook)
    }

    /*  private inner class DiffCallBack(private val oldDatas: List<Book>, private val newDatas: List<Book>) :
          DiffUtil.Callback() {

          override fun areItemsTheSame(p0: Int, p1: Int): Boolean {
              return oldDatas[p0].bookName == newDatas[p1].bookName && oldDatas[p0].siteName == newDatas[p1].siteName
          }

          override fun getOldListSize(): Int {
              return oldDatas.size
          }

          override fun getNewListSize(): Int {
              return newDatas.size
          }

          override fun areContentsTheSame(p0: Int, p1: Int): Boolean {
              return oldDatas[p0].bookName == newDatas[p1].bookName && oldDatas[p0].siteName == newDatas[p1].siteName
          }

      }*/
}