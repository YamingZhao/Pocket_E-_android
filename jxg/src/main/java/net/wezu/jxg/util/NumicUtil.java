package net.wezu.jxg.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Created by snox on 2015/12/2.
 */
public class NumicUtil {

    public static DecimalFormat format = new DecimalFormat("#0.00");

    /**
     * 格式化金钱
     *
     * @param value
     * @return
     */
    public static String formatDouble(double value) {
        format.setRoundingMode(RoundingMode.HALF_UP);
        return format.format(value);
    }

    /**
     * 格式化金钱
     *
     * @param value
     * @return
     */
    public static String formatDouble(BigDecimal value) {
        format.setRoundingMode(RoundingMode.HALF_UP);
        return format.format(value);
    }
}
