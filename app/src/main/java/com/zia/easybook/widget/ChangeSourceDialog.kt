package com.zia.easybook.widget

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import com.zia.easybook.Dao.MergerBook
import com.zia.easybook.R
import com.zia.easybook.SourceAdapter
import kotlinx.android.synthetic.main.change_source_dialog.*

/**
 * author: kang4
 * Date: 2019/10/23
 * Description:
 */
class ChangeSourceDialog(context: Context) : Dialog(context, R.style.alertDialogTheme) {

    val adapter: SourceAdapter

    lateinit var mListener: itemClickListener

    companion object {
        fun builder(context: Context): ChangeSourceDialog {
            return ChangeSourceDialog(context)
        }
    }


    init {
        val view = LayoutInflater.from(context).inflate(R.layout.change_source_dialog, null)
        setContentView(view)
        adapter = SourceAdapter()
        source_list.layoutManager = LinearLayoutManager(context)
        source_list.adapter = adapter
        adapter.setOnItemClickListener(object : SourceAdapter.onItemClickListener {
            override fun itemClick(site: MergerBook.Site) {
                this@ChangeSourceDialog.mListener.itemClick(site)
                this@ChangeSourceDialog.dismiss()
            }
        })
    }

    fun setData(list: ArrayList<MergerBook.Site>,url:String): ChangeSourceDialog {
        adapter.setData(list,url)
        return this
    }

    interface itemClickListener {
        fun itemClick(site: MergerBook.Site)
    }

    fun setListener(listener: itemClickListener): ChangeSourceDialog {
        this.mListener = listener
        return this
    }


}
