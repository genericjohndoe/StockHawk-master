package com.sam_chordas.android.stockhawk.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by sam_chordas on 10/5/15.
 */

public class QuoteDatabase extends SQLiteOpenHelper {
  public static final int VERSION = 1;

  static final String DATABASE_NAME = "quotes.db";

  public QuoteDatabase(Context context) {
    super(context, DATABASE_NAME, null, VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase sqLiteDatabase) {
    final String CREATE_DATABASE = "CREATE TABLE " + QuoteColumns.QuoteEntry.TABLE_NAME + " (" +
            QuoteColumns.QuoteEntry._ID + " INTEGER PRIMARY KEY, " +
            QuoteColumns.QuoteEntry.SYMBOL + " TEXT NOT NULL, " +
            QuoteColumns.QuoteEntry.PERCENT_CHANGE + " TEXT NOT NULL, " +
            QuoteColumns.QuoteEntry.CHANGE + " TEXT NOT NULL, " +
            QuoteColumns.QuoteEntry.BIDPRICE + " TEXT NOT NULL, " +
            QuoteColumns.QuoteEntry.NAME + " TEXT NOT NULL, " +
            QuoteColumns.QuoteEntry.ISCURRENT + " INTEGER NOT NULL, " +
            QuoteColumns.QuoteEntry.ISUP + " INTEGER NOT NULL " + " );";

    sqLiteDatabase.execSQL(CREATE_DATABASE);

  }

  @Override
  public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
    // This database is only a cache for online data, so its upgrade policy is
    // to simply to discard the data and start over
    // Note that this only fires if you change the version number for your database.
    // It does NOT depend on the version number for your application.
    // If you want to update the schema without wiping data, commenting out the next 2 lines
    // should be your top priority before modifying this method.
    sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + QuoteColumns.QuoteEntry.TABLE_NAME);
    onCreate(sqLiteDatabase);
  }


}
