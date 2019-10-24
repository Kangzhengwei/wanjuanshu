package com.zia.easybook

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zia.easybook.Dao.MergerBook
import kotlinx.android.synthetic.main.source_list_item.view.*

/**
 * author: kang4
 * Date: 2019/10/23
 * Description:
 */
class SourceAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var list = ArrayList<MergerBook.Site>()
    private lateinit var onListener: onItemClickListener
    private lateinit var currentUrl: String

    fun setData(list: ArrayList<MergerBook.Site>, url: String) {
        this.list = list
        this.currentUrl = url
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.source_list_item, parent, false)
        return SourceHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SourceHolder -> {
                val site = list[position]
                holder.itemView.site_name.text = site.siteName
                holder.itemView.site_url.text = site.url
                holder.itemView.setOnClickListener {
                    onListener.itemClick(site)
                }
                if (site.url == currentUrl) {
                    holder.itemView.tick.visibility = View.VISIBLE
                } else {
                    holder.itemView.tick.visibility = View.INVISIBLE
                }
            }
        }
    }

    class SourceHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    interface onItemClickListener {
        fun itemClick(site: MergerBook.Site)
    }

    fun setOnItemClickListener(listener: onItemClickListener) {
        this.onListener = listener
    }

}
