package com.eightmile.elevatorcontrol.db;

import java.util.ArrayList;
import java.util.List;

import com.eightmile.elevatorcontrol.model.AdInfo;
import com.eightmile.elevatorcontrol.model.LayoutInfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;


public class ElevatorDB {
	/**
	 * 数据库表名
	 */
	public static final String DB_NAME = "db_elevator";
	
	/**
	 * 数据库版本号
	 */
	public static final int DB_VERSION = 1;
	
	private static ElevatorDB elevatorDB;
	
	private SQLiteDatabase db;
	
	/**
	 * 构造方法私有化
	 * @param context
	 * @author zc
	 */
	private ElevatorDB(Context context){
		ElevatorOpenHelper dbHelper = new ElevatorOpenHelper(context, DB_NAME, null, DB_VERSION);
		db = dbHelper.getWritableDatabase();
	}
	
	/**
	 * 获取ElevatorDB实例
	 * @param context
	 * @return elevatorDB
	 * @author zc
	 */
	public synchronized static ElevatorDB getInstance(Context context){
		if(elevatorDB == null){
			elevatorDB = new ElevatorDB(context);
		}
		return elevatorDB;
	}
	
	/**
	 * 将从服务器返回的布局数据插入到数据库
	 * @param response 服务器返回的布局数据
	 */
	public void saveLayout(String response){
		if(!TextUtils.isEmpty(response)){
			ContentValues values = new ContentValues();
			values.put("content", response);
			db.insert("layout", null, values);
		}
	}
	
	/**
	 * 从数据库中查询存储的布局数据
	 * @return 布局数据的一个集合
	 */
	public List<LayoutInfo> loadLayout(){
		List<LayoutInfo> list = new ArrayList<LayoutInfo>();
		Cursor cursor = db.query("layout", null, null, null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				LayoutInfo layout = new LayoutInfo();
				layout.setId(cursor.getInt(cursor.getColumnIndex("id")));
				layout.setContent(cursor.getString(cursor.getColumnIndex("content")));
				list.add(layout);
			} while (cursor.moveToNext());
		}
		if (cursor != null) {
			cursor.close();
		}
		return list;
	}
	
	/**
	 * 覆盖数据库中原先保存的布局数据
	 * @param response 从服务器中查询到的数据
	 * @param id 条件匹配值
	 * @return 修改的列数
	 */
	public int updateLayout(String response, int id){
		ContentValues values = new ContentValues();
		values.put("content", response);
		int i = db.update("layout", values, "id = ?", new String[]{String.valueOf(id)});
		return i;
	}
	
	public void saveAd(String response){
		if(!TextUtils.isEmpty(response)){
			ContentValues values = new ContentValues();
			values.put("content", response);
			db.insert("ad", null, values);
		}
	}
	
	public List<AdInfo> loadAd(){
		List<AdInfo> list = new ArrayList<AdInfo>();
		Cursor cursor = db.query("ad", null, null, null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				AdInfo ad = new AdInfo();
				ad.setId(cursor.getInt(cursor.getColumnIndex("id")));
				ad.setContent(cursor.getString(cursor.getColumnIndex("content")));
				list.add(ad);
			} while (cursor.moveToNext());
		}
		if (cursor != null) {
			cursor.close();
		}
		return list;
	}
	
	public int updateAd(String response, int id){
		ContentValues values = new ContentValues();
		values.put("content", response);
		int i = db.update("ad", values, "id = ?", new String[]{String.valueOf(id)});
		return i;
	}
}
