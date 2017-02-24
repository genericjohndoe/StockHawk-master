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
 * Created by joeljohnson on 2/22/17.
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

        LineDataSet dataSet = new LineDataSet(entries, "Stock Price");
        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(this);
        xAxis.setLabelRotationAngle(45f);
        chart.invalidate();

    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis((long) value);
        return new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
    }
}
