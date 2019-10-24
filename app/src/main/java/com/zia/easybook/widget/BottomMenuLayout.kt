package com.zia.easybook.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.zia.easybook.R
import kotlinx.android.synthetic.main.bottom_menu_layout.view.*

/**
 * author: kang4
 * Date: 2019/10/23
 * Description:
 */
class BottomMenuLayout : FrameLayout {

    private var callback: Callback? = null

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }
    private fun init(context: Context) {
        val view= LayoutInflater.from(context).inflate(R.layout.bottom_menu_layout, this)
    }

    fun setListener(callback: Callback) {
        this.callback = callback
        bindEvent()
    }
    fun bindEvent(){
        ll_catalog.setOnClickListener{
            callback?.openChapterList()
        }
    }



    interface Callback {
        fun skipToPage(page: Int)

        fun onMediaButton()

        fun autoPage()

        fun setNightTheme()

        fun skipPreChapter()

        fun skipNextChapter()

        fun openReplaceRule()

        fun openChapterList()

        fun openAdjust()

        fun openReadInterface()

        fun openMoreSetting()

        fun toast(id: Int)

        fun dismiss()
    }
}
