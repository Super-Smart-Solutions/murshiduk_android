package com.saatco.murshadik;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.saatco.ItemOffsetDecoration;
import com.saatco.murshadik.Helpers.FirebaseObjectHelper;
import com.saatco.murshadik.Helpers.ToolbarHelper;
import com.saatco.murshadik.adapters.ChatCategoryAdapter;
import com.saatco.murshadik.db.StorageHelper;
import com.saatco.murshadik.model.Item;
import com.saatco.murshadik.model.User;

import java.util.ArrayList;

public class CategoryChildActivity extends BaseActivity implements ChatCategoryAdapter.OnSelectItemClickListener {

    ArrayList<Item> categories = new ArrayList<>();
    RecyclerView recyclerView;
    ChatCategoryAdapter adapter;
    ArrayList<User> consultants = new ArrayList<>();
    String region_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_child);

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        ToolbarHelper.setToolBar(this, getIntent().getStringExtra("NAME"), findViewById(R.id.toolbarTrans));


        categories = (ArrayList<Item>) getIntent().getSerializableExtra("CHILDREN");
        region_name = getIntent().getStringExtra("region_name");
        //consultants = (ArrayList<User>) getIntent().getSerializableExtra("consultants");

        recyclerView = findViewById(R.id.rv_category);

        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));

        adapter = new ChatCategoryAdapter(categories, getApplicationContext(), this);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getApplicationContext(), R.dimen.item_offset);
        recyclerView.addItemDecoration(itemDecoration);

    }

    @Override
    protected void onResume() {
        super.onResume();

        consultants = StorageHelper.getConsultants() != null ? StorageHelper.getConsultants() : new ArrayList<>();

    }

    @Override
    public void onCategoryClick(View view, int position, Item item) {

        // ArrayList<User> consultantsByCategory = new ArrayList<>();


       /* for(User consultant : consultants){

            if(consultant.getStatus() != null) {
                String[] items = consultant.getStatus().split("\\s*,\\s*");

                for (String skill : items) {
                    if (item.getNameAr().equals(skill))
                        consultantsByCategory.add(consultant);
                }
            }
        }*/

        if (item.hasChildren()) {
            Intent intent = new Intent(CategoryChildActivity.this, CategoryChildActivity.class);
            intent.putExtra("CHILDREN", item.getChildrens());
            intent.putExtra("NAME", item.getNameAr());
            if (region_name != null)
                intent.putExtra("region_name", region_name);
            startActivity(intent);
        } else {
            Intent intent = new Intent(CategoryChildActivity.this, SelectUserActivity.class);
            intent.putExtra("is_sorted", true);
            intent.putExtra("skill", item.getNameAr());
            if (region_name != null)
                intent.putExtra("region_name", region_name);
            startActivity(intent);
        }

    }

    @Override
    public void onUserClick(View view, int position, User item) {

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));

        Configuration config = new Configuration(base.getResources().getConfiguration());
        config.fontScale = 1.2f;
        applyOverrideConfiguration(config);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

}
