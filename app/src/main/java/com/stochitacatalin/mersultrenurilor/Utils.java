package com.stochitacatalin.mersultrenurilor;

import java.util.Locale;

public class Utils {
    public static String toTime(int totalSecs,boolean withSeconds) {
        if(totalSecs == -1)
            return "-";
        totalSecs = totalSecs%86400;
        int hours = totalSecs / 3600;
        int minutes = (totalSecs % 3600) / 60;
        int seconds = totalSecs % 60;
        if (withSeconds)
            return String.format(Locale.getDefault(),"%02d:%02d:%02d", hours, minutes, seconds);
        else
            return String.format(Locale.getDefault(),"%02d:%02d", hours, minutes);
    }
}
