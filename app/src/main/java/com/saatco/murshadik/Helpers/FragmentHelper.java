package com.saatco.murshadik.Helpers;

import androidx.annotation.IdRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

public class FragmentHelper {
    public static void showIn(FragmentActivity context, Fragment fragment, @IdRes int idRes) {
        context.getSupportFragmentManager().beginTransaction().replace(idRes, fragment).commit();
    }
}
