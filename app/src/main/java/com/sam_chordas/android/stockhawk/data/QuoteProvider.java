package com.sam_chordas.android.stockhawk.data;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;


/**
 * Created by sam_chordas on 10/5/15.
 */

public class QuoteProvider extends ContentProvider {

  private static final UriMatcher sUriMatcher = buildUriMatcher();
  private QuoteDatabase mOpenHelper;

  private static final int QUOTES = 100;
  private static final int QUOTES_ID = 101;

  static UriMatcher buildUriMatcher() {

    final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
    final String authority = QuoteColumns.AUTHORITY;

    // For each type of URI you want to add, create a corresponding code.
    matcher.addURI(authority, QuoteColumns.QUOTES, QUOTES);
    matcher.addURI(authority, QuoteColumns.QUOTES + "/#", QUOTES_ID);

    return matcher;
  }

  @Override
  public boolean onCreate() {
    mOpenHelper = new QuoteDatabase(getContext());
    return true;
  }


  @Override
  public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                      String sortOrder) {
    // Here's the switch statement that, given a URI, will determine what kind of request it is,
    // and query the database accordingly.

    return mOpenHelper.getReadableDatabase().query(
            QuoteColumns.QuoteEntry.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            sortOrder
    );
  }

  @Override
  public Uri insert(Uri uri, ContentValues values) {
    final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
    Uri returnUri;

    long _id = db.insert(QuoteColumns.QuoteEntry.TABLE_NAME, null, values);

    if (_id > 0) {
      returnUri = ContentUris.withAppendedId(QuoteColumns.QuoteEntry.CONTENT_URI, _id);
    }else {
      throw new android.database.SQLException("Failed to insert row into " + uri.toString());
    }
    return returnUri;
  }

  @Override
  public int bulkInsert(Uri uri, ContentValues[] values) {
    final SQLiteDatabase db = mOpenHelper.getWritableDatabase();


    db.beginTransaction();
    int returnCount = 0;
    try {
      for (ContentValues value : values) {

        long _id = db.insert(QuoteColumns.QuoteEntry.TABLE_NAME, null, value);
        if (_id != -1) {
          returnCount++;
        }
      }
      db.setTransactionSuccessful();
    } finally {
      db.endTransaction();
      Log.i("path", db.getPath());
      Log.i("concise summary", db.toString());
    }
    getContext().getContentResolver().notifyChange(uri, null);
    return returnCount;
  }


  @Override
  public int delete(Uri uri, String selection, String[] selectionArgs) {
    final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
    int rowsDeleted;
    // this makes delete all rows return the number of rows deleted
    if (null == selection) selection = "1";
    rowsDeleted = db.delete(
            QuoteColumns.QuoteEntry.TABLE_NAME, selection, selectionArgs);
    return rowsDeleted;
  }

  @Override
  public int update(
          Uri uri, ContentValues values, String selection, String[] selectionArgs) {
    final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

    int rowsUpdated;

    rowsUpdated = db.update(QuoteColumns.QuoteEntry.TABLE_NAME, values, selection,
            selectionArgs);
    return rowsUpdated;
  }

  @Override
  public String getType(Uri uri) {

    // Use the Uri Matcher to determine what kind of URI this is.
    final int match = sUriMatcher.match(uri);

    switch (match) {
      case QUOTES:
        return QuoteColumns.QuoteEntry.CONTENT_TYPE;
      case QUOTES_ID:
        return QuoteColumns.QuoteEntry.CONTENT_ITEM_TYPE;
      default:
        throw new UnsupportedOperationException("Unknown uri: " + uri);
    }

  }

  @Override
  public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations)
          throws OperationApplicationException {


    ContentProviderResult[] result = new ContentProviderResult[operations.size()];
    SQLiteDatabase db = mOpenHelper.getWritableDatabase();
    db.beginTransaction();
    int i = 0;
    try {
      for (ContentProviderOperation operation : operations) {
        result[i++] = operation.apply(this, result, i);
      }
      db.setTransactionSuccessful();
      getContext().getContentResolver().notifyChange(operations.get(0).getUri(), null);
    } catch (OperationApplicationException e) {
      Log.e("QP", e.getLocalizedMessage());
    } finally {
      db.endTransaction();
    }
    return result;
  }
}
