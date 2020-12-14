package com.zjw.apps3pluspro.utils;

import android.content.Context;
import android.graphics.Color;
import android.widget.RelativeLayout;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.module.home.entity.SleepData;
import com.zjw.apps3pluspro.utils.log.MyLog;
import com.zjw.apps3pluspro.view.HeartMarkerView;
import com.zjw.apps3pluspro.view.MyMarkerView;
import com.zjw.apps3pluspro.view.PoHeartMarkerView;
import com.zjw.apps3pluspro.view.SleepChartView;
import com.zjw.apps3pluspro.view.TempMarkerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyChartUtils {


    public static int[] chart_data_week = {
            R.string.monday,
            R.string.tuesday,
            R.string.wednesday,
            R.string.thursday,
            R.string.friday,
            R.string.saturday,
            R.string.sunday
    };

    /**
     * 天数据=运动
     *
     * @param context
     * @param barChart
     * @param steps24
     */
    public static void showDaySportBarChart(Context context, BarChart barChart, String[] steps24, boolean is_touch) {

        //加载数据
        int count = steps24.length;
        // 添加X轴坐标
        ArrayList<String> xValues = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            xValues.add("");
        }
        ArrayList<BarEntry> yValues = new ArrayList<BarEntry>();
        for (int i = 0; i < count; i++) {
            float value = Float.parseFloat(steps24[i]);
            yValues.add(new BarEntry(value, i));
        }
        // Y轴的数据集
        BarDataSet barDataSet = new BarDataSet(yValues, "");
        barDataSet.setColor(context.getResources().getColor(R.color.my_exercise_line_chart_color));
        barDataSet.setDrawValues(false);
        barDataSet.setHighlightEnabled(false);
        ArrayList<BarDataSet> barDataSets = new ArrayList<BarDataSet>();
        barDataSets.add(barDataSet);
        BarData barData = new BarData(xValues, barDataSets);


        //加载UI
        barChart.setDrawBorders(false); // //是否在折线图上添加边框

        barChart.setDescription("");// 数据描述
        // 如果没有数据的时候，会显示这个，类似ListView的EmptyView
        barChart.setNoDataTextDescription("");

        barChart.setDrawGridBackground(false); // 是否显示表格颜色
        barChart.setGridBackgroundColor(Color.TRANSPARENT); // 设置网格部分的颜色，默认的是白色
        // 表格的的颜色，在这里是是给颜色设置一个透明度

        barChart.setTouchEnabled(is_touch); // 设置是否可以触摸

        barChart.setDragEnabled(true);// 是否可以拖拽
        barChart.setScaleEnabled(false);// 是否可以缩放
        barChart.setScaleYEnabled(false);// 设置Y轴不可缩放
        barChart.setDoubleTapToZoomEnabled(false);// 设置双击屏幕不可缩放

        barChart.setPinchZoom(false);// X,Y轴能否同时缩放
        barChart.setBackgroundColor(Color.TRANSPARENT);// 设置背景
        barChart.setDrawBarShadow(false); // 每一根柱状图的背景是否有阴影
        barChart.zoom(1.0f, 1.0f, 1.0f, 1.0f);// 这个可以设置X,Y轴的缩放比例（第一个参数：X轴的缩放比例，默认1.0f,第二个参数是Y轴的，第三个是X轴坐标比例,默认也是1.0f，第四个参数是Y轴的坐标比例）
        barChart.setData(barData); // 设置数据

        // X轴设定
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawLabels(false);
        xAxis.setAxisLineColor(Color.TRANSPARENT);//x轴颜色=透明
        xAxis.setAxisLineWidth(1.0f);

        Legend mLegend = barChart.getLegend(); // 设置比例图标示

        mLegend.setForm(Legend.LegendForm.SQUARE);// 样式
        mLegend.setFormSize(6f);// 字体
        mLegend.setTextColor(Color.GRAY);// 颜色
        mLegend.setEnabled(false);

        MyMarkerView markerView = new MyMarkerView(context, R.layout.heart_markview_pop);
        barChart.setMarkerView(markerView);
        // Y轴的设定
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getAxisRight().setDrawGridLines(false);
        barChart.getXAxis().setDrawGridLines(false);
        barChart.getAxisLeft().setEnabled(false);
        barChart.getAxisRight().setEnabled(false);
//        barChart.animateY(2500);

    }

    public static void showDaySleepView(Context context, List<SleepData> sleep_list_data,
                                        RelativeLayout rl_day_sleep_chart_view1, RelativeLayout rl_day_sleep_chart_view2, RelativeLayout rl_day_sleep_chart_view3
    ) {


        //图表
        if (sleep_list_data.size() > 0) {
            int color0 = context.getResources().getColor(R.color.sleep_state0);
            int color1 = context.getResources().getColor(R.color.sleep_state1);
            int color2 = context.getResources().getColor(R.color.sleep_state2);
            int color3 = context.getResources().getColor(R.color.sleep_state3);
            int color4 = context.getResources().getColor(R.color.sleep_state4);
            int color_null = context.getResources().getColor(R.color.sleep_state_null);

//            SleepChartView sleepChartView1 = new SleepChartView(context, null, color0, color_null, color_null, color_null, color4);
//            SleepChartView sleepChartView2 = new SleepChartView(context, null, color0, color1, color2, color_null, color4);
//            SleepChartView sleepChartView3 = new SleepChartView(context, null, color0, color1, color2, color3, color4);

            SleepChartView sleepChartView1 = new SleepChartView(context, null, color0, color_null, color_null, color_null, color4);
            SleepChartView sleepChartView2 = new SleepChartView(context, null, color_null, color1, color2, color_null, color_null);
            SleepChartView sleepChartView3 = new SleepChartView(context, null, color_null, color_null, color_null, color3, color_null);
            sleep_list_data = SleepData.getNorSleepData(sleep_list_data);
            MyChartUtils.showSleepView(sleep_list_data, sleepChartView1);
            MyChartUtils.showSleepView(sleep_list_data, sleepChartView2);
            MyChartUtils.showSleepView(sleep_list_data, sleepChartView3);
            rl_day_sleep_chart_view1.removeAllViews();
            rl_day_sleep_chart_view1.addView(sleepChartView1);
            rl_day_sleep_chart_view2.removeAllViews();
            rl_day_sleep_chart_view2.addView(sleepChartView2);
            rl_day_sleep_chart_view3.removeAllViews();
            rl_day_sleep_chart_view3.addView(sleepChartView3);
        }


    }

    /**
     * 天数据页-睡眠
     *
     * @param sleep_data_list
     * @param sleepChartView
     */
    public static void showSleepView(List<SleepData> sleep_data_list, SleepChartView sleepChartView) {

        String[] arrayOfString2 = new String[2];

        if (sleep_data_list != null) {

            String start_time = sleep_data_list.get(0).getStartTime();
            String end_time = sleep_data_list.get(sleep_data_list.size() - 1).getStartTime();
//            MyLog.i(TAG, "拖动睡眠 开始时间 = " + start_time);
//            MyLog.i(TAG, "拖动睡眠 结束时间 = " + end_time);
            String seek_bar_count = MyTime.TotalTime2(start_time, end_time);
            arrayOfString2[0] = start_time;
            arrayOfString2[1] = end_time;
            sleepChartView.setxLables(Arrays.asList(arrayOfString2));
            sleepChartView.setxMin(0);
            sleepChartView.setxMax(Integer.valueOf(seek_bar_count));
            sleepChartView.setItems(sleep_data_list);
            sleepChartView.invalidate();
            return;
        } else {
            sleepChartView.setItems(null);
            arrayOfString2[0] = "22:00";
            arrayOfString2[1] = "08:00";
            sleepChartView.setxMax(480);
            sleepChartView.setxMin(0);
            sleepChartView.setxLables(Arrays.asList(arrayOfString2));
            sleepChartView.invalidate();
        }

    }


    /**
     * 天数据页，整点心率
     *
     * @param context
     * @param barChart
     * @param po_heart
     */
    public static void showDayPoHearBarChart(Context context, LineChart barChart, String[] po_heart, boolean is_touche) {

        //====加载数据
        int count = po_heart.length;

        ArrayList<String> xValues = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            // x轴显示的数据，这里默认使用数字下标显示
            if (i == 0) {
                xValues.add("");
            } else {
                xValues.add("");
            }


        }

        // y轴的数据

        ArrayList<Entry> yValues = new ArrayList<Entry>();
        for (int i = 0; i < count; i++) {
            float value = Float.parseFloat(po_heart[i]);
            yValues.add(new BarEntry(value, i));


        }

        // create a dataset and give it a type
        // y轴的数据集合
        LineDataSet lineDataSet = new LineDataSet(yValues, "" /*显示在比例图上*/);

        //用y轴的集合来设置参数
        lineDataSet.setLineWidth(2f); // 线宽
        lineDataSet.setCircleSize(0f);// 显示的圆形大小 0f 表示没有
        lineDataSet.setDrawCircleHole(false);//是否空心
        lineDataSet.setCircleColor(Color.parseColor("#FC4D4D"));// 圆形的颜色

        lineDataSet.setDrawValues(false);//是否显示数字
        lineDataSet.setHighLightColor(Color.parseColor("#00000000")); // 高亮的线的颜色
        lineDataSet.setDrawCubic(false);//是否显示曲线
        lineDataSet.setCubicIntensity(0.05f);//曲线率
        lineDataSet.setFillColor(Color.TRANSPARENT);//填充颜色
        lineDataSet.setDrawFilled(false);//是否填充
        lineDataSet.setFillAlpha(76);//透明度百分之30
        lineDataSet.setColor(Color.parseColor("#FC4D4D"));// 线条颜色颜色


//        lineDataSet.setCircleColors(MyColor);//设置圈的颜色数组
//        lineDataSet.setColors(MyColor);//设置线的颜色数组（不能设置曲线属性）

        ArrayList<LineDataSet> lineDataSets = new ArrayList<LineDataSet>();
        lineDataSets.add(lineDataSet); // add the datasets

        // create a data object with the datasets
        LineData barData = new LineData(xValues, lineDataSets);

        //=====加载UI
        barChart.setDrawBorders(false); // //是否在折线图上添加边框

        barChart.setDescription("");// 数据描述
        // 如果没有数据的时候，会显示这个，类似ListView的EmptyView
        barChart.setNoDataTextDescription("");
        barChart.setDrawGridBackground(true); // 是否显示表格颜色
        barChart.setGridBackgroundColor(Color.TRANSPARENT); // 设置网格部分的颜色，默认的是白色
//        barChart.setGridBackgroundColor(Color.parseColor("#bedcfe")); // 设置网格部分的颜色，默认的是白色
        // 表格的的颜色，在这里是是给颜色设置一个透明度

        barChart.setTouchEnabled(is_touche); // 设置是否可以触摸
        barChart.setDragEnabled(true);// 是否可以拖拽
        barChart.setScaleEnabled(false);// 是否可以缩放
        barChart.setScaleYEnabled(false);// 设置Y轴不可缩放
        barChart.setDoubleTapToZoomEnabled(false);// 设置双击屏幕不可缩放

        barChart.setPinchZoom(false);// X,Y轴能否同时缩放
        barChart.setBackgroundColor(Color.parseColor("#08FC4D4D"));// 设置背景
