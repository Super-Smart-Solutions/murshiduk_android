package com.saatco.murshadik;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;

import com.saatco.murshadik.Helpers.ToolbarHelper;
import com.saatco.murshadik.adapters.CategoryAdapter;
import com.saatco.murshadik.databinding.ActivityAgricultureNoteBinding;
import com.saatco.murshadik.model.Item;

import java.util.ArrayList;

public class AgricultureNoteActivity extends AppCompatActivity implements CategoryAdapter.OnSelectItemClickListener {

    public static final String EXTRA_ITEMS = "agriculture_note_items";
    public static final String EXTRA_TITLE = "agriculture_note_title";

    ActivityAgricultureNoteBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAgricultureNoteBinding.inflate(getLayoutInflater(), null, false);
        setContentView(binding.getRoot());

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        ToolbarHelper.setToolBar(this, getIntent().getStringExtra(EXTRA_TITLE), binding.appBar.getRoot());

        ArrayList<Item> items = (ArrayList<Item>) getIntent().getSerializableExtra(EXTRA_ITEMS);
        initRVItems(items);


    }

    void initRVItems(ArrayList<Item> items) {
        CategoryAdapter adapter = new CategoryAdapter(items, this, this, false);
        binding.rvItems.setAdapter(adapter);
        binding.rvItems.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void onCategoryClick(View view, int position, Item item) {

        Intent intent = new Intent(this, ItemListActivity2.class);
        intent.putExtra("TITLE", item.getNameAr());
        intent.putExtra("CAT_ID", item.getId());
        startActivity(intent);

    }
}