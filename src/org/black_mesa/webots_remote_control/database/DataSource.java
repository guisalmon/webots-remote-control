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
	private String[] allServerColumns = { DataBaseContract.ServerTable._ID, DataBaseContract.ServerTable.NAME,
			DataBaseContract.ServerTable.ADRESS, DataBaseContract.ServerTable.PORT };

	public DataSource(Context context) {
		dbHelper = new DataBaseHelper(context);
	}

	/**
	 * Opens the database or throws an exception
	 * @throws SQLException
	 */
	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	/**
	 * Closes the database
	 */
	public void close() {
		dbHelper.close();
	}

	/**
	 * Creates a new Server and inserts it into the database
	 * @param name of the Server
	 * @param adress of the Server
	 * @param port of the Server
	 * @return created Server
	 */
	public Server createServer(String name, String adress, int port) {
		ContentValues values = new ContentValues();
		values.put(DataBaseContract.ServerTable.NAME, name);
		values.put(DataBaseContract.ServerTable.ADRESS, adress);
		values.put(DataBaseContract.ServerTable.PORT, port);
		long insertId = database.insert(DataBaseContract.ServerTable.TABLE_NAME, null, values);
		Cursor cursor = database.query(DataBaseContract.ServerTable.TABLE_NAME, allServerColumns,
				DataBaseContract.ServerTable._ID + " = " + insertId, null, null, null, null);
		cursor.moveToFirst();
		Server newServer = cursorToServer(cursor);
		cursor.close();
		return newServer;

	}

	/**
	 * Deletes the Server from the database
	 * @param Server to delete
	 */
	public void deleteServer(Server server) {
		long id = server.getId();
		database.delete(DataBaseContract.ServerTable.TABLE_NAME, DataBaseContract.ServerTable._ID + " = " + id, null);
	}

	/**
	 * Retrieves all the servers from the database
	 * @return List of Server
	 */
	public List<Server> getAllServers() {
		List<Server> servers = new ArrayList<Server>();
		Cursor cursor = database.query(DataBaseContract.ServerTable.TABLE_NAME, allServerColumns, null, null, null,
				null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Server server = cursorToServer(cursor);
			servers.add(server);
			cursor.moveToNext();
		}
		cursor.close();
		return servers;
	}
	
	/**
	 * Updates the server in the database
	 * @param server to update
	 */
	public void updateServer(Server server){
		String strFilter = DataBaseContract.ServerTable._ID + "=" + server.getId();
		ContentValues values = new ContentValues();
		values.put(DataBaseContract.ServerTable.NAME, server.getName());
		values.put(DataBaseContract.ServerTable.ADRESS, server.getAdress());
		values.put(DataBaseContract.ServerTable.PORT, server.getPort());
		database.update(DataBaseContract.ServerTable.TABLE_NAME, values, strFilter, null);
	}

	private Server cursorToServer(Cursor cursor) {
		return new Server(cursor.getLong(0), cursor.getString(1), cursor.getString(2), cursor.getInt(3));
	}
}