//        barChart.setDrawBarShadow(false); // 每一根柱状图的背景是否有阴影
        barChart.zoom(1.0f, 1.0f, 1.0f, 1.0f);// 这个可以设置X,Y轴的缩放比例（第一个参数：X轴的缩放比例，默认1.0f,第二个参数是Y轴的，第三个是X轴坐标比例,默认也是1.0f，第四个参数是Y轴的坐标比例）
        barChart.setData(barData); // 设置数据
//        barChart.setData(getLineData(10,10)); // 设置数据


        // X轴设定
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawLabels(true);
//        xAxis.setAxisLineColor(Color.TRANSPARENT);
        xAxis.setAxisLineColor(Color.TRANSPARENT);
        xAxis.setAxisLineWidth(1.5f);
        xAxis.setDrawGridLines(false);//是否显示X轴网格
//        xAxis.setTextSize(0f);//X轴字体大小

        xAxis.setLabelsToSkip(11);    //设置坐标相隔多少，参数是int类型
//        xAxis.resetLabelsToSkip();   //将自动计算坐标相隔多少
//        xAxis.setAvoidFirstLastClipping(true);
//        xAxis.setSpaceBetweenLabels(4);


        Legend mLegend = barChart.getLegend(); // 设置比例图标示
        mLegend.setForm(Legend.LegendForm.SQUARE);// 样式
        mLegend.setFormSize(10f);// 字体
        mLegend.setTextColor(Color.parseColor("#ff0000"));// 颜色
        mLegend.setMaxSizePercent(0.95f);   //坐标线描述 占据的大小x%  默认0.95 即95%
        mLegend.setEnabled(false);


        //点击显示的View
        PoHeartMarkerView markerView = new PoHeartMarkerView(context,
                R.layout.po_heart_markview_pop);
        barChart.setMarkerView(markerView);
        // Y轴的设定

        barChart.getAxisLeft().setAxisLineColor(Color.TRANSPARENT);//Y轴颜色
        barChart.getAxisLeft().setTextColor(Color.TRANSPARENT);
        barChart.getAxisLeft().setGridColor(Color.TRANSPARENT);//Y轴网格颜色
        barChart.getAxisLeft().setAxisLineWidth(1.5f);
        barChart.getAxisLeft().setDrawAxisLine(true);
        barChart.getAxisLeft().setDrawGridLines(false);//是否显示Y轴网格
        barChart.getAxisLeft().setEnabled(true);//是否显示Y轴
//      barChart.getAxisLeft().setAxisMaxValue(200);//设置Y周最大值
//      barChart.getAxisLeft().setAxisMinValue(20);//设置Y周最小值
        barChart.getAxisLeft().setSpaceTop(35f);//和顶端的距离，百分比
//        barChart.getAxisLeft().setSpaceBottom(50f);//和顶端的距离，百分比，没效果
        barChart.getAxisLeft().setStartAtZero(true);//显示零相关的,设置成flase ，地下会出来空隙
//        barChart.getAxisLeft().setShowOnlyMinMax(true);//是否显示最大最小值
        barChart.getAxisLeft().setLabelCount(5, false);//第一个参数是Y轴坐标的个数，第二个参数是 是否不均匀分布，true是不均匀分布
        barChart.getAxisRight().setDrawGridLines(false);
        barChart.getAxisRight().setEnabled(true);
        barChart.getAxisRight().setAxisLineColor(Color.TRANSPARENT);//Y轴颜色
        barChart.getAxisRight().setTextColor(Color.TRANSPARENT);

