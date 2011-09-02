package com.rouesnel.comp5047.healthyeater;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.RatingBar;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Laurence Rouesnel
 * Date: 1/09/11
 * Time: 3:06 PM
 */
public class Model {

  private static final String TABLE_NAME = "pictures";
  private static final String DATE_TAKEN = "date_taken";
  private static final String DATE_RATED = "date_rated";
  private static final String PICTURE_DATA = "photo_data";
  private static final String RATING = "rating";
  private static final String ROWID = "ROWID";

  private class DatabaseOpener extends SQLiteOpenHelper {
                  private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "healthyeater";

    private static final String CREATE_TABLE_SQL =
                "CREATE TABLE " + TABLE_NAME + " (" +
                    DATE_TAKEN + " DATETIME," +
                    PICTURE_DATA + " BLOB," +
                    DATE_RATED + " DATETIME" +
                    RATING + " STRING);";

    DatabaseOpener(Context context) {
      super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SQL);
    }

  @Override
  public void onUpgrade(SQLiteDatabase sqLiteDatabase, int previousVersion,
                        int currentVersion) {
  }

  }

  private SQLiteDatabase db;
  private Context context;

  public Model(Context context) {
    this.context = context;

  }

  public void Close() {
    db.close();
  }

  public void Open() {
    DatabaseOpener opener = new DatabaseOpener(context);
    db = opener.getWritableDatabase();
  }

  public long storePicture(byte[] jpegData) {
    ContentValues rowData = new ContentValues();
    rowData.put(DATE_TAKEN, DateFormat.getDateInstance().format(new Date()));
    rowData.put(PICTURE_DATA, jpegData);
    return db.insert(TABLE_NAME, null, rowData);
  }

  public long ratePicture(long pictureId, String rating) {
    ContentValues rowData = new ContentValues();
    rowData.put(DATE_RATED, DateFormat.getDateInstance().format(new Date()));
    rowData.put(RATING, rating);
    return db.update(TABLE_NAME, rowData, ROWID + "=" + pictureId, null);
  }

  public byte[] getPicture(long pictureId) {
    Cursor c = db.query(TABLE_NAME, new String[] { PICTURE_DATA },
        null, null, null, null, null, "1");

    // TODO(laurencer): add in error handling.
    c.moveToFirst();
    return c.getBlob(0);
  }

}
