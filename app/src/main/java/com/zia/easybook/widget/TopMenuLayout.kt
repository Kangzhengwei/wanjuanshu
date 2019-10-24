package com.zia.easybook.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toolbar
import com.google.android.material.appbar.AppBarLayout
import com.zia.easybook.R
import com.zia.easybook.utils.StatusBarUtil
import kotlinx.android.synthetic.main.top_menu_layout.view.*

/**
 * author: kang4
 * Date: 2019/10/23
 * Description:
 */
class TopMenuLayout : FrameLayout {
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
        val view = LayoutInflater.from(context).inflate(R.layout.top_menu_layout, this)
        val param = AppBarLayout.LayoutParams(toolbar.layoutParams)
        param.topMargin = StatusBarUtil.getStatusBarHeight(context)
        toolbar.layoutParams = param
    }

}
