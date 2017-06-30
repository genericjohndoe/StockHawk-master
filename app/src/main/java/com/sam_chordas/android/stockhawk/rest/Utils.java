package com.sam_chordas.android.stockhawk.rest;

import android.content.ContentValues;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.sam_chordas.android.stockhawk.data.QuoteColumns;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Vector;

/**
 * Created by sam_chordas on 10/8/15.
 * file contains a number of methods pertinent to data insertion
 */
public class Utils {

  private static String LOG_TAG = Utils.class.getSimpleName();

  public static boolean showPercent = true;

  /**
   * inserts stock data into ContentValues for later database insert
   * @param JSON the JSON string used to extract the stock data
   * @param context used to generate toast if stock symbol isn't valid
   * @return an array of ContentValues with stock information
   */
  public static ContentValues[] quoteJsonToContentVals(String JSON, Context context){
    //Represents a single operation to be performed as part of a batch of operations.
    JSONObject jsonObject = null;
    JSONArray resultsArray = null;
    Vector<ContentValues> cVVector = new  Vector<>() ;
    ContentValues[] cvArray;
    try{
      jsonObject = new JSONObject(JSON);
      if (jsonObject != null && jsonObject.length() != 0){
        jsonObject = jsonObject.getJSONObject("query");
        int count = Integer.parseInt(jsonObject.getString("count"));

        if (count == 1){
          stockBulkInsert(cVVector,jsonObject.getJSONObject("results")
                  .getJSONObject("quote"), context);
          cvArray = new ContentValues[cVVector.size()];
          cVVector.toArray(cvArray);
          return cvArray;
        } else{
          resultsArray = jsonObject.getJSONObject("results").getJSONArray("quote");
          if (resultsArray != null && resultsArray.length() != 0){
            for (int i = 0; i < resultsArray.length(); i++){
              stockBulkInsert(cVVector,resultsArray.getJSONObject(i), context);
            }
            if ( cVVector.size() > 0) {
              cvArray = new ContentValues[cVVector.size()];
              cVVector.toArray(cvArray);
              return cvArray;
            }
          }
        }
      }
    } catch (JSONException e){
      Log.e(LOG_TAG, "String to JSON failed: " + e.toString());
    }
    return null;
  }

  /**
   * used to format the bidprice
   * @param bidPrice the bidp rice in string format
   * @return the formatted string
   */
  public static String truncateBidPrice(String bidPrice){
    return String.format("%.2f", Float.parseFloat(bidPrice));
  }

  /**
   * formats changes in stock price
   * @param change the change in stock price from the previous day
   * @param isPercentChange used to dictate if relative or absolute change is shown
   * @return the change in stock price (can be relative or absolute
   */
  public static String truncateChange(String change, boolean isPercentChange){
      String weight = change.substring(0, 1);
      String ampersand = "";
      if (isPercentChange) {
        ampersand = change.substring(change.length() - 1, change.length());
        change = change.substring(0, change.length() - 1);
      }
      change = change.substring(1, change.length());
      double round = (double) Math.round(Double.parseDouble(change) * 100) / 100;
      change = String.format("%.2f", round);
      StringBuffer changeBuffer = new StringBuffer(change);
      changeBuffer.insert(0, weight);
      changeBuffer.append(ampersand);
      change = changeBuffer.toString();
      return change;
  }


  /**
   * Used to insert stock information into the database
   * @param vector the collections oobjects used to hold the stock info
   * @param jsonObject provides the stock information
   * @param context used to show a toast given the case, the symbol entered isn't valid
   * @return an int depending on whether the symbol entered was valid
   * @throws JSONException
   */
  public static int stockBulkInsert(Vector<ContentValues> vector, JSONObject jsonObject, Context context) throws JSONException{
    ContentValues stockInfo = new ContentValues();
    String change = jsonObject.getString("Change");
    final String symbol = jsonObject.getString("symbol");
    stockInfo.put(QuoteColumns.QuoteEntry.SYMBOL, symbol);
    if (jsonObject.getString("Bid").equals("null")){
      Handler h = new Handler(context.getMainLooper());
      final Context context1 = context;
      h.post(new Runnable() {
        @Override
        public void run() {
          Toast.makeText(context1, symbol.toUpperCase() + " Not Found",Toast.LENGTH_SHORT).show();
        }
      });
      return 0;
    }
    stockInfo.put(QuoteColumns.QuoteEntry.BIDPRICE, truncateBidPrice(jsonObject.getString("Bid")));
    stockInfo.put(QuoteColumns.QuoteEntry.PERCENT_CHANGE, truncateChange(
            jsonObject.getString("ChangeinPercent"), true));
    stockInfo.put(QuoteColumns.QuoteEntry.CHANGE, truncateChange(change, false));
    stockInfo.put(QuoteColumns.QuoteEntry.ISCURRENT, 1);
    if (change.charAt(0) == '-'){
      stockInfo.put(QuoteColumns.QuoteEntry.ISUP, 0);
    }else{
      stockInfo.put(QuoteColumns.QuoteEntry.ISUP, 1);
    }
    stockInfo.put(QuoteColumns.QuoteEntry.NAME, jsonObject.getString("Name"));
    vector.add(stockInfo);
    return 1;

  }
}
