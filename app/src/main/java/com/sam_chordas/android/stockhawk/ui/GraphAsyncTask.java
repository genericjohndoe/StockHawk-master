package com.sam_chordas.android.stockhawk.ui;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Pulls historical stock data from the internet
 */

public class GraphAsyncTask extends AsyncTask<String, Void, Void> {

    GraphLoadedCallback graphLoadedCallback;

    public GraphAsyncTask(GraphLoadedCallback graphLoadedCallback) {
        this.graphLoadedCallback = graphLoadedCallback;
    }

    private void getStockInfoFromJSON(String JsonString)
            throws JSONException {
        MyStocksActivity.StockData = new ArrayList<>();
        JSONObject Json = new JSONObject(JsonString);
        JSONObject StockInfo = Json.getJSONObject("Time Series (Daily)");
        Iterator x = StockInfo.keys();
        while (x.hasNext()){
            StockData datum = new StockData();
            String key = (String) x.next();
            String[] stringDate = key.split("-");
            Calendar cal = Calendar.getInstance();
            cal.set(Integer.parseInt(stringDate[0]),
                    Integer.parseInt(stringDate[1]), Integer.parseInt(stringDate[2].substring(0, 2)));
            Log.i("cal", cal.toString());
            datum.date = cal.getTimeInMillis();
            JSONObject object = (JSONObject) StockInfo.get(key);
            datum.price = object.getDouble("4. close");
            datum.CalDate = key.split(" ")[0];
            MyStocksActivity.StockData.add(datum);
        }
    }


    @Override
    protected Void doInBackground(String... strings) {
        Log.i("MSA", "doInBackground");
        OkHttpClient client = new OkHttpClient();

        String url = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=" + strings[0] +
                "&outputsize=compact&apikey=IYFRSS5SYRI6OTEP";
        Log.i("url", url);

        Request request = new Request.Builder()
                .url(url)
                .build();
        try {
            Response response = client.newCall(request).execute();
            getStockInfoFromJSON(response.body().string());
        } catch (IOException | JSONException | IllegalStateException e) {
            Log.i("GraphAsyncTask", e.toString());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        graphLoadedCallback.graphDataLoaded();
        super.onPostExecute(aVoid);
    }

}
