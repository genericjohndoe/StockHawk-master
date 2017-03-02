package com.sam_chordas.android.stockhawk.service;

import android.app.IntentService;
import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;

import com.google.android.gms.gcm.TaskParams;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;

/**
 * Created by sam_chordas on 10/1/15.
 */
/*IntentService is a base class for {@link Service}s that handle asynchronous
        * requests (expressed as {@link Intent}s) on demand.  Clients send requests
        * through {@link android.content.Context#startService(Intent)} calls; the
        * service is started as needed, handles each Intent in turn using a worker
        * thread, and stops itself when it runs out of work.*/
public class StockIntentService extends IntentService {

    public StockIntentService() {
        super(StockIntentService.class.getName());
    }

    public StockIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        StockTaskService stockTaskService = new StockTaskService(this);
        Bundle args = new Bundle();
        if (intent.getStringExtra(getString(R.string.tag)).equals(getString(R.string.add))) {
            args.putString(QuoteColumns.SYMBOL, intent.getStringExtra(QuoteColumns.SYMBOL));
        }
        // We can call OnRunTask from the intent service to force it to run immediately instead of
        // scheduling a task.
        try {
            stockTaskService.onRunTask(new TaskParams(intent.getStringExtra(getString(R.string.tag)), args));
        } catch (SQLiteConstraintException e) {
            e.printStackTrace();
        }
    }
}