//        barChart.animateX(2000);//动画从左往右
//        barChart.animateY(1500);//动画下往上
//        barChart.animateXY(100,1000);// 两个轴动画，从左到右，从下到上

    }

    /**
     * 天数据页，连续心率
     *
     * @param context
     * @param barChart
     * @param wo_heart
     */
    public static void showDayWoHearBarChart(Context context, LineChart barChart, String[] wo_heart, boolean is_touch) {


        final int Color0 = Color.TRANSPARENT;
        final int Color1 = Color.parseColor("#FF5C57");
        final int Color2 = Color.parseColor("#FF5C57");
        final int Color3 = Color.parseColor("#FF5C57");
        final int Color4 = Color.parseColor("#FF5C57");
        final int Color5 = Color.parseColor("#FF5C57");
        int[] MyColor = {Color1, Color2, Color3, Color3, Color3, Color3};

        //X轴网格颜色
        final int XZhou = Color.TRANSPARENT;
        //Y轴网格颜色
        final int YGridColor = Color.parseColor("#ffffff");


        //====加载数据

        int count = wo_heart.length;

        ArrayList<String> xValues = new ArrayList<String>();

        for (int i = 0; i < count; i++) {
            // x轴显示的数据，这里默认使用数字下标显示
            if (i == 0) {
                xValues.add("");
            } else {
                xValues.add("");
            }

        }


        // y轴的数据
//        String[] steps24 = my_heartList;
        ArrayList<Entry> yValues = new ArrayList<Entry>();
        MyColor = new int[count];
        for (int i = 0; i < count; i++) {
            float value = Float.parseFloat(wo_heart[i]);
            yValues.add(new BarEntry(value, i));
            float value_one = -1;

            if (i < (count - 2)) {
                value_one = Float.parseFloat(wo_heart[i + 1]);
            }

            if (value_one != -1 && value_one == 0) {
                MyColor[i] = Color0;
            } else {
                if (value <= 0) {
                    MyColor[i] = Color0;
                } else if (value > 0 && value <= 100) {
                    MyColor[i] = Color1;
                } else if (value > 100 && value <= 120) {
                    MyColor[i] = Color2;
                } else if (value > 120 && value <= 140) {
                    MyColor[i] = Color3;
                } else if (value > 140 && value <= 160) {
                    MyColor[i] = Color4;
                } else if (value > 160) {
                    MyColor[i] = Color5;
                }
            }


        }

        // create a dataset and give it a type
        // y轴的数据集合
        LineDataSet lineDataSet = new LineDataSet(yValues, "" /*显示在比例图上*/);

        //用y轴的集合来设置参数
        lineDataSet.setLineWidth(1.5f); // 线宽
//        lineDataSet.setCircleSize(1.1f);// 显示的圆形大小 0f 表示没有
        lineDataSet.setDrawCircleHole(false);//是否空心
        lineDataSet.setCircleColor(Color0);// 圆形的颜色
        lineDataSet.setColor(Color0);// 线条颜色颜色
        lineDataSet.setDrawValues(false);//是否显示数字
        lineDataSet.setHighLightColor(Color.parseColor("#ffffff")); // 高亮的线的颜色
        lineDataSet.setHighlightLineWidth(1f);
        lineDataSet.setDrawVerticalHighlightIndicator(true);
        lineDataSet.setDrawHorizontalHighlightIndicator(true);
//        lineDataSet.setCircleColorHole(Color.parseColor("#ff0000"));
        lineDataSet.setDrawHighlightIndicators(true);//是否高亮
        lineDataSet.setHighlightEnabled(is_touch);//是否可以触摸
//        lineDataSet.setDrawCubic(true);//是否显示曲线
//        lineDataSet.setCubicIntensity(0.05f);//曲线率
//        lineDataSet.setFillColor(XZhou);//填充颜色
//        lineDataSet.setDrawFilled(true);//是否填充
//        lineDataSet.setFillAlpha(76);//透明度百分之30


        lineDataSet.setColors(MyColor);//设置线的颜色数组（不能设置曲线属性）
//        lineDataSet.setCircleColors(MyColor);//设置圈的颜色数组
        ArrayList<LineDataSet> lineDataSets = new ArrayList<LineDataSet>();
        lineDataSets.add(lineDataSet); // add the datasets

        // create a data object with the datasets
        LineData barData = new LineData(xValues, lineDataSets);


        //=====加载UI


        barChart.setDrawBorders(false); // //是否在折线图上添加边框

        barChart.setDescription("");// 数据描述
        // 如果没有数据的时候，会显示这个，类似ListView的EmptyView
        barChart.setNoDataTextDescription("");
        barChart.setDrawGridBackground(false); // 是否显示表格颜色
        barChart.setGridBackgroundColor(Color.TRANSPARENT); // 设置网格部分的颜色，默认的是白色
//        barChart.setGridBackgroundColor(Color.parseColor("#bedcfe")); // 设置网格部分的颜色，默认的是白色
        // 表格的的颜色，在这里是是给颜色设置一个透明度

        barChart.setTouchEnabled(is_touch); // 设置是否可以触摸
        barChart.setDragEnabled(true);// 是否可以拖拽
        barChart.setScaleEnabled(false);// 是否可以缩放
        barChart.setScaleYEnabled(false);// 设置Y轴不可缩放
        barChart.setDoubleTapToZoomEnabled(false);// 设置双击屏幕不可缩放

        barChart.setPinchZoom(false);// X,Y轴能否同时缩放
        barChart.setBackgroundColor(Color.parseColor("#00FC4D4D"));// 设置背景
//        barChart.setDrawBarShadow(false); // 每一根柱状图的背景是否有阴影
        barChart.zoom(1.0f, 1.0f, 1.0f, 1.0f);// 这个可以设置X,Y轴的缩放比例（第一个参数：X轴的缩放比例，默认1.0f,第二个参数是Y轴的，第三个是X轴坐标比例,默认也是1.0f，第四个参数是Y轴的坐标比例）
        barChart.setData(barData); // 设置数据

        // X轴设定
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawLabels(true);
//        xAxis.setAxisLineColor(Color.TRANSPARENT);
        xAxis.setAxisLineColor(Color0);
        xAxis.setAxisLineWidth(1f);
        xAxis.setDrawGridLines(false);//是否显示X轴网格
//        xAxis.setTextSize(0f);//X轴字体大小

        xAxis.setLabelsToSkip(11);    //设置坐标相隔多少，参数是int类型
//        xAxis.resetLabelsToSkip();   //将自动计算坐标相隔多少
//        xAxis.setAvoidFirstLastClipping(true);
//        xAxis.setSpaceBetweenLabels(4);

        Legend mLegend = barChart.getLegend(); // 设置比例图标示
        mLegend.setForm(Legend.LegendForm.SQUARE);// 样式
        mLegend.setFormSize(10f);// 字体
        mLegend.setTextColor(Color.parseColor("#ff0000"));// 颜色
        mLegend.setMaxSizePercent(0.95f);   //坐标线描述 占据的大小x%  默认0.95 即95%
        mLegend.setEnabled(false);
//
        //点击显示的View
        HeartMarkerView markerView = new HeartMarkerView(context,
                R.layout.heart_markview_pop3);
        barChart.setMarkerView(markerView);
        // Y轴的设定
        barChart.getAxisLeft().setAxisLineColor(Color0);//Y轴颜色
        barChart.getAxisLeft().setGridColor(YGridColor);//Y轴网格颜色
        barChart.getAxisLeft().setAxisLineWidth(1.5f);
        barChart.getAxisLeft().setDrawAxisLine(false);
        barChart.getAxisLeft().setDrawGridLines(false);//是否显示Y轴网格
        barChart.getAxisLeft().setGridLineWidth(1f);
        barChart.getAxisLeft().setEnabled(false);//是否显示Y轴
//      barChart.getAxisLeft().setAxisMaxValue(200);//设置Y周最大值
        barChart.getAxisLeft().setAxisMinValue(20);//设置Y周最小值
        barChart.getAxisLeft().setSpaceTop(40f);//和顶端的距离，百分比
        barChart.getAxisLeft().setSpaceBottom(20f);//和顶端的距离，百分比，没效果
        barChart.getAxisLeft().setStartAtZero(false);//显示零相关的,设置成flase ，隐藏0
//      barChart.getAxisLeft().setShowOnlyMinMax(true);//是否显示最大最小值
        barChart.getAxisLeft().setLabelCount(3, false);//第一个参数是Y轴坐标的个数，第二个参数是 是否不均匀分布，true是不均匀分布
        barChart.getAxisRight().setDrawGridLines(false);//是否显示Y轴网格
        barChart.getAxisRight().setAxisLineColor(Color0);//Y轴颜色
        barChart.getAxisRight().setTextColor(Color0);


//      barChart.animateX(2000);//动画从左往右
//      barChart.animateY(1500);//动画下往上
//      barChart.animateXY(100,1000);// 两个轴动画，从左到右，从下到上

    }


    /**
     * 天数据-血压
     *
     * @param context
     * @param barChart
     * @param sbp_data
     * @param dbp_data
     */
    public static void showDayBpChart(Context context, BarChart barChart, String[] sbp_data, String[] dbp_data) {

        //加载数据

        int count = sbp_data.length;
        // 添加X轴坐标
        ArrayList<String> xValues = new ArrayList<String>();

        for (int i = 0; i < count; i++) {
            xValues.add("");
        }


        ArrayList<BarEntry> yValues = new ArrayList<BarEntry>();
        for (int i = 0; i < count; i++) {

            float val1 = Float.valueOf(dbp_data[i]);
            float val2 = (Float.valueOf(dbp_data[i]) + Float.valueOf(sbp_data[i])) / 2;
            float val3 = Float.valueOf(sbp_data[i]);
            yValues.add(new BarEntry(new float[]{val1, val2, val3}, i));

        }
        // Y轴的数据集

        int[] colors = new int[3];
        colors[0] = context.getResources().getColor(R.color.bp_day_state1);
        colors[1] = context.getResources().getColor(R.color.bp_day_state2);
        colors[2] = context.getResources().getColor(R.color.bp_day_state3);

        BarDataSet barDataSet = new BarDataSet(yValues, "");
        barDataSet.setColors(colors);
        barDataSet.setDrawValues(false);
        barDataSet.setHighlightEnabled(false);
        ArrayList<BarDataSet> barDataSets = new ArrayList<BarDataSet>();
        barDataSets.add(barDataSet);
        BarData barData = new BarData(xValues, barDataSets);

        //加载UI
        barChart.setDrawBorders(false); // //是否在折线图上添加边框

        barChart.setDescription("");// 数据描述
        // 如果没有数据的时候，会显示这个，类似ListView的EmptyView
        barChart.setNoDataTextDescription("");

        barChart.setDrawGridBackground(false); // 是否显示表格颜色
        barChart.setGridBackgroundColor(Color.TRANSPARENT); // 设置网格部分的颜色，默认的是白色
        // 表格的的颜色，在这里是是给颜色设置一个透明度

        barChart.setTouchEnabled(false); // 设置是否可以触摸

        barChart.setDragEnabled(true);// 是否可以拖拽
        barChart.setScaleEnabled(false);// 是否可以缩放
        barChart.setScaleYEnabled(false);// 设置Y轴不可缩放
        barChart.setDoubleTapToZoomEnabled(false);// 设置双击屏幕不可缩放

        barChart.setPinchZoom(false);// X,Y轴能否同时缩放
        barChart.setBackgroundColor(Color.TRANSPARENT);// 设置背景
        barChart.setDrawBarShadow(false); // 每一根柱状图的背景是否有阴影
        barChart.zoom(1.0f, 1.0f, 1.0f, 1.0f);// 这个可以设置X,Y轴的缩放比例（第一个参数：X轴的缩放比例，默认1.0f,第二个参数是Y轴的，第三个是X轴坐标比例,默认也是1.0f，第四个参数是Y轴的坐标比例）
        barChart.setData(barData); // 设置数据

        // X轴设定
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawLabels(false);
        xAxis.setAxisLineColor(Color.TRANSPARENT);//x轴颜色=透明
        xAxis.setAxisLineWidth(1.0f);

        Legend mLegend = barChart.getLegend(); // 设置比例图标示

        mLegend.setForm(Legend.LegendForm.SQUARE);// 样式
        mLegend.setFormSize(6f);// 字体
        mLegend.setTextColor(Color.GRAY);// 颜色
        mLegend.setEnabled(false);

        MyMarkerView markerView = new MyMarkerView(context, R.layout.heart_markview_pop);
        barChart.setMarkerView(markerView);
        // Y轴的设定
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getAxisRight().setDrawGridLines(false);
        barChart.getXAxis().setDrawGridLines(false);
        barChart.getAxisLeft().setEnabled(false);
        barChart.getAxisRight().setEnabled(false);
//        barChart.animateY(2500);

    }


    /**
     * 周数据-运动
     *
     * @param context
     * @param barChart
     * @param sport_data
     */
    public static void showWeekSportBarChart(Context context, BarChart barChart, List<String> sport_data) {


//        sport_data = getNewtData(sport_data);

        // 添加X轴坐标
        ArrayList<String> xValues = new ArrayList<String>();
        // 添加Y轴坐标
        ArrayList<BarEntry> yValues = new ArrayList<BarEntry>();

        //加载数据
        int count = sport_data.size();

        if (count == 7) {

            MyLog.i("步数周图表", "满足条件1");

            for (int i = 0; i < count; i++) {
                // x轴显示的数据，这里默认使用数字下标显示
                xValues.add(context.getString(chart_data_week[i]));
                float value = Float.parseFloat(sport_data.get(i));
                yValues.add(new BarEntry(value, i));
            }
        }
        //
        else if (count < 7 && count >= 1) {

            MyLog.i("步数周图表", "满足条件2");

            int start_pos = 7 - count;


            //补充空数据
            for (int i = count; i < 7; i++) {
                // x轴显示的数据，这里默认使用数字下标显示
                MyLog.i("步数周图表 1", "i = " + i + " end = " + (i - count));

                int new_i = i - count;

                if (new_i >= 0 && new_i < chart_data_week.length) {
                    xValues.add(context.getString(chart_data_week[new_i]));

                    if (new_i >= 0 && new_i < count) {
                        float value = 0;
                        yValues.add(new BarEntry(value, new_i));
                    }
                }

            }


            //填充数据
            for (int i = start_pos; i < 7; i++) {
                // x轴显示的数据，这里默认使用数字下标显示
                MyLog.i("步数周图表 2", "i = " + i + " end = " + i);

                if (i >= 0 && i < chart_data_week.length) {
                    xValues.add(context.getString(chart_data_week[i]));

                    int new_i = i - start_pos;

                    if (new_i >= 0 && new_i < count) {
                        float value = Float.parseFloat(sport_data.get(new_i));
                        yValues.add(new BarEntry(value, i));
                    }


                }
            }

//
//            for (int i = 0; i < count; i++) {
//                xValues.add("" + i);
//                float value = Float.parseFloat(sport_data.get(i));
//                yValues.add(new BarEntry(value, i));
//            }


        } else {

            MyLog.i("步数周图表", "满足条件3");

            for (int i = 0; i < count; i++) {
                xValues.add("" + i);
                float value = Float.parseFloat(sport_data.get(i));
                yValues.add(new BarEntry(value, i));
            }
        }


//        for (int i = 0; i < count; i++) {
//            float value = Float.parseFloat(sport_data.get(i));
//            yValues.add(new BarEntry(value, i));
//        }

        // Y轴的数据集
        BarDataSet barDataSet = new BarDataSet(yValues, "");
        barDataSet.setColor(context.getResources().getColor(R.color.my_exercise_line_chart_color));//设置条形图颜色
        barDataSet.setDrawValues(false);
        barDataSet.setHighlightEnabled(false);
        ArrayList<BarDataSet> barDataSets = new ArrayList<BarDataSet>();
        barDataSets.add(barDataSet);
        BarData barData = new BarData(xValues, barDataSets);

        //加载UI
        barChart.setDrawBorders(false); // //是否在折线图上添加边框
        barChart.setDescription("");// 数据描述
        // 如果没有数据的时候，会显示这个，类似ListView的EmptyView
        barChart.setNoDataTextDescription("");

        barChart.setDrawGridBackground(false); // 是否显示表格颜色
        barChart.setGridBackgroundColor(Color.TRANSPARENT); // 设置网格部分的颜色，默认的是白色
        // 表格的的颜色，在这里是是给颜色设置一个透明度

        barChart.setTouchEnabled(false); // 设置是否可以触摸

        barChart.setDragEnabled(true);// 是否可以拖拽
        barChart.setScaleEnabled(false);// 是否可以缩放
        barChart.setScaleYEnabled(false);// 设置Y轴不可缩放
        barChart.setDoubleTapToZoomEnabled(false);// 设置双击屏幕不可缩放

        barChart.setPinchZoom(false);// X,Y轴能否同时缩放
        barChart.setBackgroundColor(Color.TRANSPARENT);// 设置背景
        barChart.setDrawBarShadow(false); // 每一根柱状图的背景是否有阴影
        barChart.zoom(1.0f, 1.0f, 1.0f, 1.0f);// 这个可以设置X,Y轴的缩放比例（第一个参数：X轴的缩放比例，默认1.0f,第二个参数是Y轴的，第三个是X轴坐标比例,默认也是1.0f，第四个参数是Y轴的坐标比例）
        barChart.setData(barData); // 设置数据

        // X轴设定
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawLabels(true);//显示X轴标签
        xAxis.setAxisLineColor(Color.TRANSPARENT);//x轴颜色=透明
        xAxis.setAxisLineWidth(1.0f);
        xAxis.setDrawGridLines(false);//是否显示X轴网格
        //        xAxis.setTextSize(0f);//X轴字体大小
        xAxis.setLabelsToSkip(0);    //设置坐标相隔多少，参数是int类型
//        xAxis.resetLabelsToSkip();   //将自动计算坐标相隔多少
//        xAxis.setAvoidFirstLastClipping(true);
//        xAxis.setSpaceBetweenLabels(4);
        xAxis.setTextColor(Color.parseColor("#a8abac"));// X轴字体颜色

        Legend mLegend = barChart.getLegend(); // 设置比例图标示

        mLegend.setForm(Legend.LegendForm.SQUARE);// 样式
        mLegend.setFormSize(6f);// 字体
        mLegend.setTextColor(Color.GRAY);// 颜色
        mLegend.setEnabled(false);

        MyMarkerView markerView = new MyMarkerView(context, R.layout.heart_markview_pop);
        barChart.setMarkerView(markerView);
        // Y轴的设定
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getAxisRight().setDrawGridLines(false);
        barChart.getXAxis().setDrawGridLines(false);
        barChart.getAxisLeft().setEnabled(false);
        barChart.getAxisRight().setEnabled(false);
//        barChart.animateY(2500);

    }


    /**
     * 周数据-睡眠
     *
     * @param context
     * @param barChart
     * @param deep_data
     * @param light_data
     * @param sober_data
     */
    public static void showWeekSleepBarChart(Context context, BarChart barChart, List<String> deep_data, List<String> light_data, List<String> sober_data) {


//        deep_data = getNewtData(deep_data);
//        light_data = getNewtData(light_data);
//        sober_data = getNewtData(sober_data);


        //加载数据
        int count = deep_data.size();

        // 添加X轴坐标
        ArrayList<String> xValues = new ArrayList<String>();
        // 添加Y轴坐标
        ArrayList<BarEntry> yValues = new ArrayList<BarEntry>();

//        for (int i = 0; i < count; i++) {
//            xValues.add("");
//        }

        if (count == 7) {
            MyLog.i("睡眠周图表", "满足条件1");
            for (int i = 0; i < count; i++) {
                // x轴显示的数据，这里默认使用数字下标显示
                xValues.add(context.getString(chart_data_week[i]));
                float val1 = Float.valueOf(deep_data.get(i));
                float val2 = Float.valueOf(light_data.get(i));
                float val3 = Float.valueOf(sober_data.get(i));

                yValues.add(new BarEntry(new float[]{val1, val2, val3}, i));
            }
        } else if (count < 7 && count >= 1) {

            MyLog.i("睡眠周图表", "满足条件2 count = " + count);

            int start_pos = 7 - count;


            //补充空数据
            for (int i = count; i < 7; i++) {
                // x轴显示的数据，这里默认使用数字下标显示


                int new_i = i - count;

                MyLog.i("睡眠周图表 1", "i = " + i + " end = " + new_i);

                if (new_i >= 0 && new_i < chart_data_week.length) {
                    xValues.add(context.getString(chart_data_week[new_i]));

                    if (new_i >= 0 && new_i < count) {

                        float val1 = 0;
                        float val2 = 0;
                        float val3 = 0;

                        yValues.add(new BarEntry(new float[]{val1, val2, val3}, new_i));
                    }
                }

            }


            //填充数据
            for (int i = start_pos; i < 7; i++) {
                // x轴显示的数据，这里默认使用数字下标显示
                MyLog.i("睡眠周图表 2", "i = " + i + " end = " + i);

                if (i >= 0 && i < chart_data_week.length) {

                    xValues.add(context.getString(chart_data_week[i]));

                    int new_i = i - start_pos;

                    MyLog.i("睡眠周图表 3", "i = " + i + " new_i = " + new_i);

                    if (new_i >= 0 && new_i < count) {
                        float val1 = Float.valueOf(deep_data.get(new_i));
                        float val2 = Float.valueOf(light_data.get(new_i));
                        float val3 = Float.valueOf(sober_data.get(new_i));

                        yValues.add(new BarEntry(new float[]{val1, val2, val3}, i));
                    }


                }
            }


        } else {
            MyLog.i("睡眠周图表", "满足条件2");

            for (int i = 0; i < count; i++) {
//                xValues.add("");
                xValues.add("" + i);
                float val1 = Float.valueOf(deep_data.get(i));
                float val2 = Float.valueOf(light_data.get(i));
                float val3 = Float.valueOf(sober_data.get(i));

                yValues.add(new BarEntry(new float[]{val1, val2, val3}, i));
            }
        }


//        for (int i = 0; i < count; i++) {
//
//            float val1 = Float.valueOf(deep_data.get(i));
//            float val2 = Float.valueOf(light_data.get(i));
//            float val3 = Float.valueOf(sober_data.get(i));
//
//            yValues.add(new BarEntry(new float[]{val1, val2, val3}, i));
//        }

        // Y轴的数据集
        int[] colors = new int[3];
        colors[0] = context.getResources().getColor(R.color.sleep_week_state1);
        colors[1] = context.getResources().getColor(R.color.sleep_week_state2);
        colors[2] = context.getResources().getColor(R.color.sleep_week_state3);

        BarDataSet barDataSet = new BarDataSet(yValues, "");
        barDataSet.setColors(colors);
        barDataSet.setDrawValues(false);
        barDataSet.setHighlightEnabled(false);
        ArrayList<BarDataSet> barDataSets = new ArrayList<BarDataSet>();
        barDataSets.add(barDataSet);
        BarData barData = new BarData(xValues, barDataSets);

        //加载UI
        barChart.setDrawBorders(false); // //是否在折线图上添加边框

        barChart.setDescription("");// 数据描述
        // 如果没有数据的时候，会显示这个，类似ListView的EmptyView
        barChart.setNoDataTextDescription("");

        barChart.setDrawGridBackground(false); // 是否显示表格颜色
        barChart.setGridBackgroundColor(Color.TRANSPARENT); // 设置网格部分的颜色，默认的是白色
        // 表格的的颜色，在这里是是给颜色设置一个透明度

        barChart.setTouchEnabled(false); // 设置是否可以触摸

        barChart.setDragEnabled(true);// 是否可以拖拽
        barChart.setScaleEnabled(false);// 是否可以缩放
        barChart.setScaleYEnabled(false);// 设置Y轴不可缩放
        barChart.setDoubleTapToZoomEnabled(false);// 设置双击屏幕不可缩放

        barChart.setPinchZoom(false);// X,Y轴能否同时缩放
        barChart.setBackgroundColor(Color.TRANSPARENT);// 设置背景
        barChart.setDrawBarShadow(false); // 每一根柱状图的背景是否有阴影
        barChart.zoom(1.0f, 1.0f, 1.0f, 1.0f);// 这个可以设置X,Y轴的缩放比例（第一个参数：X轴的缩放比例，默认1.0f,第二个参数是Y轴的，第三个是X轴坐标比例,默认也是1.0f，第四个参数是Y轴的坐标比例）
        barChart.setData(barData); // 设置数据

        // X轴设定
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawLabels(true);//是否显示X轴标签
        //        xAxis.setAxisLineColor(Color.TRANSPARENT);
        xAxis.setAxisLineColor(Color.TRANSPARENT);//x轴颜色=透明
        xAxis.setAxisLineWidth(1.0f);
        xAxis.setDrawGridLines(false);//是否显示X轴网格
//        xAxis.setTextSize(0f);//X轴字体大小
//        xAxis.setLabelsToSkip(1);    //设置坐标相隔多少，参数是int类型
//        xAxis.resetLabelsToSkip();   //将自动计算坐标相隔多少
//        xAxis.setAvoidFirstLastClipping(true);
//        xAxis.setSpaceBetweenLabels(4);
        xAxis.setTextColor(Color.parseColor("#a8abac"));// X轴字体颜色

        Legend mLegend = barChart.getLegend(); // 设置比例图标示

        mLegend.setForm(Legend.LegendForm.SQUARE);// 样式
        mLegend.setFormSize(6f);// 字体
        mLegend.setTextColor(Color.GRAY);// 颜色
        mLegend.setEnabled(false);

        MyMarkerView markerView = new MyMarkerView(context, R.layout.heart_markview_pop);
        barChart.setMarkerView(markerView);
        // Y轴的设定
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getAxisRight().setDrawGridLines(false);
        barChart.getXAxis().setDrawGridLines(false);
        barChart.getAxisLeft().setEnabled(false);
        barChart.getAxisRight().setEnabled(false);
//        barChart.animateY(2500);

    }


    /**
     * 周数据页，心率数据
     *
     * @param context
     * @param barChart
     * @param allday_avg_data
     * @param sleep_avg_data
     */
    public static void showWeekHearBarChart(Context context, LineChart barChart, List<String> allday_avg_data, List<String> sleep_avg_data) {


        //==============加载数据
        int count = allday_avg_data.size();
        // 添加X轴坐标
        ArrayList<String> xValues = new ArrayList<String>();
        // y轴的数据
        ArrayList<Entry> yValues1 = new ArrayList<Entry>();
        ArrayList<Entry> yValues2 = new ArrayList<Entry>();
//        for (int i = 0; i < count; i++) {
//            // x轴显示的数据，这里默认使用数字下标显示
//            if (i == 0) {
//                xValues.add("");
//            } else {
//                xValues.add("");
//            }
//        }

        if (count == 7) {
            for (int i = 0; i < count; i++) {
                // x轴显示的数据，这里默认使用数字下标显示
                xValues.add(context.getString(chart_data_week[i]));
                float value1 = Float.parseFloat(allday_avg_data.get(i));
                float value2 = Float.parseFloat(sleep_avg_data.get(i));
                yValues1.add(new BarEntry(value1, i));
                yValues2.add(new BarEntry(value2, i));
            }
        } else if (count < 7 && count >= 1) {

            MyLog.i("心率周图表", "满足条件2");

            int start_pos = 7 - count;


            //补充空数据
            for (int i = count; i < 7; i++) {
                // x轴显示的数据，这里默认使用数字下标显示
                MyLog.i("心率周图表 1", "i = " + i + " end = " + (i - count));

                int new_i = i - count;

                if (new_i >= 0 && new_i < chart_data_week.length) {
                    xValues.add(context.getString(chart_data_week[new_i]));

                    if (new_i >= 0 && new_i < count) {

                        float value1 = 0;
                        float value2 = 0;
                        yValues1.add(new BarEntry(value1, new_i));
                        yValues2.add(new BarEntry(value2, new_i));
                    }
                }

            }


            //填充数据
            for (int i = start_pos; i < 7; i++) {
                // x轴显示的数据，这里默认使用数字下标显示
                MyLog.i("心率周图表 2", "i = " + i + " end = " + i);

                if (i >= 0 && i < chart_data_week.length) {
                    xValues.add(context.getString(chart_data_week[i]));

                    int new_i = i - start_pos;

                    if (new_i >= 0 && new_i < count) {
                        float value1 = Float.parseFloat(allday_avg_data.get(new_i));
                        float value2 = Float.parseFloat(sleep_avg_data.get(new_i));
                        yValues1.add(new BarEntry(value1, i));
                        yValues2.add(new BarEntry(value2, i));
                    }


                }
            }

//
//            for (int i = 0; i < count; i++) {
//                xValues.add("" + i);
//                float value = Float.parseFloat(sport_data.get(i));
//                yValues.add(new BarEntry(value, i));
//            }


        } else {
            for (int i = 0; i < count; i++) {
                xValues.add("" + i);
                float value1 = Float.parseFloat(allday_avg_data.get(i));
                float value2 = Float.parseFloat(sleep_avg_data.get(i));
                yValues1.add(new BarEntry(value1, i));
                yValues2.add(new BarEntry(value2, i));
            }
        }

//
//        for (int i = 0; i < count; i++) {
//            float value1 = Float.parseFloat(allday_avg_data.get(i));
//            float value2 = Float.parseFloat(sleep_avg_data.get(i));
//            yValues1.add(new BarEntry(value1, i));
//            yValues2.add(new BarEntry(value2, i));
//        }

        // create a dataset and give it a type
        // y轴的数据集合
        LineDataSet lineDataSet1 = new LineDataSet(yValues1, "" /*显示在比例图上*/);
        LineDataSet lineDataSet2 = new LineDataSet(yValues2, "" /*显示在比例图上*/);


        int[] colors = new int[2];
        colors[0] = context.getResources().getColor(R.color.heart_week_state1);
        colors[1] = context.getResources().getColor(R.color.heart_week_state2);

        //用y轴的集合来设置参数
        lineDataSet1.setLineWidth(1f); // 线宽
        lineDataSet1.setCircleSize(3f);// 显示的圆形大小 0f 表示没有
        lineDataSet1.setDrawCircleHole(false);//是否空心
        lineDataSet1.setCircleColor(colors[0]);// 圆形的颜色
        lineDataSet1.setDrawValues(false);//是否显示数字
        lineDataSet1.setHighLightColor(Color.parseColor("#00000000")); // 高亮的线的颜色
        lineDataSet1.setDrawCubic(true);//是否显示曲线
        lineDataSet1.setCubicIntensity(0.05f);//曲线率
        lineDataSet1.setFillColor(Color.TRANSPARENT);//填充颜色
        lineDataSet1.setDrawFilled(false);//是否填充
        lineDataSet1.setFillAlpha(76);//透明度百分之30
        lineDataSet1.setColor(colors[0]);// 线条颜色颜色

        lineDataSet2.setLineWidth(1f); // 线宽
        lineDataSet2.setCircleSize(3f);// 显示的圆形大小 0f 表示没有
        lineDataSet2.setDrawCircleHole(false);//是否空心
        lineDataSet2.setCircleColor(colors[1]);// 圆形的颜色
        lineDataSet2.setDrawValues(false);//是否显示数字
        lineDataSet2.setHighLightColor(Color.parseColor("#00000000")); // 高亮的线的颜色
        lineDataSet2.setDrawCubic(true);//是否显示曲线
        lineDataSet2.setCubicIntensity(0.05f);//曲线率
        lineDataSet2.setFillColor(Color.TRANSPARENT);//填充颜色
        lineDataSet2.setDrawFilled(false);//是否填充
        lineDataSet2.setFillAlpha(76);//透明度百分之30
        lineDataSet2.setColor(colors[1]);// 线条颜色颜色


        ArrayList<LineDataSet> lineDataSets = new ArrayList<LineDataSet>();
        lineDataSets.add(lineDataSet1); // add the datasets
        lineDataSets.add(lineDataSet2); // add the datasets

        // create a data object with the datasets
        LineData barData = new LineData(xValues, lineDataSets);


        //==============加载UI

        barChart.setDrawBorders(false); // //是否在折线图上添加边框

        barChart.setDescription("");// 数据描述
        // 如果没有数据的时候，会显示这个，类似ListView的EmptyView
        barChart.setNoDataTextDescription("");
        barChart.setDrawGridBackground(true); // 是否显示表格颜色
        barChart.setGridBackgroundColor(Color.TRANSPARENT); // 设置网格部分的颜色，默认的是白色
//        barChart.setGridBackgroundColor(Color.parseColor("#bedcfe")); // 设置网格部分的颜色，默认的是白色
        // 表格的的颜色，在这里是是给颜色设置一个透明度

        barChart.setTouchEnabled(false); // 设置是否可以触摸
        barChart.setDragEnabled(true);// 是否可以拖拽
        barChart.setScaleEnabled(false);// 是否可以缩放
        barChart.setScaleYEnabled(false);// 设置Y轴不可缩放
        barChart.setDoubleTapToZoomEnabled(false);// 设置双击屏幕不可缩放

        barChart.setPinchZoom(false);// X,Y轴能否同时缩放
        barChart.setBackgroundColor(Color.TRANSPARENT);// 设置背景
//        barChart.setDrawBarShadow(false); // 每一根柱状图的背景是否有阴影
        barChart.zoom(1.0f, 1.0f, 1.0f, 1.0f);// 这个可以设置X,Y轴的缩放比例（第一个参数：X轴的缩放比例，默认1.0f,第二个参数是Y轴的，第三个是X轴坐标比例,默认也是1.0f，第四个参数是Y轴的坐标比例）
        barChart.setData(barData); // 设置数据
//        barChart.setData(getLineData(10,10)); // 设置数据


        // X轴设定
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawLabels(true);//是否显示X轴标签
//        xAxis.setAxisLineColor(Color.TRANSPARENT);
        xAxis.setAxisLineColor(Color.TRANSPARENT);
        xAxis.setAxisLineWidth(1.5f);
        xAxis.setDrawGridLines(false);//是否显示X轴网格
//        xAxis.setTextSize(0f);//X轴字体大小
//        xAxis.setLabelsToSkip(1);    //设置坐标相隔多少，参数是int类型
//        xAxis.resetLabelsToSkip();   //将自动计算坐标相隔多少
//        xAxis.setAvoidFirstLastClipping(true);
//        xAxis.setSpaceBetweenLabels(4);
        xAxis.setTextColor(Color.parseColor("#a8abac"));// X轴字体颜色


        Legend mLegend = barChart.getLegend(); // 设置比例图标示
        mLegend.setForm(Legend.LegendForm.SQUARE);// 样式
        mLegend.setFormSize(10f);// 字体
        mLegend.setTextColor(Color.parseColor("#ff0000"));// 颜色
        mLegend.setMaxSizePercent(0.95f);   //坐标线描述 占据的大小x%  默认0.95 即95%
        mLegend.setEnabled(false);

        //点击显示的View
        PoHeartMarkerView markerView = new PoHeartMarkerView(context,
                R.layout.po_heart_markview_pop);
        barChart.setMarkerView(markerView);
        // Y轴的设定

        barChart.getAxisLeft().setAxisLineColor(Color.TRANSPARENT);//Y轴颜色
        barChart.getAxisLeft().setTextColor(Color.TRANSPARENT);
        barChart.getAxisLeft().setGridColor(Color.TRANSPARENT);//Y轴网格颜色
        barChart.getAxisLeft().setAxisLineWidth(1.5f);
        barChart.getAxisLeft().setDrawAxisLine(true);
        barChart.getAxisLeft().setDrawGridLines(false);//是否显示Y轴网格
        barChart.getAxisLeft().setEnabled(true);//是否显示Y轴
//      barChart.getAxisLeft().setAxisMaxValue(200);//设置Y周最大值
//      barChart.getAxisLeft().setAxisMinValue(20);//设置Y周最小值
        barChart.getAxisLeft().setSpaceTop(35f);//和顶端的距离，百分比
//        barChart.getAxisLeft().setSpaceBottom(50f);//和顶端的距离，百分比，没效果
        barChart.getAxisLeft().setStartAtZero(true);//显示零相关的,设置成flase ，地下会出来空隙
//        barChart.getAxisLeft().setShowOnlyMinMax(true);//是否显示最大最小值
        barChart.getAxisLeft().setLabelCount(5, false);//第一个参数是Y轴坐标的个数，第二个参数是 是否不均匀分布，true是不均匀分布
        barChart.getAxisRight().setDrawGridLines(false);
        barChart.getAxisRight().setEnabled(true);
        barChart.getAxisRight().setAxisLineColor(Color.TRANSPARENT);//Y轴颜色
        barChart.getAxisRight().setTextColor(Color.TRANSPARENT);

//        barChart.animateX(2000);//动画从左往右
//        barChart.animateY(1500);//动画下往上
//        barChart.animateXY(100,1000);// 两个轴动画，从左到右，从下到上

    }


    /**
     * 周数据页，健康值
     *
     * @param context
     * @param barChart
     * @param health_index_data
     */
    public static void showWeekHealthBarChart(Context context, LineChart barChart, List<String> health_index_data) {

        //======加载数据
        int count = health_index_data.size();

        // X轴的数据
        ArrayList<String> xValues = new ArrayList<String>();
        // y轴的数据
        ArrayList<Entry> yValues = new ArrayList<Entry>();

//        if (count == 7) {
//            for (int i = 0; i < count; i++) {
//                // x轴显示的数据，这里默认使用数字下标显示
//                xValues.add(context.getString(chart_data_week[i]));
//            }
//        } else {
//            for (int i = 0; i < count; i++) {
//                xValues.add("");
//            }
//        }

        if (count == 7) {
            for (int i = 0; i < count; i++) {
                // x轴显示的数据，这里默认使用数字下标显示
                xValues.add(context.getString(chart_data_week[i]));
                float value = Float.parseFloat(health_index_data.get(i));
                yValues.add(new BarEntry(value, i));
            }
        } else if (count < 7 && count >= 1) {

            MyLog.i("健康值周图表", "满足条件2");

            int start_pos = 7 - count;


            //补充空数据
            for (int i = count; i < 7; i++) {
                // x轴显示的数据，这里默认使用数字下标显示
                MyLog.i("健康值周图表 1", "i = " + i + " end = " + (i - count));

                int new_i = i - count;

                if (new_i >= 0 && new_i < chart_data_week.length) {
                    xValues.add(context.getString(chart_data_week[new_i]));

                    if (new_i >= 0 && new_i < count) {

                        float value = 0;
                        yValues.add(new BarEntry(value, new_i));
                    }
                }

            }


            //填充数据
            for (int i = start_pos; i < 7; i++) {
                // x轴显示的数据，这里默认使用数字下标显示
                MyLog.i("健康值周图表 2", "i = " + i + " end = " + i);

                if (i >= 0 && i < chart_data_week.length) {
                    xValues.add(context.getString(chart_data_week[i]));

                    int new_i = i - start_pos;

                    if (new_i >= 0 && new_i < count) {
                        float value = Float.parseFloat(health_index_data.get(new_i));
                        yValues.add(new BarEntry(value, i));
                    }


                }
            }

//
//            for (int i = 0; i < count; i++) {
//                xValues.add("" + i);
//                float value = Float.parseFloat(sport_data.get(i));
//                yValues.add(new BarEntry(value, i));
//            }


        } else {
            for (int i = 0; i < count; i++) {
                xValues.add("" + i);
                float value = Float.parseFloat(health_index_data.get(i));
                yValues.add(new BarEntry(value, i));
            }
        }


//        for (int i = 0; i < count; i++) {
//            float value = Float.parseFloat(health_index_data.get(i));
//            yValues.add(new BarEntry(value, i));
//        }

        // create a dataset and give it a type
        // y轴的数据集合
        LineDataSet lineDataSet = new LineDataSet(yValues, "" /*显示在比例图上*/);

        //用y轴的集合来设置参数
        lineDataSet.setLineWidth(1f); // 线宽
        lineDataSet.setCircleSize(3f);// 显示的圆形大小 0f 表示没有
        lineDataSet.setDrawCircleHole(true);//是否空心
        lineDataSet.setCircleColor(Color.parseColor("#fff27273"));// 圆形的颜色

        lineDataSet.setDrawValues(false);//是否显示数字
        lineDataSet.setHighLightColor(Color.parseColor("#00000000")); // 高亮的线的颜色
        lineDataSet.setDrawCubic(true);//是否显示曲线
        lineDataSet.setCubicIntensity(0.05f);//曲线率
        lineDataSet.setFillColor(Color.TRANSPARENT);//填充颜色
        lineDataSet.setDrawFilled(false);//是否填充
        lineDataSet.setFillAlpha(76);//透明度百分之30
        lineDataSet.setColor(Color.parseColor("#fff26e7e"));// 线条颜色颜色

//        lineDataSet.setCircleColors(MyColor);//设置圈的颜色数组
//        lineDataSet.setColors(MyColor);//设置线的颜色数组（不能设置曲线属性）

        ArrayList<LineDataSet> lineDataSets = new ArrayList<LineDataSet>();
        lineDataSets.add(lineDataSet); // add the datasets

        // create a data object with the datasets
        LineData lineData = new LineData(xValues, lineDataSets);

        //=====加载UI

        barChart.setDrawBorders(false); // //是否在折线图上添加边框

        barChart.setDescription("");// 数据描述
        // 如果没有数据的时候，会显示这个，类似ListView的EmptyView
        barChart.setNoDataTextDescription(context.getString(R.string.no_data));//没有数据的时候，显示这句话
        barChart.setDrawGridBackground(true); // 是否显示表格颜色
        barChart.setGridBackgroundColor(Color.TRANSPARENT); // 设置网格部分的颜色，默认的是白色
//        barChart.setGridBackgroundColor(Color.parseColor("#bedcfe")); // 设置网格部分的颜色，默认的是白色
        // 表格的的颜色，在这里是是给颜色设置一个透明度

        barChart.setTouchEnabled(false); // 设置是否可以触摸
        barChart.setDragEnabled(true);// 是否可以拖拽
        barChart.setScaleEnabled(false);// 是否可以缩放
        barChart.setScaleYEnabled(false);// 设置Y轴不可缩放
        barChart.setDoubleTapToZoomEnabled(false);// 设置双击屏幕不可缩放

        barChart.setPinchZoom(false);// X,Y轴能否同时缩放
        barChart.setBackgroundColor(Color.TRANSPARENT);// 设置背景
//        barChart.setDrawBarShadow(false); // 每一根柱状图的背景是否有阴影
        barChart.zoom(1.0f, 1.0f, 1.0f, 1.0f);// 这个可以设置X,Y轴的缩放比例（第一个参数：X轴的缩放比例，默认1.0f,第二个参数是Y轴的，第三个是X轴坐标比例,默认也是1.0f，第四个参数是Y轴的坐标比例）
        barChart.setData(lineData); // 设置数据
//        barChart.setData(getLineData(10,10)); // 设置数据


        // X轴设定
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawLabels(true);//是否显示X轴标签
//        xAxis.setAxisLineColor(Color.TRANSPARENT);
        xAxis.setAxisLineColor(Color.TRANSPARENT);
        xAxis.setAxisLineWidth(1.5f);
        xAxis.setDrawGridLines(false);//是否显示X轴网格
//        xAxis.setTextSize(0f);//X轴字体大小
//        xAxis.setLabelsToSkip(1);    //设置坐标相隔多少，参数是int类型
//        xAxis.resetLabelsToSkip();   //将自动计算坐标相隔多少
//        xAxis.setAvoidFirstLastClipping(true);
//        xAxis.setSpaceBetweenLabels(4);
        xAxis.setTextColor(Color.parseColor("#a8abac"));// X轴字体颜色

        Legend mLegend = barChart.getLegend(); // 设置比例图标示
        mLegend.setForm(Legend.LegendForm.SQUARE);// 样式
        mLegend.setFormSize(10f);// 字体
        mLegend.setTextColor(Color.parseColor("#ff0000"));// 颜色
        mLegend.setMaxSizePercent(0.95f);   //坐标线描述 占据的大小x%  默认0.95 即95%
        mLegend.setEnabled(false);


        //点击显示的View
        PoHeartMarkerView markerView = new PoHeartMarkerView(context, R.layout.po_heart_markview_pop);
        barChart.setMarkerView(markerView);

        // Y轴的设定
        barChart.getAxisLeft().setAxisLineColor(Color.TRANSPARENT);//Y轴颜色
        barChart.getAxisLeft().setTextColor(Color.TRANSPARENT);
        barChart.getAxisLeft().setGridColor(Color.TRANSPARENT);//Y轴网格颜色
        barChart.getAxisLeft().setAxisLineWidth(1.5f);
        barChart.getAxisLeft().setDrawAxisLine(true);
        barChart.getAxisLeft().setDrawGridLines(false);//是否显示Y轴网格
        barChart.getAxisLeft().setEnabled(true);//是否显示Y轴
//      barChart.getAxisLeft().setAxisMaxValue(200);//设置Y周最大值
//      barChart.getAxisLeft().setAxisMinValue(20);//设置Y周最小值
        barChart.getAxisLeft().setSpaceTop(35f);//和顶端的距离，百分比
//        barChart.getAxisLeft().setSpaceBottom(50f);//和顶端的距离，百分比，没效果
        barChart.getAxisLeft().setStartAtZero(true);//显示零相关的,设置成flase ，地下会出来空隙
//        barChart.getAxisLeft().setShowOnlyMinMax(true);//是否显示最大最小值
        barChart.getAxisLeft().setLabelCount(5, false);//第一个参数是Y轴坐标的个数，第二个参数是 是否不均匀分布，true是不均匀分布
        barChart.getAxisRight().setDrawGridLines(false);
        barChart.getAxisRight().setEnabled(true);
        barChart.getAxisRight().setAxisLineColor(Color.TRANSPARENT);//Y轴颜色
        barChart.getAxisRight().setTextColor(Color.TRANSPARENT);

        //动画刷新相关
//        barChart.animateX(2000);//动画从左往右
//        barChart.animateY(1500);//动画下往上
//        barChart.animateXY(100,1000);// 两个轴动画，从左到右，从下到上

    }


    /**
     * 周数据页，血压数据
     *
     * @param context
     * @param barChart
     * @param bp_haert
     * @param bp_sbp
     * @param bp_dbp
     */
    public static void showWeekBpBarChart(Context context, LineChart barChart, String[] bp_haert, String[] bp_sbp, String[] bp_dbp) {


        //================数据加载==============================
        int count = bp_haert.length;

        ArrayList<String> xValues = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            // x轴显示的数据，这里默认使用数字下标显示
            if (i == 0) {
                xValues.add("");
            } else {
                xValues.add("");
            }
        }

        // y轴的数据
        ArrayList<Entry> yValues1 = new ArrayList<Entry>();
        ArrayList<Entry> yValues2 = new ArrayList<Entry>();
        ArrayList<Entry> yValues3 = new ArrayList<Entry>();

        for (int i = 0; i < count; i++) {
            float value1 = Float.parseFloat(bp_haert[i]);
            float value2 = Float.parseFloat(bp_sbp[i]);
            float value3 = Float.parseFloat(bp_dbp[i]);
            yValues1.add(new BarEntry(value1, i));
            yValues2.add(new BarEntry(value2, i));
            yValues3.add(new BarEntry(value3, i));
        }
        // create a dataset and give it a type
        // y轴的数据集合
        LineDataSet lineDataSet1 = new LineDataSet(yValues1, "" /*显示在比例图上*/);
        LineDataSet lineDataSet2 = new LineDataSet(yValues2, "" /*显示在比例图上*/);
        LineDataSet lineDataSet3 = new LineDataSet(yValues3, "" /*显示在比例图上*/);


        int[] colors = new int[3];
        colors[0] = context.getResources().getColor(R.color.bp_week_state1);
        colors[1] = context.getResources().getColor(R.color.bp_week_state2);
        colors[2] = context.getResources().getColor(R.color.bp_week_state3);

        //用y轴的集合来设置参数
        lineDataSet1.setLineWidth(1f); // 线宽
        lineDataSet1.setCircleSize(3f);// 显示的圆形大小 0f 表示没有
        lineDataSet1.setDrawCircleHole(false);//是否空心
        lineDataSet1.setCircleColor(colors[0]);// 圆形的颜色
        lineDataSet1.setDrawValues(false);//是否显示数字
        lineDataSet1.setHighLightColor(Color.parseColor("#00000000")); // 高亮的线的颜色
        lineDataSet1.setDrawCubic(true);//是否显示曲线
        lineDataSet1.setCubicIntensity(0.05f);//曲线率
        lineDataSet1.setFillColor(Color.TRANSPARENT);//填充颜色
        lineDataSet1.setDrawFilled(false);//是否填充
        lineDataSet1.setFillAlpha(76);//透明度百分之30
        lineDataSet1.setColor(colors[0]);// 线条颜色颜色

        lineDataSet2.setLineWidth(1f); // 线宽
        lineDataSet2.setCircleSize(3f);// 显示的圆形大小 0f 表示没有
        lineDataSet2.setDrawCircleHole(false);//是否空心
        lineDataSet2.setCircleColor(colors[1]);// 圆形的颜色
        lineDataSet2.setDrawValues(false);//是否显示数字
        lineDataSet2.setHighLightColor(Color.parseColor("#00000000")); // 高亮的线的颜色
        lineDataSet2.setDrawCubic(true);//是否显示曲线
        lineDataSet2.setCubicIntensity(0.05f);//曲线率
        lineDataSet2.setFillColor(Color.TRANSPARENT);//填充颜色
        lineDataSet2.setDrawFilled(false);//是否填充
        lineDataSet2.setFillAlpha(76);//透明度百分之30
        lineDataSet2.setColor(colors[1]);// 线条颜色颜色


        lineDataSet3.setLineWidth(1f); // 线宽
        lineDataSet3.setCircleSize(3f);// 显示的圆形大小 0f 表示没有
        lineDataSet3.setDrawCircleHole(false);//是否空心
        lineDataSet3.setCircleColor(colors[2]);// 圆形的颜色
        lineDataSet3.setDrawValues(false);//是否显示数字
        lineDataSet3.setHighLightColor(Color.parseColor("#00000000")); // 高亮的线的颜色
        lineDataSet3.setDrawCubic(true);//是否显示曲线
        lineDataSet3.setCubicIntensity(0.05f);//曲线率
        lineDataSet3.setFillColor(Color.TRANSPARENT);//填充颜色
        lineDataSet3.setDrawFilled(false);//是否填充
        lineDataSet3.setFillAlpha(76);//透明度百分之30
        lineDataSet3.setColor(colors[2]);// 线条颜色颜色


        ArrayList<LineDataSet> lineDataSets = new ArrayList<LineDataSet>();
        lineDataSets.add(lineDataSet1); // add the datasets
        lineDataSets.add(lineDataSet2); // add the datasets
        lineDataSets.add(lineDataSet3); // add the datasets

        // create a data object with the datasets
        LineData barData = new LineData(xValues, lineDataSets);


        //================UI设置==============================


        barChart.setDrawBorders(false); // //是否在折线图上添加边框

        barChart.setDescription("");// 数据描述
        // 如果没有数据的时候，会显示这个，类似ListView的EmptyView
        barChart.setNoDataTextDescription("");
        barChart.setDrawGridBackground(true); // 是否显示表格颜色
        barChart.setGridBackgroundColor(Color.TRANSPARENT); // 设置网格部分的颜色，默认的是白色
