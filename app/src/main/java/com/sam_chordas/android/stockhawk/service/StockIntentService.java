package com.sam_chordas.android.stockhawk.service;

import android.app.IntentService;
import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.TaskParams;

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
        Log.d(StockIntentService.class.getSimpleName(), "Stock Intent Service");
        StockTaskService stockTaskService = new StockTaskService(this);
        Bundle args = new Bundle();
        if (intent.getStringExtra("tag").equals("add")) {
            args.putString("symbol", intent.getStringExtra("symbol"));
        }
        // We can call OnRunTask from the intent service to force it to run immediately instead of
        // scheduling a task.
        try {
            stockTaskService.onRunTask(new TaskParams(intent.getStringExtra("tag"), args));
        } catch (SQLiteConstraintException e) {
            e.printStackTrace();
        }
    }
}
