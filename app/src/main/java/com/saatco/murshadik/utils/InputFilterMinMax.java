package com.saatco.murshadik.utils;

import android.text.InputFilter;
import android.text.Spanned;

public class InputFilterMinMax implements InputFilter {
    private long minValue;
    private long maxValue;

    public InputFilterMinMax(long minValue, long maxValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        try {
            long input = Long.parseLong(dest.toString() + source.toString());
            if (isInRange(input)) {
                return null;
            }
        } catch (NumberFormatException e) {
            // do nothing
        }
        return "";
    }

    private boolean isInRange(long value) {
        return value >= minValue && value <= maxValue;
    }
}
