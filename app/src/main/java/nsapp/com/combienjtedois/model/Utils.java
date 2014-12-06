package nsapp.com.combienjtedois.model;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.view.Display;

public class Utils {

    public static final String PATTERN_DATE = "dd-MM-yyyy : H:m";

    public static String PERSON_KEY = "person_key";
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

    public static int getScreenWidth(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        int width;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            display.getSize(size);
            width = size.x;
        } else {
            width = display.getWidth();
        }
        return width;
    }
}
