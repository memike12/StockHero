package edu.usna.mobileos.stockhero;

/**
 * Used to get a string array of the dow stocks. Will implement selecting components that pertain
 * to specific date provided in the future.
 */
public class DowIndex {
    private String[] dowHistoricalComponents = {"MMM", "AA", "HON", "AXP", "T", "BA", "CAT", "CVX",
            "C", "EKDKQ", "KO", "DD", "XOM", "GE", "GM", "HPQ", "HD", "INTC", "IBM", "IP", "JNJ",
            "JPM","MCD","MRK","MSFT", "MO", "PG", "UTX", "WMT", "DIS", "AIG", "PFE", "VZ", "BAC",
            "CVX", "TRV", "UNH", "GS", "NKE", "V", "AAPL"};

    public String[] getDow(String date){
        return dowHistoricalComponents;
    }
}
