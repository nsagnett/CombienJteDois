package nsapp.com.combienjtedois.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import nsapp.com.combienjtedois.R;

public class DBManager {

    private static final String ID_PERSON_KEY = "idP";
    public static final String NAME_PERSON_KEY = "name";
    public static final String MODIFICATION_DATE_KEY = "date";
    public static final String PHONE_NUMBER_KEY = "phoneNumber";
    public static final String TOTAL_COUNT_KEY = "totalCount";

    private static final String ID_DEBT_KEY = "idD";
    private static final String ID_PERSON_DEBT_KEY = "idFKP";
    public static final String AMOUNT_KEY = "amount";
    public static final String REASON_KEY = "reason";

    public static final String IMAGE_PROFILE = "profileImageUrl";
    private static final String DATABASE_NAME = "dataBaseApp";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_TABLE_PERSON = "person";
    private static final String DATABASE_TABLE_DEBT = "debt";

    private static final String CREATE_TABLE_PERSON_QUERY = "create table person (idP integer primary key autoincrement, "
            + "name text not null, "
            + "phoneNumber text, "
            + "date text, "
            + "profileImageUrl text,"
            + "totalCount text not null); ";

    private static final String CREATE_TABLE_DEBT_QUERY = "create table debt (idD integer primary key autoincrement, "
            + "idFKP integer not null, "
            + "amount text not null, "
            + "reason text not null, "
            + "date text, "
            + "profileImageUrl text,"
            + "foreign key(idFKP) references person(idP)); ";

