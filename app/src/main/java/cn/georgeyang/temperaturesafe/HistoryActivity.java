package cn.georgeyang.temperaturesafe;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import cn.georgeyang.temperaturesafe.entity.TemperatureDataEntity;
import cn.georgeyang.temperaturesafe.utils.AppUtil;
import cn.georgeyang.temperaturesafe.utils.TitleUtil;
import cn.georgeyang.temperaturesafe.widget.AppPickerDialog;

/**
 * Created by george.yang on 16/9/7.
 */
public class HistoryActivity extends AppCompatActivity implements View.OnClickListener {

    private LineChart mChart;
    private TextView tvChoice;
    private Spinner mSpinner;
    private ArrayAdapter adapter;
//    private static final String[] datas = new String[]{"今天","昨天","前天","三天前","四天前","五天前","六天前","七天前","上一周"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_datahistory);
        TitleUtil.init(this).setTitle("历史记录").autoBack();
        mChart = (LineChart) findViewById(R.id.chart);
        tvChoice = (TextView) findViewById(R.id.tvChoice);
        tvChoice.setOnClickListener(this);
//        mSpinner = (Spinner) findViewById(R.id.spinner);

//        //将可选内容与ArrayAdapter连接起来
//        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,datas);
//        //设置下拉列表的风格
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//
//        //将adapter 添加到spinner中
//        mSpinner.setAdapter(adapter);
//
//        //添加事件Spinner事件监听
//        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                initData();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

