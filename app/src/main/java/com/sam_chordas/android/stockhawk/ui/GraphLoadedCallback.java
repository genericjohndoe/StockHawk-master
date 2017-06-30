package com.sam_chordas.android.stockhawk.ui;

/**
 * callback used to notify system when historical stock data in the graph was been downloaded and
 * processed
 */

public interface GraphLoadedCallback {
    void graphDataLoaded();
}
