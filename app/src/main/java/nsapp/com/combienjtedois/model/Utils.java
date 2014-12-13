package nsapp.com.combienjtedois.model;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import nsapp.com.combienjtedois.R;

public class Utils {

    public static final String PATTERN_DATE = "dd-MM-yyyy : HH:mm";
    public static final String EVENT_PATTERN_DATE = "dd-MM-yyyy";

    public static final String PERSON_KEY = "person_key";
    public static final String PATH_KEY = "path_key";

    public static final int ANIMATION_DURATION = 400;

    public static final int IMPORT_CONTACT_CODE = 0;
    public static final int IMPORT_PERSON_IMAGE_CODE = 1;
    public static final int IMPORT_DEBT_IMAGE_CODE = 2;
    public static final int TAKE_PICTURE_FOR_PERSON = 3;
    public static final int TAKE_PICTURE_FOR_DEBT = 4;
    public static final int UPDATE_DEBT_COUNT = 5;

    public static DBManager dbManager = null;

    public static String camelCase(String s) {
        String result = "";
        s = s.toLowerCase();
        int length = s.length();
        for (int i = 0; i < length; i++) {
            if (i == 0) {
                result += String.valueOf(s.charAt(i)).toUpperCase();
            } else {
                result += String.valueOf(s.charAt(i));
            }
        }
        return result;
    }

    public static String getPathImage(Context context, Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        if (context.getContentResolver() != null && uri != null) {
            Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
            int column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index_data);
        }
        return "";
    }

    public static Bitmap getImageFromPath(String path) {
        return BitmapFactory.decodeFile(path);
    }

    private static long getLifeTimeInMillis(String date) {
        long now = new Date().getTime();
        long dateSaved = 0;
        SimpleDateFormat format = new SimpleDateFormat(PATTERN_DATE);
        try {
            dateSaved = format.parse(date).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return now - dateSaved;
    }

    public static String convertLifeTime(Context context, String date) {
        Long seconds = TimeUnit.MILLISECONDS.toSeconds(getLifeTimeInMillis(date));
        Long minutes = TimeUnit.MILLISECONDS.toMinutes(getLifeTimeInMillis(date));
        Long hours = TimeUnit.MILLISECONDS.toHours(getLifeTimeInMillis(date));
        Long days = TimeUnit.MILLISECONDS.toDays(getLifeTimeInMillis(date));

        if (seconds < 60) {
            return seconds + context.getString(R.string.second) + "s";
        } else if (minutes < 60) {
            if (minutes == 1) {
                return minutes + context.getString(R.string.minut);
            } else {
                return minutes + context.getString(R.string.minut) + "s";
            }
        } else if (hours < 24) {
            if (hours == 1) {
                return hours + context.getString(R.string.hour);
            } else {
                return hours + context.getString(R.string.hour) + "s";
            }
        } else {
            if (days == 1) {
                return days + context.getString(R.string.day);
            } else {
                return days + context.getString(R.string.day) + "s";
            }
        }
    }

    public static String convertLifeTimeFromMillis(Context context, long dateMillis) {
        Long seconds = dateMillis / 1000;
        Long minutes = seconds / 60;
        Long hours = minutes / 60;
        Long days = hours / 24;

        if (seconds < 60) {
            return seconds + context.getString(R.string.second) + "s";
        } else if (minutes < 60) {
            if (minutes == 1) {
                return minutes + context.getString(R.string.minut);
            } else {
                return minutes + context.getString(R.string.minut) + "s";
            }
        } else if (hours < 24) {
            if (hours == 1) {
                return hours + context.getString(R.string.hour);
            } else {
                return hours + context.getString(R.string.hour) + "s";
            }
        } else {
            if (days == 1) {
                return days + context.getString(R.string.day);
            } else {
                return days + context.getString(R.string.day) + "s";
            }
        }
    }
}
