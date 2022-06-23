package com.xobyx.satfinder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.InputStream;

public class DBHelper extends SQLiteOpenHelper {
    private static final String d = "d";
    private static SQLiteDatabase mDB = null;
    private static final String DB_FILENAME = "satellite.db";
    private static String DB_PATH = null;

    public DBHelper(Context arg4) {
        super(arg4, DBHelper.DB_PATH, null, 3);
    }

    public static SQLiteDatabase getDB(Context context) {
        Class dbHelperClass = DBHelper.class;
        synchronized (dbHelperClass) {
            DBHelper.DB_PATH = context.getFilesDir().getAbsolutePath() + "/" + DBHelper.DB_FILENAME;
            DBHelper.TryLoadDB_Assest_File(context);
            if (DBHelper.mDB == null) {
                DBHelper.mDB = new DBHelper(context.getApplicationContext()).getWritableDatabase();
            }

        }

        return DBHelper.mDB;
    }

    public static void TryLoadDB_Assest_File(Context arg4) {
        SQLiteDatabase sqLiteDatabase;
        InputStream AssetStremFile;
        FileOutputStream fileOutputStream;
        Log.i(DBHelper.d, "data dir " + arg4.getFilesDir().getAbsolutePath());
        String path = arg4.getFilesDir().getAbsolutePath() + "/" + DBHelper.DB_FILENAME;
        try {
            sqLiteDatabase = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException unused_ex) {
            Log.i(DBHelper.d, "db not exist");
            sqLiteDatabase = null;

        }

        if (sqLiteDatabase != null) {
            sqLiteDatabase.close();
            return;
        }
        try {
            AssetStremFile = arg4.getAssets().open(DBHelper.DB_FILENAME);
            fileOutputStream = new FileOutputStream(path);
            byte[] bytes = new byte[1024];
            while (true) {
                int i = AssetStremFile.read(bytes);
                if (i <= 0) {
                    break;
                }

                fileOutputStream.write(bytes, 0, i);
            }

            fileOutputStream.flush();

            AssetStremFile.close();

            fileOutputStream.close();


        } catch (Throwable ignored) {
        }


    }

    public static void CloseDB() {
        if (DBHelper.mDB != null) {
            DBHelper.mDB.close();
            DBHelper.mDB = null;
        }
    }

    private void insertOrbyTvSatTransponder(SQLiteDatabase satelliteDB) {
        Log.d(DBHelper.d, "upgrade version to 2");
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", "OBY TV");
        contentValues.put("position", -1168);

        contentValues.put("preset", 0);
        contentValues.put("lnb_freq", 12);

        contentValues.put("lnb_22k", 1);
        contentValues.put("lnb_diseqc", 0);
        contentValues.put("preset", 1);
        long id = satelliteDB.insert("satellite", null, contentValues);
        if (id == -1L) {
            Log.i(DBHelper.d, "insert OBY TV failed");
        } else {
            ContentValues values = new ContentValues();
            values.put("satellite_id", id);
            values.put("frequency", 11940000);
            values.put("symbol_rate", 30000000);
            values.put("polization", 1);
            if (satelliteDB.insert("transponder", null, values) == -1L) {
                Log.d(DBHelper.d, "insert transId failed");

            }
        }
    }

    @Override  // android.database.sqlite.SQLiteOpenHelper
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists satellite (satellite_id integer primary key autoincrement, name text, position integer,preset integer,fav integer default 0)");
        db.execSQL("create table if not exists transponder (satellite_id integer,frequency integer, symbol_rate integer,polization integer,fav integer default 0)");
    }

    @Override  // android.database.sqlite.SQLiteOpenHelper
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }

    @Override  // android.database.sqlite.SQLiteOpenHelper
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(DBHelper.d, "onUpgrade oldVersion:" + oldVersion + "\t newVersion:" + newVersion);
        while (oldVersion < newVersion) {
            if (oldVersion == 1) {
                this.insertOrbyTvSatTransponder(db);
            } else if (oldVersion == 2) {
                Log.d(DBHelper.d, "update db from 2 to 3");
                db.beginTransaction();
                Cursor v2 = db.rawQuery("select count(*) from satellite where satellite_id>=(select satellite_id from satellite where satellite_id not in(select distinct satellite_id from transponder))", null);
                if (v2 != null) {
                    v2.moveToFirst();
                    int v4 = v2.getInt(0);
                    if (v4 == 1) {
                        Log.d(DBHelper.d, "新增的卫星只有1个，则将更新TP，将TP的卫星ID减掉1即可");
                        Cursor v0 = db.rawQuery("select count(*) from transponder where satellite_id>(select satellite_id from satellite where satellite_id not in(select distinct satellite_id from transponder))", null);
                        if (v0 != null) {
                            v2.moveToFirst();
                            if (v2.getInt(0) > 0) {
                                db.execSQL("update transponder set satellite_id=satellite_id-1 where satellite_id>(select distinct satellite_id from satellite where satellite_id not in(select distinct satellite_id from transponder))");
                            } else {
                                db.execSQL("delete from satellite where satellite_id not in(select distinct satellite_id from transponder)");
                            }

                            v0.close();
                        }
                    } else if (v4 > 1) {
                        Log.d(DBHelper.d, "新增的卫星超过1个，直接删除没有tp的卫星");
                        db.execSQL("delete from satellite where satellite_id not in(select distinct satellite_id from transponder)");
                    }

                    v2.close();
                }

                db.setTransactionSuccessful();
                db.endTransaction();
            }

            ++oldVersion;
        }
    }
}

