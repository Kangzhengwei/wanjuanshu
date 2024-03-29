package com.zia.easybook

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.zia.easybookmodule.bean.Catalog
import kotlinx.android.synthetic.main.item_catalog.view.*
import java.util.*


/**
 * Created by zia on 2018/11/1.
 */
class CatalogAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var list = ArrayList<Catalog>()
    private lateinit var mListener: onItemClickListener

    fun freshCatalogs(catalogs: ArrayList<Catalog>) {
        this.list = catalogs
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.item_catalog, p0, false)
        return CatalogHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CatalogHolder -> {
                val catalog = list[position]
                holder.itemView.item_catalog_name.text = catalog.chapterName
                holder.itemView.setOnClickListener {
                    mListener?.onItemClick(catalog)
                }
            }
        }
    }

    class CatalogHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    interface onItemClickListener {
        fun onItemClick(catalog: Catalog)
    }

    fun setOnItemClickListener(listener: onItemClickListener) {
        this.mListener = listener
    }
}