        Calendar calendar = Calendar.getInstance();
        selectYesar = calendar.get(Calendar.YEAR);
        selectMonth = calendar.get(Calendar.MONTH);
        selectDay = calendar.get(Calendar.DAY_OF_MONTH);
        initChart();
        showData(true);
    }

    private List<TemperatureDataEntity> dataList;

    private void initChart() {
//        mChart.setOnChartGestureListener(this);
//        mChart.setOnChartValueSelectedListener(this);
        mChart.setDrawGridBackground(false);

        // no description text
        mChart.setDescription("");
        mChart.setDescriptionColor(R.color.mine_red);
        mChart.setNoDataTextDescription("暂无温度数据");

        mChart.setDragDecelerationFrictionCoef(0.9f);


        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
//        mChart.setScaleEnabled(true);
//         mChart.setScaleXEnabled(true);
         mChart.setScaleYEnabled(false);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(false);

        // set an alternative background color
        mChart.setBackgroundColor(Color.LTGRAY);


        // set an alternative background color
        // mChart.setBackgroundColor(Color.GRAY);

        // create a custom MarkerView (extend MarkerView) and specify the layout
        // to use for it
//        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);
//        mv.setChartView(mChart); // For bounds control
//        mChart.setMarker(mv); // Set the marker to the chart

//        LimitLine llXAxis = new LimitLine(10f, "Index 10");
//        llXAxis.setLineWidth(4f);
//        llXAxis.enableDashedLine(10f, 10f, 0f);
//        llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
//        llXAxis.setTextSize(10f);

//        XAxis xAxis = mChart.getXAxis();
//        xAxis.enableGridDashedLine(10f, 10f, 0f);
//        xAxis.setValueFormatter(new TimeAxisValueFormatter(System.currentTimeMillis()));
//        xAxis.setAxisMaximum(100);//X结束位置
//        xAxis.setAxisMinimum(20);//y结束位置


//        LimitLine ll1 = new LimitLine(150f, "Upper Limit");
//        ll1.setLineWidth(4f);
//        ll1.enableDashedLine(10f, 10f, 0f);
//        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
//        ll1.setTextSize(10f);

//        YAxis leftAxis = mChart.getAxisLeft();
//        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
//        leftAxis.setAxisMaximum(50f);
//        leftAxis.setAxisMinimum(0f);
//        leftAxis.enableGridDashedLine(5f, 10f, 0f);
//        leftAxis.setDrawZeroLine(false);

        // limit lines are drawn behind data (and not on top)
//        leftAxis.setDrawLimitLinesBehindData(true);

        mChart.getAxisRight().setEnabled(false);
        mChart.setVisibleXRangeMinimum(3);//最少显示3个点

    }

    private void setChatSize(boolean isFirst,float startX,float endX,float startY,float endY) {
        XAxis xAxis = mChart.getXAxis();
        xAxis.enableGridDashedLine(10f, 10f, 0f);
        xAxis.setValueFormatter(new TimeAxisValueFormatter());
        xAxis.setAxisMinimum(startX);//y结束位置
        xAxis.setAxisMaximum(endX);//X结束位置

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.setAxisMinimum(startY);
        leftAxis.setAxisMaximum(endY);
        leftAxis.enableGridDashedLine(5f, 10f, 0f);
        leftAxis.setDrawZeroLine(false);

        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true);

        if (!isFirst) {
            LineData lineData = mChart.getData();
            if (lineData!=null) {
                lineData.notifyDataChanged();
            }
            mChart.notifyDataSetChanged();
            mChart.postInvalidate();
        }
    }


    private void showData(boolean isFrist) {
        dataList = AppUtil.getDataListByDate(selectYesar,selectMonth,selectDay);
        if (dataList==null || dataList.size()<=1) {
            Toast.makeText(this,"暂无数据",Toast.LENGTH_SHORT).show();
            return;
        }
        tvChoice.setText(String.format("%s-%s-%s",new Object[]{selectYesar+"",selectMonth+"",selectDay+""}));
        float minX = Integer.MAX_VALUE,maxX = Integer.MIN_VALUE,minY = Integer.MAX_VALUE,maxY=Integer.MIN_VALUE;
        ArrayList<Entry> values = new ArrayList<Entry>();
        //15分钟为一个单位
        Calendar todayZeroTime = Calendar.getInstance();
        todayZeroTime.set(Calendar.HOUR_OF_DAY,0);
        todayZeroTime.set(Calendar.MINUTE,0);
        todayZeroTime.set(Calendar.SECOND,0);
        long zeroTime = todayZeroTime.getTimeInMillis();
        for (TemperatureDataEntity entity:dataList) {
            //今天运行了多少毫秒
            long todayMillis =  entity._addTime - zeroTime;
            if (todayMillis<=0 || todayMillis>=1000*60*60*24) {
                continue;
            }
            //这个时间运行了多少分钟
            long todayMINUTE = todayMillis / (1000*60);
            if (todayMINUTE%15==0) {
                if (entity.temperature < minY) {
                    minY = entity.temperature;
                }
                if (entity.temperature> maxY) {
                    maxY = entity.temperature;
                }

                //刚好15分钟
                if (todayMINUTE < minX) {
                    minX = todayMINUTE;
                }
                if (todayMINUTE > maxX) {
                    maxX = todayMINUTE;
                }
                Entry entry = new Entry(todayMINUTE,entity.temperature);
                values.add(entry);
            }
        }

        //pass
//        Random random = new Random();
//        int removeIndex = random.nextInt(values.size()/3);
//        for (int i = 0;i<5;i++) {
//            try {
//                Log.d("test","remove:" + removeIndex);
//
//                values.remove(removeIndex);
//            } catch (Exception e) {
//
//            }
//        }



//        int range = 40;
//        int count = 100;
//        ArrayList<Entry> values = new ArrayList<Entry>();
//        for (int i = 0; i < count; i++) {
//            float val = (float) (Math.random() * range) + 3;
//            values.add(new Entry(i, val));
//        }

//        minX = minX / 1000 * 60 * 60;//5分钟为一个单位
//        maxX = maxX / 1000 * 60 * 60;
        setChatSize(isFrist,minX,maxX,minY-10,maxY+10);

        LineDataSet set1;

        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet)mChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            set1 = new LineDataSet(values, "温度-时间表");

            // set the line to be drawn like this "- - - - - -"
            set1.enableDashedLine(10f, 5f, 0f);
            set1.enableDashedHighlightLine(10f, 5f, 0f);
            set1.setColor(Color.BLACK);
            set1.setCircleColor(Color.BLACK);
            set1.setLineWidth(1f);
            set1.setCircleRadius(3f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(9f);
            set1.setDrawFilled(true);
            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);

            if (Utils.getSDKInt() >= 18) {
                // fill drawable only supported on api level 18 and above
                Drawable drawable = ContextCompat.getDrawable(this, R.color.collect_red);
                set1.setFillDrawable(drawable);
            }
            else {
                set1.setFillColor(Color.BLACK);
            }

            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(set1); // add the datasets

            // create a data object with the datasets
            LineData data = new LineData(dataSets);

            // set data
            mChart.setData(data);
        }

    }

    private int selectYesar,selectMonth,selectDay;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvChoice:
                AppPickerDialog datePicker = new AppPickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        selectYesar = year;
                        selectMonth = monthOfYear;
                        selectDay = dayOfMonth;
                        showData(false);
                    }
                },selectYesar,selectMonth,selectDay);
                datePicker.show();
                break;
        }
    }


//    @Override
//    public void onResume() {
//        //Draws the chart with latest data (in case of updates)
//        drawChart(chart, lineData, secondLineData);
//        super.onResume();
//    }
//
//    @Override
//    public void onPause() {
//        // Enforcing to reload the data in onResume()
//        lineData = null;
//        secondLineData = null;
//
//        // chart = null;
//        super.onPause();
//    }
//
//    @Override
//    public void onDestroyView() {
//        if (chart != null) chart = null;
//        super.onDestroyView();
//    }
}
