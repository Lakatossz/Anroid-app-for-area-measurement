package com.example.semestralka_pokus.field.fieldDB;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Trida pro praci s databazi - ne primo zapis.
 */
public class FieldDBHelper extends SQLiteOpenHelper
{
    private static final String PACKAGE_NAME = "com.test.demo";

    @SuppressLint("SdCardPath")
    private static final String DATABASE_PATH = "/data/data/" + PACKAGE_NAME + "/databases/";
    private static final String DATABASE_NAME = "contactdata";
    private static final int DATABASE_VERSION = 1;

    private final Context myContext;

    public FieldDBHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        myContext = context;
    }

    /**
     * Vytvori databazi.
     */
    @Override
    public void onCreate(SQLiteDatabase database)
    {
        FieldTable.onCreate(database);
    }

    /**
     * Updgraduje databazi.
     */
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
    {
        FieldTable.onUpgrade(database, oldVersion, newVersion);
    }

    /*public void scratch(SQLiteDatabase database) {
        FieldTable.scratch(database);
    }*/

    /*public void createDataBase() throws IOException {
        boolean dbExist = checkDataBase();
        if (!dbExist) {

            File dirFile = new File(DATABASE_PATH);
            if (!dirFile.exists()) {
                dirFile.mkdir();
            }

            this.getReadableDatabase();

            try {
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }*/

    /*private boolean checkDataBase() {
        SQLiteDatabase checkDB = null;
        try
        {
            String myPath = DATABASE_PATH + DATABASE_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e)
        {
            throw new Error("Error not existing database");
        }
        if (checkDB != null) checkDB.close();

        return checkDB != null;
    }*/

    /*private void copyDataBase() throws IOException {

        // Open your local db as the input stream
        InputStream myInput = myContext.getAssets().open(DATABASE_NAME);

        // Path to the just created empty db
        String outFileName = DATABASE_PATH + DATABASE_NAME;

        // Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        // transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0)
        {
            myOutput.write(buffer, 0, length);
        }

        // Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }*/

    /*public void openDataBase() throws SQLException {

        // Open the database
        String myPath = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

    }*/
}