//        barChart.setGridBackgroundColor(Color.parseColor("#bedcfe")); // 设置网格部分的颜色，默认的是白色
        // 表格的的颜色，在这里是是给颜色设置一个透明度

        barChart.setTouchEnabled(false); // 设置是否可以触摸
        barChart.setDragEnabled(true);// 是否可以拖拽
        barChart.setScaleEnabled(false);// 是否可以缩放
        barChart.setScaleYEnabled(false);// 设置Y轴不可缩放
        barChart.setDoubleTapToZoomEnabled(false);// 设置双击屏幕不可缩放

        barChart.setPinchZoom(false);// X,Y轴能否同时缩放
        barChart.setBackgroundColor(Color.TRANSPARENT);// 设置背景
//        barChart.setDrawBarShadow(false); // 每一根柱状图的背景是否有阴影
        barChart.zoom(1.0f, 1.0f, 1.0f, 1.0f);// 这个可以设置X,Y轴的缩放比例（第一个参数：X轴的缩放比例，默认1.0f,第二个参数是Y轴的，第三个是X轴坐标比例,默认也是1.0f，第四个参数是Y轴的坐标比例）
        barChart.setData(barData); // 设置数据
//        barChart.setData(getLineData(10,10)); // 设置数据


        // X轴设定
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawLabels(true);
//        xAxis.setAxisLineColor(Color.TRANSPARENT);
        xAxis.setAxisLineColor(Color.TRANSPARENT);
        xAxis.setAxisLineWidth(1.5f);
        xAxis.setDrawGridLines(false);//是否显示X轴网格
