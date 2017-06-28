package com.sam_chordas.android.stockhawk.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by sam_chordas on 10/5/15.
 */
public class QuoteColumns {
    public static final String AUTHORITY = "com.sam_chordas.android.stockhawk.data.QuoteProvider";

    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String QUOTES = "quotes";

    public static final class QuoteEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(QUOTES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "/" + QUOTES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTHORITY + "/" + QUOTES;

        public static final String _ID = "_id";
        public static final String SYMBOL = "symbol";
        public static final String PERCENT_CHANGE = "percent_change";
        public static final String CHANGE = "change";
        public static final String BIDPRICE = "bid_price";
        public static final String CREATED = "created";
        public static final String ISUP = "is_up";
        public static final String ISCURRENT = "is_current";
        public static final String NAME = "Name";
        public static final String TABLE_NAME = "quotes";

        public static Uri buildUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
