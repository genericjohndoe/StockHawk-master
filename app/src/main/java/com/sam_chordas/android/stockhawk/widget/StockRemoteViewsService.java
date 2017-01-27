package com.sam_chordas.android.stockhawk.widget;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.rest.Utils;

/**
 * Created by joeljohnson on 6/14/16.
 */
public class StockRemoteViewsService extends RemoteViewsService {
    final String[] STOCK_COLUMNS = {QuoteColumns._ID, QuoteColumns.SYMBOL,QuoteColumns.PERCENT_CHANGE,
            QuoteColumns.CHANGE, QuoteColumns.BIDPRICE, QuoteColumns.ISCURRENT, QuoteColumns.ISUP};

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;

            Uri uri = Uri.parse("content://com.sam_chordas.android.stockhawk.data.QuoteProvider")
                    .buildUpon().appendPath("quotes").build();

            @Override
            public void onCreate() {
                // Nothing to do
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }
                // This method is called by the app hosting the widget (e.g., the launcher)
                // However, our ContentProvider is not exported so it doesn't have access to the
                // data. Therefore we need to clear (and finally restore) the calling identity so
                // that calls use our process and permission

                final long identityToken = Binder.clearCallingIdentity();


                data = getContentResolver().query(uri,
                        STOCK_COLUMNS,
                        null,
                        null,
                        null);
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.widget_detail_list_item);
                String stock_symbol = data.getString(data.getColumnIndex(QuoteColumns.SYMBOL));
                String bid_price = data.getString(data.getColumnIndex(QuoteColumns.BIDPRICE));
                String change = data.getString(data.getColumnIndex(QuoteColumns.CHANGE));
                String percent_change = data.getString(data.getColumnIndex(QuoteColumns.PERCENT_CHANGE));

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                    setRemoteContentDescription(views, null);
                }
                views.setTextViewText(R.id.stock_symbol, stock_symbol);
                views.setTextViewText(R.id.bid_price,bid_price);
                //views.setTextViewText(R.id.change, change);
                if (Utils.showPercent){
                    views.setTextViewText(R.id.change, percent_change);
                } else{
                    views.setTextViewText(R.id.change, change);
                }
                if (data.getInt(data.getColumnIndex("is_up")) == 1){
                    views.setTextColor(R.id.change, getResources().getColor(R.color.material_green_700));
                } else {
                    views.setTextColor(R.id.change, getResources().getColor(R.color.material_red_700));
                }


                /*final Intent fillInIntent = new Intent();

                fillInIntent.setData(uri.buildUpon().appendPath(QuoteColumns._ID).build());
                views.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);*/
                return views;
            }

            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
            private void setRemoteContentDescription(RemoteViews views, String description) {

                views.setContentDescription(R.id.stock_symbol,
                        data.getString(data.getColumnIndex(QuoteColumns.NAME)));
                views.setContentDescription(R.id.bid_price,
                        data.getString(data.getColumnIndex(QuoteColumns.BIDPRICE)));
                if (Utils.showPercent){
                    views.setContentDescription(R.id.change,
                            data.getString(data.getColumnIndex(QuoteColumns.PERCENT_CHANGE)));
                } else{
                    views.setContentDescription(R.id.change,
                            data.getString(data.getColumnIndex(QuoteColumns.CHANGE)));
                }
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_detail_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (data.moveToPosition(position))
                    return data.getLong(data.getColumnIndex(QuoteColumns._ID));
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
