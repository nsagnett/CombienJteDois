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
    private static final String TOTAL_COUNT_KEY = "totalCount";


    // DEBT
    private static final String ID_PERSON_DEBT_KEY = "idFKP";
    private static final String AMOUNT_KEY = "amount";
    public static final String REASON_KEY = "reason";

    //OBJECT
    public static final String CATEGORY_OBJECT_KEY = "category";
    public static final String NAME_OBJECT_KEY = "nameObject";
    public static final String TYPE_OBJECT_KEY = "type";

    //EVENT
    public static final String CONSIGNEE_KEY = "consignee";
    public static final String PARTICIPANT_NUMBER_KEY = "participantNumber";
    public static final String SUBJECT_KEY = "subject";
    public static final String VALUE_KEY = "value";

    //PARTICIPANT
    private static final String ID_EVENT_PARTICIPANT_KEY = "idFKEvent";
    public static final String BUDGET_KEY = "budget";
    public static final String PAID_KEY = "paid";

    // COMMON
    private static final String IDENTIFIER_KEY = "id";
    public static final String DATE_KEY = "date";
    public static final String NAME_KEY = "name";
    public static final String PHONE_NUMBER_KEY = "phoneNumber";

    private static final String DATABASE_NAME = "dataBaseApp";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_TABLE_PERSON = "person";
    private static final String DATABASE_TABLE_DEBT = "debt";
    private static final String DATABASE_TABLE_OBJECT = "loanObject";
    private static final String DATABASE_TABLE_EVENT = "event";
    private static final String DATABASE_TABLE_PARTICIPANT = "participant";

    private static final String CREATE_TABLE_PERSON_QUERY = "create table person (id integer primary key autoincrement, "
            + "name text not null, "
            + "phoneNumber text, "
            + "date text, "
            + "totalCount text not null); ";

    private static final String CREATE_TABLE_DEBT_QUERY = "create table debt (id integer primary key autoincrement, "
            + "idFKP integer not null, "
            + "amount text not null, "
            + "reason text not null, "
            + "date text, "
            + "foreign key(idFKP) references person(id)); ";

    private static final String CREATE_TABLE_LOAN_OBJECT_QUERY = "create table loanObject (id integer primary key autoincrement, "
            + "name text not null, "
            + "category text not null, "
            + "nameObject text not null, "
            + "type text not null, "
            + "date text) ";

    private static final String CREATE_TABLE_EVENT_QUERY = "create table event (id integer primary key autoincrement, "
            + "consignee text not null, "
            + "participantNumber integer, "
            + "subject text not null, "
            + "value text not null, "
            + "date text) ";

    private static final String CREATE_TABLE_PARTICIPANT_QUERY = "create table participant (id integer primary key autoincrement, "
            + "idFKEvent integer not null, "
            + "name text not null, "
            + "budget text not null, "
            + "paid integer not null, "
            + "phoneNumber text, "
            + "foreign key(idFKEvent) references event(id))";

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
            db.execSQL(CREATE_TABLE_EVENT_QUERY);
            db.execSQL(CREATE_TABLE_PARTICIPANT_QUERY);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS person");
            db.execSQL("DROP TABLE IF EXISTS debt");
            db.execSQL("DROP TABLE IF EXISTS loanObject");
            db.execSQL("DROP TABLE IF EXISTS event");
            db.execSQL("DROP TABLE IF EXISTS participant");
            onCreate(db);
        }
    }

    public DBManager(Context ctx) {
        this.context = ctx;
    }

    public void open() throws SQLException {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        sqLiteDatabase = databaseHelper.getWritableDatabase();
    }

    /**
     * *************
     * PERSON QUERIES
     * **************
     */
    public void createPerson(String name, String phoneNumber, String date) {
        ContentValues initialValues = new ContentValues();
        name = Utils.camelCase(name);
        if (fetchIdPerson(name) == 0) {
            initialValues.put(NAME_KEY, name);
            initialValues.put(TOTAL_COUNT_KEY, "");
            initialValues.put(PHONE_NUMBER_KEY, phoneNumber);
            initialValues.put(DATE_KEY, date);

            Toast.makeText(context, context.getString(R.string.toast_add_person), Toast.LENGTH_SHORT).show();
            sqLiteDatabase.insert(DATABASE_TABLE_PERSON, null, initialValues);
        } else {
            Toast.makeText(context, String.format(context.getString(R.string.person_already_present_format), name), Toast.LENGTH_SHORT).show();
        }
    }

    public void setModificationDatePerson(int idPerson, String date) {
        ContentValues args = new ContentValues();
        args.put(DATE_KEY, date);

        sqLiteDatabase.update(DATABASE_TABLE_PERSON, args, IDENTIFIER_KEY + "="
                + idPerson, null);
    }

    public void modifyPerson(int idPerson, String name, String phoneNumber, String date) {
        ContentValues args = new ContentValues();
        args.put(NAME_KEY, Utils.camelCase(name));
        args.put(PHONE_NUMBER_KEY, phoneNumber);
        args.put(DATE_KEY, date);

        Toast.makeText(context, context.getString(R.string.toast_modify), Toast.LENGTH_SHORT).show();
        sqLiteDatabase.update(DATABASE_TABLE_PERSON, args, IDENTIFIER_KEY + "="
                + idPerson, null);
    }

    public int fetchIdPerson(String name) {
        Cursor c = sqLiteDatabase.query(DATABASE_TABLE_PERSON, new String[]{
                IDENTIFIER_KEY, NAME_KEY, PHONE_NUMBER_KEY, TOTAL_COUNT_KEY, DATE_KEY}, NAME_KEY + "= '" + name + "'", null, null, null, null);

        if (c != null && c.moveToFirst()) {
            return c.getInt(c.getColumnIndex(IDENTIFIER_KEY));
        }
        return 0;
    }


    public Cursor fetchAllPersons() {
        return sqLiteDatabase.query(DATABASE_TABLE_PERSON, new String[]{
                        IDENTIFIER_KEY, NAME_KEY, PHONE_NUMBER_KEY, TOTAL_COUNT_KEY, DATE_KEY}, null, null, null, null,
                null);
    }


    public void deletePerson(int idPerson) {
        Toast.makeText(context, context.getString(R.string.toast_delete_person), Toast.LENGTH_SHORT).show();
        sqLiteDatabase.delete(DATABASE_TABLE_PERSON, IDENTIFIER_KEY + "=" + idPerson, null);
        sqLiteDatabase.delete(DATABASE_TABLE_DEBT, ID_PERSON_DEBT_KEY + "=" + idPerson, null);
    }

    /**
     * ************
     * DEBT QUERIES
     * *************
     */

    public void createDebt(int idPerson, String amount, String reason, String date) {
        ContentValues initialValues = new ContentValues();
        reason = Utils.camelCase(reason);
        if (fetchIdDebt(idPerson, reason) == 0) {
            initialValues.put(ID_PERSON_DEBT_KEY, idPerson);
            initialValues.put(AMOUNT_KEY, amount);
            initialValues.put(REASON_KEY, reason);
            initialValues.put(DATE_KEY, date);

            Toast.makeText(context, context.getString(R.string.toast_add_element), Toast.LENGTH_SHORT).show();
            sqLiteDatabase.insert(DATABASE_TABLE_DEBT, null, initialValues);
        } else {
            Toast.makeText(context, context.getString(R.string.debt_already_present), Toast.LENGTH_SHORT).show();
        }
    }

    public void modifyDebt(int idDebt, String amount, String date) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(AMOUNT_KEY, amount);
        initialValues.put(DATE_KEY, date);

        Toast.makeText(context, context.getString(R.string.toast_modify), Toast.LENGTH_SHORT).show();
        sqLiteDatabase.update(DATABASE_TABLE_DEBT, initialValues, IDENTIFIER_KEY + "=" + idDebt, null);
    }

    public int fetchIdDebt(int idPerson, String reason) {
        Cursor c = sqLiteDatabase.query(DATABASE_TABLE_DEBT, new String[]{
                IDENTIFIER_KEY, ID_PERSON_DEBT_KEY, AMOUNT_KEY, REASON_KEY, DATE_KEY}, ID_PERSON_DEBT_KEY + "='" + idPerson + "' AND " + REASON_KEY + "='" + reason + "'", null, null, null, null);

        if (c != null && c.moveToFirst()) {
            return c.getInt(c.getColumnIndex(IDENTIFIER_KEY));
        }
        return 0;
    }

    private Cursor fetchDebt(int rowIdDebt) {
        Cursor mCursor = sqLiteDatabase.query(true, DATABASE_TABLE_DEBT, new String[]{
                IDENTIFIER_KEY, ID_PERSON_DEBT_KEY, AMOUNT_KEY, REASON_KEY, DATE_KEY}, IDENTIFIER_KEY + "="
                + rowIdDebt, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public Cursor fetchAllDebt(int idPerson) {
        return sqLiteDatabase.query(DATABASE_TABLE_DEBT, new String[]{
                        IDENTIFIER_KEY, ID_PERSON_DEBT_KEY, AMOUNT_KEY, REASON_KEY, DATE_KEY}, ID_PERSON_DEBT_KEY + "=" + idPerson, null, null, null,
                null);
    }

    public String getCount(int idDebt) {
        Cursor c = fetchDebt(idDebt);

        if (c != null) {
            c.moveToFirst();
            return c.getString(c.getColumnIndex(AMOUNT_KEY));
        }
        return null;
    }

    public String getTotalCount(int idPerson) {
        Cursor c = fetchAllDebt(idPerson);
        Double total = 0.;

        if (c != null) {
            while (c.moveToNext()) {
                total += c.getDouble(c.getColumnIndex(AMOUNT_KEY));
            }
        }
        return total.toString();
    }

    public void deleteDebt(int idDebt, int idPerson) {
        Toast.makeText(context, context.getString(R.string.toast_delete_element), Toast.LENGTH_SHORT).show();
        sqLiteDatabase.delete(DATABASE_TABLE_DEBT, IDENTIFIER_KEY + "=" + idDebt + " AND " + ID_PERSON_DEBT_KEY + "=" + idPerson, null);
    }

    /**
     * *************
     * OBJECT QUERIES
     * **************
     */
    public void createObject(String name, String category, String nameObject, String type, String date) {
        ContentValues initialValues = new ContentValues();
        name = Utils.camelCase(name);
        category = Utils.camelCase(category);
        nameObject = Utils.camelCase(nameObject);
        type = Utils.camelCase(type);

        initialValues.put(NAME_KEY, name);
        initialValues.put(CATEGORY_OBJECT_KEY, category);
        initialValues.put(NAME_OBJECT_KEY, nameObject);
        initialValues.put(TYPE_OBJECT_KEY, type);
        initialValues.put(DATE_KEY, date);

        Toast.makeText(context, context.getString(R.string.toast_add_element), Toast.LENGTH_SHORT).show();
        sqLiteDatabase.insert(DATABASE_TABLE_OBJECT, null, initialValues);
    }

    public Cursor fetchAllObjects() {
        return sqLiteDatabase.query(DATABASE_TABLE_OBJECT, new String[]{
                        IDENTIFIER_KEY, NAME_KEY, CATEGORY_OBJECT_KEY, NAME_OBJECT_KEY, TYPE_OBJECT_KEY, DATE_KEY}, null, null, null, null,
                null);
    }

    public int fetchIdObject(String namePerson, String date) {
        Cursor c = sqLiteDatabase.query(DATABASE_TABLE_OBJECT, new String[]{
                IDENTIFIER_KEY, NAME_KEY, CATEGORY_OBJECT_KEY, NAME_OBJECT_KEY, TYPE_OBJECT_KEY, DATE_KEY}, NAME_KEY + "='" + namePerson + "'AND " +
                DATE_KEY + "='" + date + "'", null, null, null, null);

        if (c != null && c.moveToFirst()) {
            return c.getInt(c.getColumnIndex(IDENTIFIER_KEY));
        }
        return 0;
    }

    public void deleteObject(int idObject) {
        Toast.makeText(context, context.getString(R.string.toast_delete_element), Toast.LENGTH_SHORT).show();
        sqLiteDatabase.delete(DATABASE_TABLE_OBJECT, IDENTIFIER_KEY + "=" + idObject, null);
    }

    /**
     * *************
     * EVENT QUERIES
     * **************
     */
    public void createEvent(String consigneeName, String subject, String value, String date) {
        ContentValues initialValues = new ContentValues();
        consigneeName = Utils.camelCase(consigneeName);
        subject = Utils.camelCase(subject);

        initialValues.put(CONSIGNEE_KEY, consigneeName);
        initialValues.put(PARTICIPANT_NUMBER_KEY, "0");
        initialValues.put(SUBJECT_KEY, subject);
        initialValues.put(VALUE_KEY, value);
        initialValues.put(DATE_KEY, date);

        Toast.makeText(context, context.getString(R.string.toast_add_event), Toast.LENGTH_SHORT).show();
        sqLiteDatabase.insert(DATABASE_TABLE_EVENT, null, initialValues);
    }

    public void modifyEvent(int idEvent, String subject, String personName, String value) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(SUBJECT_KEY, Utils.camelCase(subject));
        initialValues.put(CONSIGNEE_KEY, Utils.camelCase(personName));
        initialValues.put(VALUE_KEY, value);

        Toast.makeText(context, context.getString(R.string.toast_modify), Toast.LENGTH_SHORT).show();
        sqLiteDatabase.update(DATABASE_TABLE_EVENT, initialValues, IDENTIFIER_KEY + "=" + idEvent, null);
    }

    public Cursor fetchAllEvents() {
        return sqLiteDatabase.query(DATABASE_TABLE_EVENT, new String[]{
                IDENTIFIER_KEY, CONSIGNEE_KEY, PARTICIPANT_NUMBER_KEY, SUBJECT_KEY, VALUE_KEY, DATE_KEY}, null, null, null, null, null);
    }

    public int fetchIdEvent(String subject, String date) {
        Cursor c = sqLiteDatabase.query(DATABASE_TABLE_EVENT, new String[]{
                IDENTIFIER_KEY, CONSIGNEE_KEY, PARTICIPANT_NUMBER_KEY, SUBJECT_KEY, VALUE_KEY, DATE_KEY}, SUBJECT_KEY + "='" + subject + "'AND " +
                DATE_KEY + "='" + date + "'", null, null, null, null);

        if (c != null && c.moveToFirst()) {
            return c.getInt(c.getColumnIndex(IDENTIFIER_KEY));
        }
        return 0;
    }

    public void updateParticipantNumber(int idEvent, int participantNumber) {
        ContentValues args = new ContentValues();
        args.put(PARTICIPANT_NUMBER_KEY, participantNumber);

        sqLiteDatabase.update(DATABASE_TABLE_EVENT, args, IDENTIFIER_KEY + "="
                + idEvent, null);
    }

    public void deleteEvent(int idEvent) {
        Toast.makeText(context, context.getString(R.string.toast_delete_event), Toast.LENGTH_SHORT).show();
        sqLiteDatabase.delete(DATABASE_TABLE_EVENT, IDENTIFIER_KEY + "=" + idEvent, null);
    }

    /**
     * *************
     * PARTICIPANT QUERIES
     * **************
     */
    public void createParticipant(int idPresent, String name, String phoneNumber, String budget) {
        ContentValues initialValues = new ContentValues();
        name = Utils.camelCase(name);

        if (fetchIdParticipant(idPresent, name) == 0) {
            initialValues.put(ID_EVENT_PARTICIPANT_KEY, idPresent);
            initialValues.put(NAME_KEY, name);
            initialValues.put(PHONE_NUMBER_KEY, phoneNumber);
            initialValues.put(BUDGET_KEY, budget);
            initialValues.put(PAID_KEY, 0);

            Toast.makeText(context, context.getString(R.string.toast_add_participant), Toast.LENGTH_SHORT).show();
            sqLiteDatabase.insert(DATABASE_TABLE_PARTICIPANT, null, initialValues);
        } else {
            Toast.makeText(context, context.getString(R.string.participant_already_present), Toast.LENGTH_SHORT).show();
        }
    }

    public Cursor fetchAllParticipants(int idPresent) {
        return sqLiteDatabase.query(DATABASE_TABLE_PARTICIPANT, new String[]{
                IDENTIFIER_KEY, ID_EVENT_PARTICIPANT_KEY, NAME_KEY, PHONE_NUMBER_KEY, BUDGET_KEY, PAID_KEY}, ID_EVENT_PARTICIPANT_KEY + "=" + idPresent, null, null, null, null);
    }

    public int fetchIdParticipant(int idPresent, String participantName) {
        Cursor c = sqLiteDatabase.query(DATABASE_TABLE_PARTICIPANT, new String[]{
                IDENTIFIER_KEY, ID_EVENT_PARTICIPANT_KEY, NAME_KEY, PHONE_NUMBER_KEY, BUDGET_KEY, PAID_KEY}, ID_EVENT_PARTICIPANT_KEY + "='" + idPresent + "' AND " +
                NAME_KEY + "='" + participantName + "'", null, null, null, null);

        if (c != null && c.moveToFirst()) {
            return c.getInt(c.getColumnIndex(IDENTIFIER_KEY));
        }
        return 0;
    }

    public void updateParticipantPaid(int idParticipant, int isPaid) {
        ContentValues args = new ContentValues();
        args.put(PAID_KEY, isPaid);

        sqLiteDatabase.update(DATABASE_TABLE_PARTICIPANT, args, IDENTIFIER_KEY + "="
                + idParticipant, null);
    }

    public void deleteParticipant(int idParticipant) {
        Toast.makeText(context, context.getString(R.string.toast_delete_participant), Toast.LENGTH_SHORT).show();
        sqLiteDatabase.delete(DATABASE_TABLE_PARTICIPANT, IDENTIFIER_KEY + "=" + idParticipant, null);
    }
}
