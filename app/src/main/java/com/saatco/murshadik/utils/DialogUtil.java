package com.saatco.murshadik.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.saatco.murshadik.ChatbotActivity;
import com.saatco.murshadik.NotificationSettingActivity;
import com.saatco.murshadik.R;
import com.saatco.murshadik.adapters.ChatCategoryAdapter;
import com.saatco.murshadik.model.Item;
import com.saatco.murshadik.model.User;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * QuickBlox team
 */
public class DialogUtil {

    public static String YES = "yes";
    public static String NO = "no";

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void showToast(Context context, int messageId) {
        Toast.makeText(context, context.getString(messageId), Toast.LENGTH_LONG).show();
    }

    public static void showCommonAlert(Activity activity, String title, String desc) {

        Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.dialog_airplane);

        TextView tvTitle = dialog.findViewById(R.id.tvTitle);
        TextView tvDesc = dialog.findViewById(R.id.tvDesc);

        tvTitle.setText(title);
        tvDesc.setText(desc);

        Button done = dialog.findViewById(R.id.btnDone);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
            }
        });
        dialog.show();

    }

    public static void showMarketAlertSettings(Activity activity, String title, String desc) {

        Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.dialog_airplane);

        TextView tvTitle = dialog.findViewById(R.id.tvTitle);
        TextView tvDesc = dialog.findViewById(R.id.tvDesc);

        tvTitle.setText(title);
        tvDesc.setText(desc);

        Button done = dialog.findViewById(R.id.btnDone);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(activity, NotificationSettingActivity.class);
                activity.startActivity(intent);

                dialog.dismiss();
            }
        });
        dialog.show();

    }

    public static void showOfflineAlert(Activity activity, String title, String desc) {

        Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.dialog_airplane);

        TextView tvTitle = dialog.findViewById(R.id.tvTitle);
        TextView tvDesc = dialog.findViewById(R.id.tvDesc);
        LinearLayout chatBot = dialog.findViewById(R.id.request_consult);
        chatBot.setVisibility(View.GONE);

        tvTitle.setText(title);
        tvDesc.setText(desc);

        Button done = dialog.findViewById(R.id.btnDone);
        done.setText("تم");

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        chatBot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, ChatbotActivity.class);
                activity.startActivity(intent);
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    /**
     * show dialog to inform user about process status
     *
     * @param activity the activity to get finished
     * @param status   type of dialog info icon (0 for error, 1 for success, 2 for info)
     * @param msg      the message to show
     * @param title    the title to show
     */
    public static void showInfoDialogAndFinishActivity(Activity activity, int status, String msg, String title) {
        @DrawableRes int icon = R.drawable.ic_error;
        if (status == 1) {
            icon = R.drawable.ic_success;
        } else if (status == 2) {
            icon = R.drawable.ic_info;
        }


        new AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                        activity.finish();
                    }
                })
                .setIcon(icon)
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        activity.finish();
                    }
                })
                .show();
    }

    /**
     * show dialog to inform user about process status
     *
     * @param status type of dialog info icon (0 for error, 1 for success, 2 for info)
     * @param msg    the message to show
     * @param title  the title to show
     */
    public static void showInfoDialog(Context activity, int status, String title, String msg) {
        @DrawableRes int icon = R.drawable.ic_error;
        if (status == 1) {
            icon = R.drawable.ic_success;
        } else if (status == 2) {
            icon = R.drawable.ic_info;
        }

        new AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(msg)
                .setIcon(icon)
                .show();
    }

    public static void showInfoDialog(Activity activity, int status, String title, String msg, MyCallbackHandler<Boolean> callbackHandler) {
        @DrawableRes int icon = R.drawable.ic_error;
        if (status == 1) {
            icon = R.drawable.ic_success;
        } else if (status == 2) {
            icon = R.drawable.ic_info;
        }



        new AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(msg)
                .setIcon(icon)
                .setPositiveButton( activity.getString(R.string.ok), (dialog, which) -> {
                    callbackHandler.onResponse(true);
                })
                .show()
                .setOnDismissListener(
                        dialog -> {
                            callbackHandler.onResponse(false);
                        }
                );


    }

    public static void yesNoDialog(Context context, String title, String message, MyCallbackHandler<String> myCallbackHandler) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(title);
        builder.setMessage(message);

        builder.setPositiveButton(context.getString(R.string.yes), (dialog, which) -> {
            // Do nothing but close the dialog
            myCallbackHandler.onResponse(YES);
            dialog.dismiss();
        });

        builder.setNegativeButton(context.getString(R.string.no), (dialog, which) -> {
            // Do nothing
            myCallbackHandler.onResponse(NO);
            dialog.dismiss();
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * show dialog to ensure user about process status
     *
     * @param context           context
     * @param title             title
     * @param message           message body
     * @param status            type of dialog info icon (0 for error, 1 for success, 2 for info)
     * @param myCallbackHandler callback method for response with string param values is ("yes" or "no")
     */
    public static void yesNoDialogWithEnsure(Context context, String title, String message, int status, MyCallbackHandler<String> myCallbackHandler) {
        @DrawableRes int icon = R.drawable.ic_error;
        if (status == 1) {
            icon = R.drawable.ic_success;
        } else if (status == 2) {
            icon = R.drawable.ic_info;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setIcon(icon);

        final boolean[] isYesPressed = {false};
        builder.setPositiveButton(context.getString(R.string.yes), null);

        builder.setNegativeButton(context.getString(R.string.no), (dialog, which) -> {
            // Do nothing
            myCallbackHandler.onResponse("no");
            dialog.dismiss();
        });

        AlertDialog alert = builder.create();

        alert.setOnShowListener(dialogInterface -> {

            Button button = alert.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view -> {
                if (isYesPressed[0]) {
                    myCallbackHandler.onResponse("yes");
                    alert.dismiss();
                } else {
                    isYesPressed[0] = true;
                    ToastUtils.shortToast(R.string.for_sure_press_yes_agin);
                }
            });
        });
        alert.show();
    }


    public static class DialogListOfItems<T> {
        public Dialog dialog;

        public DialogListOfItems(Activity activity, String title, ArrayList<T> list, MyCallbackHandler<View> onItemClick) {
            dialog = new Dialog(activity);
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.round_border_green);

            dialog.setContentView(R.layout.dialog_searchable_spinner);


            ListView listView = dialog.findViewById(R.id.list_view);
            TextView tv_title = dialog.findViewById(R.id.tv_title);
            EditText et_search = dialog.findViewById(R.id.et_search);

            tv_title.setText(title);

            // Initialize array adapter
            ArrayAdapter<T> adapter = new ArrayAdapter<>(activity, R.layout.item_simple_list, list);


            // set adapter
            listView.setAdapter(adapter);

            et_search.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    adapter.getFilter().filter(s);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            listView.setOnItemClickListener((parent, view, position, id) -> {
                // when item selected from list
                // set selected item on textView
                onItemClick.onResponse(view);
                onItemClick.onPosition(position);
                // Dismiss dialog
                dialog.dismiss();
            });


        }

        public void show() {
            dialog.show();
        }
    }

    public static class DialogListOfItemsWithIcon {
        public Dialog dialog;

        public DialogListOfItemsWithIcon(Activity activity, String title, ArrayList<Item> list, MyCallbackHandler<Item> onItemClick) {
            dialog = new Dialog(activity, android.R.style.Theme_Light_NoTitleBar_Fullscreen);//
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.round_border_green);

            dialog.setContentView(R.layout.dialog_searchable_items);


            RecyclerView listView = dialog.findViewById(R.id.list_view);
            TextView tv_title = dialog.findViewById(R.id.tv_title);
            EditText et_search = dialog.findViewById(R.id.et_search);

            tv_title.setText(title);

            // Initialize array adapter
            ChatCategoryAdapter adapter = new ChatCategoryAdapter(list, activity, new ChatCategoryAdapter.OnSelectItemClickListener() {
                @Override
                public void onCategoryClick(View view, int position, Item item) {
                    // when item selected from list
                    // set selected item on textView
                    onItemClick.onResponse(item);
                    onItemClick.onPosition(position);
                    // Dismiss dialog
                    dialog.dismiss();
                }

                @Override
                public void onUserClick(View view, int position, User item) {

                }
            });


            // set adapter
            listView.setAdapter(adapter);
            listView.setLayoutManager(new GridLayoutManager(activity, 2, RecyclerView.VERTICAL, false));
            listView.setItemAnimator(new DefaultItemAnimator());

            et_search.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    adapter.updateList(list.stream().filter(item -> item.getNameAr().contains(s)).collect(Collectors.toList()));
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });


        }

        public void show() {
            dialog.show();
        }
    }

    /**
     * create dialog list easily
     *
     * @param <T> data type of list adapter
     */
    public static class DialogListOfObjects<T> {
        public Dialog dialog;

        public DialogListOfObjects(Activity activity, String title, ArrayList<T> list, MyCallbackHandler<T> onItemClick) {
            dialog = new Dialog(activity);
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.round_border_green);

            dialog.setContentView(R.layout.dialog_searchable_spinner);


            ListView listView = dialog.findViewById(R.id.list_view);
            TextView tv_title = dialog.findViewById(R.id.tv_title);
            EditText et_search = dialog.findViewById(R.id.et_search);

            tv_title.setText(title);

            // Initialize array adapter
            ArrayAdapter<T> adapter = new ArrayAdapter<>(activity, R.layout.item_simple_list, list);


            // set adapter
            listView.setAdapter(adapter);

            et_search.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    adapter.getFilter().filter(s);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            listView.setOnItemClickListener((parent, view, position, id) -> {
                // when item selected from list
                // set selected item on textView
                onItemClick.onResponse(adapter.getItem(position));
                // Dismiss dialog
                dialog.dismiss();
            });


        }

        public void show() {
            dialog.show();
        }
    }


}