package org.black_mesa.webots_remote_control.database;

import android.provider.BaseColumns;

public final class DataBaseContract {

    public static final  int    DATABASE_VERSION   = 1;
    public static final  String DATABASE_NAME      = "database.db";
    private static final String TEXT_TYPE          = " TEXT";
    private static final String INTEGER_TYPE       = " INTEGER";
    private static final String COMMA_SEP          = ",";

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public DataBaseContract() {}

    public static abstract class ServerTable implements BaseColumns {
        public static final String TABLE_NAME = "servers";
        public static final String NAME = "server_name";
        public static final String PORT = "server_port";
        public static final String ADRESS = "server_adress";


        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                NAME + TEXT_TYPE + COMMA_SEP + 
                ADRESS + TEXT_TYPE + COMMA_SEP +
                PORT + INTEGER_TYPE + COMMA_SEP + " )";
        
        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
    
    
}
