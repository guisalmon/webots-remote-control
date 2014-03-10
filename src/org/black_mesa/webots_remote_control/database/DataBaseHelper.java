package org.black_mesa.webots_remote_control.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataBaseHelper extends SQLiteOpenHelper{
	
	// Database creation sql statement
	private static final String DATABASE_CREATE = DataBaseContract.ServerTable.CREATE_TABLE;
	
	private static final String DATABASE_DELETE = DataBaseContract.ServerTable.DELETE_TABLE;
	
	public DataBaseHelper (Context context) {
		super(context, DataBaseContract.DATABASE_NAME, null, DataBaseContract.DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(DataBaseHelper.class.getName(),
		        "Upgrading database from version " + oldVersion + " to "
		            + newVersion + ", which will destroy all old data");
		db.execSQL(DATABASE_DELETE);
		onCreate(db);
	}
	
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
