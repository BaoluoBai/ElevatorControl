package com.eightmile.elevatorcontrol.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class ElevatorOpenHelper extends SQLiteOpenHelper {
	
	public static final String CREATE_LAYOUT = "create table layout(" +
			"id integer primary key autoincrement, " +
			"content text)";
	
	public static final String CREATE_AD = "create table ad(" +
			"id integer primary key autoincrement, " +
			"content text)";
	
	private Context mContext;
	
	public ElevatorOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
		mContext = context;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(CREATE_LAYOUT);
		db.execSQL(CREATE_AD);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("drop table if exists layout");
		db.execSQL("drop table if exists ad");
		onCreate(db);
	}

}