//        xAxis.setTextSize(0f);//X轴字体大小

        xAxis.setLabelsToSkip(11);    //设置坐标相隔多少，参数是int类型
//        xAxis.resetLabelsToSkip();   //将自动计算坐标相隔多少
//        xAxis.setAvoidFirstLastClipping(true);
//        xAxis.setSpaceBetweenLabels(4);


        Legend mLegend = barChart.getLegend(); // 设置比例图标示
        mLegend.setForm(Legend.LegendForm.SQUARE);// 样式
        mLegend.setFormSize(10f);// 字体
        mLegend.setTextColor(Color.parseColor("#ff0000"));// 颜色
        mLegend.setMaxSizePercent(0.95f);   //坐标线描述 占据的大小x%  默认0.95 即95%
        mLegend.setEnabled(false);


        //点击显示的View
        PoHeartMarkerView markerView = new PoHeartMarkerView(context,
                R.layout.po_heart_markview_pop);
        barChart.setMarkerView(markerView);
        // Y轴的设定

        barChart.getAxisLeft().setAxisLineColor(Color.TRANSPARENT);//Y轴颜色
        barChart.getAxisLeft().setTextColor(Color.TRANSPARENT);
        barChart.getAxisLeft().setGridColor(Color.TRANSPARENT);//Y轴网格颜色
        barChart.getAxisLeft().setAxisLineWidth(1.5f);
        barChart.getAxisLeft().setDrawAxisLine(true);
        barChart.getAxisLeft().setDrawGridLines(false);//是否显示Y轴网格
        barChart.getAxisLeft().setEnabled(true);//是否显示Y轴
