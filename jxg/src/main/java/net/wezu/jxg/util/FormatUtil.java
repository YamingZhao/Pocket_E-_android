package net.wezu.jxg.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by snox on 2016/4/23.
 */
public class FormatUtil {
    private FormatUtil() { }

    private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public static String formatDate(Date date) {
        return dateFormat.format(date);
    }
}
