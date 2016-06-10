package com.example.compaq.nfc_teacher;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;



//创建数据库
public class NameListDbHelper extends SQLiteOpenHelper{


	public static int DATABASE_VERSION=1;
	public static  String NAMELIST_ID="_id";
	public static  String NAMELIST_NAME="name";
	public static  String NAMELIST_XUEHAO="xuehao";
	public static  String NAMELIST_CHUXI="chuxi";
	public static  String NAMELIST_QUEXI="quexi";
	public static  String NAMELIST_QINGJIA="qingjia";

	public NameListDbHelper(Context context) {
		super(context, "namelist.db", null, 1);
		// TODO Auto-generated constructor stub
		//System.out.println("=====创建数据库=====!");
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub

		System.out.println("=============onCreate=============");
		arg0.execSQL("create table if not exists namelist "+"(" +
				"_id integer primary key,"
				+"name text,"
				+"xuehao text,"
				+"chuxi integer,"
				+"quexi integer,"
				+"qingjia integer);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		arg0.execSQL("DROP TABLE IF EXISTS namelist;");
		onCreate(arg0);
		System.out.println("=====更新数据库=====!");
	}
}