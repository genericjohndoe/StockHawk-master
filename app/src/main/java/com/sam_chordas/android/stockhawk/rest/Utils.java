package com.sam_chordas.android.stockhawk.rest;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.util.Log;

import com.sam_chordas.android.stockhawk.data.QuoteColumns;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by sam_chordas on 10/8/15.
 */
public class Utils {

  private static String LOG_TAG = Utils.class.getSimpleName();

  public static boolean showPercent = true;

  public static ContentValues[] quoteJsonToContentVals(String JSON){
    //Represents a single operation to be performed as part of a batch of operations.
    ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>();
    JSONObject jsonObject = null;
    JSONArray resultsArray = null;
    Vector<ContentValues> cVVector;
    ContentValues[] cvArray;
    try{
      jsonObject = new JSONObject(JSON);
      if (jsonObject != null && jsonObject.length() != 0){
        jsonObject = jsonObject.getJSONObject("query");
        int count = Integer.parseInt(jsonObject.getString("count"));
        Log.i("Database", "count " + count);

        if (count == 1){
          jsonObject = jsonObject.getJSONObject("results")
              .getJSONObject("quote");
          ContentValues stockInfo = new ContentValues();
          String change = jsonObject.getString("Change");
          stockInfo.put(QuoteColumns.QuoteEntry.SYMBOL, jsonObject.getString("symbol"));

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
          cVVector = new Vector<ContentValues>(1);
          cVVector.add(stockInfo);
          cvArray = new ContentValues[cVVector.size()];
          cVVector.toArray(cvArray);
          return cvArray;
          //batchOperations.add(buildBatchOperation(jsonObject));
        } else{
          resultsArray = jsonObject.getJSONObject("results").getJSONArray("quote");
          cVVector = new Vector<ContentValues>(resultsArray.length());
          if (resultsArray != null && resultsArray.length() != 0){
            for (int i = 0; i < resultsArray.length(); i++){
              jsonObject = resultsArray.getJSONObject(i);
              ContentValues stockInfo = new ContentValues();
              String change = jsonObject.getString("Change");
              stockInfo.put(QuoteColumns.QuoteEntry.SYMBOL, jsonObject.getString("symbol"));

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
              cVVector.add(stockInfo);
              //batchOperations.add(buildBatchOperation(jsonObject));
            }
            if ( cVVector.size() > 0) {
              cvArray = new ContentValues[cVVector.size()];
              cVVector.toArray(cvArray);
              return cvArray;
              //int inserted = mContext.getContentResolver().bulkInsert(QuoteColumns.QuoteEntry.CONTENT_URI, cvArray);
              //Log.i("MMD2", "movies inserted " + inserted);
            }
          }
        }
      }
    } catch (JSONException e){
      Log.e(LOG_TAG, "String to JSON failed: " + e);
    }
    return null;
  }

  public static String truncateBidPrice(String bidPrice){
    if(!bidPrice.equals(null)) {
      bidPrice = String.format("%.2f", Float.parseFloat(bidPrice));
    } else {
      bidPrice = "0.00";
    }
    return bidPrice;
  }

  public static String truncateChange(String change, boolean isPercentChange){
    String weight = change.substring(0,1);
    String ampersand = "";
    if (isPercentChange){
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

  public static ContentProviderOperation buildBatchOperation(JSONObject jsonObject){
    ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(
        QuoteColumns.QuoteEntry.CONTENT_URI);
    try {
      String change = jsonObject.getString("Change");
      builder.withValue(QuoteColumns.QuoteEntry.SYMBOL, jsonObject.getString("symbol"));
      builder.withValue(QuoteColumns.QuoteEntry.BIDPRICE, truncateBidPrice(jsonObject.getString("Bid")));
      builder.withValue(QuoteColumns.QuoteEntry.PERCENT_CHANGE, truncateChange(
          jsonObject.getString("ChangeinPercent"), true));
      builder.withValue(QuoteColumns.QuoteEntry.CHANGE, truncateChange(change, false));
      builder.withValue(QuoteColumns.QuoteEntry.ISCURRENT, 1);
      if (change.charAt(0) == '-'){
        builder.withValue(QuoteColumns.QuoteEntry.ISUP, 0);
      }else{
        builder.withValue(QuoteColumns.QuoteEntry.ISUP, 1);
      }
      builder.withValue(QuoteColumns.QuoteEntry.NAME, jsonObject.getString("Name"));

    } catch (JSONException e){
      e.printStackTrace();
    }
    catch (NumberFormatException e){
      e.printStackTrace();
    }
    return builder.build();
  }

  public static void stockBulkInsert(JSONObject jsonObject){

  }
}