    private DatabaseHelper databaseHelper;
    private SQLiteDatabase sqLiteDatabase;
    private final Context context;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE_PERSON_QUERY);
            db.execSQL(CREATE_TABLE_DEBT_QUERY);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS person");
            db.execSQL("DROP TABLE IF EXISTS debt");
            onCreate(db);
        }
    }

    public DBManager(Context ctx) {
        this.context = ctx;
    }

    public DBManager open() throws SQLException {
        databaseHelper = new DatabaseHelper(context);
        sqLiteDatabase = databaseHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        databaseHelper.close();
    }

    /**
     * PERSON QUERIES
     */
    public long createPerson(String name, String phoneNumber, String date) {
        ContentValues initialValues = new ContentValues();
        name = Utils.camelCase(name);
        if (fetchIdPerson(name) == 0) {
            initialValues.put(NAME_PERSON_KEY, name);
            initialValues.put(TOTAL_COUNT_KEY, "");
            initialValues.put(PHONE_NUMBER_KEY, phoneNumber);
            initialValues.put(MODIFICATION_DATE_KEY, date);
            initialValues.put(IMAGE_PROFILE, "");

            return sqLiteDatabase.insert(DATABASE_TABLE_PERSON, null, initialValues);
        } else {
            Toast.makeText(context, String.format(context.getString(R.string.person_already_present_format), name), Toast.LENGTH_SHORT).show();
        }
        return 0;
    }

    public boolean setImageProfileUrlPerson(long idPerson, String url) {
        ContentValues args = new ContentValues();
        args.put(IMAGE_PROFILE, url);

        return sqLiteDatabase.update(DATABASE_TABLE_PERSON, args, ID_PERSON_KEY + "="
                + idPerson, null) > 0;
    }

    public boolean setModificationDatePerson(long idPerson, String date) {
        ContentValues args = new ContentValues();
        args.put(MODIFICATION_DATE_KEY, date);

        return sqLiteDatabase.update(DATABASE_TABLE_PERSON, args, ID_PERSON_KEY + "="
                + idPerson, null) > 0;
    }

    public boolean modifyPerson(long idPerson, String name, String totalCount, String phoneNumber, String date) {
        ContentValues args = new ContentValues();
        args.put(NAME_PERSON_KEY, Utils.camelCase(name));
        args.put(TOTAL_COUNT_KEY, totalCount);
        args.put(PHONE_NUMBER_KEY, phoneNumber);
        args.put(MODIFICATION_DATE_KEY, date);

        return sqLiteDatabase.update(DATABASE_TABLE_PERSON, args, ID_PERSON_KEY + "="
                + idPerson, null) > 0;
    }

    public long fetchIdPerson(String name) {
        Cursor c = sqLiteDatabase.query(DATABASE_TABLE_PERSON, new String[]{
                ID_PERSON_KEY, NAME_PERSON_KEY, PHONE_NUMBER_KEY, IMAGE_PROFILE, TOTAL_COUNT_KEY, MODIFICATION_DATE_KEY}, NAME_PERSON_KEY + "= '" + name + "'", null, null, null, null);

        if (c != null && c.moveToFirst()) {
            return c.getInt(c.getColumnIndex(ID_PERSON_KEY));
        }
        return 0;
    }


    public Cursor fetchAllPersons() {
        return sqLiteDatabase.query(DATABASE_TABLE_PERSON, new String[]{
                        ID_PERSON_KEY, NAME_PERSON_KEY, PHONE_NUMBER_KEY, IMAGE_PROFILE, TOTAL_COUNT_KEY, MODIFICATION_DATE_KEY}, null, null, null, null,
                null);
    }


    public boolean deletePerson(int idPerson) {
        return sqLiteDatabase.delete(DATABASE_TABLE_PERSON, ID_PERSON_KEY + "=" + idPerson,
                null) > 0 && sqLiteDatabase.delete(DATABASE_TABLE_DEBT, ID_PERSON_DEBT_KEY + "=" + idPerson,
                null) > 0;
    }

    /**
     * DEBT QUERIES
     */

    public long createDebt(long idPerson, String amount, String reason, String date) {
        ContentValues initialValues = new ContentValues();
        reason = Utils.camelCase(reason);
        if (fetchIdDebt(idPerson, reason) == 0) {
            initialValues.put(ID_PERSON_DEBT_KEY, idPerson);
            initialValues.put(AMOUNT_KEY, amount);
            initialValues.put(REASON_KEY, reason);
            initialValues.put(MODIFICATION_DATE_KEY, date);
            initialValues.put(IMAGE_PROFILE, "");

            return sqLiteDatabase.insert(DATABASE_TABLE_DEBT, null, initialValues);
        } else {
            Toast.makeText(context, context.getString(R.string.debt_already_present), Toast.LENGTH_SHORT).show();
        }
        return 0;
    }

    public boolean modifyDebt(long idDebt, String amount, String date) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(AMOUNT_KEY, amount);
        initialValues.put(MODIFICATION_DATE_KEY, date);

        return sqLiteDatabase.update(DATABASE_TABLE_DEBT, initialValues, ID_DEBT_KEY + "=" + idDebt, null) > 0;
    }

    public boolean setModificationDateDebt(long idDebt, String date) {
        ContentValues args = new ContentValues();
        args.put(MODIFICATION_DATE_KEY, date);

        return sqLiteDatabase.update(DATABASE_TABLE_DEBT, args, ID_DEBT_KEY + "="
                + idDebt, null) > 0;
    }

    public boolean setImageProfileUrlDebt(long idDebt, String url) {
        ContentValues args = new ContentValues();
        args.put(IMAGE_PROFILE, url);

        return sqLiteDatabase.update(DATABASE_TABLE_DEBT, args, ID_DEBT_KEY + "="
                + idDebt, null) > 0;
    }

    public long fetchIdDebt(long idPerson, String reason) {
        Cursor c = sqLiteDatabase.query(DATABASE_TABLE_DEBT, new String[]{
                ID_DEBT_KEY, ID_PERSON_DEBT_KEY, AMOUNT_KEY, REASON_KEY, IMAGE_PROFILE, MODIFICATION_DATE_KEY}, ID_PERSON_DEBT_KEY + "='" + idPerson + "' AND " + REASON_KEY + "='" + reason + "'", null, null, null, null);

        if (c != null && c.moveToFirst()) {
            return c.getInt(c.getColumnIndex(ID_DEBT_KEY));
        }
        return 0;
    }

    private Cursor fetchDebt(long rowIdDebt) {
        Cursor mCursor = sqLiteDatabase.query(true, DATABASE_TABLE_DEBT, new String[]{
                ID_DEBT_KEY, ID_PERSON_DEBT_KEY, AMOUNT_KEY, REASON_KEY, IMAGE_PROFILE, MODIFICATION_DATE_KEY}, ID_DEBT_KEY + "="
                + rowIdDebt, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public Cursor fetchAllDebt(long idPerson) {
        return sqLiteDatabase.query(DATABASE_TABLE_DEBT, new String[]{
                        ID_DEBT_KEY, ID_PERSON_DEBT_KEY, AMOUNT_KEY, REASON_KEY, IMAGE_PROFILE, MODIFICATION_DATE_KEY}, ID_PERSON_DEBT_KEY + "=" + idPerson, null, null, null,
                null);
    }

    public String getCount(long idDebt) {
        Cursor c = fetchDebt(idDebt);

        if (c != null) {
            c.moveToFirst();
            return c.getString(c.getColumnIndex(AMOUNT_KEY));
        }
        return null;
    }

    public String getTotalCount(long idPerson) {
        Cursor c = fetchAllDebt(idPerson);
        Double total = 0.;

        if (c != null) {
            while (c.moveToNext()) {
                total += c.getDouble(c.getColumnIndex(AMOUNT_KEY));
            }
        }
        return total.toString();
    }

    public boolean deleteDebt(int idDebt, int idPerson) {
        return sqLiteDatabase.delete(DATABASE_TABLE_DEBT, ID_DEBT_KEY + "=" + idDebt + " AND " + ID_PERSON_DEBT_KEY + "=" + idPerson,
                null) > 0;
    }
}
