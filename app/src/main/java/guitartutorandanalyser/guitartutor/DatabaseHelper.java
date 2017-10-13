package guitartutorandanalyser.guitartutor;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static String DB_PATH;
    private static String DB_NAME = "guitarTutorDB.db";
    private final Context context;

    public SQLiteDatabase myDataBase;

    public DatabaseHelper(Context context) {

        super(context, DB_NAME, null, 1);
        this.context = context;
        DB_PATH = this.context.getDatabasePath(DB_NAME).toString();
    }


    //Create an empty database on the system and rewrite it from assets database.

    public void createDataBase() throws IOException {

        if (!checkDataBase()) {
            //Empty database will be created on the default system path of application then overwrite database with our database.
            this.getWritableDatabase();
            try {
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
        // else do nothing - database already exist
    }

    //Check if the database already exist to avoid re-copying the file each time you open the application.

    public boolean checkDataBase() {

        SQLiteDatabase checkDB = null;

        try {

            checkDB = SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {

        }

        if (checkDB != null) {

            checkDB.close();
            return true;
        }

        return false;
    }

    // Copy database from assets to the system into the empty database

    private void copyDataBase() throws IOException {

        InputStream myInput = context.getAssets().open(DB_NAME);

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(DB_PATH);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    public void openDataBase() throws SQLException {

        myDataBase = SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
    }

    @Override
    public synchronized void close() {

        if (myDataBase != null)
            myDataBase.close();

        super.close();
    }

    public boolean updateDatabase(int id, String date, String time, float real) {

        ContentValues contentValues = new ContentValues();
        contentValues.put("_id", id);
        contentValues.put("DATE", date);
        contentValues.put("TIME", time);
        contentValues.put("HEIGHT", real);

        try {
            myDataBase.update("mytable", contentValues, "_id = ?", new String[]{String.valueOf(id)});

        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}