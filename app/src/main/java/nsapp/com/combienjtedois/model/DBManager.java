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

    // PERSON
    private static final String ID_PERSON_KEY = "idP";
    public static final String NAME_PERSON_KEY = "name";
    public static final String PHONE_NUMBER_KEY = "phoneNumber";
    private static final String TOTAL_COUNT_KEY = "totalCount";


    // DEBT
    private static final String ID_DEBT_KEY = "idD";
    private static final String ID_PERSON_DEBT_KEY = "idFKP";
    private static final String AMOUNT_KEY = "amount";
    public static final String REASON_KEY = "reason";

    //OBJECT
    private static final String ID_OBJECT_KEY = "idO";
    public static final String CATEGORY_OBJECT_KEY = "category";
    public static final String NAME_OBJECT_KEY = "nameObject";
    public static final String TYPE_OBJECT_KEY = "type";

    // COMMON
    public static final String DATE_KEY = "date";
    public static final String IMAGE_PROFILE = "profileImageUrl";


    private static final String DATABASE_NAME = "dataBaseApp";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_TABLE_PERSON = "person";
    private static final String DATABASE_TABLE_DEBT = "debt";
    private static final String DATABASE_TABLE_OBJECT = "loanObject";

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

    private static final String CREATE_TABLE_LOAN_OBJECT_QUERY = "create table loanObject (idO integer primary key autoincrement, "
            + "name text not null, "
            + "category text not null, "
            + "nameObject text not null, "
            + "type text not null, "
            + "date text) ";

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
            db.execSQL(CREATE_TABLE_LOAN_OBJECT_QUERY);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS person");
            db.execSQL("DROP TABLE IF EXISTS debt");
            db.execSQL("DROP TABLE IF EXISTS loanObject");
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
     * *************
     * PERSON QUERIES
     * **************
     */
    public long createPerson(String name, String phoneNumber, String date) {
        ContentValues initialValues = new ContentValues();
        name = Utils.camelCase(name);
        if (fetchIdPerson(name) == 0) {
            initialValues.put(NAME_PERSON_KEY, name);
            initialValues.put(TOTAL_COUNT_KEY, "");
            initialValues.put(PHONE_NUMBER_KEY, phoneNumber);
            initialValues.put(DATE_KEY, date);
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
        args.put(DATE_KEY, date);

        return sqLiteDatabase.update(DATABASE_TABLE_PERSON, args, ID_PERSON_KEY + "="
                + idPerson, null) > 0;
    }

    public boolean modifyPerson(long idPerson, String name, String totalCount, String phoneNumber, String date) {
        ContentValues args = new ContentValues();
        args.put(NAME_PERSON_KEY, Utils.camelCase(name));
        args.put(TOTAL_COUNT_KEY, totalCount);
        args.put(PHONE_NUMBER_KEY, phoneNumber);
        args.put(DATE_KEY, date);

        return sqLiteDatabase.update(DATABASE_TABLE_PERSON, args, ID_PERSON_KEY + "="
                + idPerson, null) > 0;
    }

    public long fetchIdPerson(String name) {
        Cursor c = sqLiteDatabase.query(DATABASE_TABLE_PERSON, new String[]{
                ID_PERSON_KEY, NAME_PERSON_KEY, PHONE_NUMBER_KEY, IMAGE_PROFILE, TOTAL_COUNT_KEY, DATE_KEY}, NAME_PERSON_KEY + "= '" + name + "'", null, null, null, null);

        if (c != null && c.moveToFirst()) {
            return c.getInt(c.getColumnIndex(ID_PERSON_KEY));
        }
        return 0;
    }


    public Cursor fetchAllPersons() {
        return sqLiteDatabase.query(DATABASE_TABLE_PERSON, new String[]{
                        ID_PERSON_KEY, NAME_PERSON_KEY, PHONE_NUMBER_KEY, IMAGE_PROFILE, TOTAL_COUNT_KEY, DATE_KEY}, null, null, null, null,
                null);
    }


    public boolean deletePerson(int idPerson) {
        return sqLiteDatabase.delete(DATABASE_TABLE_PERSON, ID_PERSON_KEY + "=" + idPerson,
                null) > 0 && sqLiteDatabase.delete(DATABASE_TABLE_DEBT, ID_PERSON_DEBT_KEY + "=" + idPerson,
                null) > 0;
    }

    /**
     * ************
     * DEBT QUERIES
     * *************
     */

    public long createDebt(long idPerson, String amount, String reason, String date) {
        ContentValues initialValues = new ContentValues();
        reason = Utils.camelCase(reason);
        if (fetchIdDebt(idPerson, reason) == 0) {
            initialValues.put(ID_PERSON_DEBT_KEY, idPerson);
            initialValues.put(AMOUNT_KEY, amount);
            initialValues.put(REASON_KEY, reason);
            initialValues.put(DATE_KEY, date);
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
        initialValues.put(DATE_KEY, date);

        return sqLiteDatabase.update(DATABASE_TABLE_DEBT, initialValues, ID_DEBT_KEY + "=" + idDebt, null) > 0;
    }

    public boolean setImageProfileUrlDebt(long idDebt, String url) {
        ContentValues args = new ContentValues();
        args.put(IMAGE_PROFILE, url);

        return sqLiteDatabase.update(DATABASE_TABLE_DEBT, args, ID_DEBT_KEY + "="
                + idDebt, null) > 0;
    }

    public long fetchIdDebt(long idPerson, String reason) {
        Cursor c = sqLiteDatabase.query(DATABASE_TABLE_DEBT, new String[]{
                ID_DEBT_KEY, ID_PERSON_DEBT_KEY, AMOUNT_KEY, REASON_KEY, IMAGE_PROFILE, DATE_KEY}, ID_PERSON_DEBT_KEY + "='" + idPerson + "' AND " + REASON_KEY + "='" + reason + "'", null, null, null, null);

        if (c != null && c.moveToFirst()) {
            return c.getInt(c.getColumnIndex(ID_DEBT_KEY));
        }
        return 0;
    }

    private Cursor fetchDebt(long rowIdDebt) {
        Cursor mCursor = sqLiteDatabase.query(true, DATABASE_TABLE_DEBT, new String[]{
                ID_DEBT_KEY, ID_PERSON_DEBT_KEY, AMOUNT_KEY, REASON_KEY, IMAGE_PROFILE, DATE_KEY}, ID_DEBT_KEY + "="
                + rowIdDebt, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public Cursor fetchAllDebt(long idPerson) {
        return sqLiteDatabase.query(DATABASE_TABLE_DEBT, new String[]{
                        ID_DEBT_KEY, ID_PERSON_DEBT_KEY, AMOUNT_KEY, REASON_KEY, IMAGE_PROFILE, DATE_KEY}, ID_PERSON_DEBT_KEY + "=" + idPerson, null, null, null,
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

    /**
     * *************
     * OBJECT QUERIES
     * **************
     */
    public long createObject(long idObject, String name, String category, String nameObject, String type, String date) {
        ContentValues initialValues = new ContentValues();
        name = Utils.camelCase(name);
        category = Utils.camelCase(category);
        nameObject = Utils.camelCase(nameObject);
        type = Utils.camelCase(type);

        initialValues.put(ID_OBJECT_KEY, idObject);
        initialValues.put(NAME_PERSON_KEY, name);
        initialValues.put(CATEGORY_OBJECT_KEY, category);
        initialValues.put(NAME_OBJECT_KEY, nameObject);
        initialValues.put(TYPE_OBJECT_KEY, type);
        initialValues.put(DATE_KEY, date);

        return sqLiteDatabase.insert(DATABASE_TABLE_OBJECT, null, initialValues);
    }

    public Cursor fetchAllObjects() {
        return sqLiteDatabase.query(DATABASE_TABLE_OBJECT, new String[]{
                        ID_OBJECT_KEY, NAME_PERSON_KEY, CATEGORY_OBJECT_KEY, NAME_OBJECT_KEY, TYPE_OBJECT_KEY, DATE_KEY}, null, null, null, null,
                null);
    }

    public long fetchIdObject(String namePerson, String date) {
        Cursor c = sqLiteDatabase.query(DATABASE_TABLE_OBJECT, new String[]{
                ID_OBJECT_KEY, NAME_PERSON_KEY, CATEGORY_OBJECT_KEY, NAME_OBJECT_KEY, TYPE_OBJECT_KEY, DATE_KEY}, NAME_PERSON_KEY + "='" + namePerson + "'AND " +
                DATE_KEY + "='" + date + "'", null, null, null, null);

        if (c != null && c.moveToFirst()) {
            return c.getInt(c.getColumnIndex(ID_OBJECT_KEY));
        }
        return 0;
    }

    public boolean deleteObject(int idObject) {
        return sqLiteDatabase.delete(DATABASE_TABLE_OBJECT, ID_OBJECT_KEY + "=" + idObject, null) > 0;
    }
}
