package com.example.semestralka_pokus.field.fieldDB;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class FieldTable {

    /* id pole. */
    public static final String KEY_ROWID = "id";
    /* Nazev pole. */
    public static final String KEY_NAME = "filed_name";
    /* Obsah pole. */
    public static final String KEY_AREA = "area";
    /* Obvod pole. */
    public static final String KEY_PERIM = "perim";
    /* Nazev tabulky. */
    public static final String TABLE_NAME = "fields";

    public static final String[] TABLE_COLUMNS = {KEY_ROWID, KEY_NAME, KEY_AREA, KEY_PERIM};     //public makes it more useful
    private static final String[] TABLE_COLTYPES = {"INTEGER PRIMARY KEY AUTOINCREMENT", "TEXT", "TEXT", "TEXT"};

    /* Retezec pro vytvoreni tabulky - SQL. */
    private static final String TABLE_CREATE = "CREATE TABLE " + TABLE_NAME + "("
            + TABLE_COLUMNS[0] + " " + TABLE_COLTYPES[0] + ","
            + TABLE_COLUMNS[1] + " " + TABLE_COLTYPES[1] + ","
            + TABLE_COLUMNS[2] + " " + TABLE_COLTYPES[2] + ","
            + TABLE_COLUMNS[3] + " " + TABLE_COLTYPES[3] + ");";

    private static final String LOGTAG = "ContactTable";

    /**
     * Vytvori databazi.
     */
    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(TABLE_CREATE);
    }

    /**
     * Upgraduje databazi.
     */
    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w(LOGTAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }

    /*public static void scratch(SQLiteDatabase database) {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        database.execSQL(TABLE_CREATE);
    }*/
}
