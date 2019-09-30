package com.zia.easybook;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class CustomProgressDialog extends Dialog {
    private ProgressBar progressBar;
    private TextView tv_msg,tv_progress;


    public CustomProgressDialog(Context context) {
        //一开始就设置为透明背景
        super(context, R.style.transparent_dialog);
        init(context);
    }


    public void init(final Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        //得到加载的view
        View v = inflater.inflate(R.layout.custom_dialog_layout, null);
        //加载布局
        LinearLayout layout = v.findViewById(R.id.dialog_view);
        getWindow().setDimAmount(0);
        progressBar = v.findViewById(R.id.progressBar);
        tv_msg = v.findViewById(R.id.textView);

        //设置不可通过点击外面区域取消
        setCanceledOnTouchOutside(false);
        setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
              //  Toast.makeText(context,"加载取消",Toast.LENGTH_SHORT).show();
            }
        });

        // 设置布局，设为全屏
        setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));

    }

    // 设置加载信息
    public void setMessage(String msg){
        tv_msg.setText(msg);
    }

    //设置进度条
    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    //获取进度条
    public ProgressBar getProgressBar() {
        return progressBar;
    }

    //设置进度
    public void setProgress(int progress){
        tv_progress.setText(progress + "%");
        progressBar.setProgress(progress);
    }
}
