
package com.zjw.apps3pluspro.view;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.Utils;
import com.zjw.apps3pluspro.R;

/**
 * Custom implementation of the MarkerView.
 *
 * @author Philipp Jahoda
 */
public class PoHeartMarkerView extends MarkerView {

    private TextView po_heart_pop_text;
    private TextView po_heart_time_text;
    private LinearLayout po_heart_pop_lin;


    public PoHeartMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);

        po_heart_pop_text = (TextView) findViewById(R.id.po_heart_pop_text);
        po_heart_time_text = (TextView) findViewById(R.id.po_heart_time_text);
        po_heart_pop_lin = (LinearLayout) findViewById(R.id.po_heart_pop_lin);
    }


    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        if (e instanceof CandleEntry) {

            CandleEntry ce = (CandleEntry) e;


        } else {

            String time = "";
            String count = "";


            String data1 = String.valueOf(e.getXIndex());
            String data2 = "00";

            if (data1.length() <= 1) {
                data1 = "0" + data1;
            }


            time = data1 + ":" + data2;
            count = "";

            if (e.getVal() != 0) {
                count = Utils.formatNumber(e.getVal(), 0, true);
            } else {
                count = "0";
            }


            po_heart_pop_text.setText(count);
            po_heart_time_text.setText(time);

        }
    }

    @Override
    public int getXOffset() {
        // this will center the marker-view horizontally
        return -(getWidth() / 2);
    }

    @Override
    public int getYOffset() {
        // this will cause the marker-view to be above the selected value
        return -getHeight();
    }
}
