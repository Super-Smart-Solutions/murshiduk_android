package com.saatco.murshadik.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.saatco.murshadik.R;
import com.saatco.murshadik.adapters.FilterDialogAdapter;
import com.saatco.murshadik.utils.MyCallbackHandler;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * custom dialog for filtering data
 */
public class FilterDialog {


    private static final int SPAN_COUNT = 3;
    Context context;
    String title;
    boolean isSearchable;
    ArrayList<FilterDialogModel> dataList;

    MyCallbackHandler<FilterDialogModel> listener;

    FilterDialogAdapter adapter;

    //views
    TextView tvTitle;
    EditText etSearch;
    RecyclerView rvItems;
    Dialog dialogFilter;

    public FilterDialog(Context context, ArrayList<FilterDialogModel> dataList, String title, boolean isSearchable) {
        this.context = context;
        this.title = title;
        this.isSearchable = isSearchable;
        this.dataList = dataList;

        init();
    }

    public void init() {
        dialogFilter = new Dialog(context);//, android.R.style.Theme_Light_NoTitleBar_Fullscreen
        dialogFilter.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogFilter.setContentView(R.layout.dialog_filter);
        dialogFilter.getWindow().setBackgroundDrawable(AppCompatResources.getDrawable(context, R.drawable.dark_transparent));
        dialogFilter.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        tvTitle = dialogFilter.findViewById(R.id.tv_dialog_title);
        etSearch = dialogFilter.findViewById(R.id.et_search);
        rvItems = dialogFilter.findViewById(R.id.rv_items);

        bindDataToViews();
    }

    private void bindDataToViews() {
        tvTitle.setText(title);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                search(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        adapter = new FilterDialogAdapter(context, dataList, (data) -> {
            if (listener != null) {
                listener.onResponse(data);
            }
            dialogFilter.dismiss();
        });

        rvItems.setLayoutManager(new GridLayoutManager(context, SPAN_COUNT, RecyclerView.VERTICAL, false));
        rvItems.setAdapter(adapter);

    }

    private void search(String text) {
        if (text.equals(""))
            adapter.updateList(dataList);
        else
            adapter.updateList(dataList.stream().filter(filterDialogModel -> filterDialogModel.getText().contains(text)).collect(Collectors.toList()));
    }

    public void setOnFilterSelectItemListener(MyCallbackHandler<FilterDialogModel> onFilterSelectItemListener) {
        listener = onFilterSelectItemListener;
    }

    public void show() {
        dialogFilter.show();
    }

    public void dismiss() {
        dialogFilter.dismiss();
    }


}

