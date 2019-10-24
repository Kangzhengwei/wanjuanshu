package com.zia.easybook.widget

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.zia.easybook.R

class CustomProgressDialog(context: Context) : Dialog(context, R.style.transparent_dialog) {
    //获取进度条
    //设置进度条
    var progressBar: ProgressBar? = null
    private var tv_msg: TextView? = null
    private val tv_progress: TextView? = null


    init {
        init(context)
    }//一开始就设置为透明背景


    fun init(context: Context) {
        val inflater = LayoutInflater.from(context)
        //得到加载的view
        val v = inflater.inflate(R.layout.custom_dialog_layout, null)
        //加载布局
        val layout = v.findViewById<LinearLayout>(R.id.dialog_view)
        window!!.setDimAmount(0f)
        progressBar = v.findViewById(R.id.progressBar)
        tv_msg = v.findViewById(R.id.textView)

        //设置不可通过点击外面区域取消
        setCanceledOnTouchOutside(false)
        setOnCancelListener {
            //  Toast.makeText(context,"加载取消",Toast.LENGTH_SHORT).show();
        }

        // 设置布局，设为全屏
        setContentView(
            layout, LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        )

    }

    // 设置加载信息
    fun setMessage(msg: String) {
        tv_msg!!.text = msg
    }

    //设置进度
    fun setProgress(progress: Int) {
        tv_progress!!.text = "$progress%"
        progressBar!!.progress = progress
    }
}
