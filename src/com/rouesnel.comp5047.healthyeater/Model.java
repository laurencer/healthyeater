package com.rouesnel.comp5047.healthyeater;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.AndroidRuntimeException;
import android.widget.RatingBar;

import java.io.*;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
  private static final String FILENAME = "photo_data";
  private static final String RATING = "rating";
  private static final String ROWID = "ROWID";
  private static final String FILE_EXTENSION = ".jpg";

  private class DatabaseOpener extends SQLiteOpenHelper {
                  private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "healthyeater";

    private static final String CREATE_TABLE_SQL =
                "CREATE TABLE " + TABLE_NAME + " (" +
                    DATE_TAKEN + " INTEGER, " +
                    FILENAME + " STRING, " +
                    DATE_RATED + " INTEGER, " +
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

  public class Picture {
    private long id;
    private File file;
    private Model model;

    public Picture(Model model, long id) {
      this.id = id;
      this.file = new File(storageDirectory, String.valueOf(id) +
          FILE_EXTENSION);
      this.model = model;
    }

    public File getFile() {
      return file;
    }

    public byte[] getBytes() {
      try {
        FileInputStream stream = new FileInputStream(file);

        // Check we can fit the file in memory.
        int length = (int)file.length();
        if (file.length() > Integer.MAX_VALUE) {
          throw new AndroidRuntimeException("Image too large to load.");
        }

        byte[] buffer = new byte[length];
        stream.read(buffer);
        return buffer;

      } catch (IOException ex) {
        throw new AndroidRuntimeException(ex);
      }
    }

    public Bitmap getBitmap() {
      byte[] bytes = getBytes();
      return BitmapFactory.decodeByteArray(bytes, 0,
          bytes.length);
    }

    public long getId() {
      return id;
    }

    public String getRating() {
      model.open();

      String rating = model.getPictureRating(id);

      model.close();

      return rating;
    }

    public void setRating(String rating) {
      model.open();

      model.ratePicture(id, rating);

      model.close();
    }

    public void retake() {
      model.open();

      model.deletePicture(id);

      model.close();

    }

    public void delete() {
      model.open();

      model.deletePicture(id);

      model.close();
    }

    public Date getDateTaken() {
      model.open();
      Date date = model.getPictureTime(id);
      model.close();

      return date;
    }
  }

  private SQLiteDatabase db;
  private Context context;

  private File storageDirectory;

  public Model(Context context) {

    this.context = context;

    // Check if we can access the external storage.
    String storageState = Environment.getExternalStorageState();
    if (!Environment.MEDIA_MOUNTED.equals(storageState)) {
      // TODO: verify if this is correct behaviour.
      throw new AndroidRuntimeException("Cannot access storage");
    }

    storageDirectory = context.getExternalFilesDir(null);
  }

  public void close() {
    db.close();
  }

  public void open() {
    DatabaseOpener opener = new DatabaseOpener(context);
    db = opener.getWritableDatabase();
  }

  private long getToday() {
    Date today = new Date();
    today.setHours(0);
    today.setMinutes(0);
    return today.getTime();
  }

  public List<Picture> getTodaysPictures() {
    Cursor c = db.query(TABLE_NAME, new String[] { ROWID },
        DATE_TAKEN + " > " + getToday(), null, null, null,
        DATE_TAKEN + " desc");

    List<Picture> pictures = new ArrayList<Picture>();
    while (c.moveToNext()) {
      pictures.add(new Picture(this, c.getLong(0)));
    }
    
    c.close();
    return pictures;
  }

  public long storePicture(byte[] jpegData) {
    ContentValues rowData = new ContentValues();
    rowData.put(DATE_TAKEN, new Date().getTime());
    long id = db.insert(TABLE_NAME, null, rowData);

    // Save the photo in external storage.
    File file = new File(storageDirectory, String.valueOf(id) + FILE_EXTENSION);
    try {
      file.createNewFile();
      FileOutputStream stream = new FileOutputStream(file);
      stream.write(jpegData);
      stream.close();
    } catch (IOException ex) {
      throw new AndroidRuntimeException(ex);
    }
    
    return id;
  }

  public Picture getPicture(long id) {
    return new Picture(this, id);
  }

  public String getPictureRating(long pictureId) {
    Cursor c = db.query(TABLE_NAME, new String[] { RATING },
        ROWID + "=" + pictureId, null, null, null,
        null, "1");

    c.moveToFirst();

    return c.getString(0);
  }

  public void deletePicture(long id) {
    File file = new File(storageDirectory, String.valueOf(id) + FILE_EXTENSION);
    file.delete();
    db.delete(TABLE_NAME, ROWID + "=" + id, null);
  }

  public Date getPictureTime(long id) {
    Cursor c = db.query(TABLE_NAME, new String[] { DATE_TAKEN },
        ROWID + "=" + id, null, null, null,
        null, "1");

    c.moveToFirst();

    long time = c.getLong(0);
    return new Date(time);
  }

  public long ratePicture(long pictureId, String rating) {
    ContentValues rowData = new ContentValues();
    rowData.put(DATE_RATED, new Date().getTime());
    rowData.put(RATING, rating);
    return db.update(TABLE_NAME, rowData, ROWID + "=" + pictureId, null);
  }
}