//      barChart.getAxisLeft().setAxisMaxValue(200);//设置Y周最大值
//      barChart.getAxisLeft().setAxisMinValue(20);//设置Y周最小值
        barChart.getAxisLeft().setSpaceTop(35f);//和顶端的距离，百分比
//        barChart.getAxisLeft().setSpaceBottom(50f);//和顶端的距离，百分比，没效果
        barChart.getAxisLeft().setStartAtZero(true);//显示零相关的,设置成flase ，地下会出来空隙
//        barChart.getAxisLeft().setShowOnlyMinMax(true);//是否显示最大最小值
        barChart.getAxisLeft().setLabelCount(5, false);//第一个参数是Y轴坐标的个数，第二个参数是 是否不均匀分布，true是不均匀分布
        barChart.getAxisRight().setDrawGridLines(false);
        barChart.getAxisRight().setEnabled(true);
        barChart.getAxisRight().setAxisLineColor(Color.TRANSPARENT);//Y轴颜色
        barChart.getAxisRight().setTextColor(Color.TRANSPARENT);

//        barChart.animateX(2000);//动画从左往右
//        barChart.animateY(1500);//动画下往上
//        barChart.animateXY(100,1000);// 两个轴动画，从左到右，从下到上

    }

    public static List<String> getNewtData(List<String> old_data) {

        List<String> new_data = new ArrayList<>();

        if (old_data.size() < 7) {
            for (int i = 0; i < (7 - old_data.size()); i++) {
                new_data.add("0");
                new_data.add("0");
            }
        }
        for (int i = 0; i < old_data.size(); i++) {

            new_data.add("0");
            new_data.add(old_data.get(i));

            if (i == old_data.size() - 1) {
                new_data.add("0");
            }
        }

        return new_data;
    }


    /**
     * 天数据页，连续血氧
     *
     * @param context
     * @param barChart
     * @param wo_heart
     */
    public static void showDayContinuitySpo2BarChart(Context context, LineChart barChart, String[] wo_heart, boolean is_touch) {

//
        final int Color0 = Color.TRANSPARENT;
        final int Color1 = Color.parseColor("#1ed3ce");
//        final int Color2 = Color.parseColor("#fbe601");
//        final int Color3 = Color.parseColor("#fcb600");
//        final int Color4 = Color.parseColor("#f06c07");
//        final int Color5 = Color.parseColor("#df1314");
        int[] MyColor = {Color0, Color1};

        //X轴网格颜色
        final int XZhou = Color.TRANSPARENT;
        //Y轴网格颜色
        final int YGridColor = Color.parseColor("#3e99ff");


        //====加载数据

        int count = wo_heart.length;

        ArrayList<String> xValues = new ArrayList<String>();

        for (int i = 0; i < count; i++) {
            // x轴显示的数据，这里默认使用数字下标显示
            if (i == 0) {
                xValues.add("");
            } else {
                xValues.add("");
            }

        }

        // y轴的数据
//        String[] steps24 = my_heartList;
        ArrayList<Entry> yValues = new ArrayList<Entry>();
        MyColor = new int[count];
        for (int i = 0; i < count; i++) {
            float value = Float.parseFloat(wo_heart[i]);
            yValues.add(new BarEntry(value, i));
            float value_one = -1;

            if (i < (count - 2)) {
                value_one = Float.parseFloat(wo_heart[i + 1]);
            }

            if (value_one != -1 && value_one == 0) {
                MyColor[i] = Color0;
            } else {
                if (value <= 0) {
                    MyColor[i] = Color0;
                } else {
                    MyColor[i] = Color1;
                }
            }
        }

        // create a dataset and give it a type
        // y轴的数据集合
        LineDataSet lineDataSet = new LineDataSet(yValues, "" /*显示在比例图上*/);

        //用y轴的集合来设置参数
        lineDataSet.setLineWidth(1.5f); // 线宽
//        lineDataSet.setCircleSize(1.1f);// 显示的圆形大小 0f 表示没有
        lineDataSet.setDrawCircleHole(false);//是否空心
        lineDataSet.setCircleColor(Color0);// 圆形的颜色
        lineDataSet.setColor(Color0);// 线条颜色颜色
        lineDataSet.setDrawValues(false);//是否显示数字
        lineDataSet.setHighLightColor(Color.parseColor("#77000000")); // 高亮的线的颜色
        lineDataSet.setHighlightLineWidth(1f);
        lineDataSet.setDrawVerticalHighlightIndicator(true);
        lineDataSet.setDrawHorizontalHighlightIndicator(true);
//        lineDataSet.setCircleColorHole(Color.parseColor("#ff0000"));
        lineDataSet.setDrawHighlightIndicators(true);//是否高亮
        lineDataSet.setHighlightEnabled(is_touch);//是否可以触摸
//        lineDataSet.setDrawCubic(true);//是否显示曲线
//        lineDataSet.setCubicIntensity(0.05f);//曲线率
//        lineDataSet.setFillColor(XZhou);//填充颜色
//        lineDataSet.setDrawFilled(true);//是否填充
//        lineDataSet.setFillAlpha(76);//透明度百分之30

        lineDataSet.setColors(MyColor);//设置线的颜色数组（不能设置曲线属性）
//        lineDataSet.setColor(Color1);//设置线的颜色数组（不能设置曲线属性）
//        lineDataSet.setCircleColors(MyColor);//设置圈的颜色数组
        ArrayList<LineDataSet> lineDataSets = new ArrayList<LineDataSet>();
        lineDataSets.add(lineDataSet); // add the datasets

        // create a data object with the datasets
        LineData barData = new LineData(xValues, lineDataSets);


        //=====加载UI


        barChart.setDrawBorders(false); // //是否在折线图上添加边框

        barChart.setDescription("");// 数据描述
        // 如果没有数据的时候，会显示这个，类似ListView的EmptyView
        barChart.setNoDataTextDescription("");
        barChart.setDrawGridBackground(true); // 是否显示表格颜色
        barChart.setGridBackgroundColor(Color.TRANSPARENT); // 设置网格部分的颜色，默认的是白色
//        barChart.setGridBackgroundColor(Color.parseColor("#bedcfe")); // 设置网格部分的颜色，默认的是白色
        // 表格的的颜色，在这里是是给颜色设置一个透明度

        barChart.setTouchEnabled(is_touch); // 设置是否可以触摸
        barChart.setDragEnabled(true);// 是否可以拖拽
        barChart.setScaleEnabled(false);// 是否可以缩放
        barChart.setScaleYEnabled(false);// 设置Y轴不可缩放
        barChart.setDoubleTapToZoomEnabled(false);// 设置双击屏幕不可缩放

        barChart.setPinchZoom(false);// X,Y轴能否同时缩放
        barChart.setBackgroundColor(Color.TRANSPARENT);// 设置背景
//        barChart.setDrawBarShadow(false); // 每一根柱状图的背景是否有阴影
        barChart.zoom(1.0f, 1.0f, 1.0f, 1.0f);// 这个可以设置X,Y轴的缩放比例（第一个参数：X轴的缩放比例，默认1.0f,第二个参数是Y轴的，第三个是X轴坐标比例,默认也是1.0f，第四个参数是Y轴的坐标比例）
        barChart.setData(barData); // 设置数据


        // X轴设定
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawLabels(true);
//        xAxis.setAxisLineColor(Color.TRANSPARENT);
        xAxis.setAxisLineColor(Color0);
        xAxis.setAxisLineWidth(1f);
        xAxis.setDrawGridLines(false);//是否显示X轴网格
//        xAxis.setTextSize(0f);//X轴字体大小

        xAxis.setLabelsToSkip(11);    //设置坐标相隔多少，参数是int类型
//        xAxis.resetLabelsToSkip();   //将自动计算坐标相隔多少
//        xAxis.setAvoidFirstLastClipping(true);
//        xAxis.setSpaceBetweenLabels(4);

        Legend mLegend = barChart.getLegend(); // 设置比例图标示
        mLegend.setForm(Legend.LegendForm.SQUARE);// 样式
        mLegend.setFormSize(10f);// 字体
        mLegend.setTextColor(Color.parseColor("#ff0000"));// 颜色
        mLegend.setMaxSizePercent(0.95f);   //坐标线描述 占据的大小x%  默认0.95 即95%
        mLegend.setEnabled(false);

//
        //点击显示的View
        HeartMarkerView markerView = new HeartMarkerView(context,
                R.layout.heart_markview_pop3);
        barChart.setMarkerView(markerView);
        // Y轴的设定


        barChart.getAxisLeft().setAxisLineColor(Color0);//Y轴颜色
        barChart.getAxisLeft().setGridColor(YGridColor);//Y轴网格颜色
        barChart.getAxisLeft().setAxisLineWidth(1.5f);
        barChart.getAxisLeft().setDrawAxisLine(true);
        barChart.getAxisLeft().setDrawGridLines(true);//是否显示Y轴网格
        barChart.getAxisLeft().setGridLineWidth(1f);
        barChart.getAxisLeft().setEnabled(true);//是否显示Y轴
//      barChart.getAxisLeft().setAxisMaxValue(200);//设置Y周最大值
        barChart.getAxisLeft().setAxisMinValue(20);//设置Y周最小值
        barChart.getAxisLeft().setSpaceTop(40f);//和顶端的距离，百分比
        barChart.getAxisLeft().setSpaceBottom(20f);//和顶端的距离，百分比，没效果
        barChart.getAxisLeft().setStartAtZero(false);//显示零相关的,设置成flase ，隐藏0
//      barChart.getAxisLeft().setShowOnlyMinMax(true);//是否显示最大最小值
        barChart.getAxisLeft().setLabelCount(3, false);//第一个参数是Y轴坐标的个数，第二个参数是 是否不均匀分布，true是不均匀分布
        barChart.getAxisRight().setDrawGridLines(false);//是否显示Y轴网格
        barChart.getAxisRight().setEnabled(true);
        barChart.getAxisRight().setAxisLineColor(Color0);//Y轴颜色
        barChart.getAxisRight().setTextColor(Color0);


//      barChart.animateX(2000);//动画从左往右
//      barChart.animateY(1500);//动画下往上
//      barChart.animateXY(100,1000);// 两个轴动画，从左到右，从下到上

    }


    /**
     * 天数据页，连续体温
     *
     * @param context
     * @param barChart
     * @param wo_heart
     */
    public static void showDayContinuityTempBarChart(Context context, LineChart barChart, String[] wo_heart, boolean is_touch) {

//
        final int Color0 = Color.TRANSPARENT;
        final int Color1 = Color.parseColor("#1ed3ce");
//        final int Color2 = Color.parseColor("#fbe601");
//        final int Color3 = Color.parseColor("#fcb600");
//        final int Color4 = Color.parseColor("#f06c07");
//        final int Color5 = Color.parseColor("#df1314");
        int[] MyColor = {Color0, Color1};

        //X轴网格颜色
        final int XZhou = Color.TRANSPARENT;
        //Y轴网格颜色
        final int YGridColor = Color.parseColor("#3e99ff");


        //====加载数据

        int count = wo_heart.length;

        ArrayList<String> xValues = new ArrayList<String>();

        for (int i = 0; i < count; i++) {
            // x轴显示的数据，这里默认使用数字下标显示
            if (i == 0) {
                xValues.add("");
            } else {
                xValues.add("");
            }

        }

        // y轴的数据
//        String[] steps24 = my_heartList;
        ArrayList<Entry> yValues = new ArrayList<Entry>();
        MyColor = new int[count];
        for (int i = 0; i < count; i++) {
            float value = Float.parseFloat(wo_heart[i]);

            yValues.add(new BarEntry(value, i));
            float value_one = -1;

            if (i < (count - 2)) {
                value_one = Float.parseFloat(wo_heart[i + 1]);
            }

            if (value_one != -1 && value_one == 0) {
                MyColor[i] = Color0;
            } else {
                if (value <= 0) {
                    MyColor[i] = Color0;
                } else {
                    MyColor[i] = Color1;
                }
            }
        }

        // create a dataset and give it a type
        // y轴的数据集合
        LineDataSet lineDataSet = new LineDataSet(yValues, "" /*显示在比例图上*/);

        //用y轴的集合来设置参数
        lineDataSet.setLineWidth(1.5f); // 线宽
//        lineDataSet.setCircleSize(1.1f);// 显示的圆形大小 0f 表示没有
        lineDataSet.setDrawCircleHole(false);//是否空心
        lineDataSet.setCircleColor(Color0);// 圆形的颜色
        lineDataSet.setColor(Color0);// 线条颜色颜色
        lineDataSet.setDrawValues(false);//是否显示数字
        lineDataSet.setHighLightColor(Color.parseColor("#77000000")); // 高亮的线的颜色
        lineDataSet.setHighlightLineWidth(1f);
        lineDataSet.setDrawVerticalHighlightIndicator(true);
        lineDataSet.setDrawHorizontalHighlightIndicator(true);
//        lineDataSet.setCircleColorHole(Color.parseColor("#ff0000"));
        lineDataSet.setDrawHighlightIndicators(true);//是否高亮
        lineDataSet.setHighlightEnabled(is_touch);//是否可以触摸
//        lineDataSet.setDrawCubic(true);//是否显示曲线
//        lineDataSet.setCubicIntensity(0.05f);//曲线率
//        lineDataSet.setFillColor(XZhou);//填充颜色
//        lineDataSet.setDrawFilled(true);//是否填充
//        lineDataSet.setFillAlpha(76);//透明度百分之30

        lineDataSet.setColors(MyColor);//设置线的颜色数组（不能设置曲线属性）
//        lineDataSet.setColor(Color1);//设置线的颜色数组（不能设置曲线属性）
//        lineDataSet.setCircleColors(MyColor);//设置圈的颜色数组
        ArrayList<LineDataSet> lineDataSets = new ArrayList<LineDataSet>();
        lineDataSets.add(lineDataSet); // add the datasets

        // create a data object with the datasets
        LineData barData = new LineData(xValues, lineDataSets);


        //=====加载UI


        barChart.setDrawBorders(false); // //是否在折线图上添加边框

        barChart.setDescription("");// 数据描述
        // 如果没有数据的时候，会显示这个，类似ListView的EmptyView
        barChart.setNoDataTextDescription("");
        barChart.setDrawGridBackground(true); // 是否显示表格颜色
        barChart.setGridBackgroundColor(Color.TRANSPARENT); // 设置网格部分的颜色，默认的是白色
//        barChart.setGridBackgroundColor(Color.parseColor("#bedcfe")); // 设置网格部分的颜色，默认的是白色
        // 表格的的颜色，在这里是是给颜色设置一个透明度

        barChart.setTouchEnabled(is_touch); // 设置是否可以触摸
        barChart.setDragEnabled(true);// 是否可以拖拽
        barChart.setScaleEnabled(false);// 是否可以缩放
        barChart.setScaleYEnabled(false);// 设置Y轴不可缩放
        barChart.setDoubleTapToZoomEnabled(false);// 设置双击屏幕不可缩放

        barChart.setPinchZoom(false);// X,Y轴能否同时缩放
        barChart.setBackgroundColor(Color.TRANSPARENT);// 设置背景
//        barChart.setDrawBarShadow(false); // 每一根柱状图的背景是否有阴影
        barChart.zoom(1.0f, 1.0f, 1.0f, 1.0f);// 这个可以设置X,Y轴的缩放比例（第一个参数：X轴的缩放比例，默认1.0f,第二个参数是Y轴的，第三个是X轴坐标比例,默认也是1.0f，第四个参数是Y轴的坐标比例）
        barChart.setData(barData); // 设置数据


        // X轴设定
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawLabels(true);
//        xAxis.setAxisLineColor(Color.TRANSPARENT);
        xAxis.setAxisLineColor(Color0);
        xAxis.setAxisLineWidth(1f);
        xAxis.setDrawGridLines(false);//是否显示X轴网格
//        xAxis.setTextSize(0f);//X轴字体大小

        xAxis.setLabelsToSkip(11);    //设置坐标相隔多少，参数是int类型
//        xAxis.resetLabelsToSkip();   //将自动计算坐标相隔多少
//        xAxis.setAvoidFirstLastClipping(true);
//        xAxis.setSpaceBetweenLabels(4);

        Legend mLegend = barChart.getLegend(); // 设置比例图标示
        mLegend.setForm(Legend.LegendForm.SQUARE);// 样式
        mLegend.setFormSize(10f);// 字体
        mLegend.setTextColor(Color.parseColor("#ff0000"));// 颜色
        mLegend.setMaxSizePercent(0.95f);   //坐标线描述 占据的大小x%  默认0.95 即95%
        mLegend.setEnabled(false);

//
        //点击显示的View
        TempMarkerView markerView = new TempMarkerView(context, R.layout.heart_markview_pop3);
        barChart.setMarkerView(markerView);
        // Y轴的设定


        barChart.getAxisLeft().setAxisLineColor(Color0);//Y轴颜色
        barChart.getAxisLeft().setGridColor(YGridColor);//Y轴网格颜色
        barChart.getAxisLeft().setAxisLineWidth(1.5f);
        barChart.getAxisLeft().setDrawAxisLine(true);
        barChart.getAxisLeft().setDrawGridLines(true);//是否显示Y轴网格
        barChart.getAxisLeft().setGridLineWidth(1f);
        barChart.getAxisLeft().setEnabled(true);//是否显示Y轴
//      barChart.getAxisLeft().setAxisMaxValue(200);//设置Y周最大值
        barChart.getAxisLeft().setAxisMinValue(20);//设置Y周最小值
        barChart.getAxisLeft().setSpaceTop(40f);//和顶端的距离，百分比
        barChart.getAxisLeft().setSpaceBottom(20f);//和顶端的距离，百分比，没效果
        barChart.getAxisLeft().setStartAtZero(false);//显示零相关的,设置成flase ，隐藏0
//      barChart.getAxisLeft().setShowOnlyMinMax(true);//是否显示最大最小值
        barChart.getAxisLeft().setLabelCount(3, false);//第一个参数是Y轴坐标的个数，第二个参数是 是否不均匀分布，true是不均匀分布
        barChart.getAxisRight().setDrawGridLines(false);//是否显示Y轴网格
        barChart.getAxisRight().setEnabled(true);
        barChart.getAxisRight().setAxisLineColor(Color0);//Y轴颜色
        barChart.getAxisRight().setTextColor(Color0);


//      barChart.animateX(2000);//动画从左往右
//      barChart.animateY(1500);//动画下往上
//      barChart.animateXY(100,1000);// 两个轴动画，从左到右，从下到上

    }

}
