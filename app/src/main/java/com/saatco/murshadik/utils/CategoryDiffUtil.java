package com.saatco.murshadik.utils;

import com.saatco.murshadik.model.Item;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

public class CategoryDiffUtil extends DiffUtil.Callback {

    private final ArrayList<Item> mOldEmployeeList;
    private final ArrayList<Item> mNewEmployeeList;

    public CategoryDiffUtil(ArrayList<Item> oldEmployeeList, ArrayList<Item> newEmployeeList) {
        this.mOldEmployeeList = oldEmployeeList;
        this.mNewEmployeeList = newEmployeeList;
    }

    @Override
    public int getOldListSize() {
        return mOldEmployeeList.size();
    }

    @Override
    public int getNewListSize() {
        return mNewEmployeeList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldEmployeeList.get(oldItemPosition).getId() == mNewEmployeeList.get(
                newItemPosition).getId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        final Item oldEmployee = mOldEmployeeList.get(oldItemPosition);
        final Item newEmployee = mNewEmployeeList.get(newItemPosition);

        return oldEmployee.equals(newEmployee);
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        // Implement method if you're going to use ItemAnimator
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}
