
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
public class HeartMarkerView extends MarkerView {

    private TextView tvContent;
    private TextView heart_time_text;
    private LinearLayout heart_pop_lin;




    public HeartMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);

        tvContent = (TextView) findViewById(R.id.heart_pop_text);
        heart_time_text = (TextView) findViewById(R.id.heart_time_text);
        heart_pop_lin = (LinearLayout) findViewById(R.id.heart_pop_lin);
    }



    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        if (e instanceof CandleEntry) {

            CandleEntry ce = (CandleEntry) e;
//            if(ce.getHigh()!=0)
//            {
//                tvContent.setText("" + Utils.formatNumber(ce.getHigh(), 0, true));
//            }
//            else
//            {
//
//            }
//            tvContent.setText("-100");

        } else {

            String time = "";
            String count ="";

//            if(e.getXIndex()>=10&&e.getXIndex()<288)
//            {

//                String data1 = String.valueOf((e.getXIndex()-10)*5/60);
//                String data2 = String.valueOf((e.getXIndex()-10)*5%60);

                 String data1 = String.valueOf(e.getXIndex()*5/60);
                 String data2 = String.valueOf(e.getXIndex()*5%60);

                if(data1.length()<=1)
                {
                    data1 = "0"+ data1;
                }

                if(data2.length()<=1)
                {
                    data2 = "0"+ data2;
                }

                 time = data1 + ":"+data2;
                 count ="";

                if(e.getVal()==0)
                {

                }
                else
                {
//                tvContent.setText("" + Utils.formatNumber(e.getVal(), 0, true)+"/"+e.getXIndex());

                    count = Utils.formatNumber(e.getVal(), 0, true);

                }

//                heart_pop_lin.setVisibility(View.VISIBLE);
//
//            }
//            else
//            {
//                heart_pop_lin.setVisibility(View.GONE);
//            }



            tvContent.setText(count);
            heart_time_text.setText(time);

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
