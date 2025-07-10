package com.saatco.murshadik.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.saatco.murshadik.R;
import com.saatco.murshadik.databinding.ViewServiceCardBinding;

public class ServiceCard extends FrameLayout {

    public ViewServiceCardBinding binding;

    public ServiceCard(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public ServiceCard(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ServiceCard(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public ServiceCard(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    void init(Context context, @Nullable AttributeSet attrs) {
        binding = ViewServiceCardBinding.inflate(LayoutInflater.from(context), this, true);

        try (TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ServiceCard, 0, 0)) {

            String title = a.getString(R.styleable.ServiceCard_service_title);
            binding.tvServiceTitle.setText(title);

            int serviceIcon = a.getResourceId(R.styleable.ServiceCard_service_icon, R.drawable.ic_person);
            binding.ivServiceIcon.setImageResource(serviceIcon);

            int serviceBackgroundColor = a.getColor(R.styleable.ServiceCard_service_background_color, getResources().getColor(R.color.white, null));
            binding.cvService.setCardBackgroundColor(serviceBackgroundColor);

            a.recycle();
        } catch (Exception ignored) {
        }

    }
}
