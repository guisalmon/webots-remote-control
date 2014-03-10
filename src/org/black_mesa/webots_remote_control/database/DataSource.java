package org.black_mesa.webots_remote_control.database;

import java.util.ArrayList;
import java.util.List;

import org.black_mesa.webots_remote_control.classes.Server;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DataSource {

	private SQLiteDatabase database;
	private DataBaseHelper dbHelper;
	private String[] allServerColumns = { DataBaseContract.ServerTable._ID,
			DataBaseContract.ServerTable.NAME,
			DataBaseContract.ServerTable.ADRESS,
			DataBaseContract.ServerTable.PORT };

	public DataSource(Context context) {
		dbHelper = new DataBaseHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}
	
	public Server createServer(String name, String adress, int port) {
		ContentValues values = new ContentValues();
		values.put(DataBaseContract.ServerTable.NAME, name);
		values.put(DataBaseContract.ServerTable.ADRESS, adress);
		values.put(DataBaseContract.ServerTable.PORT, port);
		long insertId = database.insert(DataBaseContract.ServerTable.TABLE_NAME,
				null, values);
		Cursor cursor = database.query(DataBaseContract.ServerTable.TABLE_NAME,
				allServerColumns, DataBaseContract.ServerTable._ID + " = "
						+ insertId, null, null, null, null);
		cursor.moveToFirst();
		Server newServer = cursorToServer(cursor);
		cursor.close();
		return newServer;

	}

	public void deleteServer(Server server) {
		long id = server.getId();
		database.delete(DataBaseContract.ServerTable.TABLE_NAME,
				DataBaseContract.ServerTable._ID + " = " + id, null);
	}

	public List<Server> getAllServers() {
		List<Server> servers = new ArrayList<Server>();
		Cursor cursor = database.query(DataBaseContract.ServerTable.TABLE_NAME,
				allServerColumns, null, null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Server server = cursorToServer(cursor);
			servers.add(server);
			cursor.moveToNext();
		}
		cursor.close();
		return servers;
	}

	private Server cursorToServer(Cursor cursor) {
		return new Server(cursor.getLong(0), cursor.getString(1), cursor.getString(2), cursor.getInt(3));
	}
}
