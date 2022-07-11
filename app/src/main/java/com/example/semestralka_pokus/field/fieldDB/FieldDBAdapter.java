package com.example.semestralka_pokus.field.fieldDB;

import static com.example.semestralka_pokus.field.fieldDB.FieldTable.KEY_AREA;
import static com.example.semestralka_pokus.field.fieldDB.FieldTable.KEY_NAME;
import static com.example.semestralka_pokus.field.fieldDB.FieldTable.KEY_PERIM;
import static com.example.semestralka_pokus.field.fieldDB.FieldTable.TABLE_COLUMNS;
import static com.example.semestralka_pokus.field.fieldDB.FieldTable.TABLE_NAME;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import com.example.semestralka_pokus.field.Field;

import java.util.ArrayList;

/**
 * Trida pro praci s databazi poli.
 */
public class FieldDBAdapter
{
    /* Kontext alikace. */
    private final Context context;
    /* Instance databaze. */
    private SQLiteDatabase  db;

    /* Instance FieldDBHelper. */
    FieldDBHelper dbHelper;

    public FieldDBAdapter(Context context)
    {
        this.context = context;
    }

    /**
     * Otevre databazi.
     */
    public synchronized FieldDBAdapter open() throws SQLException
    {
        dbHelper = new FieldDBHelper(context);
        db = dbHelper.getWritableDatabase();
        return this;
    }

    /*
    public synchronized void close()
    {
        dbHelper.close();
    }*/

    /**
     * Prida do databaze radek.
     */
    public long createRow(Field field)
    {
        ContentValues values = createContentValue(field);
        return db.insert(TABLE_NAME, null, values);
    }

    /*public boolean updateRow(long rowIndex, Field field)
    {
        ContentValues values = createContentValue(field);

        return db.update(TABLE_NAME, values, KEY_ROWID + " = " + rowIndex, null) > 0;
    }*/

    /**
     * Odstrani radek s danym nazvem.
     */
    public boolean deleteRow(String name)
    {
        return db.delete(TABLE_NAME, KEY_NAME + " =? ", new String[]{name}) > 0;
    }

    /*public boolean deleteRow(long rowIndex)
    {
        return db.delete(TABLE_NAME, KEY_ROWID + " =?" + rowIndex, null) > 0;
    }

    public void deleteAllRows()
    {
        for(int i = 0; i < fetchAllEntries().getCount(); i++)
            deleteRow(i);
    }*/

    /**
     * Ziska hodnoty z databaze.
     */
    public Cursor fetchAllEntries()
    {
        return db.query(TABLE_NAME, TABLE_COLUMNS, null, null, null, null, null);
    }

    /*public Cursor fetchEntry(long rowIndex) throws SQLException
    {
        Cursor mCursor = db.query(true, TABLE_NAME, TABLE_COLUMNS, KEY_ROWID + " = " + rowIndex, null, null, null, null, null);
        if (mCursor != null)
        {
            mCursor.moveToFirst();
        }
        return mCursor;
    }*/

    /**
     * Preda do pole hodnoty z databaze.
     */
    public ArrayList<Field> fetchAllFields()
    {
        ArrayList<Field> res = new ArrayList<Field>();

        Cursor resultSet = fetchAllEntries();

        if (resultSet.moveToFirst())
            for(int i = 0; i < resultSet.getCount(); i++)
            {
                @SuppressLint("Range") String name = resultSet.getString(resultSet.getColumnIndex(KEY_NAME));
                @SuppressLint("Range") double area = Double.parseDouble(resultSet.getString(resultSet.getColumnIndex(KEY_AREA)));
                @SuppressLint("Range") double perimeter = Double.parseDouble(resultSet.getString(resultSet.getColumnIndex(KEY_PERIM)));

                Field c = new Field(name, area, perimeter);

                res.add(c);
                if(!resultSet.moveToNext())
                    break;
            }
        resultSet.close();
        return res;
    }

    /*public synchronized void reflectWith(ArrayList<Field> fields)
    {
        //      deleteAllRows();
        dbHelper.scratch(db);
        fields.trimToSize();
        Field empty = new Field();
        //empty.empty();

        for(Field f : fields)
        {
            if(!f.getName().equals(empty.getName()))
                createRow(f);   //if not empty, add it
        }
    }*/

    /**
     * Vytvori ContentValue pro vlozeni do databaze.
     */
    private ContentValues createContentValue(Field field)
    {
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, field.getName());
        values.put(KEY_AREA, Double.toString(field.getArea()));
        values.put(KEY_PERIM, Double.toString(field.getPerimeter()));
        return values;
    }
}
