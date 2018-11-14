package syde.co.smartregister;

import android.widget.TimePicker;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Aviroez on 24/06/2015.
 */
public class Dates {
    public static final String DATETIMEZONE_FORMAT = "yyyy-MM-dd HH:mm:ssZ";
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_LOCAL_FORMAT = "dd-MM-yyyy";
    public static final String CALENDAR_LOCAL_FORMAT = "dd MMMM yyyy";
    public static final String DATETIME_LOCAL_FORMAT = "dd MMMM yyyy HH:mm:ss";
    public static final String DATE_SQL_FORMAT = "yyyy-MM-dd";
    public static final String TIMESTAMP_FORMAT = "HH:mm:ss";

    public static String timeToString(TimePicker time) {
        Format formatter;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, time.getCurrentHour());
        calendar.set(Calendar.MINUTE, time.getCurrentMinute());

        formatter = new SimpleDateFormat("HH:mm:ss");
        return formatter.format(calendar.getTime());
    }

    public static Date toDate(String dateString) {
        try {
            return new SimpleDateFormat(DATETIME_FORMAT).parse(dateString);
        } catch (ParseException e) {
            return null;
        }
    }

    public static Date toDate(String dateString, String format) {
        try {
            return new SimpleDateFormat(format).parse(dateString);
        } catch (ParseException e) {
            return null;
        }
    }

    public static String toString(Date date, String format) {
        return new SimpleDateFormat(format).format(date);
    }

    public static String reformat(String dateString, String currentFormat, String newFormat) {
        try {
            Date date = new SimpleDateFormat(currentFormat).parse(dateString);
            return new SimpleDateFormat(newFormat).format(date);
        } catch (ParseException e) {
            return null;
        }
    }
}
