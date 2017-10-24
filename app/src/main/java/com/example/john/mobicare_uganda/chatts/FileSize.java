package com.example.john.mobicare_uganda.chatts;

import java.text.DecimalFormat;

/**
 * Created by john on 10/23/17.
 */

public class FileSize {
    public static String size(long size){
        String hrSize = "";
        try {
            double m = size / (1024.0 *1024.0);
            DecimalFormat dec = new DecimalFormat("0.00");

            if (m > 1) {
                hrSize = dec.format(m).concat(" MB");
            } else {
                hrSize = dec.format(size).concat(" KB");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return hrSize;
    }
}
