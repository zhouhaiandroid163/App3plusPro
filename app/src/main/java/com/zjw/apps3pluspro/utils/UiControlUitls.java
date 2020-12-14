package com.zjw.apps3pluspro.utils;

import android.content.Context;
import android.widget.TextView;

import com.zjw.apps3pluspro.R;

public class UiControlUitls {

    public static void updateUnitLeftUi(Context context, TextView left_text, TextView right_text, int is_left) {
        if (is_left == 1) {
            left_text.setBackgroundResource(R.drawable.my_unit_left_on);
            left_text.setTextColor(context.getResources().getColor(R.color.my_unit_color_off));

            right_text.setBackgroundResource(R.drawable.my_unit_right_off);
            right_text.setTextColor(context.getResources().getColor(R.color.my_unit_color_on));

        } else {
            left_text.setBackgroundResource(R.drawable.my_unit_left_off);
            left_text.setTextColor(context.getResources().getColor(R.color.my_unit_color_on));

            right_text.setBackgroundResource(R.drawable.my_unit_right_on);
            right_text.setTextColor(context.getResources().getColor(R.color.my_unit_color_off));
        }
    }

    public static void updateUnitRightUi(Context context, TextView left_text, TextView right_text, int is_right) {
        if (is_right == 1) {
            left_text.setBackgroundResource(R.drawable.my_unit_left_off);
            left_text.setTextColor(context.getResources().getColor(R.color.my_unit_color_on));

            right_text.setBackgroundResource(R.drawable.my_unit_right_on);
            right_text.setTextColor(context.getResources().getColor(R.color.my_unit_color_off));
        } else {
            left_text.setBackgroundResource(R.drawable.my_unit_left_on);
            left_text.setTextColor(context.getResources().getColor(R.color.my_unit_color_off));

            right_text.setBackgroundResource(R.drawable.my_unit_right_off);
            right_text.setTextColor(context.getResources().getColor(R.color.my_unit_color_on));

        }
    }


}
