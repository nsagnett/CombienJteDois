package nsapp.com.combienjtedois.model;

public class Tools {

    public static DBManager dbManager = null;

    public static final int ANIMATION_DURATION = 400;

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
}
