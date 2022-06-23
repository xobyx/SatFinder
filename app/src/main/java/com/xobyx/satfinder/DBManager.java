package com.xobyx.satfinder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.xobyx.satfinder.base.ChannelBase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DBManager {
    private static DBManager DBM;
    private SQLiteDatabase SDB;
    private onDBChange onChange;
    public interface onDBChange
    {
        void DBchange();
    }
    public void setOnChangeListener(onDBChange a)
    {
        onChange=a;

    }
    public DBManager(Context context) {
        this.SDB = DBHelper.getDB(context);
        if(!this.isChannels_available()) {
            DBHelper.CloseDB();
            boolean delete = new File(context.getFilesDir().getAbsolutePath() + "/satellite.db").delete();
            this.SDB = DBHelper.getDB(context);
        }
    }

    public static DBManager getInctance(Context context) {
        if(DBManager.DBM == null) {
            DBManager.DBM = new DBManager(context.getApplicationContext());
        }

        return DBManager.DBM;
    }

    public int InsertSatellite(Satellite sat) {
        int rowId;
        ContentValues values = new ContentValues();
        if(sat.satelite_id > 0) {
            values.put("satellite_id", sat.satelite_id);
        }

        values.put("name", sat.name);
        values.put("position", sat.position);
        values.put("preset", sat.preset);
        values.put("lnb_freq", sat.sLNB_Freq);
        values.put("lnb_22k", sat.sLNB_22k);
        values.put("lnb_diseqc", sat.sLNB_Disq);
        values.put("preset", sat.preset);
        this.SDB.beginTransaction();
        try {
            rowId = (int)this.SDB.insert("satellite", null, values);
            this.SDB.setTransactionSuccessful();
        }
        catch(Throwable throwable) {
            this.SDB.endTransaction();
            throw throwable;
        }

        this.SDB.endTransaction();
        if(onChange!=null)onChange.DBchange();
        return rowId;
    }

    public int InsertTransponder(Transponder trans) {
        int rowId;
        ContentValues values = new ContentValues();
        values.put("satellite_id", trans.satellite_id);
        values.put("frequency", trans.mFrequency);
        values.put("symbol_rate", trans.mSymbolRate);
        values.put("polization", trans.mPolization);
        this.SDB.beginTransaction();
        try {
            rowId = (int)this.SDB.insert("transponder", null, values);
            this.SDB.setTransactionSuccessful();
        }
        catch(Throwable throwable) {
            this.SDB.endTransaction();
            throw throwable;
        }

        this.SDB.endTransaction();
        if(onChange!=null)onChange.DBchange();
        trans.tpId = rowId;
        return rowId;
    }

    public void deleteAllTransponderChannelsForSatId(int sat_id) {
        String name = SatEditActivity.class.getSimpleName();
        List<Transponder> transponders = this.GetTransponderList(sat_id);
        if(transponders == null) {
            Log.d(name, "tps size: 0");
            return;
        }

        Log.d(name, "tps size:" + transponders.size());
        for(Transponder transponder: transponders) {
            this.DeleteTransponder_Channels(transponder);
        }
        if(onChange!=null)onChange.DBchange();
    }

    public void InsertTransponderChannels(int tp_id, List<ChannelBase> channels) {
        this.SDB.beginTransaction();
        try {
            this.SDB.delete("channel", "tp_id =?", new String[]{String.valueOf(tp_id)});
            for(ChannelBase channelBase : channels) {
                ContentValues values = new ContentValues();
                values.put("tp_id", tp_id);
                values.put("channel_name", channelBase.getChannelName());
                values.put("ctype", channelBase.getChannelType());
                this.SDB.insert("channel", null, values);
            }

            this.SDB.setTransactionSuccessful();
        }
        catch(Throwable throwable) {
            this.SDB.endTransaction();
            throw throwable;
        }
        if(onChange!=null)onChange.DBchange();
        this.SDB.endTransaction();
    }

    public boolean isChannels_available() {
        Cursor cursor = this.SDB.rawQuery("select * from sqlite_master WHERE name = \"channel\"", null);
        if(cursor != null) {
            boolean moveToFirst = cursor.moveToFirst();
            int i = 0;
            while(moveToFirst) {
                ++i;
                moveToFirst = cursor.moveToNext();
            }

            cursor.close();
            return i > 0;
        }

        return false;
    }

    Satellite getSatellite_db(int sat_id) {
        Cursor cursor = this.SDB.rawQuery("select * from satellite where satellite_id=?", new String[]{String.valueOf(sat_id)});
        if(cursor != null && (cursor.moveToFirst())) {
            Satellite satellite = new Satellite();
            satellite.satelite_id = cursor.getInt(cursor.getColumnIndex("satellite_id"));
            satellite.name = cursor.getString(cursor.getColumnIndex("name"));
            satellite.position = (float)cursor.getInt(cursor.getColumnIndex("position"));
            satellite.preset = cursor.getInt(cursor.getColumnIndex("preset"));
            satellite.sLNB_22k = cursor.getInt(cursor.getColumnIndex("lnb_22k"));
            satellite.sLNB_Freq = cursor.getInt(cursor.getColumnIndex("lnb_freq"));
            satellite.sLNB_Disq = cursor.getInt(cursor.getColumnIndex("lnb_diseqc"));
            satellite.isFav= cursor.getInt(cursor.getColumnIndex("fav"))==1;
            satellite.mTransponders = this.GetTransponderList(satellite.satelite_id);
            cursor.close();
            return satellite;
        }

        return null;
    }

    public void Close() {
        DBHelper.CloseDB();
        if(DBManager.DBM != null) {
            DBManager.DBM = null;
        }
    }

    public void DeleteSatellite_db(Satellite satellite) {
        this.SDB.beginTransaction();
        try {
            this.SDB.delete("satellite", "satellite_id =?", new String[]{String.valueOf(satellite.satelite_id)});
            this.SDB.setTransactionSuccessful();
        }
        catch(Throwable throwable) {
            this.SDB.endTransaction();
            throw throwable;
        }

        this.SDB.endTransaction();
        if(onChange!=null)onChange.DBchange();
    }

    public void DeleteTransponder_Channels(Transponder trans) {
        this.SDB.beginTransaction();
        try {
            this.SDB.delete("channel", "tp_id =?", new String[]{String.valueOf(trans.tpId)});
            this.SDB.delete("transponder", "satellite_id =? and frequency =?", new String[]{String.valueOf(trans.satellite_id), String.valueOf(trans.mFrequency)});
            this.SDB.setTransactionSuccessful();
        }
        catch(Throwable throwable) {
            this.SDB.endTransaction();
            throw throwable;
        }

        this.SDB.endTransaction();
        if(onChange!=null)onChange.DBchange();
    }

    int GetFirstSatellite_id() {
        Cursor cursor = this.SDB.rawQuery("select satellite_id from satellite order by satellite_id desc limit 1", null);
        if(cursor != null && (cursor.moveToFirst())) {
            int satellite_id = cursor.getInt(cursor.getColumnIndex("satellite_id"));
            cursor.close();
            return satellite_id;
        }

        return 0;
    }

    public List<Transponder> GetTransponderList(int sat_id) {
        ArrayList<Transponder> list = new ArrayList<>();
        Cursor cursor = this.SDB.rawQuery("select * from transponder where satellite_id = ? order by polization", new String[]{String.valueOf(sat_id)});
        if(cursor != null && (cursor.moveToFirst())) {
            do {
                Transponder trans = new Transponder();
                trans.satellite_id = sat_id;
                trans.mFrequency = cursor.getInt(cursor.getColumnIndex("frequency"));
                trans.mSymbolRate = cursor.getInt(cursor.getColumnIndex("symbol_rate"));
                trans.mPolization = cursor.getInt(cursor.getColumnIndex("polization"));
                trans.tpId = cursor.getInt(cursor.getColumnIndex("tp_id"));
                trans.fav =cursor.getInt(cursor.getColumnIndex("fav"));
                trans.Channels = null;
                list.add(trans);
            }
            while(cursor.moveToNext());

            cursor.close();
            return list;
        }
        if (cursor != null) {
            cursor.close();
        }
        return null;
    }

    public boolean isContainSatellite(Satellite pSat) {
        Cursor cursor = this.SDB.rawQuery("select * from satellite where satellite_id=?", new String[]{String.valueOf(pSat.satelite_id)});
        if(cursor != null) {
            int count = cursor.getCount();
            cursor.close();
            return count != 0;
        }

        return false;
    }

    public boolean isContainTransponder(Transponder trans) {
        String[] param = new String[3];
        boolean found = false;
        param[0] = String.valueOf(trans.satellite_id);
        param[1] = String.valueOf(trans.mFrequency);
        param[2] = String.valueOf(trans.mPolization);
        Cursor cursor = this.SDB.rawQuery("select * from transponder where satellite_id=? and frequency=?and polization=?", param);
        if(cursor != null && cursor.getCount() != 0) {
            found = true;
        }

        if(found) {
            cursor.moveToFirst();
            trans.tpId = cursor.getInt(cursor.getColumnIndex("tp_id"));
            cursor.close();
        }

        return found;
    }

    int d(Satellite satellite) {
        Cursor cursor = this.SDB.rawQuery("SELECT COUNT(*) from satellite where preset=0", null);
        int i = 0;
        int count = cursor.moveToFirst() ? cursor.getInt(cursor.getColumnIndex("COUNT(*)")) : 0;
        Cursor cursor1 = this.SDB.rawQuery("SELECT COUNT(*) from satellite where preset=1", null);
        if(cursor1.moveToFirst()) {
            cursor1.getInt(cursor1.getColumnIndex("COUNT(*)"));
        }
        cursor.close();
        cursor1.close();
        Cursor cursor2 = this.SDB.rawQuery(satellite.preset == 1 ? "SELECT COUNT(*) from satellite where preset=1 and satellite_id<" + satellite.satelite_id : "SELECT COUNT(*) from satellite where preset=0 and satellite_id<" + satellite.satelite_id, null);
        if(cursor2.moveToFirst()) {
            i = cursor2.getInt(cursor2.getColumnIndex("COUNT(*)"));
        }

        int i1 = satellite.preset == 0 ? count - i - 1 : count + i;
        cursor2.close();
        return i1;
    }

    public List<Satellite> GetAllSatellites() {
        ArrayList<Satellite> list = new ArrayList<>();
        Cursor cursor = this.SDB.rawQuery("select * from satellite where preset=0 ORDER BY satellite_id DESC", null);
         while (cursor != null && (cursor.moveToFirst())) {
            Satellite satellite = new Satellite();
            satellite.satelite_id = cursor.getInt(cursor.getColumnIndex("satellite_id"));
            satellite.name = cursor.getString(cursor.getColumnIndex("name"));
            satellite.position = (float)cursor.getInt(cursor.getColumnIndex("position"));
            satellite.preset = cursor.getInt(cursor.getColumnIndex("preset"));
            satellite.sLNB_22k = cursor.getInt(cursor.getColumnIndex("lnb_22k"));
            satellite.sLNB_Freq = cursor.getInt(cursor.getColumnIndex("lnb_freq"));
            satellite.sLNB_Disq = cursor.getInt(cursor.getColumnIndex("lnb_diseqc"));
            satellite.isFav= cursor.getInt(cursor.getColumnIndex("fav"))==1;
            satellite.mTransponders = null;
            list.add(satellite);
        }
        if (cursor!=null)cursor.close();
        ArrayList<Satellite> list1 = new ArrayList<>();
        Cursor cursor1 = this.SDB.rawQuery("select * from satellite where preset=1 ORDER BY satellite_id ASC", null);
        if(cursor1 != null && (cursor1.moveToFirst())) {
            do {
                Satellite satellite = new Satellite();
                satellite.satelite_id = cursor1.getInt(cursor1.getColumnIndex("satellite_id"));
                satellite.name = cursor1.getString(cursor1.getColumnIndex("name"));
                satellite.position = (float)cursor1.getInt(cursor1.getColumnIndex("position"));
                satellite.sLNB_22k = cursor1.getInt(cursor1.getColumnIndex("lnb_22k"));
                satellite.sLNB_Freq = cursor1.getInt(cursor1.getColumnIndex("lnb_freq"));
                satellite.sLNB_Disq = cursor1.getInt(cursor1.getColumnIndex("lnb_diseqc"));
                satellite.preset = cursor1.getInt(cursor1.getColumnIndex("preset"));
                satellite.isFav= cursor1.getInt(cursor.getColumnIndex("fav"))==1;
                satellite.mTransponders = null;
                if("OBY TV".equals(satellite.name)) {
                    list1.add(0, satellite);
                }
                else {
                    list1.add(satellite);
                }
            }
            while(cursor1.moveToNext());

            cursor1.close();
            list.addAll(list1);
            return list;
        }

        return null;
    }

    public List<ChannelBase> getTransponderChannels(int tp_id) {
        ArrayList<ChannelBase> list = new ArrayList<>();
        Cursor cursor = this.SDB.rawQuery("select * from channel where tp_id=?", new String[]{String.valueOf(tp_id)});
        if(cursor != null && (cursor.moveToFirst())) {
            do {
                int channel_name = cursor.getColumnIndex("channel_name");
                int ctype = cursor.getColumnIndex("ctype");
                ChannelBase n =new ChannelBase(cursor.getString(channel_name),(byte) cursor.getInt(ctype));

                list.add(n);
            }
            while(cursor.moveToNext());

            cursor.close();
            return list;
        }
        if (cursor != null) {
            cursor.close();
        }
        return null;
    }

    public void UpdateTransponder(Transponder transponder) {
        ContentValues values = new ContentValues();
        values.put("satellite_id", transponder.satellite_id);
        values.put("frequency", transponder.mFrequency);
        values.put("symbol_rate", transponder.mSymbolRate);
        values.put("polization", transponder.mPolization);
        this.SDB.beginTransaction();
        try {
            this.SDB.update("transponder", values, "tp_id =?", new String[]{String.valueOf(transponder.tpId)});
            this.SDB.setTransactionSuccessful();
        }
        catch(Throwable throwable) {
            this.SDB.endTransaction();
            throw throwable;
        }

        this.SDB.endTransaction();
    }
    public void UpdateTransponder_fav(Transponder trans) {
        ContentValues values = new ContentValues();

        values.put("fav", trans.fav);
        this.SDB.beginTransaction();
        try {
            this.SDB.update("transponder", values, "tp_id =?", new String[]{String.valueOf(trans.tpId)});
            this.SDB.setTransactionSuccessful();
        }
        catch(Throwable throwable) {
            this.SDB.endTransaction();
            throw throwable;
        }

        this.SDB.endTransaction();
    }

    public int Check_UpdateTransponder(Transponder transponder) {
        int tpId = transponder.tpId;
        if(this.isContainTransponder(transponder)) {
            this.UpdateTransponder(transponder);
            return tpId;
        }

        return this.InsertTransponder(transponder);
    }

    public void update_satellite(Satellite sat) {

        ContentValues values = new ContentValues();
        values.put("lnb_freq", sat.sLNB_Freq);
        values.put("lnb_22k", sat.sLNB_22k);
        values.put("lnb_diseqc", sat.sLNB_Disq);
        this.SDB.beginTransaction();
        try {
            this.SDB.update("satellite", values, "satellite_id = ?", new String[]{String.valueOf(sat.satelite_id)});
            this.SDB.setTransactionSuccessful();
        }
        catch(Throwable throwable) {
            this.SDB.endTransaction();
            throw throwable;
        }
        if(onChange!=null)onChange.DBchange();
        this.SDB.endTransaction();
    }

    public void update_satellite_fav(Satellite satellite) {
        ContentValues values = new ContentValues();
        values.put("fav", satellite.isFav?1:0);

        this.SDB.beginTransaction();
        try {
            this.SDB.update("satellite", values, "satellite_id = ?", new String[]{String.valueOf(satellite.satelite_id)});
            this.SDB.setTransactionSuccessful();
        }
        catch(Throwable throwable) {
            this.SDB.endTransaction();
            throw throwable;
        }
        if(onChange!=null)onChange.DBchange();
        this.SDB.endTransaction();
    }
    public void update_satllite_db(Satellite satellite) {
        ContentValues values = new ContentValues();
        values.put("name", satellite.name);
        values.put("position", satellite.position);
        values.put("preset", satellite.preset);
        values.put("lnb_freq", satellite.sLNB_Freq);
        values.put("lnb_22k", satellite.sLNB_22k);
        values.put("lnb_diseqc", satellite.sLNB_Disq);
        values.put("preset", satellite.preset);
        values.put("fav", satellite.isFav ? 1 : 0);
        this.SDB.beginTransaction();
        try {
            this.SDB.update("satellite", values, "satellite_id = ?", new String[]{String.valueOf(satellite.satelite_id)});
            this.SDB.setTransactionSuccessful();
        }
        catch(Throwable throwable) {
            this.SDB.endTransaction();
            throw throwable;
        }
        if(onChange!=null)onChange.DBchange();
        this.SDB.endTransaction();
    }

    public int update_check_satellite(Satellite satellite) {
        int id = satellite.satelite_id;
        if(this.isContainSatellite(satellite)) {
            this.update_satllite_db(satellite);
            return id;
        }

        return this.InsertSatellite(satellite);
    }
}

