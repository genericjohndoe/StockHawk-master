package com.sam_chordas.android.stockhawk.ui;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by joeljohnson on 2/22/17.
 */

public class GraphAsyncTask extends AsyncTask<String, Void, Void> {
    String[] dates = new String[2];

    GraphLoadedCallback graphLoadedCallback;

    public GraphAsyncTask(GraphLoadedCallback graphLoadedCallback) {
        this.graphLoadedCallback = graphLoadedCallback;
    }

    private Void getStockInfoFromJSON(String JsonString)
            throws JSONException {
        MyStocksActivity.StockData = new ArrayList<>();
        JSONObject Json = new JSONObject(JsonString);
        JSONObject query = Json.getJSONObject("query");
        JSONObject results = query.getJSONObject("results");
        JSONArray StockInfo = results.getJSONArray("quote");

        for (int i = 0; i < StockInfo.length(); i++) {
            StockData datum = new StockData();
            JSONObject stockPrice = StockInfo.getJSONObject(i);
            String[] stringDate = stockPrice.getString("Date").split("-");
            Calendar cal = Calendar.getInstance();
            cal.set(Integer.parseInt(stringDate[0]),
            Integer.parseInt(stringDate[1]), Integer.parseInt(stringDate[2]));
            datum.date = cal.getTimeInMillis();
            datum.price = stockPrice.getDouble("Close");
            datum.CalDate = stockPrice.getString("Date");
            MyStocksActivity.StockData.add(datum);

        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Calendar calendar = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        calendar2.add(Calendar.DATE, -7);
        dates[1] = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
        dates[0] = new SimpleDateFormat("yyyy-MM-dd").format(calendar2.getTime());

    }

    @Override
    protected Void doInBackground(String... strings) {
        OkHttpClient client = new OkHttpClient();

        StringBuilder urlStringBuilder = new StringBuilder();
        try{
            // Base URL for the Yahoo query
            urlStringBuilder.append("https://query.yahooapis.com/v1/public/yql?q=");
            urlStringBuilder.append(URLEncoder.encode("select Date, Close from yahoo.finance.historicaldata where symbol "
                    + "= \""+strings[0]+"\" and startDate = \""+ dates[0]+"\" and endDate = \"" + dates[1] + "\"", "UTF-8"));
            urlStringBuilder.append("&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables."
                    + "org%2Falltableswithkeys&callback=");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url = urlStringBuilder.toString();

        Request request = new Request.Builder()
                .url(url)
                .build();
        try {
            Response response = client.newCall(request).execute();
            getStockInfoFromJSON(response.body().string());
        } catch (IOException e) {

        } catch (JSONException e){

        } catch (IllegalStateException e){

        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        graphLoadedCallback.graphDataLoaded();
        super.onPostExecute(aVoid);
    }

}
