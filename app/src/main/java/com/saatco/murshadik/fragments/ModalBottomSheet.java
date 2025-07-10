package com.saatco.murshadik.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.saatco.murshadik.R;
import com.saatco.murshadik.databinding.BottomSheetDefualtBinding;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A generic modal bottom sheet that extends BottomSheetDialogFragment.
 *
 * @param <T> the type of items to be displayed in the bottom sheet
 */
public class ModalBottomSheet<T> extends BottomSheetDialogFragment {

    // Binding object for the bottom sheet layout
    BottomSheetDefualtBinding binding;

    // Listener for item click events
    OnItemClickListener<T> listener;

    // List of items to be displayed in the bottom sheet
    ArrayList<T> items;

    // Title of the bottom sheet
    String title;

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here
     * @return Return the View for the fragment's UI, or null
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetDefualtBinding.inflate(inflater, container, false);
        prepare();
        return binding.getRoot();
    }

    private void prepare() {
        binding.tvTitle.setText(title);

        // Initialize array adapter
        ArrayAdapter<T> adapter = new ArrayAdapter<>(requireContext(), R.layout.item_simple_list, items);

        binding.rvItems.setAdapter(adapter);

        binding.rvItems.setOnItemClickListener((parent, view, position, id) -> {
            if (listener != null) {
                listener.onItemClick(position, items.get(position));
            }
            dismiss();
        });
    }

    /**
     * Builder class for creating instances of ModalBottomSheet.
     *
     * @param <T> the type of items to be displayed in the bottom sheet
     */
    public static class Builder<T> {
        ModalBottomSheet<T> modalBottomSheet;

        /**
         * Constructor for Builder.
         */
        public Builder() {
            modalBottomSheet = new ModalBottomSheet<>();
        }

        /**
         * Sets the items to be displayed in the bottom sheet.
         *
         * @param items the list of items
         * @return the Builder instance
         */
        public Builder<T> setItems(ArrayList<T> items) {
            modalBottomSheet.items = items;
            return this;
        }

        public Builder<T> setItems(T[] items) {
            modalBottomSheet.items = new ArrayList<>();
            modalBottomSheet.items.addAll(Arrays.asList(items));
            return this;
        }

        /**
         * Sets the title of the bottom sheet.
         *
         * @param title the title
         * @return the Builder instance
         */
        public Builder<T> setTitle(String title) {
            modalBottomSheet.title = title;
            return this;
        }

        /**
         * Sets the listener for item click events.
         *
         * @param listener the listener
         * @return the Builder instance
         */
        public Builder<T> setListener(OnItemClickListener<T> listener) {
            modalBottomSheet.listener = listener;
            return this;
        }

        /**
         * Builds and returns the ModalBottomSheet instance.
         *
         * @return the ModalBottomSheet instance
         */
        public ModalBottomSheet<T> build() {
            return modalBottomSheet;
        }

    }

    /**
     * Interface definition for a callback to be invoked when an item in the bottom sheet is clicked.
     *
     * @param <T> the type of the item
     */
    public interface OnItemClickListener<T> {
        /**
         * Called when an item has been clicked.
         *
         * @param index the index of the clicked item
         * @param item  the clicked item
         */
        void onItemClick(int index, T item);
    }
}