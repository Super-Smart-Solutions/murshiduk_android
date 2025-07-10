package com.saatco.murshadik.Helpers;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.saatco.murshadik.LanguageUtil;
import com.saatco.murshadik.R;
import com.saatco.murshadik.utils.KeyboardUtils;

public class ToolbarHelper {

    public static void setToolBar(Activity context, String title, ViewGroup toolbarLayout) {


        ImageView backBtn = toolbarLayout.findViewById(R.id.btn_back);
        TextView tvTitle = toolbarLayout.findViewById(R.id.toolbar_title);
        tvTitle.setText(title);

//        if (LanguageUtil.getLanguage(context).equals("ar"))
//            backBtn.setImageResource(R.drawable.arrow_right);

        backBtn.setOnClickListener(view -> context.finish());
    }

    public static void setToolBarTrans(Activity context, String title, ViewGroup toolbarLayout) {

        ImageView backBtn = toolbarLayout.findViewById(R.id.btn_back);
        TextView toolbar_title = toolbarLayout.findViewById(R.id.toolbar_title);

        toolbar_title.setText(title);

        if (LanguageUtil.getLanguage(context).equals("ar"))
            backBtn.setImageResource(R.drawable.arrow_right);


        backBtn.setOnClickListener(view -> context.finish());
        KeyboardUtils.hideKeyboard(context);
    }

    public static void setToolBarTransWithTitle(Activity context, String title, ViewGroup toolbarLayout) {

        ImageView backBtn = toolbarLayout.findViewById(R.id.btn_back);
        ImageView ivBtn = toolbarLayout.findViewById(R.id.iv_btn_back);
        TextView toolbar_title = toolbarLayout.findViewById(R.id.toolbar_title);
        TextView tv_title = toolbarLayout.findViewById(R.id.tv_title);
        if (toolbar_title != null)
            toolbar_title.setText(title);
        else if (tv_title != null)
            tv_title.setText(title);

        if (LanguageUtil.getLanguage(context).equals("ar"))
            if (backBtn != null)
                backBtn.setImageResource(R.drawable.arrow_right);
            else if (ivBtn != null)
                ivBtn.setImageResource(R.drawable.arrow_right);

        if (backBtn != null)
            backBtn.setOnClickListener(view -> context.finish());
        else if (ivBtn != null)
            ivBtn.setOnClickListener(view -> context.finish());
    }

    public static void hideToolBarTransWithTitle(Activity context, ViewGroup toolbarLayout) {

        ImageView backBtn = toolbarLayout.findViewById(R.id.btn_back);
        TextView toolbar_title = toolbarLayout.findViewById(R.id.toolbar_title);

        toolbar_title.setVisibility(View.GONE);
        backBtn.setVisibility(View.GONE);

    }

    public static void ShowToolBarTransWithTitle(Activity context, ViewGroup toolbarLayout) {

        ImageView backBtn = toolbarLayout.findViewById(R.id.btn_back);
        TextView toolbar_title = toolbarLayout.findViewById(R.id.toolbar_title);

        toolbar_title.setVisibility(View.VISIBLE);
        backBtn.setVisibility(View.VISIBLE);
    }

}

