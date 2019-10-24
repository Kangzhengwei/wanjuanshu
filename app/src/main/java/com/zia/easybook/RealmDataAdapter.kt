package com.zia.easybook

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.zia.easybook.Dao.BookRealm
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

   /* private val width = DimenUtil.dip2px(mContext, 12f)
    private val singleWidth = (DimenUtil.getScreeWidth(mContext as Activity) - 4 * width) / 3
    private val singleHeight = (singleWidth * 1.25).toInt()
*/

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
                val options = RequestOptions()
                        .centerCrop()
                        .placeholder(R.mipmap.icon_default)
                        .error(R.mipmap.icon_default)
                        .fallback(R.mipmap.icon_default)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                Glide.with(mContext).load(book.imageUrl).apply(options).into(holder.itemView.roundImageView)
                holder.itemView.item_book_name.text = book.bookName
                holder.itemView.item_book_author.text = book.author
                holder.itemView.setOnClickListener { bookSelectListener.onBookSelect(holder.itemView, book) }
                holder.itemView.setOnLongClickListener {
                    longClickListener.onLongClickListener(holder.itemView, book, position)
                    false
                }
                /*val params = holder.itemView.roundImageView.layoutParams
                params.height = singleHeight
                params.width = singleWidth
                holder.itemView.roundImageView.layoutParams = params*/

                if (!TextUtils.isEmpty(book.imageUrl)) {
                    holder.itemView.item_book_name.visibility = View.INVISIBLE
                    holder.itemView.item_book_author.visibility = View.INVISIBLE
                } else {
                    holder.itemView.item_book_name.visibility = View.VISIBLE
                    holder.itemView.item_book_author.visibility = View.VISIBLE
                }
               /* when {
                    position % 3 == 0 -> {
                        val cdlp = RelativeLayout.LayoutParams(holder.itemView.item_book_layout.layoutParams)
                        cdlp.setMargins(width, width, width, 0)
                        holder.itemView.item_book_layout.layoutParams = cdlp
                    }
                    position % 3 == 2 -> {
                        val cdlp = RelativeLayout.LayoutParams(holder.itemView.item_book_layout.layoutParams)
                        cdlp.setMargins(width, width, width, 0)
                        holder.itemView.item_book_layout.layoutParams = cdlp
                    }
                    else -> {
                        val cdlp = RelativeLayout.LayoutParams(holder.itemView.item_book_layout.layoutParams)
                        cdlp.setMargins(0, width, 0, 0)
                        holder.itemView.item_book_layout.layoutParams = cdlp
                    }
                }*/
            }
        }
    }

    class BookHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    interface BookSelectListener {
        fun onBookSelect(itemView: View, book: BookRealm)
    }

    interface OnLongClickListener {
        fun onLongClickListener(itemView: View, book: BookRealm, position: Int)
    }

    fun setOnLongClickListener(listener: OnLongClickListener) {
        this.longClickListener = listener
    }

    private inner class DiffCallBack(private val oldDatas: List<BookRealm>, private val newDatas: List<BookRealm>) :
            DiffUtil.Callback() {

        override fun areItemsTheSame(p0: Int, p1: Int): Boolean {
            return oldDatas[p0].bookName == newDatas[p1].bookName
        }

        override fun getOldListSize(): Int {
            return oldDatas.size
        }

        override fun getNewListSize(): Int {
            return newDatas.size
        }

        override fun areContentsTheSame(p0: Int, p1: Int): Boolean {
            return oldDatas[p0].bookName == newDatas[p1].bookName
        }

    }

}