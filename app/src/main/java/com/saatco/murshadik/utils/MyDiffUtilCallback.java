package com.saatco.murshadik.utils;

import com.saatco.murshadik.model.User;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

/**
 * Created by kuliza-319 on 16/2/17.
 */

public class MyDiffUtilCallback extends DiffUtil.Callback {
    private final ArrayList<User> mOldEmployeeList;
    private final ArrayList<User> mNewEmployeeList;

    public MyDiffUtilCallback(ArrayList<User> oldEmployeeList, ArrayList<User> newEmployeeList) {
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
        return mOldEmployeeList.get(oldItemPosition).getChatId().equals(mNewEmployeeList.get(
                newItemPosition).getChatId());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        final User oldEmployee = mOldEmployeeList.get(oldItemPosition);
        final User newEmployee = mNewEmployeeList.get(newItemPosition);

        return oldEmployee.equals(newEmployee);
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        // Implement method if you're going to use ItemAnimator
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}
