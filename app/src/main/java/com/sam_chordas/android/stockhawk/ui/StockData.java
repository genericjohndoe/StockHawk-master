package com.sam_chordas.android.stockhawk.ui;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by joeljohnson on 2/22/17.
 */

public class StockData implements Parcelable {
    long date;
    double price;
    String CalDate;

    public StockData(){}

    public StockData(long date, double price, String CalDate){
        this.date = date;
        this.price = price;
        this.CalDate = CalDate;
    }

    private StockData(Parcel in){
        date = in.readLong();
        price = in.readDouble();
        CalDate = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "" + date + " -- " + price + " -- " + CalDate;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(date);
        parcel.writeDouble(price);
        parcel.writeString(CalDate);
    }

    public static final Parcelable.Creator<StockData> CREATOR = new Parcelable.Creator<StockData>() {
        @Override
        public StockData createFromParcel(Parcel parcel) {
            return new StockData(parcel);
        }

        @Override
        public StockData[] newArray(int i) {
            return new StockData[i];
        }
    };
}
