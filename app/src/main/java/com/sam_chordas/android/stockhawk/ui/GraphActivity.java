package com.sam_chordas.android.stockhawk.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.EntryXComparator;
import com.sam_chordas.android.stockhawk.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;


/**
 * Shows historical data of stock
 */

public class GraphActivity  extends AppCompatActivity implements IAxisValueFormatter {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_graph);

        LineChart chart = (LineChart) findViewById(R.id.chart);

        List<Entry> entries = new ArrayList<Entry>();
        for (StockData data : MyStocksActivity.StockData) {
            entries.add(new Entry(data.date, (float) data.price));
        }
        Collections.sort(entries, new EntryXComparator());

        LineDataSet dataSet = new LineDataSet(entries, getString(R.string.stock_price));
        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(this);
        xAxis.setLabelRotationAngle(45f);
        chart.invalidate();

    }

    /**
     * formats dates shown on graph axis
     * @param value The time since the epoch in milliseconds
     * @param axis the axis being formatted
     * @return a String to be shown in the historical data graph, along the axis
     */
    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis((long) value);
        return new SimpleDateFormat(getString(R.string.date_format)).format(cal.getTime());
    }
}
