package cn.georgeyang.temperaturesafe;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.os.Build;
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
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import cn.georgeyang.database.Mdb;
import cn.georgeyang.temperaturesafe.entity.ChartResultVo;
import cn.georgeyang.temperaturesafe.entity.RecorderEntity;
import cn.georgeyang.temperaturesafe.entity.SettingEntity;
import cn.georgeyang.temperaturesafe.entity.TemperatureDataEntity;
import cn.georgeyang.temperaturesafe.utils.AppUtil;
import cn.georgeyang.temperaturesafe.utils.TitleUtil;
import cn.georgeyang.temperaturesafe.widget.AppPickerDialog;

/**
 * Created by george.yang on 16/9/7.
 */
public class HistoryActivity extends AppCompatActivity implements View.OnClickListener {

    private LineChart mChart;
    private TextView tvChoice,tvName;
    private Spinner mSpinner;
    private ArrayAdapter adapter;
//    private static final String[] datas = new String[]{"今天","昨天","前天","三天前","四天前","五天前","六天前","七天前","上一周"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mdb.init(this);

        setContentView(R.layout.activity_datahistory);
        TitleUtil.init(this).setTitle("历史记录").autoBack();
        mChart = (LineChart) findViewById(R.id.chart);
        tvChoice = (TextView) findViewById(R.id.tvChoice);
        tvChoice.setOnClickListener(this);
        tvName = (TextView) findViewById(R.id.tvName);
        tvName.setOnClickListener(this);


        Calendar calendar = Calendar.getInstance();
        selectYear = calendar.get(Calendar.YEAR);
        selectMonth = calendar.get(Calendar.MONTH);
        selectDay = calendar.get(Calendar.DAY_OF_MONTH);

        recorderList = Mdb.getInstance().findAll(RecorderEntity.class);
        SettingEntity settingEntity = AppUtil.getSettingEntity(this);
        selectRecorder = Mdb.getInstance().findOnebyWhere(RecorderEntity.class,"_id=" + settingEntity.recorderId);
        if (selectRecorder==null) {
            selectRecorder = new RecorderEntity("默认");
        }

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
        mChart.setBackgroundColor(Color.WHITE);

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
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
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



    private void showData(boolean isFirst) {
        if (selectRecorder == null) {
            Toast.makeText(this,"请选择成员!",Toast.LENGTH_SHORT).show();
            return;
        }
        tvChoice.setText(String.format("%s-%s-%s",new Object[]{selectYear+"",selectMonth+"",selectDay+""}));
        tvName.setText(selectRecorder.name);

        //db.rawQuery("SELECT * FROM myTable WHERE myColumn IS NULL", null);
        Mdb.getInstance().getdb().execSQL("UPDATE TemperatureDataEntity SET recordId=? WHERE recordId IS NULL",new Object[]{selectRecorder._id+""});
        dataList = AppUtil.getDataListByDate(selectYear,selectMonth,selectDay,selectRecorder._id);
        dataList = AppUtil.getChartListByInterval(dataList,Vars.ShowInterval);
        ChartResultVo result = AppUtil.buildChartList(dataList,selectYear,selectMonth,selectDay);

        if (result ==null || result.resultList==null || result.resultList.size()<=0) {
            Toast.makeText(this,"暂无数据",Toast.LENGTH_SHORT).show();
            return;
        }

//        setChatSize(isFirst,result.minX - Vars.EmptyMin,result.maxX + Vars.EmptyMin,result.minY-EmptyY,result.maxY+EmptyY);
        setChatSize(isFirst,result.minX - Vars.EmptyMin,result.maxX + Vars.EmptyMin,Vars.MinShowTemperature,Vars.MaxShowTemperature);


        LineDataSet set1;

        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet)mChart.getData().getDataSetByIndex(0);
            set1.setValues(result.resultList);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            set1 = new LineDataSet(result.resultList, "温度-时间表");

//            set1.enableDashedLine(10f, 5f, 0f);
//            set1.enableDashedHighlightLine(10f, 5f, 0f);
//            set1.setColor(Color.BLACK);
//            set1.setCircleColor(Color.BLACK);
//            set1.setLineWidth(1f);
//            set1.setCircleRadius(3f);
//            set1.setDrawCircleHole(false);
//            set1.setValueTextSize(9f);
//            set1.setDrawFilled(true);
//            set1.setFormLineWidth(1f);
//            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
//            set1.setFormSize(15.f);
            set1.setAxisDependency(YAxis.AxisDependency.LEFT);
            set1.setColor(ColorTemplate.getHoloBlue());
            set1.setCircleColor(Color.BLACK);
            set1.setLineWidth(2f);
            set1.setCircleRadius(3f);
            set1.setFillAlpha(65);
            set1.setFillColor(Color.rgb(51, 181, 229));
            set1.setHighLightColor(Color.rgb(244, 117, 117));
            set1.setDrawCircleHole(false);


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

//             set data
            mChart.setData(data);
        }
    }


    private List<RecorderEntity> recorderList;
    private String[] recodeNames;
    private int selectYear,selectMonth,selectDay;
    private RecorderEntity selectRecorder;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvChoice:
                AppPickerDialog datePicker = new AppPickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        selectYear = year;
                        selectMonth = monthOfYear;
                        selectDay = dayOfMonth;
                        showData(false);
                    }
                },selectYear,selectMonth,selectDay);
                datePicker.show();
                break;
            case R.id.tvName:
                if (recodeNames==null) {
                    List<String> recodeList = new ArrayList<>();
                    for (RecorderEntity entity:recorderList) {
                        recodeList.add(entity.name);
                    }
                    recodeNames = recodeList.toArray(new String[recodeList.size()]);
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("请选择成员");
                builder.setItems(recodeNames, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectRecorder = recorderList.get(which);
                        showData(false);
                    }
                });
               builder.show();
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
