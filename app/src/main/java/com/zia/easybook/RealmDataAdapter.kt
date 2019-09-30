package com.zia.easybook

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.item_search.view.*

/**
 * author: kang4
 * Date: 2019/9/18
 * Description:
 */

class RealmDataAdapter(val bookSelectListener: BookSelectListener, var mContext: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var books: List<BookRealm> = ArrayList<BookRealm>()
    private lateinit var longClickListener: OnLongClickListener

    fun freshBooks(books: List<BookRealm>) {
        this.books = books
        notifyDataSetChanged()
    }

    /**
     * 使用diffUtil分步添加数据
     * targetName:目标小说名字，排序依据这个名字排
     */
    fun addBooks(targetName: String, newDatas: List<BookRealm>?) {
        if (newDatas == null || newDatas.isEmpty()) return
        val l = mergeBooks(targetName, newDatas)
        val diffResult = DiffUtil.calculateDiff(DiffCallBack(books, l), true)
        books = l
        diffResult.dispatchUpdatesTo(this)
    }

    //排序
    private fun mergeBooks(bookName: String, newDatas: List<BookRealm>): ArrayList<BookRealm> {
        val result = ArrayList<BookRealm>(books)
        result.addAll(newDatas)
        result.sortWith(Comparator { o1, o2 ->
            BookRealm.compare(bookName, o1, o2)
        })
        return result
    }


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.item_search, p0, false)
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
                var options = RequestOptions()
                    .centerCrop()
                    .placeholder(R.mipmap.icon_default)
                    .error(R.mipmap.icon_default)
                    .fallback(R.mipmap.icon_default)
                    .diskCacheStrategy(DiskCacheStrategy.ALL);
                Glide.with(mContext).load(book.imageUrl).apply(options)
                    .into(holder.itemView.roundImageView)
                holder.itemView.item_book_name.text = book.bookName
                holder.itemView.item_book_author.text = book.author
                holder.itemView.item_book_lastUpdateChapter.text = "最新：${book.lastChapterName}"
                holder.itemView.item_book_site.text = book.siteName
                holder.itemView.item_book_lastUpdateTime.text = "更新：${book.lastUpdateTime}"
                holder.itemView.setOnClickListener { bookSelectListener.onBookSelect(holder.itemView, book) }
                holder.itemView.setOnLongClickListener {
                    longClickListener.onLongClickListener(holder.itemView, book,position)
                    false
                }

            }
        }
    }

    class BookHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    interface BookSelectListener {
        fun onBookSelect(itemView: View, book: BookRealm)
    }

    interface OnLongClickListener {
        fun onLongClickListener(itemView: View, book: BookRealm,position: Int)
    }

    fun setOnLongClickListener(listener: OnLongClickListener) {
        this.longClickListener = listener
    }

    private inner class DiffCallBack(private val oldDatas: List<BookRealm>, private val newDatas: List<BookRealm>) :
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

    }

}